package fds.radar.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendationRequestDTO {
    // 프론트 -> Spring Boot로 오는 요청
    private Long userId;
    private String age;
    private String gender;
    private String region;
    private String incomeBracket;
    private String occupationGroup;
    private String maritalStatus;

    // 보험 추천 전용
    private String crossCoverage;   // 특약 보유 형태
    private String diseaseHistory;  // 질병 이력
}
