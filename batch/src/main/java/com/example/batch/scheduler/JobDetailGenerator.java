package com.example.batch.scheduler;

import lombok.RequiredArgsConstructor;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JobDetailGenerator {

    private final JobLauncher jobLauncher;
    private final JobLocator jobLocator;

    public JobDetail generate(String jobName) {
        return JobBuilder.newJob(BatchScheduledJob.class)
                .setJobData(createJobDataMap(jobName))
                .storeDurably()
                .withIdentity(jobName)
                .build();
    }

    private JobDataMap createJobDataMap(String jobName) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("jobName", jobName);
        jobDataMap.put("jobLauncher", jobLauncher);
        jobDataMap.put("jobLocator", jobLocator);
        return jobDataMap;
    }
}
