package com.tradingtrends.batch.infrastructure.config;


import com.tradingtrends.batch.application.dto.CorporateCodesResponseDto;
import com.tradingtrends.batch.application.service.CorporateService;
import com.tradingtrends.batch.infrastructure.tasklet.RepeatScheduleTasklet;
import java.io.File;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DartCorporateApiBatchConfig {

    /*
        Chunk-Oriented Processing은 대용량 데이터를 단계별로 처리할 때 유리하며, 대규모 배치 작업에 적합합니다.
        Tasklet은 작업이 단순하고 데이터의 양이 많지 않거나, 단일 작업(예: API 호출, 파일 처리)에서 효율적입니다.
     */
        /*
        Chunk-Oriented Processing은 대용량 데이터를 단계별로 처리할 때 유리하며, 대규모 배치 작업에 적합합니다.
        Tasklet은 작업이 단순하고 데이터의 양이 많지 않거나, 단일 작업(예: API 호출, 파일 처리)에서 효율적입니다.
     */

    private final RepeatScheduleTasklet repeatScheduleTasklet;

    @Bean
    public Job dartApiJob(JobRepository jobRepository, Step dartApiStep) {
        return new JobBuilder("dartApiJob", jobRepository)
                .start(dartApiStep)
                .build();
    }

    @Bean
    public Step dartApiStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("dartApiStep", jobRepository)
                .tasklet(repeatScheduleTasklet, transactionManager)
                .build();
    }


}

