package backend.capstone.domain.mobility.currentlocation.service;

import backend.capstone.domain.mobility.currentlocation.dto.CurrentLocationCacheValue;
import backend.capstone.domain.mobility.dayroute.dto.GpsPointBatchUploadRequest.GpsPointRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentLocationCacheService {

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

        redisTemplate.opsForValue().set(redisKey(userId), serialize(cacheValue));
    }

    private String redisKey(Long userId) {
        return "current:location:user:" + userId;
    }

    private String serialize(CurrentLocationCacheValue cacheValue) {
        try {
            return objectMapper.writeValueAsString(cacheValue);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize current location cache value.", e);
        }
    }
}
