package fds.radar.dto.financial;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FinancialInstitutionResponse {
    private Long id;
    private String name;
    private String type;
}
