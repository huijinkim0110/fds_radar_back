package fds.radar.repository.fraud;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fds.radar.common.LockRequestStatus;
import fds.radar.entity.dispute.LockRequests;
import jakarta.persistence.LockModeType;

public interface LockRequestRepository extends JpaRepository<LockRequests, Long>{
    
    // 내 잠금 요청 목록
    List<LockRequests> findByUser_UserId(Long userId);

    Optional<LockRequests> findByLockRequestIdAndUser_UserId(Long lockRequestId, Long userId);

    // 관리자 처리 대기 목록
    List<LockRequests> findByRequestStatus(LockRequestStatus requestStatus);

    // 같은 fraud_case 중복 잠금요청 방지
    boolean existsByFraudCase_FraudCaseIdAndRequestStatus(
        Long fraudCaseId, LockRequestStatus requestStatus);

    // D 연동 자동잠금 — 이 사건으로 이미 잠금 걸었나
    Optional<LockRequests> findByFraudCase_FraudCaseId(Long fraudCaseId);

    }