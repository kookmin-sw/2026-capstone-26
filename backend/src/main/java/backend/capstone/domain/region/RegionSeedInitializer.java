package backend.capstone.domain.region;

import backend.capstone.domain.region.service.RegionSeedBulkInsertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "seed.region.enabled", havingValue = "true")
@Slf4j
public class RegionSeedInitializer implements ApplicationRunner {

    private final RegionSeedBulkInsertService regionSeedBulkInsertService;

    @Override
    public void run(ApplicationArguments args) {
        int insertedCount = regionSeedBulkInsertService.importSeed();
        log.info("region seed initializer finished. insertedCount={}", insertedCount);
    }
}
