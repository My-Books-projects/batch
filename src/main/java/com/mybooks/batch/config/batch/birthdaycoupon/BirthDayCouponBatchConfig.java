package com.mybooks.batch.config.batch.birthdaycoupon;

import com.mybooks.batch.coupon.mapper.CouponMapper;
import com.mybooks.batch.coupon.model.BirthDayCoupon;
import com.mybooks.batch.usercoupon.model.UserCoupon;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
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
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * packageName    : store.mybooks.resource.config
 * fileName       : BirthdayCouponConfig
 * author         : damho-lee
 * date           : 3/11/24
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 3/11/24          damho-lee          최초 생성
 */
@Configuration
@RequiredArgsConstructor
public class BirthDayCouponBatchConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    public final SqlSessionFactory sqlSessionFactory;
    private final JobLauncher jobLauncher;
    private final CouponMapper couponMapper;
    private static final int CHUNK_SIZE = 10;

    /**
     * methodName : runJob <br>
     * author : damho-lee <br>
     * description : 생일쿠폰 생성 및 발급 하는 Job 을 실행시키는 메서드. 매달 마지막 일 23시 30분에 실행<br>
     *
     * @throws JobInstanceAlreadyCompleteException Job 이 이미 완료되어 실행시킬 Job 이 없다 -> JobParameter 가 다르면 다른 Job 으로 인식한다
     * @throws JobExecutionAlreadyRunningException Job 이 이미 실행중이다 -> 스프링 배치가 DB 의 테이블을 참조하는데 배치 프로그램이
     *                                             비정상적으로 종료되는 경우 DB 테이블에 완료되었다고 작성하지 못하기 때문에 발생
     * @throws JobParametersInvalidException       JobParameter 가 유요하지 않다 -> 필요한 Parameter 가 제공되지 않거나
     *                                             제공한 입력이 유요하지 않은 경우 발생
     * @throws JobRestartException                 작업을 다시 시작하려는 불법적인 시도를 나타내는 예외
     */
//    @Scheduled(cron = "0 30 23 L * *")
    @Scheduled(cron = "0 40 9 21 3 *")
    public void runJob() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException,
            JobParametersInvalidException, JobRestartException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd:HH:mm:ss");
        String formattedDateTime = LocalDateTime.now().format(formatter);

        JobParameters parameters = new JobParametersBuilder()
                .addString("requestDate", formattedDateTime)
                .toJobParameters();
        jobLauncher.run(birthdayCouponJob(), parameters);
    }

    /**
     * methodName : birthdayCouponJob <br>
     * author : damho-lee <br>
     * description : 생일쿠폰 만든 후 생일쿠폰 발급해주는 Job.<br>
     *
     * @return job
     */
    @Bean
    public Job birthdayCouponJob() {
        return jobBuilderFactory.get("birthDayCouponJob")
                .start(makeBirthDayCouponStep(null))
                .next(giveBirthDayCouponStep(null))
                .build();
    }

    /**
     * methodName : makeBirthDayCouponStep <br>
     * author : damho-lee <br>
     * description : 생일쿠폰 만드는 Step.<br>
     *
     * @param requestDate String
     * @return TaskletStep
     */
    @Bean
    @JobScope
    public TaskletStep makeBirthDayCouponStep(@Value("#{jobParameters['requestDate']}") String requestDate) {
        return stepBuilderFactory.get("makeBirthDayCouponStep")
                .tasklet((stepContribution, chunkContext) -> {
                    BirthDayCoupon birthDayCoupon = createBirthDayCoupon();
                    couponMapper.createBirthDayCoupon(birthDayCoupon);
                    ExecutionContext jobExecutionContext =
                            chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
                    jobExecutionContext.put("couponId", birthDayCoupon.getId());

                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    /**
     * methodName : giveBirthDayCouponStep <br>
     * author : damho-lee <br>
     * description : 생일쿠폰 나눠주는 Step.<br>
     *
     * @param requestDate String
     * @return step
     */
    @Bean
    @JobScope
    public Step giveBirthDayCouponStep(@Value("#{jobParameters['requestDate']}") String requestDate) {
        return stepBuilderFactory.get("giveBirthDayCouponStep")
                .<Long, UserCoupon>chunk(CHUNK_SIZE)
                .reader(birthDayCouponReader(null))
                .processor(birthDayCouponProcessor(null, null))
                .writer(birthDayCouponWriter(null))
                .build();
    }

    /**
     * methodName : birthDayCouponReader <br>
     * author : damho-lee <br>
     * description : 생일쿠폰 발급 대상자(만드는 시점 다음 달 생일자)들을 읽는 reader.<br>
     *
     * @param requestDate String
     * @return MyBatisPagingItemReader
     */
    @Bean
    @StepScope
    public MyBatisPagingItemReader<Long> birthDayCouponReader(
            @Value("#{jobParameters['requestDate']}") String requestDate) {
        MyBatisPagingItemReader<Long> reader = new MyBatisPagingItemReader<>();
        Map<String, Object> parameterValues = new HashMap<>();

        parameterValues.put("month", String.format("%02d", LocalDate.now().plusMonths(1).getMonthValue()));
        reader.setPageSize(CHUNK_SIZE);
        reader.setSqlSessionFactory(sqlSessionFactory);
        reader.setQueryId("com.mybooks.batch.user.mapper.UserMapper.getUserIdWhoWasBornThisMonth");
        reader.setParameterValues(parameterValues);

        return reader;
    }

    /**
     * methodName : birthDayCouponProcessor <br>
     * author : damho-lee <br>
     * description : userId 를 받아 userId 와 couponId 를 갖는 UserCoupon 반환.<br>
     *
     * @param requestDate String
     * @param couponId    Long
     * @return ItemProcessor
     */
    @Bean
    @StepScope
    public ItemProcessor<Long, UserCoupon> birthDayCouponProcessor(
            @Value("#{jobParameters['requestDate']}") String requestDate,
            @Value("#{jobExecutionContext['couponId']}") Long couponId) {
        return userId -> new UserCoupon(userId, couponId);
    }

    /**
     * methodName : birthDayCouponWriter <br>
     * author : damho-lee <br>
     * description : UserCoupon 에 값을 넣는 Writer.<br>
     *
     * @param requestDate String
     * @return MyBatisBatchItemWriter
     */
    @Bean
    @StepScope
    public MyBatisBatchItemWriter<UserCoupon> birthDayCouponWriter(
            @Value("#{jobParameters['requestDate']}") String requestDate) {
        return new MyBatisBatchItemWriterBuilder<UserCoupon>()
                .sqlSessionFactory(sqlSessionFactory)
                .statementId("com.mybooks.batch.usercoupon.mapper.UserCouponMapper.createUserBirthDayCoupon")
                .build();
    }

    private BirthDayCoupon createBirthDayCoupon() {
        LocalDate startDate = LocalDate.now().plusMonths(1).withDayOfMonth(1);
        LocalDate endDate = LocalDate.now().plusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
        String name = String.format("%02d월 생일쿠폰", startDate.getMonthValue());
        return BirthDayCoupon.builder()
                .name(name)
                .orderMin(0)
                .maxDiscountCost(10000)
                .discountRate(20)
                .startDate(startDate)
                .endDate(endDate)
                .isRate(true)
                .isTargetOrder(true)
                .createdDate(LocalDate.now())
                .build();
    }
}