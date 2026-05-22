package backend.capstone.domain.mobility.statics.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record VisitedRegionsSection(
    @Schema(description = "최근 7일 누적 체류 시간 기준 상위 방문 지역 목록")
    List<VisitedRegionSummaryItem> topRegions
) {

    public record VisitedRegionSummaryItem(
        @Schema(example = "1", description = "순위")
        int rank,

        @Schema(example = "성북구", description = "방문 지역 이름")
        String regionName
    ) {

    }
}
