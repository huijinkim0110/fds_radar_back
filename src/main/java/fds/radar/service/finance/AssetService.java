package fds.radar.service.finance;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fds.radar.common.AssetType;
import fds.radar.dto.finance.AssetRequest;
import fds.radar.dto.finance.AssetResponse;
import fds.radar.entity.finance.Assets;
import fds.radar.entity.user.Users;
import fds.radar.repository.financial.AssetRepository;
import fds.radar.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AssetService {

    private final AssetRepository assetRepository;
    private final UserRepository userRepository;

    // 자산 등록
    @Transactional
    public AssetResponse create(
            Long userId,
            AssetRequest request) {

        Users user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다.")
                );

        Assets asset = Assets.builder()
                .user(user)
                .assetType(
                        AssetType.valueOf(request.getAssetType())
                )
                .currentValue(request.getAmount())
                .evaluatedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();

        assetRepository.save(asset);

        return toResponse(asset);
    }

    // 사용자의 전체 자산 조회
    @Transactional(readOnly = true)
    public List<AssetResponse> getAssets(Long userId) {

        return assetRepository.findByUser_UserId(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // 자산 한 건 조회
    @Transactional(readOnly = true)
    public AssetResponse getAsset(Long assetId) {

        Assets asset = assetRepository.findById(assetId)
                .orElseThrow(() ->
                        new IllegalArgumentException("자산을 찾을 수 없습니다.")
                );

        return toResponse(asset);
    }

    // 자산 수정
    @Transactional
    public AssetResponse update(
            Long assetId,
            AssetRequest request) {

        Assets asset = assetRepository.findById(assetId)
                .orElseThrow(() ->
                        new IllegalArgumentException("수정할 자산을 찾을 수 없습니다.")
                );

        asset.setAssetType(
                AssetType.valueOf(request.getAssetType())
        );

        asset.setCurrentValue(request.getAmount());
        asset.setEvaluatedAt(LocalDateTime.now());

        assetRepository.save(asset);

        return toResponse(asset);
    }

    // 자산 삭제
    @Transactional
    public void delete(Long assetId) {

        Assets asset = assetRepository.findById(assetId)
                .orElseThrow(() ->
                        new IllegalArgumentException("삭제할 자산을 찾을 수 없습니다.")
                );

        assetRepository.delete(asset);
    }

    // 총자산 계산
    @Transactional(readOnly = true)
    public Long getTotalAssets(Long userId) {

        return assetRepository.findByUser_UserId(userId)
                .stream()
                .mapToLong(Assets::getCurrentValue)
                .sum();
    }

    // Entity -> Response 변환
    private AssetResponse toResponse(Assets asset) {

        return AssetResponse.builder()
                .id(asset.getAssetId())
                .assetType(asset.getAssetType().name())
                .amount(asset.getCurrentValue())
                .financialInstitutionName(asset.getInstitutionName())
                .build();
    }
}