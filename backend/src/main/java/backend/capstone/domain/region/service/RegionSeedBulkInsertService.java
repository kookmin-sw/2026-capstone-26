package backend.capstone.domain.region.service;

import backend.capstone.domain.region.entity.Region;
import backend.capstone.domain.region.repository.RegionRepository;
import backend.capstone.domain.region.service.dto.RegionSeedRow;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegionSeedBulkInsertService {

    private static final int BATCH_SIZE = 1000;

    private final RegionSeedExcelReader regionSeedExcelReader;
    private final RegionRepository regionRepository;
    private final JdbcTemplate jdbcTemplate;

    //todo: 필터링한 row수랑 db에 저장된 region 수가 같으면 바로 리턴하기
    @Transactional
    public int importSeed() {
        List<RegionSeedRow> seedRows = regionSeedExcelReader.readAll();
        List<Region> regions = seedRows.stream()
            .filter(this::isInsertTarget)
            .map(this::toRegion)
            .toList();

        if (regions.isEmpty()) {
            log.error("적재할 region seed 데이터가 없습니다.");
            return 0;
        }

        if (regionRepository.count() > 0) {
            log.info("기존 region 데이터가 존재합니다. legal_dong_code 기준 upsert로 반영합니다.");
        }

        List<Region> deduplicatedRegions = deduplicate(regions);
        batchInsert(deduplicatedRegions);
        log.info("region seed 적재 완료: {}건", deduplicatedRegions.size());
        return deduplicatedRegions.size();
    }

    private boolean isInsertTarget(RegionSeedRow row) {
        return hasText(row.legalDongCode())
            && row.legalDongCode().length() == 10 //법정동 코드 길이는 10여야함
            && hasText(row.sidoName())
            && hasText(row.sigunguName())
            && hasText(row.eupMyeonDongName())
            && !hasText(row.dongRiName()) //동리명은 없어야함
            && !hasText(row.deletedDate()); //말소일자 없어야함
    }

    private Region toRegion(RegionSeedRow row) {
        return Region.builder()
            .legalDongCode(row.legalDongCode())
            .sidoName(row.sidoName())
            .sigunguName(row.sigunguName())
            .dongName(row.eupMyeonDongName())
            .build();
    }

    //법정동 코드가 엑셀 파일에 여러 번 나오면 첫 번째 것만 남김
    private List<Region> deduplicate(List<Region> regions) {
        List<Region> deduplicated = new ArrayList<>();
        Set<String> seenCodes = new HashSet<>();

        for (Region region : regions) {
            if (!seenCodes.add(region.getLegalDongCode())) {
                continue;
            }
            deduplicated.add(region);
        }

        return deduplicated;
    }

    private void batchInsert(List<Region> regions) {
        //새 법정동 코드이면 insert
        //법정동 코드 유니크 충돌이면 update
        String sql = """
            insert into region (legal_dong_code, sido_name, sigungu_name, legal_dong_name, created_at, updated_at)
            values (?, ?, ?, ?, now(), now())
            on duplicate key update
                sido_name = values(sido_name),
                sigungu_name = values(sigungu_name),
                legal_dong_name = values(legal_dong_name),
                updated_at = now()
            """;

        for (int start = 0; start < regions.size(); start += BATCH_SIZE) {
            int end = Math.min(start + BATCH_SIZE, regions.size());
            List<Region> batch = regions.subList(start, end);

            jdbcTemplate.batchUpdate(sql, batch, batch.size(), (ps, region) -> {
                ps.setString(1, region.getLegalDongCode());
                ps.setString(2, region.getSidoName());
                ps.setString(3, region.getSigunguName());
                ps.setString(4, region.getDongName());
            });
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
