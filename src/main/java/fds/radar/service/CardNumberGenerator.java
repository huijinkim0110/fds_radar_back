package fds.radar.service;

import java.util.concurrent.ThreadLocalRandom;

import fds.radar.repository.CardRepository;

public class CardNumberGenerator {
    
    private final CardRepository cardRepository;

    public CardNumberGenerator(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public String generate() {
        String number;
        do {
            number = build();
        } while (cardRepository.existsByCardNumber(number));
        return number;
    }

    private String build() {
        // 16 4-4-4-4
        StringBuilder sb = new StringBuilder();
        for (int g = 0; g < 4; g++) {
            if(g > 0) sb.append("-");
            sb.append(String.format("%04d", ThreadLocalRandom.current().nextInt(0, 10000)));
        }
        return sb.toString();

    }
}
