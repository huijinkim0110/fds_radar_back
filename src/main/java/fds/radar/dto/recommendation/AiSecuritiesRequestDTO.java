package fds.radar.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiSecuritiesRequestDTO {
    // Spring Boot -> FastAPI로 보내는 요청
    private String age;
    private String gender;
    private String region;
    private String income_bracket;
    private String occupation_group;
    private String marital_status;
    private String investment_propensity;
}
