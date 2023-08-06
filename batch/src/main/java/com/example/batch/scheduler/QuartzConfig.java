package com.example.batch.scheduler;

import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.io.IOException;
import java.time.ZoneId;
import java.util.Properties;
import java.util.TimeZone;

import static com.example.batch.utils.CronUtils.AM_12_00;

@Configuration
@RequiredArgsConstructor
public class QuartzConfig {

    private final JobDetailGenerator jobDetailGenerator;

    @Bean
    public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
        JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
        jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
        return jobRegistryBeanPostProcessor;
    }

    @Bean
    public JobDetail todayScheduleJobDetail() {
        return jobDetailGenerator.generate("todayScheduleJob");
    }

    @Bean
    public Trigger todayScheduleTrigger() {
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder
                .cronSchedule(AM_12_00)
                .inTimeZone(TimeZone.getTimeZone(ZoneId.of("Asia/Seoul")));

        return TriggerBuilder.newTrigger()
                .forJob(todayScheduleJobDetail())
                .withSchedule(cronScheduleBuilder)
                .build();
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
        SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
        scheduler.setTriggers(todayScheduleTrigger());
        scheduler.setQuartzProperties(quartzProperties());
        scheduler.setJobDetails(todayScheduleJobDetail());
        return scheduler;
    }

    @Bean
    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }

}
