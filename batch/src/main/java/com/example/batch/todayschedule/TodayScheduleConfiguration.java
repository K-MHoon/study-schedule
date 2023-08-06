package com.example.batch.todayschedule;

import com.example.common.entity.schedule.Schedule;
import com.example.common.entity.schedule.ScheduleHistory;
import com.example.common.entity.schedule.ScheduleTodo;
import com.example.common.enums.IsUse;
import com.example.common.enums.SchedulePeriod;
import com.example.common.enums.ScheduleType;
import com.example.common.repository.schedule.ScheduleHistoryRepository;
import com.example.common.repository.schedule.ScheduleRepository;
import com.example.common.repository.schedule.ScheduleTodoRepository;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class TodayScheduleConfiguration {

    private static final int CHUNK_SIZE = 10;
    private final ScheduleRepository scheduleRepository;
    private final ScheduleHistoryRepository scheduleHistoryRepository;
    private final ScheduleTodoRepository scheduleTodoRepository;

    @Bean
    public Job todayScheduleJob(JobRepository jobRepository
            , Step patternScheduleStep
            , Step longTermScheduleStep) {
        return new JobBuilder("todayScheduleJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(patternScheduleStep)
                .next(longTermScheduleStep)
                .build();
    }

    @Bean
    public Step patternScheduleStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("patternScheduleStep", jobRepository)
                .<Schedule, Schedule>chunk(CHUNK_SIZE, transactionManager)
                .reader(patternScheduleItemReader())
                .processor(patternScheduleItemProcessor())
                .writer(patternScheduleItemWriter())
                .build();
    }

    @Bean
    public Step longTermScheduleStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("longTermScheduleStep", jobRepository)
                .<Schedule, Schedule>chunk(CHUNK_SIZE, transactionManager)
                .reader(longTermScheduleItemReader())
                .processor(longTermScheduleItemProcessor())
                .writer(longTermScheduleItemWriter())
                .build();
    }

    @Bean
    public RepositoryItemReader<Schedule> patternScheduleItemReader() {
        return new RepositoryItemReaderBuilder<Schedule>()
                .name("patternScheduleItemReader")
                .repository(scheduleRepository)
                .pageSize(CHUNK_SIZE)
                .methodName("findByScheduleTypeAndIsUseAndNextScheduleDate")
                .arguments(ScheduleType.PATTERN, IsUse.Y, LocalDate.now().minusDays(1))
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public RepositoryItemReader<Schedule> longTermScheduleItemReader() {
        return new RepositoryItemReaderBuilder<Schedule>()
                .name("longTermScheduleItemReader")
                .repository(scheduleRepository)
                .pageSize(CHUNK_SIZE)
                .methodName("findByScheduleTypeAndIsUseAndEndDate")
                .arguments(ScheduleType.LONG_TERM, IsUse.Y, LocalDate.now().minusDays(1))
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public ItemProcessor<Schedule, Schedule> patternScheduleItemProcessor() {
        return schedule -> {
            log.info("schedule Id = {}", schedule.getId());
            if (schedule.getPeriod() == SchedulePeriod.DAY) {
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
            List<ScheduleTodo> scheduleTodoList = scheduleTodoRepository.findAllBySchedule(schedule);
            createScheduleHistory(scheduleTodoList);
            resetScheduleTodo(scheduleTodoList);
            return schedule;
        };
    }

    @Bean
    public ItemProcessor<Schedule, Schedule> longTermScheduleItemProcessor() {
        return schedule -> {
            log.info("schedule Id = {}", schedule.getId());
            List<ScheduleTodo> scheduleTodoList = scheduleTodoRepository.findAllBySchedule(schedule);
            createScheduleHistory(scheduleTodoList);
            schedule.delete();
            return schedule;
        };
    }

    private void createScheduleHistory(List<ScheduleTodo> scheduleTodoList) {
        List<ScheduleHistory> scheduleHistoryList = scheduleTodoList.stream()
                .map(st -> ScheduleHistory.builder()
                        .schedule(st.getSchedule())
                        .todo(st.getTodo())
                        .isClear(st.getIsClear())
                        .activeDate(LocalDate.now().minusDays(1))
                        .reason(st.getReason())
                        .build())
                .collect(Collectors.toList());
        scheduleHistoryRepository.saveAll(scheduleHistoryList);
    }

    private void resetScheduleTodo(List<ScheduleTodo> scheduleTodoList) {
        scheduleTodoList.forEach(scheduleTodo -> scheduleTodo.reset());
        scheduleTodoRepository.saveAll(scheduleTodoList);
    }

    @Bean
    public RepositoryItemWriter<Schedule> patternScheduleItemWriter() {
        return new RepositoryItemWriterBuilder<Schedule>()
                .repository(scheduleRepository) // saveAll
                .build();
    }

    @Bean
    public RepositoryItemWriter<Schedule> longTermScheduleItemWriter() {
        return new RepositoryItemWriterBuilder<Schedule>()
                .repository(scheduleRepository) // saveAll
                .build();
    }
}
