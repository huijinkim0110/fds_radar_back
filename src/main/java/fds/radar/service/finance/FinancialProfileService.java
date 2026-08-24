package fds.radar.service.finance;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // 금융 프로필 조회
    @Transactional(readOnly = true)
    public FinancialProfileResponse getProfile(Long userId) {

        FinancialProfiles profile = financialProfileRepository
                .findByUser_UserId(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("재무 프로필이 없습니다.")
                );

        return toResponse(profile);
    }

    // 금융 프로필 수정
    @Transactional
    public FinancialProfileResponse update(
            Long userId,
            FinancialProfileRequest request) {

        FinancialProfiles profile = financialProfileRepository
                .findByUser_UserId(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("등록된 금융 프로필이 없습니다.")
                );

        long availableMonthlyAmount =
                request.getMonthlyIncome() - request.getMonthlyExpenses();

        profile.setOccupation(request.getOccupation());
        profile.setIncomeSource(request.getIncomeSource());
        profile.setMonthlyIncome(request.getMonthlyIncome());
        profile.setMonthlyExpenses(request.getMonthlyExpenses());
        profile.setCreditLevel(request.getCreditLevel());
        profile.setAvailableMonthlyAmount(availableMonthlyAmount);
        profile.setEmergencyFundAmount(request.getEmergencyFundAmount());
        profile.setUpdatedAt(LocalDateTime.now());

        FinancialProfiles saved =
                financialProfileRepository.save(profile);

        return toResponse(saved);
    }

    // 금융 프로필 존재 여부
    @Transactional(readOnly = true)
    public boolean hasProfile(Long userId) {

        return financialProfileRepository
                .findByUser_UserId(userId)
                .isPresent();
    }

    // 있으면 수정, 없으면 등록
    @Transactional
    public FinancialProfileResponse upsertProfile(
            FinancialProfileRequest request) {

        Users user = userRepository
                .findById(request.getUserId())
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다.")
                );

        long availableMonthlyAmount =
                request.getMonthlyIncome() - request.getMonthlyExpenses();

        FinancialProfiles profile =
                financialProfileRepository
                        .findByUser_UserId(request.getUserId())
                        .orElseGet(() ->
                                FinancialProfiles.builder()
                                        .user(user)
                                        .createdAt(LocalDateTime.now())
                                        .build()
                        );

        profile.setOccupation(request.getOccupation());
        profile.setIncomeSource(request.getIncomeSource());
        profile.setMonthlyIncome(request.getMonthlyIncome());
        profile.setMonthlyExpenses(request.getMonthlyExpenses());
        profile.setCreditLevel(request.getCreditLevel());
        profile.setAvailableMonthlyAmount(availableMonthlyAmount);
        profile.setEmergencyFundAmount(request.getEmergencyFundAmount());
        profile.setUpdatedAt(LocalDateTime.now());

        FinancialProfiles saved =
                financialProfileRepository.save(profile);

        return toResponse(saved);
    }

    // Entity -> Response 변환
    private FinancialProfileResponse toResponse(
            FinancialProfiles profile) {

        return FinancialProfileResponse.builder()
                .financialProfileId(profile.getFinancialProfileId())
                .occupation(profile.getOccupation())
                .incomeSource(profile.getIncomeSource())
                .monthlyIncome(profile.getMonthlyIncome())
                .monthlyExpenses(profile.getMonthlyExpenses())
                .creditLevel(profile.getCreditLevel())
                .availableMonthlyAmount(profile.getAvailableMonthlyAmount())
                .emergencyFundAmount(profile.getEmergencyFundAmount())
                .build();
    }

   // 금융 프로필 등록
@Transactional
public FinancialProfileResponse create(
        Long userId,
        FinancialProfileRequest request) {

    Users user = userRepository.findById(userId)
            .orElseThrow(() ->
                    new IllegalArgumentException("사용자를 찾을 수 없습니다.")
            );

    // 이미 등록된 프로필이 있으면 막기
    if (financialProfileRepository.findByUser_UserId(userId).isPresent()) {
        throw new IllegalArgumentException("이미 등록된 금융 프로필이 있습니다.");
    }

    long availableMonthlyAmount =
            request.getMonthlyIncome() - request.getMonthlyExpenses();

    FinancialProfiles profile = FinancialProfiles.builder()
            .user(user)
            .occupation(request.getOccupation())
            .incomeSource(request.getIncomeSource())
            .monthlyIncome(request.getMonthlyIncome())
            .monthlyExpenses(request.getMonthlyExpenses())
            .creditLevel(request.getCreditLevel())
            .availableMonthlyAmount(availableMonthlyAmount)
            .emergencyFundAmount(request.getEmergencyFundAmount())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    FinancialProfiles saved =
            financialProfileRepository.save(profile);

    return toResponse(saved);
}
}