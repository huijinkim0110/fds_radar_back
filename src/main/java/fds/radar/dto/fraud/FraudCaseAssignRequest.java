package fds.radar.dto.fraud;

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
public class FraudCaseAssignRequest {
    private Long adminId;        // 새로 배정할 담당자
    private Long actingAdminId;  // 배정을 실행한(로그인한) 관리자
}