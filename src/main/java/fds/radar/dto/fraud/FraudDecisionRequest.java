package fds.radar.dto.fraud;

import fds.radar.common.FraudDecision;
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
public class FraudDecisionRequest {
    private FraudDecision decision;
}