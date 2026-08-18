package fds.radar.service.account;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fds.radar.dto.account.TransferRecipientResponse;
import fds.radar.dto.account.TransferRecipientsCreateRequest;
import fds.radar.entity.account.Accounts;
import fds.radar.entity.account.Institutions;
import fds.radar.entity.account.TransferRecipients;
import fds.radar.entity.user.Users;
import fds.radar.exception.BusinessException;
import fds.radar.exception.NotFoundException;
import fds.radar.repository.account.AccountRepository;
import fds.radar.repository.account.InstitutionRepository;
import fds.radar.repository.account.TransferRepository;
import fds.radar.repository.user.UserRepository;

@Service
public class TransferRecipientService {
    
    private final TransferRepository transferRepository;
    private final TransferRepository recipientRepository;
    private final UserRepository userRepository;
    private final InstitutionRepository institutionRepository;
    private final AccountRepository accountRepository;

    public TransferRecipientService(TransferRepository transferRepository, TransferRepository recipientRepository,
                                    UserRepository userRepository, InstitutionRepository institutionRepository, AccountRepository accountRepository) {
        this.transferRepository = transferRepository;
        this.recipientRepository = recipientRepository;
        this.userRepository = userRepository;
        this.institutionRepository = institutionRepository;
        this.accountRepository = accountRepository;
        }
    
    // 수취인 저장 - 아직 송금하지 않았으니 신규상태
    @Transactional
    public TransferRecipientResponse save(Long userId, TransferRecipientsCreateRequest request) {
        // 같은 기관 + 계좌 중복 방지
        boolean dup = 
            transferRepository.existsByUser_UserIdAndInstitution_InstitutionIdAndAccountNumber(userId, request.getInstitutionId(), request.getAccountNumber());
    if(dup) {
        throw new BusinessException("이미 저장된 수취인입니다.");
    } 

    Users user = userRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));
    Institutions institutions = institutionRepository.findById(request.getInstitutionId())
        .orElseThrow(() -> new BusinessException("금융기간을 찾을 수 없습니다."));

    TransferRecipients transferRecipients = TransferRecipients.builder()
        .user(user)    
        .institution(institutions)
        .recipientName(request.getRecipientName())
        .accountNumber(request.getAccountNumber())
        .isRegistered(true) // 주소록 저장됨
        .firstTransferAt(null) // 아직 송금 이력 없음 -> 신규
        .lastTransferAt(null)
        .build();

    return TransferRecipientResponse.from(transferRepository.save(transferRecipients));
    }

    // 내 수취인 목록
    @Transactional(readOnly = true)
    public List<TransferRecipientResponse> getMyRecipient(Long userId) {
        return recipientRepository.findByUser_UserId(userId).stream()
            .map(TransferRecipientResponse::from)
            .toList();
    }

    // 수취인 삭제 - 본인 것만
    @Transactional
    public void delete(Long userId, Long recipientId) {
        TransferRecipients recipients = recipientRepository.findByRecipientIdAndUser_UserId(recipientId, userId) 
            .orElseThrow(() -> new NotFoundException("수취인을 찾을 수 없습니다."));
            recipientRepository.delete(recipients);
    }

   
@Transactional
    public void transfer(String receiverAccountNumber, Long amount) {
        // 1. 현재 로그인한 사용자의 이름(아이디)을 가져옴
        //String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        // TODO 테스트용 코드 추후에 지우기
        // 테스트용 임시 코드
        String currentEmail = "test@test.com";

        // 2. UserRepository를 통해 Users 엔티티를 조회함
        Users currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new NotFoundException("로그인한 유저를 찾을 수 없습니다."));

        // 3. 유저의 계좌 목록을 조회함 (List<Accounts> 반환)
        List<Accounts> senderAccounts = accountRepository.findByUser_UserId(currentUser.getUserId());

        // 4. 계좌가 하나도 없다면 예외 발생
        if (senderAccounts == null || senderAccounts.isEmpty()) {
            throw new NotFoundException("등록된 계좌를 찾을 수 없습니다.");
        }

        // 5. 목록에서 첫 번째 계좌를 보내는 계좌로 지정 (만약 대표 계좌 선택 로직이 있다면 그에 맞게 수정)
        Accounts senderAccount = senderAccounts.get(0);

        // 6. 전달받은 Long 타입의 amount를 BigDecimal로 변환
        BigDecimal transferAmount = BigDecimal.valueOf(amount);

        // 7. 잔액 비교 (compareTo 결과가 0보다 작으면 잔액 부족)
        if (senderAccount.getBalance().compareTo(transferAmount) < 0) {
            throw new BusinessException("잔액이 부족합니다.");
        }

        // 8. 계좌 잔액 차감
        senderAccount.setBalance(senderAccount.getBalance().subtract(transferAmount));
    }
}
