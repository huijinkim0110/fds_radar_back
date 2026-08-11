package fds.radar.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fds.radar.common.LockRequestStatus;
import fds.radar.entity.dispute.LockRequests;
import jakarta.persistence.LockModeType;

public interface LockRequestRepository extends JpaRepository<LockRequests, Long>{
    
    // 내 잠금 요청 목록
    List<LockRequests> findByUserId(Long userId);

    // 본인 요청 상세 (남의 요청 조회 차단)
    Optional<LockRequests> findByIdAndUserId(Long id, Long userId);

    // 관리자 처리 대기 목록
    List<LockRequests> findByRequestStatus(LockRequestStatus status);

    // 특정 대상(카드/계좌) 처리 안된 요청 확인 - 중복 요청 방지
    boolean existsByTargetTypeAndTargetIdAndStatus(
        LockModeType targetType, Long targetId, LockRequestStatus status
    );

    // fraud_case 연동 - 중복 자동잠금 방지
    Optional<LockRequests> findByFraudCaseId(Long fraudCaseId);
}
