package com.mybooks.batch.config;

import java.time.LocalDate;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
@EnableBatchProcessing
@RequiredArgsConstructor
public class BirthDayCouponConfig {
    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job birthDayCouponJob() {
        return jobBuilderFactory.get("birthDayCoupon")
                .start(step())
                .build();
    }

    @Bean
    @JobScope
    public Step step() {
        return stepBuilderFactory.get("step")
                .<>
    }

    @Bean
    @StepScope
    public RepositoryItemReader<User> userReader() {
        return new RepositoryItemReaderBuilder<User>()
                .repository(userRepository)
                .pageSize(10)
                .maxItemCount(10)
                .name("userRepositoryItemReader")
                .methodName("findByBirthMonthDayIsLikeAndUserStatus_Id")
                .arguments(Arrays.asList(String.format("%2d-", LocalDate.now().getMonthValue()), "활성"))
                .build();
    }

    @Bean
    @StepScope
    public RepositoryItemReader<Coupon> couponReader() {
        return new RepositoryItemReaderBuilder<Coupon>()
                .repository(couponRepository)
                .
    }
}
