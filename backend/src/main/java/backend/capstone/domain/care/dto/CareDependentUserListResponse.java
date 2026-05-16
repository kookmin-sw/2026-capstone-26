package backend.capstone.domain.care.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record CareDependentUserListResponse(
    @Schema(example = "2", description = "보호대상자 수")
    int dependentUserCount,

    @Schema(description = "보호대상자 목록")
    List<CareDependentUserItem> dependentUsers
) {

    @Builder
    public record CareDependentUserItem(
        @Schema(example = "10", description = "보호대상자 ID")
        Long dependentUserId,

        @Schema(example = "홍길동", description = "보호대상자 닉네임")
        String nickname,

        @Schema(example = "https://example.com/profile.png", description = "프로필 이미지 URL")
        String profileImageUrl,

        @Schema(description = "최신 위치 정보. 위치가 없으면 null")
        LatestGpsPointItem latestGpsPoint
    ) {

    }

    @Builder
    public record LatestGpsPointItem(
        @Schema(example = "37.5665", description = "위도")
        double latitude,

        @Schema(example = "126.9780", description = "경도")
        double longitude,

        @Schema(example = "2026-05-11T09:00:00+09:00", description = "KST 기준 기록 시각")
        OffsetDateTime recordedAt
    ) {

    }
}
