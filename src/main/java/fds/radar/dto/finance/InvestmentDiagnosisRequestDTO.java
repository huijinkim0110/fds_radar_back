package fds.radar.dto.finance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvestmentDiagnosisRequestDTO {
    private Long userId;

    // 1. 연령대 (20대 이하=3, 30~40대=2, 50대=1, 60대이상=0)
    private Integer ageScore;
    
    // 2. 투자경험 (없음=0, 예적금위주=1, 펀드채권=2, 주식파생=3)
    private Integer investmentExperienceScore;

    // 3. 금융상품 지식수준(매우낮음=0 ~ 매우높음=3)
    private Integer knowledgeLevelScore;

    // 4. 투자 가능 기간(1년미만=0, 1~3년=1, 3년이상=2)
    private Integer preferredPeriodScore;

    // 5. 자산 대비 투자 가능 금액 비중(낮음=0 ~ 높음=2)
    private Integer assetRatioScore;

    // 6. 원금 손실 감내 수준(전혀안됨=0 ~ 높은손실=3)
    private Integer lossToleranceScore;

    // 7. 원금 보장 필요 여부
    private Boolean principalProtectionRequired;
}
