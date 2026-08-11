package fds.radar.service.account;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fds.radar.common.CardStatus;
import fds.radar.dto.CardCreateRequest;
import fds.radar.dto.CardResponse;
import fds.radar.entity.account.Cards;
import fds.radar.exception.NotFoundException;

import java.time.LocalDateTime;
import fds.radar.entity.account.Institutions;
import fds.radar.entity.user.Users;

import fds.radar.repository.account.CardRepository;
import fds.radar.repository.account.InstitutionRepository;
import fds.radar.repository.user.UserRepository;


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

    // 카드 발급 — User 직속, 금융기관 지정, 만료일 서버 계산
    @Transactional
    public CardResponse issueCard(Long userId, CardCreateRequest request) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다"));

        Institutions institution = institutionRepository.findById(request.getInstitutionId())
                .orElseThrow(() -> new NotFoundException("금융기관을 찾을 수 없습니다"));

        LocalDateTime now = LocalDateTime.now();

        Cards card = Cards.builder()
                .user(user)
                .institution(institution)
                .cardNumber(cardNumberGenerator.generate())
                .cardName(request.getCardName())
                .cardType(request.getCardType())
                .creditLimit(request.getCreditLimit())
                .availableLimit(request.getCreditLimit())   // 발급 시 사용가능 = 이용한도
                .status(CardStatus.ACTIVE)
                .issuedAt(now)
                .expiredAt(now.plusYears(5))                // 서버가 발급일 + 5년
                .build();

        return CardResponse.from(cardRepository.save(card));
    }
}