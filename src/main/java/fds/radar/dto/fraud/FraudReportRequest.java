package fds.radar.dto.fraud;

import lombok.Getter;

@Getter
public class FraudReportRequest {
    private Long transactionId;
    private String reasonCategory; // [D파트 담당자 추가] 선택형 사유
    private String reason; // 상세 내용 (기존 필드, 의미를 "상세 내용"으로 명확히)
}