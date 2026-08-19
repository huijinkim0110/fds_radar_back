package fds.radar.service.finance;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fds.radar.common.IncomeSource;
import fds.radar.dto.financial.FinancialProfileRequest;
import fds.radar.dto.financial.FinancialProfileResponse;
import fds.radar.entity.finance.FinancialProfiles;
import fds.radar.entity.user.Users;
import fds.radar.repository.financial.FinancialProfileRepository;
import fds.radar.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FinancialProfileService {
 
    
    private final FinancialProfileRepository financialProfileRepository;
    private final UserRepository userRepository;


    // 금융 프로필 등록
    @Transactional
    public FinancialProfileResponse create(
        Long userId,
        FinancialProfileRequest request) {

    if (FinancialProfileRepository.findByUser_UserId(userId).isPresent()) {
        throw new IllegalArgumentException("이미 등록된 금융 프로필이 있습니다.");
    }

    Users user = userRepository.findById(userId)
            .orElseThrow(() -> 
                    new IllegalArgumentException("사용자를 찾을 수 없습니다.")
        );

    Long availableMonthlyAmount = 
            request.getMonthlyIncome() - request.getMonthlyExpense();
            
    FinancialProfiles profile = FinancialProfiles.builder()
            .user(user)
            .occupation(request.getOccupation())
            .incomeSource(
                    IncomeSource.valueOf(request.getEmploymentType())
            )
            .monthlyIncome(request.getMonthlyIncome())
            .monthlyExpenses(request.getMonthlyExpense())
            .creditLevel(
                    Integer.valueOf(request.getCreditRating())
            )
            .availableMonthlyAmount(availableMonthlyAmount)
            .emergencyFundAmount(0L)
            .createdAt((LocalDateTime.now()))
            .updatedAt(LocalDateTime.now())
            .build();

    financialProfileRepository.save(profile);

    return toResponse(profile);

    }

    // 금융 프로필 조회
    @Transactional(readOnly = true)
    public FinancialProfileResponse getProfile(Long userId) {

        FinancialProfiles profile = FinancialProfileRepository
                .findByUser_UserId(userId)
                .orElseThrow(() -> 
                        new IllegalArgumentException(
                                "등록된 금융 프로필이 없습니다."
                        )
            );

        return toResponse(profile);
    }

    // 금융 프로팔 수정
    @Transactional
    public FinancialProfileResponse update(
            Long userId,
            FinancialProfileRequest request) {
                
        FinancialProfiles profile = FinancialProfileRepository
                .findByUser_UserId(userId)
                .orElseThrow(() -> 
                    new IllegalArgumentException(
                            "등록된 금융 프로필이 없습니다."
                    )
            );

        Long availableMonthlyAmount = 
                request.getMonthlyIncome() - request.getMonthlyExpense();
            
        profile.setOccupation(request.getOccupation());
        
        profile.setIncomeSource(
                IncomeSource.valueOf(request.getEmploymentType())
        );
                
        profile.setMonthlyIncome(request.getMonthlyIncome());
        profile.setMonthlyExpenses(request.getMonthlyExpense());
        
        profile.setCreditLevel(
                Integer.valueOf(request.getCreditRating())
        );
        
        profile.setAvailableMonthlyAmount(availableMonthlyAmount);
        profile.setUpdatedAt(LocalDateTime.now());
    
        financialProfileRepository.save(profile);
        
        return toResponse(profile);
    }

    // Entity -> Response 변환
    private FinancialProfileResponse toResponse(FinancialProfiles profile) {

        return FinancialProfileResponse.builder()
            .id(profile.getFinancialProfileId())
            .occupation(profile.getOccupation())
            .employmentType(profile.getIncomeSource().name())
            .monthlyIncome(profile.getMonthlyIncome())
            .monthlyExpense(profile.getMonthlyExpenses())
            .creditRating(String.valueOf(profile.getCreditLevel()))
            .savingCapacity(profile.getAvailableMonthlyAmount())
            .build();
    }
}