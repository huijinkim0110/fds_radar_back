package fds.radar.service.finance;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fds.radar.dto.financial.FinancialProfileRequest;
import fds.radar.dto.financial.FinancialProfileResponse;
import fds.radar.entity.finance.FinancialProfiles;
import fds.radar.entity.user.Users;
import fds.radar.exception.NotFoundException;
import fds.radar.repository.financial.FinancialProfileRepository;
import fds.radar.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FinancialProfileService {
    
    private final FinancialProfileRepository financialProfileRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly=true)
    public FinancialProfileResponse getProfile(Long userId) {
        FinancialProfiles profile = financialProfileRepository.findByUser_UserId(userId)
                                                              .orElseThrow(() -> new NotFoundException("재무 프로필이 없습니다."));

        return toResponse(profile);
    }

    @Transactional(readOnly=true)
    public boolean hasProfile(Long userId) {
        return financialProfileRepository.findByUser_UserId(userId).isPresent();
    }

    @Transactional
    public FinancialProfileResponse upsertProfile(FinancialProfileRequest dto) {
        Users user = userRepository.findById(dto.getUserId())
                                   .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

        long availableMonthlyAmount = dto.getMonthlyIncome() - dto.getMonthlyExpenses();

        FinancialProfiles profile = financialProfileRepository.findByUser_UserId(dto.getUserId())
                                                              .orElseGet(() -> FinancialProfiles.builder()
                                                                                                .user(user)
                                                                                                .createdAt(LocalDateTime.now())
                                                                                                .build());

        profile.setOccupation(dto.getOccupation());
        profile.setIncomeSource(dto.getIncomeSource());
        profile.setMonthlyIncome(dto.getMonthlyIncome());
        profile.setMonthlyExpenses(dto.getMonthlyExpenses());
        profile.setCreditLevel(dto.getCreditLevel());
        profile.setAvailableMonthlyAmount(availableMonthlyAmount);
        profile.setEmergencyFundAmount(dto.getEmergencyFundAmount());
        profile.setUpdatedAt(LocalDateTime.now());

        FinancialProfiles saved = financialProfileRepository.save(profile);
        return toResponse(saved);
    }

    private FinancialProfileResponse toResponse(FinancialProfiles profile) {
        return FinancialProfileResponse.builder()
                                       .financialProfileId(profile.getFinancialProfileId())
                                       .occupation(profile.getOccupation())
                                       .incomeSource(profile.getIncomeSource())
                                       .monthlyIncome(profile.getMonthlyIncome())
                                       .monthlyExpenses(profile.getMonthlyExpenses())
                                       .creditLevel(profile.getCreditLevel())
                                       .availableMonthlyAmount(profile.getAvailableMonthlyAmount())
                                       .emergencyFundAmount(profile.getEmergencyFundAmount())
                                       .updatedAt(profile.getUpdatedAt())
                                       .build();
    }
}
