package fds.radar.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiInsuranceRequestDTO {
    private String age;
    private String gender;
    private String region;
    private String income_bracket;
    private String occupation_group;
    private String marital_status;
    private String risk_grade;
    private String cross_coverage;
    private String disease_history;  
}
