package com.example.batch.todayschedule;

import com.example.common.entity.schedule.Schedule;
import com.example.common.enums.IsUse;
import com.example.common.enums.SchedulePeriod;
import com.example.common.enums.ScheduleType;
import com.example.common.repository.schedule.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class TodayScheduleConfiguration {

    private static final int CHUNK_SIZE = 10;
    private final ScheduleRepository scheduleRepository;

    @Bean
    public Job todayScheduleJob(JobRepository jobRepository, Step todayScheduleStep) {
        return new JobBuilder("todayScheduleJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(todayScheduleStep)
                .build();
    }
    @Bean
    public Step todayScheduleStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("todayScheduleStep", jobRepository)
                .<Schedule, Schedule>chunk(CHUNK_SIZE, transactionManager)
                .reader(todayScheduleItemReader())
                .processor(todayScheduleItemProcessor())
                .writer(todayScheduleItemWriter())
                .build();
    }

    @Bean
    public RepositoryItemReader<Schedule> todayScheduleItemReader() {
        return new RepositoryItemReaderBuilder<Schedule>()
                .name("todayScheduleItemReader")
                .repository(scheduleRepository)
                .pageSize(CHUNK_SIZE)
                .methodName("findByScheduleTypeAndIsUseAndNextScheduleDate")
                .arguments(ScheduleType.PATTERN, IsUse.Y, LocalDate.now())
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public ItemProcessor<Schedule, Schedule> todayScheduleItemProcessor() {
        return schedule -> {
            log.info("schedule Id = {}", schedule.getId());
            if(schedule.getPeriod() == SchedulePeriod.DAY) {
                schedule.updateNextScheduleDate(schedule.getNextScheduleDate().plusDays(1));
            } else if (schedule.getPeriod() == SchedulePeriod.WEEK) {
                schedule.updateNextScheduleDate(schedule.getNextScheduleDate().plusWeeks(1));
            } else if (schedule.getPeriod() == SchedulePeriod.MONTH) {
                schedule.updateNextScheduleDate(schedule.getNextScheduleDate().plusMonths(1));
            } else if (schedule.getPeriod() == SchedulePeriod.YEAR) {
                schedule.updateNextScheduleDate(schedule.getNextScheduleDate().plusYears(1));
            } else {
                Long customDay = schedule.getCustom();
                schedule.updateNextScheduleDate(schedule.getNextScheduleDate().plusDays(customDay));
            }
            log.info("nextScheduleDate = {}", schedule.getNextScheduleDate());
            return schedule;
        };
    }

    @Bean
    public RepositoryItemWriter<Schedule> todayScheduleItemWriter() {
        return new RepositoryItemWriterBuilder<Schedule>()
                .repository(scheduleRepository) // saveAll
                .build();
    }
}
