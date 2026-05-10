package backend.capstone.domain.mobility.lastlocation.service;

import backend.capstone.domain.mobility.dayroute.dto.GpsPointBatchUploadRequest.GpsPointRequest;
import backend.capstone.domain.mobility.lastlocation.dto.CurrentLocationCacheValue;
import backend.capstone.domain.mobility.lastlocation.exception.CurrentLocationCacheException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        CurrentLocationCacheValue cacheValue = new CurrentLocationCacheValue(latestPoint.latitude(),
            latestPoint.longitude(), latestPoint.recordedAt());

        saveLatestLocation(userId, cacheValue);
    }

    public void saveLatestLocation(Long userId, CurrentLocationCacheValue cacheValue) {
        try {
            redisTemplate.opsForValue().set(redisKey(userId), serialize(cacheValue));
        } catch (RuntimeException e) {
            throw new CurrentLocationCacheException("최신 위치를 Redis에 저장하지 못했습니다.", e);
        }
    }

    public Map<Long, CurrentLocationCacheValue> getLatestLocations(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Map.of();
        }

        List<String> keys = new ArrayList<>();
        for (Long userId : userIds) {
            keys.add(redisKey(userId));
        }

        List<String> cachedValues;
        try {
            cachedValues = redisTemplate.opsForValue().multiGet(keys);
        } catch (RuntimeException e) {
            throw new CurrentLocationCacheException("Redis에서 최신 위치를 조회하지 못했습니다.", e);
        }

        if (cachedValues == null || cachedValues.isEmpty()) {
            return Map.of();
        }

        Map<Long, CurrentLocationCacheValue> locationsByUserId = new HashMap<>();
        for (int i = 0; i < userIds.size(); i++) {
            String cachedValue = cachedValues.get(i);
            if (cachedValue == null) {
                continue;
            }

            locationsByUserId.put(userIds.get(i), deserialize(cachedValue));
        }

        return locationsByUserId;
    }

    private String redisKey(Long userId) {
        return "current:location:user:" + userId;
    }

    private String serialize(CurrentLocationCacheValue cacheValue) {
        try {
            return objectMapper.writeValueAsString(cacheValue);
        } catch (JsonProcessingException e) {
            throw new CurrentLocationCacheException("최신 위치를 JSON으로 직렬화하지 못했습니다.", e);
        }
    }

    private CurrentLocationCacheValue deserialize(String cachedValue) {
        try {
            return objectMapper.readValue(cachedValue, CurrentLocationCacheValue.class);
        } catch (IOException e) {
            throw new CurrentLocationCacheException("최신 위치 캐시 값을 역직렬화하지 못했습니다.", e);
        }
    }
}
