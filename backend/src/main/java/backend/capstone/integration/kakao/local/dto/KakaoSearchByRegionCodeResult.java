package backend.capstone.integration.kakao.local.dto;

import java.util.List;

public record KakaoSearchByRegionCodeResult(
    Meta meta,
    List<Document> documents
) {

    public record Meta(
        Integer total_count
    ) {

    }

    public record Document(
        String region_type,
        String code,
        String address_name,
        String region_1depth_name,
        String region_2depth_name,
        String region_3depth_name,
        String region_4depth_name,
        String x,
        String y
    ) {

    }
}
