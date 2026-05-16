package backend.capstone.domain.mobility.latestgpspoint.entity;

import backend.capstone.domain.user.entity.User;
import backend.capstone.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "latest_location")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LatestGpsPoint extends BaseTimeEntity {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @MapsId //이 연관관계의 fk 값을 pk 필드와 공유하겠다
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Column(name = "recorded_at", nullable = false)
    private Instant recordedAt;

    @Builder
    private LatestGpsPoint(User user, double latitude, double longitude, Instant recordedAt) {
        this.user = user;
        this.latitude = latitude;
        this.longitude = longitude;
        this.recordedAt = recordedAt;
    }

    public static LatestGpsPoint of(User user, double latitude, double longitude,
        Instant recordedAt) {
        return LatestGpsPoint.builder()
            .user(user)
            .latitude(latitude)
            .longitude(longitude)
            .recordedAt(recordedAt)
            .build();
    }

    public void update(double latitude, double longitude, Instant recordedAt) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.recordedAt = recordedAt;
    }
}
