package backend.capstone.domain.mobility.lastlocation.service;

import backend.capstone.domain.mobility.dayroute.dto.GpsPointBatchUploadRequest.GpsPointRequest;
import backend.capstone.domain.mobility.lastlocation.entity.LatestLocation;
import backend.capstone.domain.mobility.lastlocation.repository.LatestLocationRepository;
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
public class LatestLocationService {

    private final LatestLocationRepository latestLocationRepository;
    private final UserService userService;

    @Transactional
    public void upsertLatestLocation(Long userId, List<GpsPointRequest> gpsPoints) {
        if (gpsPoints == null || gpsPoints.isEmpty()) {
            return;
        }

        GpsPointRequest latestPoint = gpsPoints.stream()
            .max(Comparator.comparing(GpsPointRequest::recordedAt))
            .orElseThrow();

        latestLocationRepository.findById(userId)
            .ifPresentOrElse(
                latestLocation -> updateIfNewer(latestLocation, latestPoint),
                () -> latestLocationRepository.save(createLatestLocation(userId, latestPoint)));
    }

    private void updateIfNewer(LatestLocation latestLocation, GpsPointRequest latestPoint) {
        Instant recordedAt = latestLocation.getRecordedAt();
        if (recordedAt != null && !latestPoint.recordedAt().isAfter(recordedAt)) {
            return;
        }

        latestLocation.update(latestPoint.latitude(), latestPoint.longitude(),
            latestPoint.recordedAt());
    }

    private LatestLocation createLatestLocation(Long userId, GpsPointRequest latestPoint) {
        User user = userService.findById(userId);
        return LatestLocation.of(user, latestPoint.latitude(), latestPoint.longitude(),
            latestPoint.recordedAt());
    }
}
