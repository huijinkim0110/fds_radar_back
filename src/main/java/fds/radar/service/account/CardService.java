package fds.radar.service.account;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import fds.radar.common.CardStatus;
import fds.radar.dto.account.CardCreateRequest;
import fds.radar.dto.account.CardResponse;
import fds.radar.entity.account.Cards;
import fds.radar.entity.account.Institutions;
import fds.radar.entity.user.Users;
import fds.radar.exception.BusinessException;
import fds.radar.exception.NotFoundException;
import fds.radar.repository.account.CardRepository;
import fds.radar.repository.account.InstitutionRepository;
import fds.radar.repository.user.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class CardService {
    
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final InstitutionRepository institutionRepository;
    private final CardNumberGenerator cardNumberGenerator;

    public CardService(CardRepository cardRepository,
                       UserRepository userRepository,
                       InstitutionRepository institutionRepository,
                       CardNumberGenerator cardNumberGenerator) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.institutionRepository = institutionRepository;
        this.cardNumberGenerator = cardNumberGenerator;
    }

    // 카드 발급 - User 직속, 금융기관 지정, 만료일 서버 계산
    @Transactional
    public CardResponse createCard(Long userId, CardCreateRequest request) {
        Users user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));
        
        Institutions institution = institutionRepository.findById(request.getInstitutionId())
            .orElseThrow(() -> new NotFoundException("금융기간을 찾을 수 없습니다."));

        LocalDateTime now = LocalDateTime.now();

        Cards card = Cards.builder()
            .user(user)
            .institution(institution)
            .cardNumber(cardNumberGenerator.generate())
            .cardName(request.getCardName())
            .cardType(request.getCardType())
            .creditLimit(request.getCreditLimit())
            .availableLimit(request.getCreditLimit()) // 발급 시 사용가능 한도
            .status(CardStatus.ACTIVE)
            .issuedAt(now)
            .expiredAt(now.plusYears(5)) // 발급일 +5년 후 해지
            .build();
    
    return CardResponse.from(cardRepository.save(card));
    }

    // 2. 내 카드 목록 조회
    public List<CardResponse> getCardsByUserId(Long userId) {
        return cardRepository.findByUser_UserId(userId).stream()
            .map(CardResponse::from)
            .collect(Collectors.toList());
    }

    // 3. 카드 이용한도 변경
    @Transactional
    public void updateCardLimit(Long cardId, BigDecimal creditLimit) {
        Cards card = cardRepository.findById(cardId)
            .orElseThrow(() -> new NotFoundException("카드를 찾을 수 없습니다."));
        card.setCreditLimit(creditLimit);
    }

    // 4. 카드 상태 변경
    @Transactional
    public void updateCardStatus(Long cardId, CardStatus status) {
        Cards card = cardRepository.findById(cardId)
            .orElseThrow(() -> new NotFoundException("카드를 찾을 수 없습니다."));
        card.setStatus(status);
    }

    @Transactional
public void payWithCard(Long cardId, BigDecimal amount) {

    Cards card = cardRepository.findByCardIdForUpdate(cardId)
        .orElseThrow(() ->
            new NotFoundException("카드를 찾을 수 없습니다."));

    if (card.getAvailableLimit().compareTo(amount) < 0) {
        throw new BusinessException("사용 가능한도가 부족합니다.");
    }

    BigDecimal newLimit =
        card.getAvailableLimit().subtract(amount);

    card.setAvailableLimit(newLimit);

    // DB에 명시적으로 저장
    cardRepository.save(card);
}
}
