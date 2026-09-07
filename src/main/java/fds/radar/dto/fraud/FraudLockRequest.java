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
    private RequestTargetType targetType;
    private String requestReason;
    private Long actingAdminId; // 잠금 요청을 실행한(로그인한) 관리자
}