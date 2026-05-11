package backend.capstone.domain.mobility.latestgpspoint.service;

import backend.capstone.domain.mobility.dayroute.dto.GpsPointBatchUploadRequest.GpsPointRequest;
import backend.capstone.domain.mobility.latestgpspoint.entity.LatestGpsPoint;
import backend.capstone.domain.mobility.latestgpspoint.repository.LatestGpsPointRepository;
import backend.capstone.domain.user.entity.User;
import backend.capstone.domain.user.service.UserService;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LatestGpsPointService {

    private final LatestGpsPointRepository latestGpsPointRepository;
    private final UserService userService;

    @Transactional
    public LatestGpsPoint upsertLatestLocation(Long userId, List<GpsPointRequest> gpsPoints) {
        if (gpsPoints == null || gpsPoints.isEmpty()) {
            return null;
        }

        GpsPointRequest latestPoint = gpsPoints.stream()
            .max(Comparator.comparing(GpsPointRequest::recordedAt))
            .orElseThrow();

        return latestGpsPointRepository.findById(userId)
            .map(latestGpsPoint -> updateIfNewer(latestGpsPoint, latestPoint))
            .orElseGet(() -> latestGpsPointRepository.save(createLatestLocation(userId, latestPoint)));
    }

    private LatestGpsPoint updateIfNewer(LatestGpsPoint latestGpsPoint, GpsPointRequest latestPoint) {
        Instant recordedAt = latestGpsPoint.getRecordedAt();
        if (recordedAt != null && !latestPoint.recordedAt().isAfter(recordedAt)) {
            return latestGpsPoint;
        }

        latestGpsPoint.update(latestPoint.latitude(), latestPoint.longitude(),
            latestPoint.recordedAt());
        return latestGpsPoint;
    }

    private LatestGpsPoint createLatestLocation(Long userId, GpsPointRequest latestPoint) {
        User user = userService.findById(userId);
        return LatestGpsPoint.of(user, latestPoint.latitude(), latestPoint.longitude(),
            latestPoint.recordedAt());
    }
}
