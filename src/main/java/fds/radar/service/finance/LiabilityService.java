package fds.radar.service.finance;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fds.radar.common.LiabilityType;
import fds.radar.dto.finance.LiabilityRequest;
import fds.radar.dto.finance.LiabilityResponse;
import fds.radar.entity.finance.Liabilities;
import fds.radar.entity.user.Users;
import fds.radar.repository.financial.LiabilityRepository;
import fds.radar.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LiabilityService {

    private final LiabilityRepository liabilityRepository;
    private final UserRepository userRepository;

    // 부채 등록
    @Transactional
    public LiabilityResponse create(
            Long userId,
            LiabilityRequest request) {

        Users user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다.")
                );

        Liabilities liability = Liabilities.builder()
                .user(user)
                .liabilityType(
                        LiabilityType.valueOf(request.getLoanType())
                )
                .originalAmount(request.getPrincipalAmount())
                .remainingAmount(request.getRemainimgAmount())
                .interestRate(
                        BigDecimal.valueOf(request.getInterestRate())
                )
                .createdAt(LocalDateTime.now())
                .build();

        liabilityRepository.save(liability);

        return toResponse(liability, 0.0);
    }

    // 사용자의 전체 부채 조회
    @Transactional(readOnly = true)
    public List<LiabilityResponse> getLiabilities(Long userId) {

        return liabilityRepository.findByUser_UserId(userId)
                .stream()
                .map(liability -> toResponse(liability, 0.0))
                .collect(Collectors.toList());
    }

    // 부채 한 건 조회
    @Transactional(readOnly = true)
    public LiabilityResponse getLiability(Long liabilityId) {

        Liabilities liability = liabilityRepository.findById(liabilityId)
                .orElseThrow(() ->
                        new IllegalArgumentException("부채 정보를 찾을 수 없습니다.")
                );

        return toResponse(liability, 0.0);
    }

    // 부채 수정
    @Transactional
    public LiabilityResponse update(
            Long liabilityId,
            LiabilityRequest request) {

        Liabilities liability = liabilityRepository.findById(liabilityId)
                .orElseThrow(() ->
                        new IllegalArgumentException("수정할 부채 정보를 찾을 수 없습니다.")
                );

        liability.setLiabilityType(
                LiabilityType.valueOf(request.getLoanType())
        );

        liability.setOriginalAmount(
                request.getPrincipalAmount()
        );

        liability.setRemainingAmount(
                request.getRemainimgAmount()
        );

        liability.setInterestRate(
                BigDecimal.valueOf(request.getInterestRate())
        );

        liabilityRepository.save(liability);

        return toResponse(liability, 0.0);
    }

    // 부채 삭제
    @Transactional
    public void delete(Long liabilityId) {

        Liabilities liability = liabilityRepository.findById(liabilityId)
                .orElseThrow(() ->
                        new IllegalArgumentException("삭제할 부채 정보를 찾을 수 없습니다.")
                );

        liabilityRepository.delete(liability);
    }

    // DSR 계산
    @Transactional(readOnly = true)
    public Double calculateDsr(Long userId, Long annualIncome) {

        if (annualIncome == null || annualIncome <= 0) {
            throw new IllegalArgumentException("연소득은 0보다 커야 합니다.");
    }

        long annualDebtPayment = liabilityRepository
                .findByUser_UserId(userId)
                .stream()
                .mapToLong(liability -> liability.getMonthlyPayment() * 12)
                .sum();

        return ((double) annualDebtPayment / annualIncome) * 100;
    }

    // 총 남은 부채 금액
    @Transactional(readOnly = true)
    public Long getTotalRemainingAmount(Long userId) {

        return liabilityRepository.findByUser_UserId(userId)
                .stream()
                .mapToLong(Liabilities::getRemainingAmount)
                .sum();
    }

    // Entity -> Response
    private LiabilityResponse toResponse(
            Liabilities liability,
            Double dsr) {

        return LiabilityResponse.builder()
                .id(liability.getLiabilityId())
                .loanType(liability.getLiabilityType().name())
                .remainingAmount(liability.getRemainingAmount())
                .dsr(dsr)
                .build();
    }
}