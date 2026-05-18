package backend.capstone.domain.mobility.statics.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record PlaceStatisticsSection(
    @Schema(example = "32", description = "장소 통계에 포함된 전체 방문 수")
    int totalVisitCount,

    @Schema(description = "가장 많이 방문한 장소 목록. 최대 5개를 반환합니다.")
    List<PlaceStatisticsItem> items
) {

    public record PlaceStatisticsItem(
        @Schema(example = "1", description = "순위")
        int rank,

        @Schema(example = "스타벅스 수유역점", description = "장소 이름")
        String placeName,

        @Schema(example = "서울특별시 성북구 정릉로 77", description = "도로명 주소")
        String roadAddress,

        @Schema(example = "8", description = "방문 횟수")
        int visitCount,

        @Schema(example = "8회", description = "프론트 표시용 방문 횟수 문자열")
        String displayVisitCount
    ) {
    }
}
