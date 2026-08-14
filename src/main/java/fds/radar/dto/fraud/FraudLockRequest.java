package fds.radar.dto.fraud;

import fds.radar.common.RequestTargetType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FraudLockRequest {
    private RequestTargetType targetType; // CARD 또는 ACCOUNT
    private String requestReason;         // 잠금 사유 (관리자가 입력)
}