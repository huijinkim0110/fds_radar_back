package fds.radar.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import fds.radar.dto.TransferRecipientResponse;
import fds.radar.dto.TransferRecipientsCreateRequest;
import fds.radar.entity.account.Institutions;
import fds.radar.entity.account.TransferRecipients;
import fds.radar.entity.user.Users;
import fds.radar.exception.BusinessException;
import fds.radar.exception.NotFoundException;
import fds.radar.repository.InstitutionRepository;


public class TransferRecipientService {
    
    private final TrnasferRecipientRepository recipientRepository;
    private final UserRepository userRepository;
    private final InstitutionRepository institutionRepository;

    public TransferRecipientService(TransferRecipientRepository recipientRepository,
        UserRepository userRepository, InstitutionRepository institutionRepository
    ) {
        this.recipientRepository = recipientRepository;
        this.userRepository = userRepository;
        this.institutionRepository = institutionRepository;
    } 

    // 수취인 저장 - 아직 송금 안했으니 신규 상태
    @Transactional
    public TransferRecipientResponse save(Long userId, TransferRecipientsCreateRequest request) {
        // 같은 기관 + 계좌 중복 방지
        boolean dup = 
            recipientRepository.existsByUserIdAndInstitution_InstitutionIdAndAccountByNumber(
                userId, request.getInstitutionId(), request.getAccountNumber());
                if(dup) {
                    throw new BusinessException("이미 저장된 수취인입니다.");
                }

                Users user = userRepository.findById(userId)
                        .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));
                Institutions institutions =
                institutionRepository.findById(request.getInstitutionId())
                        .orElseThrow(() -> new BusinessException("금융기간을 찾을 수 없습니다."));

                TransferRecipients transferRecipients = TransferRecipients.builder()
                    .user(user)
                    .institution(institutions)
                    .recipientName(request.getRecipientName())
                    .accountNumber(request.getAccountNumber())
                    .isRegistered(true) // 주소록 저장됨
                    .firstTransferAt(null) // 아직 송금 이력 없은 -> 신규
                    .lastTransferAt(null)
                    .build();

                    return TransferRecipientResponse.from(transferRecipientsRepository.save(recipient));
    }

    // 내 수취인 목록
    @Transactional(readOnly = true)
    public List<TransferRecipientResponse> getMyRecipients(Long userId) {
        return recipientRepository.findByUserId(userId).stream()
            .map(TransferRecipientResponse::from)
            .toList();
    }

    // 수취인 삭제 - 본인 것만
    @Transactional
    public void delete(Long userId, Long recipientId) {
        TransferRecipients recipients = recipientRepository.findByIdAndUserId(recipientId, userId)
                .orElseThrow(() -> new NotFoundException("수취인을 찾을 수 없습니다."));
                recipientRepository.delete(recipients);
    }


}
