package backend.capstone.domain.mobility.statics.service;

import backend.capstone.domain.mobility.statics.type.StatisticPeriod;
import java.time.LocalDate;
import java.time.YearMonth;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class StatisticDateRangeCalculator {

    static StatisticDateRange calculate(StatisticPeriod period, LocalDate today) {
        return switch (period) {
            case WEEK -> new StatisticDateRange(today.minusDays(6), today);
            case MONTH -> new StatisticDateRange(today.minusDays(29), today);
            case SIX_MONTHS -> new StatisticDateRange(
                YearMonth.from(today).minusMonths(5).atDay(1), today);
            case YEAR -> new StatisticDateRange(
                YearMonth.from(today).minusMonths(11).atDay(1), today);
        };
    }
}
