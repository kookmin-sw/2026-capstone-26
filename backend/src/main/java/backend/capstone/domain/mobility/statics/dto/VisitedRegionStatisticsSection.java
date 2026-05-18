package backend.capstone.domain.mobility.statics.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record VisitedRegionStatisticsSection(
    @Schema(example = "21", description = "방문 동네 통계에 포함된 전체 방문 수")
    int totalVisitCount,

    @Schema(description = "방문 동네 비율 목록. 상위 3개 동네와 그 외를 반환합니다.")
    List<VisitedRegionStatisticsItem> items
) {

    public record VisitedRegionStatisticsItem(
        @Schema(example = "1", description = "순위")
        int rank,

        @Schema(example = "수유동", description = "동네 이름. 그 외 항목은 '그 외'로 반환합니다.")
        String regionName,

        @Schema(example = "9", description = "해당 동네 방문 수")
        int visitCount,

        @Schema(example = "42.9", description = "전체 방문 수 대비 비율. 소수점 한 자리까지 반환합니다.")
        double ratio,

        @Schema(example = "43%", description = "프론트 표시용 정수 퍼센트 문자열")
        String displayRatio
    ) {
    }
}
