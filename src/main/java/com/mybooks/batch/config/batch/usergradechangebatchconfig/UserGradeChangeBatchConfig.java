package com.mybooks.batch.config.batch.usergradechangebatchconfig;

import com.mybooks.batch.useractualpayment.model.UserActualPayment;
import com.mybooks.batch.useractualpayment.model.UserGrade;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder;
import org.mybatis.spring.batch.builder.MyBatisPagingItemReaderBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * packageName    : com.mybooks.batch.config.batch.usergradechangebatchconfig
 * fileName       : UserGradeChangeBatchConfig
 * author         : damho-lee
 * date           : 3/21/24
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 3/21/24          damho-lee          최초 생성
 */
@Configuration
@RequiredArgsConstructor
public class UserGradeChangeBatchConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    public final SqlSessionFactory sqlSessionFactory;
    private final JobLauncher jobLauncher;
    private static final int CHUNK_SIZE = 10;

    @Scheduled(cron = "30 * * * * *", zone = "Asia/Seoul")
    public void runJob() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException,
            JobParametersInvalidException, JobRestartException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd:HH:mm:ss");
        String formattedDateTime = LocalDateTime.now().format(formatter);

        JobParameters parameters = new JobParametersBuilder()
                .addString("requestDate", formattedDateTime)
                .toJobParameters();
        jobLauncher.run(userGradeChangeJob(), parameters);
    }

    @Bean
    public Job userGradeChangeJob() {
        return jobBuilderFactory.get("changeUserGradeJob")
                .start(changeUserGradeStep(null))
                .build();
    }

    @Bean
    @JobScope
    public Step changeUserGradeStep(@Value("#{jobParameters['requestDate']}") String requestDate) {
        return stepBuilderFactory.get("changeUserGradeStep")
                .<UserActualPayment, UserGrade>chunk(CHUNK_SIZE)
                .reader(userActualPaymentReader(null))
                .processor(userActualPaymentProcessor(null))
                .writer(updateUserGradeWriter(null))
                .build();
    }

    @Bean
    @StepScope
    public MyBatisPagingItemReader<UserActualPayment> userActualPaymentReader(
            @Value("#{jobParameters['requestDate']}") String requestDate) {
        return new MyBatisPagingItemReaderBuilder<UserActualPayment>()
                .pageSize(CHUNK_SIZE)
                .sqlSessionFactory(sqlSessionFactory)
                .queryId("com.mybooks.batch.useractualpayment.mapper.UserActualPaymentMapper.getUserActualPayment")
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<UserActualPayment, UserGrade> userActualPaymentProcessor(
            @Value("#{jobParameters['requestDate']}") String requestDate) {
        return userActualPayment -> new UserGrade(userActualPayment.getUserId(), userActualPayment.getUserGradeId());
    }

    @Bean
    @StepScope
    public MyBatisBatchItemWriter<UserGrade> updateUserGradeWriter(
            @Value("#{jobParameters['requestDate']}") String requestDate) {
        return new MyBatisBatchItemWriterBuilder<UserGrade>()
                .sqlSessionFactory(sqlSessionFactory)
                .statementId("com.mybooks.batch.useractualpayment.mapper.UserActualPaymentMapper.updateUserGrade")
                .build();
    }
}