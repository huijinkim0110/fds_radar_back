package fds.radar.dto.finance;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AssetResponse {
    private Long id;
    private String assetType;
    private Long amount;
    private String financialInstitutionName;
}
