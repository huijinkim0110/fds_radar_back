package fds.radar.service.financialProduct;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import fds.radar.common.InstitutionStatus;
import fds.radar.common.InstitutionType;
import fds.radar.common.ProductStatus;
import fds.radar.common.ProductType;
import fds.radar.common.RiskLevel;
import fds.radar.entity.account.Institutions;
import fds.radar.entity.financialProduct.FinancialProducts;
import fds.radar.repository.account.InstitutionRepository;
import fds.radar.repository.financialProduct.FinancialProductsRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FssProductSyncService {

    private final FinancialProductsRepository financialProductsRepository;
    private final InstitutionRepository institutionRepository;

    private final RestClient restClient = RestClient.create("https://finlife.fss.or.kr");
    private static final String AUTH_KEY = "eaa070e83b8d4c8d3df7a9dc9aecff6a";

    // 정기예금 데이터 가져와서 저장
    @Transactional
    public int syncDeposits() {
        return syncProducts("/finlifeapi/depositProductsSearch.json", ProductType.DEPOSIT);
    }

    // 적금 데이터 가져와서 저장
    @Transactional
    public int syncSavings() {
        return syncProducts("/finlifeapi/savingProductsSearch.json", ProductType.SAVINGS);
    }

    @SuppressWarnings("unchecked")
    private int syncProducts(String path, ProductType productType) {
        Map<String, Object> response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(path)
                        .queryParam("auth", AUTH_KEY)
                        .queryParam("topFinGrpNo", "020000")
                        .queryParam("pageNo", "1")
                        .build())
                .retrieve()
                .body(Map.class);

        Map<String, Object> result = (Map<String, Object>) response.get("result");
        List<Map<String, Object>> baseList = (List<Map<String, Object>>) result.get("baseList");
        List<Map<String, Object>> optionList = (List<Map<String, Object>>) result.get("optionList");

        int savedCount = 0;

        for (Map<String, Object> base : baseList) {
            String finCoNo = (String) base.get("fin_co_no");
            String finPrdtCd = (String) base.get("fin_prdt_cd");
            String koreanName = (String) base.get("kor_co_nm");
            String productName = (String) base.get("fin_prdt_nm");
            String etcNote = (String) base.get("etc_note");

            // 이 상품의 12개월 금리를 optionList에서 찾기
            BigDecimal rate12 = findRateByTerm(optionList, finCoNo, finPrdtCd, "12");
            if (rate12 == null) {
                continue; // 12개월 옵션이 없는 상품은 건너뜀
            }

            // 은행(기관) 없으면 새로 생성, 있으면 재사용
            Institutions institution = institutionRepository.findByInstitutionName(koreanName)
                    .orElseGet(() -> institutionRepository.save(
                            Institutions.builder()
                                    .institutionName(koreanName)
                                    .institutionType(InstitutionType.BANK)
                                    .status(InstitutionStatus.ACTIVE)
                                    .createdAt(LocalDateTime.now())
                                    .build()));

            FinancialProducts product = FinancialProducts.builder()
                    .institution(institution)
                    .productName(productName)
                    .productType(productType)
                    .description(etcNote)
                    .riskLevel(RiskLevel.VERY_LOW)
                    .principalProtection(true)
                    .subscriptionPeriod(12)
                    .recommendedPeriod(12)
                    .expectedReturnRate(rate12)
                    .productStatus(ProductStatus.ON_SALE)
                    .saleStartDate(LocalDateTime.now())
                    .saleEndDate(LocalDateTime.now().plusYears(1))
                    .build();

            financialProductsRepository.save(product);
            savedCount++;
        }

        return savedCount;
    }

    @SuppressWarnings("unchecked")
    private BigDecimal findRateByTerm(List<Map<String, Object>> optionList, String finCoNo, String finPrdtCd,
            String term) {
        for (Map<String, Object> option : optionList) {
            if (finCoNo.equals(option.get("fin_co_no"))
                    && finPrdtCd.equals(option.get("fin_prdt_cd"))
                    && term.equals(option.get("save_trm"))) {
                Object rate = option.get("intr_rate");
                if (rate == null)
                    return null;
                return BigDecimal.valueOf(((Number) rate).doubleValue());
            }
        }
        return null;
    }
}
