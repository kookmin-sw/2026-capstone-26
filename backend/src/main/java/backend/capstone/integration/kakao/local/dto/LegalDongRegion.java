package backend.capstone.integration.kakao.local.dto;

import lombok.Builder;

@Builder
public record LegalDongRegion(
    String legalDongCode,
    String sidoName,
    String sigunguName,
    String eupMyeonDongName,
    String dongRiName
) {

}
