package fds.radar.service.finance;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fds.radar.common.FinancialCategory;
import fds.radar.common.RecordType;
import fds.radar.dto.financial.FinancialRecordRequest;
import fds.radar.dto.financial.FinancialRecordResponse;
import fds.radar.entity.finance.FinancialRecords;
import fds.radar.entity.user.Users;
import fds.radar.repository.financial.FinancialRecordRepository;
import fds.radar.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FinancialRecordSerive {
    
    private final FinancialRecordRepository financialRecordRepository;
    private final UserRepository userRepository;

    // 수입/지출 기록 등록
    @Transactional
    public FinancialRecordResponse create(
        Long userId,
        FinancialRecordRequest request) {

    Users user = userRepository.findById(userId)
            .orElseThrow(() -> 
                    new IllegalArgumentException("사용자를 찾을 수 없습니다.")
        );

    FinancialRecords record = FinancialRecords.builder()
            .user(user)
            .recordType(RecordType.valueOf(request.getType()))
            .financialCategory(
                    FinancialCategory.valueOf(request.getCategory())
            )
            .amount(request.getAmount())
            .occurredAt(request.getRecordDate().atStartOfDay())
            .createdAt(LocalDateTime.now())
            .build();

        financialRecordRepository.save(record);

        return toResponse(record);
    }

    // 사용자의 전체 수입/지출 기록 조회
    @Transactional(readOnly = true)
    public List<FinancialRecordResponse> getRecords(Long userId) {

        return financialRecordRepository.findByUser_UserId(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // 수입/지출 기록 한 건 조회
    @Transactional(readOnly = true)
    public FinancialRecordResponse getRecord(Long recordId) {

        FinancialRecords record = financialRecordRepository.findById(recordId)
                .orElseThrow(() -> 
                    new IllegalArgumentException(
                            "금융 기록을 찾을 수 없습니다."
                    )
            );
        
        return toResponse(record);
    }

    // 수입/지출 기록 수정
    @Transactional
    public FinancialRecordResponse update(
            Long recordId,
            FinancialRecordRequest request) {

        FinancialRecords record = financialRecordRepository.findById(recordId)
                .orElseThrow(() -> 
                    new IllegalArgumentException(
                            "수정할 금융 기록을 찾을 수 없습니다."
                    )
            );

        record.setRecordType(
                RecordType.valueOf(request.getType())
        );

        record.setFinancialCategory(
                FinancialCategory.valueOf((request.getCategory()))
        );

        record.setAmount(request.getAmount());
        record.setOccurredAt(request.getRecordDate().atStartOfDay());
        
        financialRecordRepository.save(record);

        return toResponse(record);
    
    }

    // 수입/지출 기록 삭제
    @Transactional
    public void delete(Long recordId) {

        FinancialRecords record = financialRecordRepository.findById(recordId)
                .orElseThrow(() -> 
                    new IllegalArgumentException(
                            "삭제할 금융 기록을 찾을 수 없습니다."
                    )
            );

        financialRecordRepository.delete(record);
    }

   // Entity -> Response 변환
    private FinancialRecordResponse toResponse(FinancialRecords record) {

        return FinancialRecordResponse.builder()
                .id(record.getFinancialRecordId())
                .category(record.getFinancialCategory().name())
                .type(record.getRecordType().name())
                .amount(record.getAmount())
                .recordDate(
                        record.getOccurredAt().toLocalDate()
                )
                .build();
    }
}
