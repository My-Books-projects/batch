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
    /**
     * The Sql session factory.
     */
    public final SqlSessionFactory sqlSessionFactory;
    private final JobLauncher jobLauncher;
    private static final int CHUNK_SIZE = 10000;

    /**
     * methodName : runJob <br>
     * author : damho-lee <br>
     * description : 최근 3개월의 실결제금액에 따라 회원 등급 조회해서 회원등급 변경하는 Job 을 실행시키는 메서드. 3개월에 한 번 1일 오전 1시에 실행<br>
     *
     * @throws JobInstanceAlreadyCompleteException Job 이 이미 완료되어 실행시킬 Job 이 없다 -> JobParameter 가 다르면 다른 Job 으로 인식한다
     * @throws JobExecutionAlreadyRunningException Job 이 이미 실행중이다 -> 스프링 배치가 DB 의 테이블을 참조하는데 배치 프로그램이
     *                                             비정상적으로 종료되는 경우 DB 테이블에 완료되었다고 작성하지 못하기 때문에 발생
     * @throws JobParametersInvalidException       JobParameter 가 유요하지 않다 -> 필요한 Parameter 가 제공되지 않거나
     *                                             제공한 입력이 유요하지 않은 경우 발생
     * @throws JobRestartException                 작업을 다시 시작하려는 불법적인 시도를 나타내는 예외
     */
    @Scheduled(cron = "0 0 1 1 */3 *", zone = "Asia/Seoul")
    public void runJob() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException,
            JobParametersInvalidException, JobRestartException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd:HH:mm:ss");
        String formattedDateTime = LocalDateTime.now().format(formatter);

        JobParameters parameters = new JobParametersBuilder()
                .addString("requestDate", formattedDateTime)
                .toJobParameters();
        jobLauncher.run(userGradeChangeJob(), parameters);
    }

    /**
     * methodName : userGradeChangeJob <br>
     * author : damho-lee <br>
     * description : 회원 등급 변경 Job. <br>
     *
     * @return job
     */
    @Bean
    public Job userGradeChangeJob() {
        return jobBuilderFactory.get("changeUserGradeJob")
                .start(changeUserGradeStep(null))
                .build();
    }

    /**
     * methodName : changeUserGradeStep <br>
     * author : damho-lee <br>
     * description : 회원 등급 변경하는 Step. <br>
     *
     * @param requestDate String
     * @return step
     */
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

    /**
     * methodName : userActualPaymentReader <br>
     * author : damho-lee <br>
     * description : userId 와 최근 3개월의 실결제금액으로 어느 등급에 해당하는지(userGradeId)를 조회하는 reader. <br>
     *
     * @param requestDate String
     * @return MyBatisPagingItemReader
     */
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

    /**
     * methodName : userActualPaymentProcessor <br>
     * author : damho-lee <br>
     * description : 회원 아이디, 실결제금액, 회원 등급 아이디 -> 회원 아이디, 회원 등급 아이디로 변환하는 processor. <br>
     *
     * @param requestDate String
     * @return ItemProcessor
     */
    @Bean
    @StepScope
    public ItemProcessor<UserActualPayment, UserGrade> userActualPaymentProcessor(
            @Value("#{jobParameters['requestDate']}") String requestDate) {
        return userActualPayment -> new UserGrade(userActualPayment.getUserId(), userActualPayment.getUserGradeId());
    }

    /**
     * methodName : updateUserGradeWriter <br>
     * author : damho-lee <br>
     * description : 회원 등급 변경하는 writer. 회원등급 변경일자는 현재시간으로.<br>
     *
     * @param requestDate String
     * @return MyBatisBatchItemWriter
     */
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