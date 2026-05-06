package backend.capstone.domain.visitedregion.entity;

import backend.capstone.domain.dayroute.entity.DayRoute;
import backend.capstone.domain.region.entity.Region;
import backend.capstone.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    name = "visited_region",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_visited_region_day_route_region",
            columnNames = {"day_route_id", "region_id"})
    }
)
public class VisitedRegion extends BaseTimeEntity {

    @Id
    @Column(name = "visited_region_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "day_route_id", nullable = false)
    private DayRoute dayRoute;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

    @Column(name = "first_visited_at", nullable = false)
    private Instant firstVisitedAt;

    @Column(name = "total_stay_seconds", nullable = false)
    private long totalStaySeconds;

    @Builder
    public VisitedRegion(DayRoute dayRoute, Region region, Instant firstVisitedAt,
        long totalStaySeconds) {
        this.dayRoute = dayRoute;
        this.region = region;
        this.firstVisitedAt = firstVisitedAt;
        this.totalStaySeconds = totalStaySeconds;
    }

    public void updateFirstVisitedAt(Instant firstVisitedAt) {
        if (firstVisitedAt == null) {
            return;
        }

        if (this.firstVisitedAt == null || firstVisitedAt.isBefore(this.firstVisitedAt)) {
            this.firstVisitedAt = firstVisitedAt;
        }
    }

    public void addStaySeconds(long staySeconds) {
        if (staySeconds <= 0) {
            return;
        }

        this.totalStaySeconds += staySeconds;
    }
}
