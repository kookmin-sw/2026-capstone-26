package backend.capstone.domain.region.entity;

import backend.capstone.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    name = "region",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_region_legal_dong_code",
            columnNames = "legal_dong_code")
    }
)
public class Region extends BaseTimeEntity {

    @Id
    @Column(name = "region_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "legal_dong_code", nullable = false, length = 10)
    private String legalDongCode;

    @Column(name = "sido_name", nullable = false, length = 50)
    private String sidoName;

    @Column(name = "sigungu_name", length = 50)
    private String sigunguName;

    @Column(name = "legal_dong_name", nullable = false, length = 50)
    private String dongName;

    @Column(name = "full_name", nullable = false, length = 200)
    private String fullName;

    @Builder
    public Region(String legalDongCode, String sidoName, String sigunguName, String dongName,
        String fullName) {
        this.legalDongCode = legalDongCode;
        this.sidoName = sidoName;
        this.sigunguName = sigunguName;
        this.dongName = dongName;
        this.fullName = fullName;
    }
}
