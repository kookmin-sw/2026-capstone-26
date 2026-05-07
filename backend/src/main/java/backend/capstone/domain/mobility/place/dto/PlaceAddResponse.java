package backend.capstone.domain.mobility.place.dto;

import backend.capstone.domain.bookmarkplace.entity.BookmarkPlaceType;
import backend.capstone.domain.mobility.place.entity.PlaceSource;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import lombok.Builder;

@Builder
public record PlaceAddResponse(
    Long placeId,
    String placeName,
    PlaceSource source,
    BookmarkPlaceType type,
    String roadAddress,
    double latitude,
    double longitude,
    int orderIndex,
    @Schema(example = "2026-04-29T13:32:43.059+09:00")
    OffsetDateTime startTime,
    @Schema(example = "2026-04-29T13:32:43.059+09:00")
    OffsetDateTime endTime
) {

}
