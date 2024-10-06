package com.tradingtrends.batch.infrastructure.trigger;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.util.SoftHashMap;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DartCorporateApiJobScheduler {

    private final Job job;
    private final JobLauncher jobLauncher;

    @Scheduled(cron = "22 5 11 * * *", zone = "Asia/Seoul")
    public void scheduleDartApiJob() {
        log.info("Scheduled Dart API Job started at: {}", System.currentTimeMillis());
        this.schedulerRun(this.job);
    }

    private void schedulerRun(final Job job) {
        final Map<String, JobParameter<?>> jobParametersMap = new SoftHashMap<>();
        jobParametersMap.put("time", new JobParameter<>(System.currentTimeMillis(), Long.class));
        final JobParameters jobParameters = new JobParameters(jobParametersMap);
        try {
            log.info("Running the Dart API job with parameters: {}", jobParameters);
            this.jobLauncher.run(job, jobParameters);
            log.info("Job executed successfully.");
        } catch (JobExecutionAlreadyRunningException e) {
            log.error("Job execution already running: {}", e.getMessage());
        } catch (JobInstanceAlreadyCompleteException e) {
            log.error("Job instance already complete: {}", e.getMessage());
        } catch (JobParametersInvalidException e) {
            log.error("Invalid job parameters: {}", e.getMessage());
        } catch (JobRestartException e) {
            log.error("Job restart exception: {}", e.getMessage());
        }
    }


}
