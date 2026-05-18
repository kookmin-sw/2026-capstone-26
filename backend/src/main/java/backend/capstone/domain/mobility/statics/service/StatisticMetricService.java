package backend.capstone.domain.mobility.statics.service;

import backend.capstone.domain.mobility.dayroute.entity.DayRoute;
import backend.capstone.domain.mobility.dayroute.repository.DayRouteRepository;
import backend.capstone.domain.mobility.statics.dto.StatisticMetricResponse;
import backend.capstone.domain.mobility.statics.type.StatisticPeriod;
import backend.capstone.global.util.DurationFormatUtils;
import backend.capstone.global.util.TimeFormatUtils;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
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
    private static final String TOTAL_OUTING_SECONDS_METRIC_TYPE = "TOTAL_OUTING_SECONDS";
    private static final String TOTAL_OUTING_COUNT_METRIC_TYPE = "TOTAL_OUTING_COUNT";
    private static final String[] KOREAN_DAY_OF_WEEK_LABELS = {"월", "화", "수", "목", "금", "토", "일"};

    private final DayRouteRepository dayRouteRepository;

    @Transactional(readOnly = true)
    public StatisticMetricResponse getOutingTimeMetric(Long userId, StatisticPeriod period,
        LocalDate today) {
        return getMetric(userId, period, today, new MetricConfig(
            OUTING_TIME_METRIC_TYPE,
            "외출",
            "외출 시간을",
            "외출 시간이",
            "빨라졌어요.",
            "늦어졌어요.",
            dayRoute -> TimeFormatUtils.toKstMinutesOfDay(dayRoute.getOutingTime()),
            average -> Math.toIntExact(Math.round(average)),
            value -> TimeFormatUtils.formatHourMinute(value.intValue())
        ));
    }

    @Transactional(readOnly = true)
    public StatisticMetricResponse getEnterHomeTimeMetric(Long userId, StatisticPeriod period,
        LocalDate today) {
        return getMetric(userId, period, today, new MetricConfig(
            ENTER_HOME_TIME_METRIC_TYPE,
            "귀가",
            "귀가 시각을",
            "귀가 시각이",
            "빨라졌어요.",
            "늦어졌어요.",
            dayRoute -> TimeFormatUtils.toKstMinutesOfDay(dayRoute.getEnterHomeTime()),
            average -> Math.toIntExact(Math.round(average)),
            value -> TimeFormatUtils.formatHourMinute(value.intValue())
        ));
    }

    @Transactional(readOnly = true)
    public StatisticMetricResponse getTotalOutingSecondsMetric(Long userId, StatisticPeriod period,
        LocalDate today) {
        return getMetric(userId, period, today, new MetricConfig(
            TOTAL_OUTING_SECONDS_METRIC_TYPE,
            "외출시간",
            "외출시간을",
            "외출시간이",
            "줄었어요.",
            "늘었어요.",
            dayRoute -> dayRoute.getTotalOutingSeconds(),
            average -> Math.toIntExact(Math.round(average)),
            value -> DurationFormatUtils.formatOutingDurationText(value.longValue())
        ));
    }

    @Transactional(readOnly = true)
    public StatisticMetricResponse getTotalOutingCountMetric(Long userId, StatisticPeriod period,
        LocalDate today) {
        return getMetric(userId, period, today, new MetricConfig(
            TOTAL_OUTING_COUNT_METRIC_TYPE,
            "외출횟수",
            "외출횟수를",
            "외출횟수가",
            "줄었어요.",
            "늘었어요.",
            dayRoute -> (long) dayRoute.getTotalOutingCount(),
            average -> roundToOneDecimal(average),
            value -> String.format(Locale.US, "%.1f회", value.doubleValue())
        ));
    }

    private StatisticMetricResponse getMetric(Long userId, StatisticPeriod period, LocalDate today,
        MetricConfig config) {
        List<StatisticBucket> buckets = createBuckets(period, today);
        LocalDate startDate = buckets.getFirst().startDate();
        LocalDate endDate = buckets.getLast().endDate();
        List<DayRoute> dayRoutes = dayRouteRepository.findByUserIdAndDateBetweenOrderByDate(
            userId, startDate, endDate);
        StatisticMetricResponse.StatisticMetricAverage average = createAverage(dayRoutes,
            config);

        return new StatisticMetricResponse(
            config.metricType(),
            period,
            startDate,
            endDate,
            average,
            createBars(buckets, groupByDate(dayRoutes), config),
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
        MetricConfig config) {
        AverageResult averageResult = calculateAverage(dayRoutes, config);
        return new StatisticMetricResponse.StatisticMetricAverage(
            averageResult.value(),
            averageResult.value() == null ? null : config.displayFormatter().apply(
                averageResult.value()),
            averageResult.sampleSize()
        );
    }

    private StatisticMetricResponse.StatisticMetricHighlight createHighlight(Long userId,
        StatisticPeriod period, LocalDate startDate,
        StatisticMetricResponse.StatisticMetricAverage currentAverage, MetricConfig config) {
        HighlightPeriodInfo highlightPeriodInfo = createHighlightPeriodInfo(period, startDate,
            config.titleMetricName());
        List<DayRoute> previousDayRoutes = dayRouteRepository.findByUserIdAndDateBetweenOrderByDate(
            userId, highlightPeriodInfo.previousStartDate(), highlightPeriodInfo.previousEndDate());
        StatisticMetricResponse.StatisticMetricAverage previousAverage = createAverage(
            previousDayRoutes, config);

        return new StatisticMetricResponse.StatisticMetricHighlight(
            highlightPeriodInfo.title(),
            createHighlightMessage(highlightPeriodInfo, currentAverage, previousAverage,
                config),
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
        MetricConfig config) {
        if (currentAverage.value() == null || previousAverage.value() == null) {
            return highlightPeriodInfo.currentLabel() + " 평균 " + config.objectMetricName() + " "
                + highlightPeriodInfo.previousCompareWithLabel() + " 비교할 기록이 부족해요.";
        }

        double difference = currentAverage.value().doubleValue()
            - previousAverage.value().doubleValue();
        if (difference < 0) {
            return highlightPeriodInfo.currentLabel() + " 평균 " + config.subjectMetricName() + " "
                + highlightPeriodInfo.previousCompareThanLabel() + " " + config.decreaseMessage();
        }
        if (difference > 0) {
            return highlightPeriodInfo.currentLabel() + " 평균 " + config.subjectMetricName() + " "
                + highlightPeriodInfo.previousCompareThanLabel() + " " + config.increaseMessage();
        }
        return highlightPeriodInfo.currentLabel() + " 평균 " + config.subjectMetricName() + " "
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
        List<StatisticBucket> buckets, Map<LocalDate, DayRoute> dayRouteMap, MetricConfig config
    ) {
        List<StatisticMetricResponse.StatisticMetricBarItem> bars = new ArrayList<>();
        for (StatisticBucket bucket : buckets) {
            List<DayRoute> bucketDayRoutes = findDayRoutesInBucket(bucket, dayRouteMap);
            AverageResult averageResult = calculateAverage(bucketDayRoutes, config);
            Number value = averageResult.value();
            bars.add(new StatisticMetricResponse.StatisticMetricBarItem(
                bucket.label(),
                bucket.startDate(),
                bucket.endDate(),
                value,
                value == null ? null : config.displayFormatter().apply(value),
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

    private AverageResult calculateAverage(List<DayRoute> dayRoutes, MetricConfig config) {
        long totalValue = 0L;
        int sampleSize = 0;
        for (DayRoute dayRoute : dayRoutes) {
            Long value = config.valueExtractor().apply(dayRoute);
            if (value == null) {
                continue;
            }
            totalValue += value;
            sampleSize++;
        }

        Number value = sampleSize == 0
            ? null
            : config.averageFormatter().apply((double) totalValue / sampleSize);
        return new AverageResult(value, sampleSize);
    }

    private static double roundToOneDecimal(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    private String toKoreanDayOfWeekLabel(LocalDate date) {
        return KOREAN_DAY_OF_WEEK_LABELS[date.getDayOfWeek().getValue() - 1];
    }

    private record StatisticBucket(String label, LocalDate startDate, LocalDate endDate) {
    }

    private record MetricConfig(
        String metricType,
        String titleMetricName,
        String objectMetricName,
        String subjectMetricName,
        String decreaseMessage,
        String increaseMessage,
        Function<DayRoute, Long> valueExtractor,
        Function<Double, Number> averageFormatter,
        Function<Number, String> displayFormatter
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

    private record AverageResult(Number value, int sampleSize) {
    }
}
