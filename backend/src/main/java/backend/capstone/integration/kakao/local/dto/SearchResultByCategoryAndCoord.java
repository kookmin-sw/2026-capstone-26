package backend.capstone.integration.kakao.local.dto;

import lombok.Builder;

@Builder
public record SearchResultByCategoryAndCoord(
    String name,
    String roadAddress,
    String jibunAddress,
    double latitude,
    double longitude
) {

}
