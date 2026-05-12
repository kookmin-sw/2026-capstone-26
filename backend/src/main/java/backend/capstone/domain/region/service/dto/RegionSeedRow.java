package backend.capstone.domain.region.service.dto;

public record RegionSeedRow(
    String legalDongCode,
    String sidoName,
    String sigunguName,
    String eupMyeonDongName,
    String dongRiName,
    String createdDate, //생성일자
    String deletedDate //말소일자
) {

}
