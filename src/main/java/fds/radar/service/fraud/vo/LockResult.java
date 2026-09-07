package fds.radar.service.fraud.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 카드/계좌 잠금 처리 결과만 담는 내부 전용 객체.
 *
 * FraudPrediction과 동일한 역할 — LockService가 반환한 "순수 결과"를
 * FraudCaseService가 받아서 LockRequests 엔티티 갱신 + 이력 기록에 쓰는 버퍼.
 *
 * @Setter가 없는 이유:
 *   - 처리 결과는 한 번 나오면 이후 임의로 바뀌면 안 되는 값이라
 *     생성 시(@Builder)에만 값을 채우고 이후로는 읽기(Getter)만 허용하는 불변 객체로 설계함.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LockResult {
    private boolean success;
    private String message;
}