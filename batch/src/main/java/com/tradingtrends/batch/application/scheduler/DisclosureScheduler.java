package com.tradingtrends.batch.application.scheduler;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.UUID;

@EnableScheduling
@Configuration
public class DisclosureScheduler {
    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job disclosureJob;

    @Scheduled(cron = "0 0 4 * * ?")
    public void runDisclosureJob() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addString("runId", UUID.randomUUID().toString())
                    .toJobParameters();
            jobLauncher.run(disclosureJob, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
