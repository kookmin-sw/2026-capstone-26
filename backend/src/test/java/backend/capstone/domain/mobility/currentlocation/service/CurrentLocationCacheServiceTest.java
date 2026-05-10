package backend.capstone.domain.mobility.currentlocation.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import backend.capstone.domain.mobility.currentlocation.dto.CurrentLocationCacheValue;
import backend.capstone.domain.mobility.currentlocation.exception.CurrentLocationCacheException;
import backend.capstone.domain.mobility.dayroute.dto.GpsPointBatchUploadRequest.GpsPointRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
class CurrentLocationCacheServiceTest {

    @InjectMocks
    private CurrentLocationCacheService currentLocationCacheService;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void 가장_최근_recordedAt_좌표를_30일_ttl로_redis에_저장한다() {
        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
        org.mockito.BDDMockito.given(redisTemplate.opsForValue()).willReturn(valueOperations);

        List<GpsPointRequest> gpsPoints = List.of(
            new GpsPointRequest(Instant.parse("2026-05-09T10:00:00Z"), 37.1, 127.1),
            new GpsPointRequest(Instant.parse("2026-05-09T10:05:00Z"), 37.2, 127.2),
            new GpsPointRequest(Instant.parse("2026-05-09T10:03:00Z"), 37.15, 127.15)
        );

        currentLocationCacheService.saveLatestLocation(1L, gpsPoints);

        ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);
        then(valueOperations).should().set(
            org.mockito.ArgumentMatchers.eq("current:location:user:1"),
            valueCaptor.capture(),
            org.mockito.ArgumentMatchers.eq(30L),
            org.mockito.ArgumentMatchers.eq(TimeUnit.DAYS)
        );

        CurrentLocationCacheValue cacheValue = readCacheValue(valueCaptor.getValue());
        org.assertj.core.api.Assertions.assertThat(cacheValue.latitude()).isEqualTo(37.2);
        org.assertj.core.api.Assertions.assertThat(cacheValue.longitude()).isEqualTo(127.2);
        org.assertj.core.api.Assertions.assertThat(cacheValue.recordedAt())
            .isEqualTo(Instant.parse("2026-05-09T10:05:00Z"));
    }

    @Test
    void 좌표가_없으면_redis에_저장하지_않는다() {
        currentLocationCacheService.saveLatestLocation(1L, List.of());

        then(redisTemplate).shouldHaveNoInteractions();
    }

    @Test
    void redis_저장에_실패하면_캐시_예외를_던진다() {
        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
        org.mockito.BDDMockito.given(redisTemplate.opsForValue()).willReturn(valueOperations);
        org.mockito.BDDMockito.willThrow(new RuntimeException("redis down"))
            .given(valueOperations)
            .set(
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyLong(),
                org.mockito.ArgumentMatchers.any()
            );

        List<GpsPointRequest> gpsPoints = List.of(
            new GpsPointRequest(Instant.parse("2026-05-09T10:05:00Z"), 37.2, 127.2)
        );

        assertThatThrownBy(() -> currentLocationCacheService.saveLatestLocation(1L, gpsPoints))
            .isInstanceOf(CurrentLocationCacheException.class)
            .hasMessage("Failed to save current location cache.");
    }

    private CurrentLocationCacheValue readCacheValue(String value) {
        try {
            return objectMapper.readValue(value, CurrentLocationCacheValue.class);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }
}
