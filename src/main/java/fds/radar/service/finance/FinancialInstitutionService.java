package fds.radar.service.finance;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fds.radar.common.InstitutionStatus;
import fds.radar.common.InstitutionType;
import fds.radar.dto.financial.FinancialInstitutionResponse;
import fds.radar.dto.financial.FinanciallnstitutionRequest;
import fds.radar.entity.account.Institutions;
import fds.radar.repository.financial.FinancialInstitutionRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FinancialInstitutionService {
    
    private final FinancialInstitutionRepository financialInstitutionRepository;

    // 금융기관 등록
    @Transactional
    public FinancialInstitutionResponse create(
            FinancialInstitutionResponse request) {
        
        Institutions institutions = Institutions.builder()
                .institutionName(request.getName())
                .institutionType(
                        InstitutionType.valueOf(request.getType())
                )  
                .status(InstitutionStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build(); 

        financialInstitutionRepository.save(institutions);

        return toResponse(institutions);
    }

    // 금융기관 전체 조회
    @Transactional(readOnly = true)
    public List<FinancialInstitutionResponse> getInstitution() {

        return financialInstitutionRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // 금융기관 한 건 조회
    @Transactional(readOnly = true)
    public FinancialInstitutionResponse getInstitution(
            Long institutionId) {

        Institutions institution = 
                financialInstitutionRepository.findById(institutionId)
                        .orElseThrow(() -> 
                                new IllegalArgumentException(
                                    "금융기관을 찾을 수 없습니다."
                                )
                    );  
                    
        return toResponse(institution);
    }

    // 금융기관 수정
    @Transactional
    public FinancialInstitutionResponse update(
            Long instiutionId,
            FinanciallnstitutionRequest request) {

        Institutions institution = 
                financialInstitutionRepository.findById(instiutionId)
                        .orElseThrow(() -> 
                            new IllegalArgumentException(
                                    "수정할 금융기관을 찾을 수 없습니다."
                            )
                    );

            institution.setInstitutionName(request.getName());
                
            institution.setInstitutionType(
                    InstitutionType.valueOf(request.getType())
            );

            financialInstitutionRepository.save(institution);
                
            return toResponse(institution);

    }

      // Entity -> Response 변환
    private FinancialInstitutionResponse toResponse(
            Institutions institution) {

        return FinancialInstitutionResponse.builder()
                .id(institution.getInstitutionId())
                .name(institution.getInstitutionName())
                .type(institution.getInstitutionType().name())
                .build();
    }
}
