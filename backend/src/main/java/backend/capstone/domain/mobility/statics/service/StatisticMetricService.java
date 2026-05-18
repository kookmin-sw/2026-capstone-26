package backend.capstone.domain.mobility.statics.service;

import backend.capstone.domain.mobility.dayroute.entity.DayRoute;
import backend.capstone.domain.mobility.dayroute.repository.DayRouteRepository;
import backend.capstone.domain.mobility.statics.dto.StatisticMetricResponse;
import backend.capstone.domain.mobility.statics.type.StatisticPeriod;
import backend.capstone.global.util.TimeFormatUtils;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StatisticMetricService {

    private static final String OUTING_TIME_METRIC_TYPE = "OUTING_TIME";
    private static final String ENTER_HOME_TIME_METRIC_TYPE = "ENTER_HOME_TIME";
    private static final String[] KOREAN_DAY_OF_WEEK_LABELS = {"월", "화", "수", "목", "금", "토", "일"};

    private final DayRouteRepository dayRouteRepository;

    @Transactional(readOnly = true)
    public StatisticMetricResponse getOutingTimeMetric(Long userId, StatisticPeriod period,
        LocalDate today) {
        return getTimeMetric(userId, period, today, new TimeMetricConfig(
            OUTING_TIME_METRIC_TYPE,
            "외출",
            "외출 시간",
            DayRoute::getOutingTime
        ));
    }

    @Transactional(readOnly = true)
    public StatisticMetricResponse getEnterHomeTimeMetric(Long userId, StatisticPeriod period,
        LocalDate today) {
        return getTimeMetric(userId, period, today, new TimeMetricConfig(
            ENTER_HOME_TIME_METRIC_TYPE,
            "귀가",
            "귀가 시각",
            DayRoute::getEnterHomeTime
        ));
    }

    private StatisticMetricResponse getTimeMetric(Long userId, StatisticPeriod period,
        LocalDate today, TimeMetricConfig config) {
        List<StatisticBucket> buckets = createBuckets(period, today);
        LocalDate startDate = buckets.getFirst().startDate();
        LocalDate endDate = buckets.getLast().endDate();
        List<DayRoute> dayRoutes = dayRouteRepository.findByUserIdAndDateBetweenOrderByDate(
            userId, startDate, endDate);
        StatisticMetricResponse.StatisticMetricAverage average = createAverage(dayRoutes,
            config.timeExtractor());

        return new StatisticMetricResponse(
            config.metricType(),
            period,
            startDate,
            endDate,
            average,
            createBars(buckets, groupByDate(dayRoutes), config.timeExtractor()),
            createHighlight(userId, period, startDate, average, config)
        );
    }

    private List<StatisticBucket> createBuckets(StatisticPeriod period, LocalDate today) {
        return switch (period) {
            case WEEK -> createDailyBuckets(today.minusDays(6), today, true);
            case MONTH -> createDailyBuckets(today.minusDays(29), today, false);
            case SIX_MONTHS -> createMonthlyBuckets(today, 6);
            case YEAR -> createMonthlyBuckets(today, 12);
        };
    }

    private List<StatisticBucket> createDailyBuckets(LocalDate startDate, LocalDate endDate,
        boolean useDayOfWeekLabel) {
        List<StatisticBucket> buckets = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            String label = useDayOfWeekLabel ? toKoreanDayOfWeekLabel(date) : String.valueOf(
                date.getDayOfMonth());
            buckets.add(new StatisticBucket(label, date, date));
        }
        return buckets;
    }

    private List<StatisticBucket> createMonthlyBuckets(LocalDate today, int monthCount) {
        List<StatisticBucket> buckets = new ArrayList<>();
        YearMonth startMonth = YearMonth.from(today).minusMonths(monthCount - 1L);
        for (int index = 0; index < monthCount; index++) {
            YearMonth yearMonth = startMonth.plusMonths(index);
            LocalDate startDate = yearMonth.atDay(1);
            LocalDate endDate = yearMonth.equals(YearMonth.from(today))
                ? today
                : yearMonth.atEndOfMonth();
            buckets.add(new StatisticBucket(yearMonth.getMonthValue() + "월", startDate, endDate));
        }
        return buckets;
    }

    private Map<LocalDate, DayRoute> groupByDate(List<DayRoute> dayRoutes) {
        Map<LocalDate, DayRoute> dayRouteMap = new LinkedHashMap<>();
        for (DayRoute dayRoute : dayRoutes) {
            dayRouteMap.put(dayRoute.getDate(), dayRoute);
        }
        return dayRouteMap;
    }

    private StatisticMetricResponse.StatisticMetricAverage createAverage(List<DayRoute> dayRoutes,
        Function<DayRoute, Instant> timeExtractor) {
        AverageResult averageResult = calculateAverageMinutes(dayRoutes, timeExtractor);
        return new StatisticMetricResponse.StatisticMetricAverage(
            averageResult.value(),
            averageResult.value() == null ? null : TimeFormatUtils.formatHourMinute(
                averageResult.value()),
            averageResult.sampleSize()
        );
    }

    private StatisticMetricResponse.StatisticMetricHighlight createHighlight(Long userId,
        StatisticPeriod period, LocalDate startDate,
        StatisticMetricResponse.StatisticMetricAverage currentAverage, TimeMetricConfig config) {
        HighlightPeriodInfo highlightPeriodInfo = createHighlightPeriodInfo(period, startDate,
            config.titleMetricName());
        List<DayRoute> previousDayRoutes = dayRouteRepository.findByUserIdAndDateBetweenOrderByDate(
            userId, highlightPeriodInfo.previousStartDate(), highlightPeriodInfo.previousEndDate());
        StatisticMetricResponse.StatisticMetricAverage previousAverage = createAverage(
            previousDayRoutes, config.timeExtractor());

        return new StatisticMetricResponse.StatisticMetricHighlight(
            highlightPeriodInfo.title(),
            createHighlightMessage(highlightPeriodInfo, currentAverage, previousAverage,
                config.messageMetricName()),
            toHighlightMetricValue(highlightPeriodInfo.currentLabel(), currentAverage),
            toHighlightMetricValue(highlightPeriodInfo.previousLabel(), previousAverage)
        );
    }

    private HighlightPeriodInfo createHighlightPeriodInfo(StatisticPeriod period,
        LocalDate startDate, String titleMetricName) {
        return switch (period) {
            case WEEK -> new HighlightPeriodInfo(
                "이번 주 " + titleMetricName, "이번 주", "지난주", "지난주와", "지난주보다",
                startDate.minusDays(7), startDate.minusDays(1));
            case MONTH -> new HighlightPeriodInfo(
                "이번 달 " + titleMetricName, "이번 달", "지난달", "지난달과", "지난달보다",
                startDate.minusDays(30), startDate.minusDays(1));
            case SIX_MONTHS -> new HighlightPeriodInfo(
                "최근 6개월 " + titleMetricName, "최근 6개월", "이전 6개월", "이전 6개월과",
                "이전 6개월보다",
                startDate.minusMonths(6), startDate.minusDays(1));
            case YEAR -> new HighlightPeriodInfo(
                "최근 1년 " + titleMetricName, "최근 1년", "이전 1년", "이전 1년과", "이전 1년보다",
                startDate.minusYears(1), startDate.minusDays(1));
        };
    }

    private String createHighlightMessage(
        HighlightPeriodInfo highlightPeriodInfo,
        StatisticMetricResponse.StatisticMetricAverage currentAverage,
        StatisticMetricResponse.StatisticMetricAverage previousAverage,
        String messageMetricName) {
        if (currentAverage.value() == null || previousAverage.value() == null) {
            return highlightPeriodInfo.currentLabel() + " 평균 " + messageMetricName + "을 "
                + highlightPeriodInfo.previousCompareWithLabel() + " 비교할 기록이 부족해요.";
        }

        int difference = currentAverage.value() - previousAverage.value();
        if (difference < 0) {
            return highlightPeriodInfo.currentLabel() + " 평균 " + messageMetricName + "이 "
                + highlightPeriodInfo.previousCompareThanLabel() + " 빨라졌어요.";
        }
        if (difference > 0) {
            return highlightPeriodInfo.currentLabel() + " 평균 " + messageMetricName + "이 "
                + highlightPeriodInfo.previousCompareThanLabel() + " 늦어졌어요.";
        }
        return highlightPeriodInfo.currentLabel() + " 평균 " + messageMetricName + "이 "
            + highlightPeriodInfo.previousCompareWithLabel() + " 같아요.";
    }

    private StatisticMetricResponse.HighlightMetricValue toHighlightMetricValue(String label,
        StatisticMetricResponse.StatisticMetricAverage average) {
        return new StatisticMetricResponse.HighlightMetricValue(
            label,
            average.value(),
            average.displayText(),
            average.sampleSize()
        );
    }

    private List<StatisticMetricResponse.StatisticMetricBarItem> createBars(
        List<StatisticBucket> buckets, Map<LocalDate, DayRoute> dayRouteMap,
        Function<DayRoute, Instant> timeExtractor
    ) {
        List<StatisticMetricResponse.StatisticMetricBarItem> bars = new ArrayList<>();
        for (StatisticBucket bucket : buckets) {
            List<DayRoute> bucketDayRoutes = findDayRoutesInBucket(bucket, dayRouteMap);
            AverageResult averageResult = calculateAverageMinutes(bucketDayRoutes, timeExtractor);
            Integer value = averageResult.value();
            bars.add(new StatisticMetricResponse.StatisticMetricBarItem(
                bucket.label(),
                bucket.startDate(),
                bucket.endDate(),
                value,
                value == null ? null : TimeFormatUtils.formatHourMinute(value),
                value != null,
                averageResult.sampleSize()
            ));
        }
        return bars;
    }

    private List<DayRoute> findDayRoutesInBucket(StatisticBucket bucket,
        Map<LocalDate, DayRoute> dayRouteMap) {
        List<DayRoute> dayRoutes = new ArrayList<>();
        for (LocalDate date = bucket.startDate(); !date.isAfter(bucket.endDate());
            date = date.plusDays(1)) {
            DayRoute dayRoute = dayRouteMap.get(date);
            if (dayRoute != null) {
                dayRoutes.add(dayRoute);
            }
        }
        return dayRoutes;
    }

    private AverageResult calculateAverageMinutes(List<DayRoute> dayRoutes,
        Function<DayRoute, Instant> timeExtractor) {
        long totalMinutes = 0L;
        int sampleSize = 0;
        for (DayRoute dayRoute : dayRoutes) {
            Long minutesOfDay = TimeFormatUtils.toKstMinutesOfDay(timeExtractor.apply(dayRoute));
            if (minutesOfDay == null) {
                continue;
            }
            totalMinutes += minutesOfDay;
            sampleSize++;
        }

        Integer value = sampleSize == 0
            ? null
            : Math.toIntExact(Math.round((double) totalMinutes / sampleSize));
        return new AverageResult(value, sampleSize);
    }

    private String toKoreanDayOfWeekLabel(LocalDate date) {
        return KOREAN_DAY_OF_WEEK_LABELS[date.getDayOfWeek().getValue() - 1];
    }

    private record StatisticBucket(String label, LocalDate startDate, LocalDate endDate) {
    }

    private record TimeMetricConfig(
        String metricType,
        String titleMetricName,
        String messageMetricName,
        Function<DayRoute, Instant> timeExtractor
    ) {
    }

    private record HighlightPeriodInfo(
        String title,
        String currentLabel,
        String previousLabel,
        String previousCompareWithLabel,
        String previousCompareThanLabel,
        LocalDate previousStartDate,
        LocalDate previousEndDate
    ) {
    }

    private record AverageResult(Integer value, int sampleSize) {
    }
}
