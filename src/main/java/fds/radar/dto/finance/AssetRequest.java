package fds.radar.dto.finance;

import lombok.Getter;

@Getter
public class AssetRequest {
    private String assetType;
    private Long amount;
    private Long financialInstitutionId;
}
