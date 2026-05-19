package backend.capstone.domain.mobility.statics.service;

import backend.capstone.domain.mobility.analysis.visitedregion.entity.VisitedRegion;
import backend.capstone.domain.mobility.analysis.visitedregion.repository.VisitedRegionRepository;
import backend.capstone.domain.mobility.dayroute.entity.DayRoute;
import backend.capstone.domain.mobility.dayroute.repository.DayRouteRepository;
import backend.capstone.domain.mobility.statics.dto.CountMetricSection;
import backend.capstone.domain.mobility.statics.dto.DurationMetricSection;
import backend.capstone.domain.mobility.statics.dto.TimeMetricSection;
import backend.capstone.domain.mobility.statics.dto.VisitedRegionsSection;
import backend.capstone.domain.mobility.statics.dto.WeeklyStatisticsResponse;
import backend.capstone.global.util.DurationFormatUtils;
import backend.capstone.global.util.TimeFormatUtils;
import java.time.Instant;
import java.time.LocalDate;
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
public class WeeklyStatisticsService {

    private static final int RECENT_DAYS = 7;
    private static final int TOP_REGION_LIMIT = 2;

    private final DayRouteRepository dayRouteRepository;
    private final VisitedRegionRepository visitedRegionRepository;

    @Transactional(readOnly = true)
    public WeeklyStatisticsResponse getWeeklyStatistics(Long userId, LocalDate today) {
        LocalDate endDate = today;
        LocalDate startDate = endDate.minusDays(RECENT_DAYS - 1L);
        List<DayRoute> dayRoutes = dayRouteRepository.findByUserIdAndDateBetweenOrderByDate(
            userId, startDate, endDate);
        List<DailySlot> dailySlots = createDailySlots(startDate, endDate, dayRoutes);

        return new WeeklyStatisticsResponse(
            startDate,
            endDate,
            buildOutingTimeSection(dailySlots),
            buildEnterHomeTimeSection(dailySlots),
            buildTotalOutingCountSection(dailySlots),
            buildTotalOutingSecondsSection(dailySlots),
            buildVisitedRegionsSection(dayRoutes)
        );
    }

    private List<DailySlot> createDailySlots(LocalDate startDate, LocalDate endDate,
        List<DayRoute> dayRoutes) {
        Map<LocalDate, DayRoute> dayRouteMap = new LinkedHashMap<>();
        for (DayRoute dayRoute : dayRoutes) {
            dayRouteMap.put(dayRoute.getDate(), dayRoute);
        }

        List<DailySlot> dailySlots = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            dailySlots.add(new DailySlot(date, dayRouteMap.get(date)));
        }
        return dailySlots;
    }

    private TimeMetricSection buildOutingTimeSection(List<DailySlot> dailySlots) {
        return buildTimeMetricSection(dailySlots, DayRoute::getOutingTime);
    }

    private TimeMetricSection buildEnterHomeTimeSection(List<DailySlot> dailySlots) {
        return buildTimeMetricSection(dailySlots, DayRoute::getEnterHomeTime);
    }

    private TimeMetricSection buildTimeMetricSection(List<DailySlot> dailySlots,
        Function<DayRoute, Instant> extractor) {
        List<TimeMetricSection.TimeMetricDailyItem> dailyValues = new ArrayList<>();
        long totalMinutes = 0L;
        int sampleSize = 0;

        for (DailySlot dailySlot : dailySlots) {
            Integer minutesOfDay = getMinutesOfDay(dailySlot.dayRoute(), extractor);
            dailyValues.add(new TimeMetricSection.TimeMetricDailyItem(
                dailySlot.date(),
                dailySlot.dayRoute() != null,
                minutesOfDay,
                minutesOfDay == null ? null : TimeFormatUtils.formatHourMinute(minutesOfDay)
            ));

            if (minutesOfDay != null) {
                totalMinutes += minutesOfDay;
                sampleSize++;
            }
        }

        Integer averageValue = sampleSize == 0
            ? null
            : Math.toIntExact(Math.round((double) totalMinutes / sampleSize));

        return new TimeMetricSection(
            new TimeMetricSection.TimeMetricAverage(
                averageValue,
                averageValue == null ? null : TimeFormatUtils.formatHourMinute(averageValue),
                sampleSize
            ),
            dailyValues
        );
    }

    private CountMetricSection buildTotalOutingCountSection(List<DailySlot> dailySlots) {
        List<CountMetricSection.CountMetricDailyItem> dailyValues = new ArrayList<>();
        long totalCount = 0L;
        int sampleSize = 0;

        for (DailySlot dailySlot : dailySlots) {
            Integer count = hasOutingRecord(dailySlot.dayRoute())
                ? dailySlot.dayRoute().getTotalOutingCount()
                : null;
            dailyValues.add(new CountMetricSection.CountMetricDailyItem(
                dailySlot.date(),
                dailySlot.dayRoute() != null,
                count,
                count == null ? null : count + "회"
            ));

            if (count != null) {
                totalCount += count;
                sampleSize++;
            }
        }

        Double averageValue = sampleSize == 0 ? null
            : Math.round(((double) totalCount / sampleSize) * 10.0) / 10.0;

        return new CountMetricSection(
            new CountMetricSection.CountMetricAverage(
                averageValue,
                averageValue == null ? null : formatCountText(averageValue),
                sampleSize
            ),
            dailyValues
        );
    }

    private DurationMetricSection buildTotalOutingSecondsSection(List<DailySlot> dailySlots) {
        List<DurationMetricSection.DurationMetricDailyItem> dailyValues = new ArrayList<>();
        long totalSeconds = 0L;
        int sampleSize = 0;

        for (DailySlot dailySlot : dailySlots) {
            Long outingSeconds = hasOutingRecord(dailySlot.dayRoute())
                ? dailySlot.dayRoute().getTotalOutingSeconds()
                : null;
            dailyValues.add(new DurationMetricSection.DurationMetricDailyItem(
                dailySlot.date(),
                dailySlot.dayRoute() != null,
                outingSeconds,
                outingSeconds == null ? null
                    : DurationFormatUtils.formatOutingDurationText(outingSeconds)
            ));

            if (outingSeconds != null) {
                totalSeconds += outingSeconds;
                sampleSize++;
            }
        }

        Double averageValue = sampleSize == 0 ? null
            : Math.round(((double) totalSeconds / sampleSize) * 10.0) / 10.0;

        return new DurationMetricSection(
            new DurationMetricSection.DurationMetricAverage(
                averageValue,
                averageValue == null ? null
                    : DurationFormatUtils.formatOutingDurationText(Math.round(averageValue)),
                sampleSize
            ),
            dailyValues
        );
    }

    private VisitedRegionsSection buildVisitedRegionsSection(List<DayRoute> dayRoutes) {
        if (dayRoutes.isEmpty()) {
            return new VisitedRegionsSection(List.of());
        }

        Map<String, RegionSummary> regionSummaryMap = new LinkedHashMap<>();
        visitedRegionRepository.findByDayRouteInOrderByTotalStaySecondsDesc(dayRoutes)
            .forEach(visitedRegion -> regionSummaryMap.computeIfAbsent(
                    visitedRegion.getRegion().getDongName(),
                    RegionSummary::new)
                .add(visitedRegion));

        List<RegionSummary> topRegions = regionSummaryMap.values().stream()
            .sorted(
                (left, right) -> Long.compare(right.totalStaySeconds(), left.totalStaySeconds()))
            .limit(TOP_REGION_LIMIT)
            .toList();

        List<VisitedRegionsSection.VisitedRegionSummaryItem> items = new ArrayList<>();
        for (int index = 0; index < topRegions.size(); index++) {
            RegionSummary regionSummary = topRegions.get(index);
            items.add(new VisitedRegionsSection.VisitedRegionSummaryItem(
                index + 1,
                regionSummary.regionName()
            ));
        }

        return new VisitedRegionsSection(items);
    }

    private Integer getMinutesOfDay(DayRoute dayRoute, Function<DayRoute, Instant> extractor) {
        if (dayRoute == null) {
            return null;
        }

        Long minutesOfDay = TimeFormatUtils.toKstMinutesOfDay(extractor.apply(dayRoute));
        return minutesOfDay == null ? null : minutesOfDay.intValue();
    }

    private boolean hasOutingRecord(DayRoute dayRoute) {
        return dayRoute != null && dayRoute.getOutingTime() != null;
    }

    private String formatCountText(double value) {
        if (value == Math.rint(value)) {
            return ((long) value) + "회";
        }
        return value + "회";
    }

    private record DailySlot(LocalDate date, DayRoute dayRoute) {

    }

    private static final class RegionSummary {

        private final String regionName;
        private long totalStaySeconds;

        private RegionSummary(String regionName) {
            this.regionName = regionName;
        }

        private void add(VisitedRegion visitedRegion) {
            totalStaySeconds += visitedRegion.getTotalStaySeconds();
        }

        private String regionName() {
            return regionName;
        }

        private long totalStaySeconds() {
            return totalStaySeconds;
        }

    }
}
