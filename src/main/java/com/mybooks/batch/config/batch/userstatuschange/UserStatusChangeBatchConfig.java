package com.mybooks.batch.config.batch.userstatuschange;

import com.mybooks.batch.user.model.User;
import com.mybooks.batch.user.model.UserStatus;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder;
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
 * packageName    : com.mybooks.batch.config.batch.userstatuschange
 * fileName       : UserStatusChangeConfig
 * author         : damho-lee
 * date           : 3/19/24
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 3/19/24          damho-lee          최초 생성
 */
@Configuration
@RequiredArgsConstructor
public class UserStatusChangeBatchConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    public final SqlSessionFactory sqlSessionFactory;
    private final JobLauncher jobLauncher;
    private static final int CHUNK_SIZE = 10;

    /**
     * methodName : runJob <br>
     * author : damho-lee <br>
     * description : 일정 시간동안 로그인 하지 않은 회원 휴면 상태로 변경하는 job 실행. 매일 자정에 실행.<br>
     *
     * @throws JobInstanceAlreadyCompleteException Job 이 이미 완료되어 실행시킬 Job 이 없다 -> JobParameter 가 다르면 다른 Job 으로 인식한다
     * @throws JobExecutionAlreadyRunningException Job 이 이미 실행중이다 -> 스프링 배치가 DB 의 테이블을 참조하는데 배치 프로그램이
     *                                             비정상적으로 종료되는 경우 DB 테이블에 완료되었다고 작성하지 못하기 때문에 발생
     * @throws JobParametersInvalidException       JobParameter 가 유효하지 않다 -> 필요한 Parameter 가 제공되지 않거나
     *                                             제공한 입력이 유효하지 않은 경우 발생
     * @throws JobRestartException                 작업을 다시 시작하려는 시도를 나타내는 예외
     */
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void runJob() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException,
            JobParametersInvalidException, JobRestartException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd:HH:mm:ss");
        String formattedDateTime = LocalDateTime.now().format(formatter);

        JobParameters parameters = new JobParametersBuilder()
                .addString("requestDate", formattedDateTime)
                .toJobParameters();
        jobLauncher.run(userStatusChangeJob(), parameters);
    }

    /**
     * methodName : userStatusChangeJob <br>
     * author : damho-lee <br>
     * description : 회원 상태 변경하는 Job.<br>
     *
     * @return job
     */
    @Bean
    public Job userStatusChangeJob() {
        return jobBuilderFactory.get("userStatusChangeJob")
                .start(userStatusChangeStep(null))
                .build();
    }

    /**
     * methodName : userStatusChangeStep <br>
     * author : damho-lee <br>
     * description : 회원 상태 변경하는 Step 구성.<br>
     *
     * @param requestDate String
     * @return step
     */
    @Bean
    @JobScope
    public Step userStatusChangeStep(@Value("#{jobParameters['requestDate']}") String requestDate) {
        return stepBuilderFactory.get("userStatusChangeStep")
                .<Long, User>chunk(CHUNK_SIZE)
                .reader(userStatusReader(null))
                .processor(userStatusProcessor(null))
                .writer(userStatusWriter(null))
                .build();
    }

    /**
     * methodName : userStatusReader <br>
     * author : damho-lee <br>
     * description : 90일 동안 로그인 하지 않고 활성상태인 회원 조회하는 reader.<br>
     *
     * @param requestDate String
     * @return MyBatisPagingItemReader
     */
    @Bean
    @StepScope
    public MyBatisPagingItemReader<Long> userStatusReader(
            @Value("#{jobParameters['requestDate']}") String requestDate) {
        MyBatisPagingItemReader<Long> reader = new MyBatisPagingItemReader<>();
        Map<String, Object> parameterValues = new HashMap<>();

        parameterValues.put("userStatus", UserStatus.ACTIVE.getStatus());
        reader.setPageSize(CHUNK_SIZE);
        reader.setSqlSessionFactory(sqlSessionFactory);
        reader.setQueryId("com.mybooks.batch.user.mapper.UserMapper.getUserStatusChangeTarget");
        reader.setParameterValues(parameterValues);

        return reader;
    }

    /**
     * methodName : userStatusProcessor <br>
     * author : damho-lee <br>
     * description : userId 를 받아서 User 를 반환.<br>
     *
     * @param requestDate String
     * @return ItemProcessor
     */
    @Bean
    @StepScope
    public ItemProcessor<Long, User> userStatusProcessor(@Value("#{jobParameters['requestDate']}") String requestDate) {
        return userId -> new User(userId, UserStatus.INACTIVE.getStatus());
    }

    /**
     * methodName : userStatusWriter <br>
     * author : damho-lee <br>
     * description : 회원 상태를 바꾸는 writer.<br>
     *
     * @param requestDate String
     * @return MyBatisBatchItemWriter
     */
    @Bean
    @StepScope
    public MyBatisBatchItemWriter<User> userStatusWriter(@Value("#{jobParameters['requestDate']}") String requestDate) {
        return new MyBatisBatchItemWriterBuilder<User>()
                .sqlSessionFactory(sqlSessionFactory)
                .statementId("com.mybooks.batch.user.mapper.UserMapper.updateUserStatus")
                .build();
    }
}