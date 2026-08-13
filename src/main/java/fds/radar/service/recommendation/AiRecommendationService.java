package fds.radar.service.recommendation;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import fds.radar.common.RiskTendency;
import fds.radar.dto.recommendation.AiInsuranceRequestDTO;
import fds.radar.dto.recommendation.AiRecommendationResponseDTO;
import fds.radar.dto.recommendation.AiSecuritiesRequestDTO;
import fds.radar.dto.recommendation.RecommendationRequestDTO;
import fds.radar.entity.finance.InvestmentProfiles;
import fds.radar.service.finance.InvestmentProfileService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AiRecommendationService {
    
    private final InvestmentProfileService investmentProfileService;

    private final RestClient restClient = RestClient.create("http://localhost:8000");

    // FastAPI 증권 추천 서버 호출
    public AiRecommendationResponseDTO getSecuritiesRecommendation(RecommendationRequestDTO dto) {
        InvestmentProfiles profile = investmentProfileService.getLatestProfile(dto.getUserId());

        AiSecuritiesRequestDTO aiRequest = AiSecuritiesRequestDTO.builder()
                                                                 .age(dto.getAge())
                                                                 .gender(dto.getGender())
                                                                 .region(dto.getRegion())
                                                                 .income_bracket(dto.getIncomeBracket())
                                                                 .occupation_group(dto.getOccupationGroup())
                                                                 .marital_status(dto.getMaritalStatus())
                                                                 .investment_propensity(mapRiskTendency(profile.getRiskTendency()))
                                                                 .build();

        return restClient.post()   
                         .uri("/recommend/securities")
                         .contentType(MediaType.APPLICATION_JSON)
                         .body(aiRequest)
                         .retrieve()
                         .body(AiRecommendationResponseDTO.class);
    }

    // FastAPI 보험 추천 서버 호출
    public AiRecommendationResponseDTO getInsuranceRecommendation(RecommendationRequestDTO dto) {
        AiInsuranceRequestDTO aiRequest = AiInsuranceRequestDTO.builder()
                                                                 .age(dto.getAge())
                                                                 .gender(dto.getGender())
                                                                 .region(dto.getRegion())
                                                                 .income_bracket(dto.getIncomeBracket())
                                                                 .occupation_group(dto.getOccupationGroup())
                                                                 .marital_status(dto.getMaritalStatus())
                                                                 .risk_grade("B") // 심사 전 상태 가정, 평균 등급으로 고정
                                                                 .cross_coverage(dto.getCrossCoverage())
                                                                 .disease_history(dto.getDiseaseHistory())
                                                                 .build();

        return restClient.post()
                         .uri("/recommend/insurance")
                         .contentType(MediaType.APPLICATION_JSON)
                         .body(aiRequest)
                         .retrieve()
                         .body(AiRecommendationResponseDTO.class);
    }

    private String mapRiskTendency(RiskTendency tendency) {
        return switch (tendency) {
            case STABLE -> "안정형";
            case NEUTRAL -> "위험중립형";
            case ACTIVE -> "적극투자형";
            case AGGRESSIVE -> "공격투자형";
        };
    }
}
