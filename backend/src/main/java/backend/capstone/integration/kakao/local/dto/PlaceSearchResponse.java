package backend.capstone.integration.kakao.local.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record PlaceSearchResponse(
    int page,
    int size,
    boolean isEnd,
    int pageableCount,
    List<PlaceSearchItem> places
) {
    public record PlaceSearchItem(
        String placeName,
        String category,
        String roadAddress,
        Double longitude,
        Double latitude
    ) {
    }
}
