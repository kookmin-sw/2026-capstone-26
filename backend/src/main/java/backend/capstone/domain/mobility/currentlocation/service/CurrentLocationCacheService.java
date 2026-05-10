package backend.capstone.domain.mobility.currentlocation.service;

import backend.capstone.domain.mobility.currentlocation.dto.CurrentLocationCacheValue;
import backend.capstone.domain.mobility.currentlocation.exception.CurrentLocationCacheException;
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

        //최신 좌표 선택
        GpsPointRequest latestPoint = gpsPoints.stream()
            .max(Comparator.comparing(GpsPointRequest::recordedAt))
            .orElseThrow();

        CurrentLocationCacheValue cacheValue = new CurrentLocationCacheValue(
            latestPoint.latitude(),
            latestPoint.longitude(),
            latestPoint.recordedAt()
        );

        try {
            redisTemplate.opsForValue().set(redisKey(userId), serialize(cacheValue));
        } catch (JsonProcessingException e) {
            throw new CurrentLocationCacheException("최신 좌표 redis에 저장 실패.", e);
        }
    }

    private String redisKey(Long userId) {
        return "current:location:user:" + userId;
    }

    //gpsPoint 객체를 json 문자열로 직렬화
    private String serialize(CurrentLocationCacheValue cacheValue) throws JsonProcessingException {
        return objectMapper.writeValueAsString(cacheValue);
    }
}
