package com.tradingtrends.corporate.presentation.request;

import jakarta.validation.constraints.AssertTrue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DisclosureSearchRequestDto {
    private String corpName;
    private String reportNm;
    private String startDate;
    private String endDate;

    // 두 날짜가 모두 null이거나 둘 다 있어야 한다는 조건
    @AssertTrue(message = "Both startDate and endDate must be provided or neither.")
    public boolean isDateRangeValid() {
        return (startDate == null && endDate == null) || (startDate != null && endDate != null);
    }
}
