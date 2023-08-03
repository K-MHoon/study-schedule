package com.example.batch.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
public class TodayScheduleConfiguration {

    @Bean
    public Job todayScheduleJob(JobRepository jobRepository, Step todayScheduleStep) {
        return new JobBuilder("todayScheduleJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(todayScheduleStep)
                .build();
    }
    @Bean
    public Step todayScheduleStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("todayScheduleStep", jobRepository)
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("hello");
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager).build();
    }
}
