package backend.capstone.integration.kakao.local.mapper;

import backend.capstone.integration.kakao.local.dto.KakaoSearchByKeywordResult;
import backend.capstone.integration.kakao.local.dto.PlaceSearchResponse;
import backend.capstone.integration.kakao.local.dto.PlaceSearchResponse.PlaceSearchItem;
import java.util.List;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class KakaoPlaceMapper {

    public static PlaceSearchResponse toPlaceSearchResponse(
        int page, KakaoSearchByKeywordResult result
    ) {
        List<PlaceSearchItem> items = result.documents() == null
            ? List.of()
            : result.documents().stream()
                .map(document -> new PlaceSearchItem(
                    document.place_name(),
                    document.category_name(),
                    document.road_address_name(),
                    parseDouble(document.x()),
                    parseDouble(document.y())
                ))
                .toList();

        return new PlaceSearchResponse(page, items.size(), result.meta().is_end(), result.meta()
            .pageable_count(), items);
    }

    private static Double parseDouble(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            double parsed = Double.parseDouble(value);
            return Double.isFinite(parsed) ? parsed : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
