package fds.radar.common;

import lombok.Getter;

@Getter
public enum SubscriptionStatus {
    ACTIVE,
    CANCELLED,
    COMPLETED // 만기 도달(일시납) / 완납(적금) - 만기 시 예상 만기금액을 계좌로 지급 처리
}
