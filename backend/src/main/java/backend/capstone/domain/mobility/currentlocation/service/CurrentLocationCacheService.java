package backend.capstone.domain.mobility.currentlocation.service;

import backend.capstone.domain.mobility.currentlocation.dto.CurrentLocationCacheValue;
import backend.capstone.domain.mobility.currentlocation.exception.CurrentLocationCacheException;
import backend.capstone.domain.mobility.dayroute.dto.GpsPointBatchUploadRequest.GpsPointRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.StringRedisTemplate;

@Service
@RequiredArgsConstructor
public class CurrentLocationCacheService {

    private static final long CURRENT_LOCATION_TTL_DAYS = 30L;

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public void saveLatestLocation(Long userId, List<GpsPointRequest> gpsPoints) {
        if (gpsPoints == null || gpsPoints.isEmpty()) {
            return;
        }

        GpsPointRequest latestPoint = gpsPoints.stream()
            .max(Comparator.comparing(GpsPointRequest::recordedAt))
            .orElseThrow();

        CurrentLocationCacheValue cacheValue = new CurrentLocationCacheValue(
            latestPoint.latitude(),
            latestPoint.longitude(),
            latestPoint.recordedAt()
        );

        String serializedValue = serialize(cacheValue);

        try {
            redisTemplate.opsForValue().set(
                redisKey(userId),
                serializedValue,
                CURRENT_LOCATION_TTL_DAYS,
                TimeUnit.DAYS
            );
        } catch (RuntimeException e) {
            throw new CurrentLocationCacheException("Failed to save current location cache.", e);
        }
    }

    private String redisKey(Long userId) {
        return "current:location:user:" + userId;
    }

    private String serialize(CurrentLocationCacheValue cacheValue) {
        try {
            return objectMapper.writeValueAsString(cacheValue);
        } catch (JsonProcessingException e) {
            throw new CurrentLocationCacheException(
                "Failed to serialize current location cache value.",
                e
            );
        }
    }
}
