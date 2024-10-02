package com.tradingtrends.batch.infrastructure.tasklet;

import com.tradingtrends.batch.application.dto.CorporateCodesResponseDto;
import com.tradingtrends.batch.application.service.CorporateService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RepeatScheduleTasklet implements Tasklet {

    private final CorporateService corporateService;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
        throws Exception {
        // CorporateService의 메서드를 호출하여 DART API 정보를 처리
        List<CorporateCodesResponseDto> result = corporateService.fetchAndSaveCorpCodeInfo();
        log.info("DART API 에서 반환된 데이터 수: {}", result.size());
        return RepeatStatus.FINISHED;  // Tasklet 작업이 완료되면 반환
    }
}
