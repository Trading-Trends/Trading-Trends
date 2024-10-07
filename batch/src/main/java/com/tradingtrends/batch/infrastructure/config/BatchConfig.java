package com.tradingtrends.batch.infrastructure.config;

import com.tradingtrends.batch.application.service.DartDisclosureService;
import com.tradingtrends.batch.application.service.DartToElasticsearchService;
import com.tradingtrends.batch.domain.model.Entity.Disclosure;
import com.tradingtrends.batch.domain.repository.DisclosureRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DartDisclosureService dartDisclosureService;
    private final DisclosureRepository disclosureRepository;
    private final DartToElasticsearchService dartToElasticsearchService;

    public BatchConfig(JobRepository jobRepository,
                       PlatformTransactionManager transactionManager,
                       DartDisclosureService dartDisclosureService,
                       DisclosureRepository disclosureRepository,
                       DartToElasticsearchService dartToElasticsearchService) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.dartDisclosureService = dartDisclosureService;
        this.disclosureRepository = disclosureRepository;
        this.dartToElasticsearchService = dartToElasticsearchService;
    }


    @Bean
    public Job disclosureJob(Step step1, Step step2) {
        return new JobBuilder("disclosureJob", jobRepository)
                .start(step1)
                .next(step2)
                .build();
    }

    @Bean
    public Step step1() {
        return new StepBuilder("fetchAndStoreDisclosures", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    List<Disclosure> disclosures = dartDisclosureService.fetchDisclosuresToday();
                    chunkContext.getStepContext().getStepExecution().getJobExecution()
                            .getExecutionContext().put("disclosures", disclosures);
                    disclosureRepository.saveAll(disclosures);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step step2() {
        return new StepBuilder("fetchAndStoreDocuments", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    // Step1에서 저장한 데이터 가져오기
                    List<Disclosure> disclosures = (List<Disclosure>) chunkContext.getStepContext()
                            .getStepExecution().getJobExecution().getExecutionContext().get("disclosures");

                    for (Disclosure disclosure : disclosures) {
                        dartToElasticsearchService.fetchDocumentAndSaveToElasticsearch(disclosure.getRceptNo(), disclosure);
                    }
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

}

