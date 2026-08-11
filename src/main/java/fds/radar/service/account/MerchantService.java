package fds.radar.service.account;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fds.radar.common.RiskStatus;
import fds.radar.dto.MerchantCreateRequest;
import fds.radar.dto.MerchantResponse;
import fds.radar.dto.MerchantRiskUpdateRequest;
import fds.radar.entity.transaction.Merchants;
import fds.radar.exception.BusinessException;
import fds.radar.exception.NotFoundException;
import fds.radar.repository.transaction.MerchanstRepository;

@Service
public class MerchantService {
    
    private final MerchanstRepository merchanstRepository;

    public MerchantService(MerchanstRepository merchanstRepository) {
        this.merchanstRepository = merchanstRepository;
    }

    // 가맹점 등록 (ADMIN) - riskStatus는 서버가 NORMAL로 세팅
    @Transactional
    public MerchantResponse create(MerchantCreateRequest request) {
        if(merchanstRepository.existsByMerchantName(request.getMerchantName())) {
            throw new BusinessException("이미 등록된 가맹점입니다.");
        }

        Merchants merchants = Merchants.builder()
            .merchantName(request.getMerchantName())
            .businessCategory(request.getBusinessCategory())
            .countryCode(request.getCountryCode())
            .region(request.getRegoin())
            .onlineMerchant(request.isOnlineMerchant())
            .riskStatus(RiskStatus.NORMAL)
            .regDate(LocalDateTime.now())
            .build();

        return MerchantResponse.from(merchanstRepository.save(merchants));
    }

    // 가맹점 목록
    @Transactional(readOnly = true)
    public List<MerchantResponse> getMerchants() {
        return merchanstRepository.findAll().stream()
            .map(MerchantResponse::from)
            .toList();
    }

    // 가맹점 상세
    @Transactional(readOnly = true)
    public MerchantResponse getMerchant(Long merchantId) {
        return MerchantResponse.from(findById(merchantId));
    }


    // 위험상태 변경 (ADMIN)
    @Transactional
    public MerchantResponse updateRisk(Long merchantId, MerchantRiskUpdateRequest request) {
        Merchants merchants = findById(merchantId);
        merchants.setRiskStatus(request.getRiskStatus());
        return MerchantResponse.from(merchants);
    }

     private Merchants findById(Long merchantId) {
        return merchanstRepository.findById(merchantId)
            .orElseThrow(() -> new NotFoundException("가맹점을 찾을 수 없습니다."));

    }

}
