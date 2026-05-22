package backend.capstone.domain.mobility.statics.service;

import backend.capstone.domain.mobility.analysis.visitedregion.entity.VisitedRegion;
import backend.capstone.domain.mobility.analysis.visitedregion.repository.VisitedRegionRepository;
import backend.capstone.domain.mobility.place.entity.Place;
import backend.capstone.domain.mobility.place.repository.PlaceRepository;
import backend.capstone.domain.mobility.statics.dto.PlaceStatisticsSection;
import backend.capstone.domain.mobility.statics.dto.VisitStatisticsResponse;
import backend.capstone.domain.mobility.statics.dto.VisitedRegionStatisticsSection;
import backend.capstone.domain.mobility.statics.type.StatisticPeriod;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VisitStatisticsService {

    private static final int REGION_VISIBLE_LIMIT = 3;
    private static final int PLACE_VISIBLE_LIMIT = 5;
    private static final String OTHER_REGION_NAME = "그 외";

    private final VisitedRegionRepository visitedRegionRepository;
    private final PlaceRepository placeRepository;

    @Transactional(readOnly = true)
    public VisitStatisticsResponse getVisitStatistics(Long userId, StatisticPeriod period,
        LocalDate today) {
        StatisticDateRange dateRange = StatisticDateRangeCalculator.calculate(period, today);
        List<VisitedRegion> visitedRegions = visitedRegionRepository.findByUserIdAndDateBetween(
            userId, dateRange.startDate(), dateRange.endDate());
        List<Place> places = placeRepository.findByUserIdAndDateBetween(
            userId, dateRange.startDate(), dateRange.endDate());

        return new VisitStatisticsResponse(
            period,
            dateRange.startDate(),
            dateRange.endDate(),
            createVisitedRegionSection(visitedRegions),
            createPlaceSection(places)
        );
    }

    private VisitedRegionStatisticsSection createVisitedRegionSection(
        List<VisitedRegion> visitedRegions) {
        Map<String, Integer> visitCountByRegion = new LinkedHashMap<>();
        for (VisitedRegion visitedRegion : visitedRegions) {
            String regionName = visitedRegion.getRegion().getDongName();
            visitCountByRegion.merge(regionName, 1, Integer::sum);
        }

        List<RegionVisitSummary> summaries = visitCountByRegion.entrySet().stream()
            .map(entry -> new RegionVisitSummary(entry.getKey(), entry.getValue()))
            .sorted(Comparator
                .comparingInt(RegionVisitSummary::visitCount).reversed()
                .thenComparing(RegionVisitSummary::regionName))
            .toList();

        int totalVisitCount = summaries.stream()
            .mapToInt(RegionVisitSummary::visitCount)
            .sum();
        List<VisitedRegionStatisticsSection.VisitedRegionStatisticsItem> items =
            createVisitedRegionItems(summaries, totalVisitCount);
        return new VisitedRegionStatisticsSection(totalVisitCount, items);
    }

    private List<VisitedRegionStatisticsSection.VisitedRegionStatisticsItem>
        createVisitedRegionItems(List<RegionVisitSummary> summaries, int totalVisitCount) {
        if (totalVisitCount == 0) {
            return List.of();
        }

        List<VisitedRegionStatisticsSection.VisitedRegionStatisticsItem> items = new ArrayList<>();
        int visibleCount = Math.min(REGION_VISIBLE_LIMIT, summaries.size());
        for (int index = 0; index < visibleCount; index++) {
            RegionVisitSummary summary = summaries.get(index);
            items.add(toVisitedRegionItem(index + 1, summary.regionName(), summary.visitCount(),
                totalVisitCount));
        }

        int otherVisitCount = summaries.stream()
            .skip(REGION_VISIBLE_LIMIT)
            .mapToInt(RegionVisitSummary::visitCount)
            .sum();
        if (otherVisitCount > 0) {
            items.add(toVisitedRegionItem(items.size() + 1, OTHER_REGION_NAME, otherVisitCount,
                totalVisitCount));
        }
        return items;
    }

    private VisitedRegionStatisticsSection.VisitedRegionStatisticsItem toVisitedRegionItem(
        int rank, String regionName, int visitCount, int totalVisitCount) {
        double ratio = roundToOneDecimal((double) visitCount * 100 / totalVisitCount);
        return new VisitedRegionStatisticsSection.VisitedRegionStatisticsItem(
            rank,
            regionName,
            visitCount,
            ratio,
            Math.round(ratio) + "%"
        );
    }

    private PlaceStatisticsSection createPlaceSection(List<Place> places) {
        Map<PlaceKey, PlaceVisitSummary> summaryByPlace = new LinkedHashMap<>();
        for (Place place : places) {
            PlaceKey key = new PlaceKey(place.getName(), place.getRoadAddress());
            summaryByPlace.computeIfAbsent(key,
                ignored -> new PlaceVisitSummary(place.getName(), place.getRoadAddress()))
                .increase();
        }

        List<PlaceVisitSummary> summaries = summaryByPlace.values().stream()
            .sorted(Comparator
                .comparingInt(PlaceVisitSummary::visitCount).reversed()
                .thenComparing(PlaceVisitSummary::placeName,
                    Comparator.nullsLast(String::compareTo))
                .thenComparing(PlaceVisitSummary::roadAddress,
                    Comparator.nullsLast(String::compareTo)))
            .toList();
        int totalVisitCount = summaries.stream()
            .mapToInt(PlaceVisitSummary::visitCount)
            .sum();

        List<PlaceStatisticsSection.PlaceStatisticsItem> items = new ArrayList<>();
        int visibleCount = Math.min(PLACE_VISIBLE_LIMIT, summaries.size());
        for (int index = 0; index < visibleCount; index++) {
            PlaceVisitSummary summary = summaries.get(index);
            items.add(new PlaceStatisticsSection.PlaceStatisticsItem(
                index + 1,
                summary.placeName(),
                summary.roadAddress(),
                summary.visitCount(),
                summary.visitCount() + "회"
            ));
        }
        return new PlaceStatisticsSection(totalVisitCount, items);
    }

    private static double roundToOneDecimal(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    private record RegionVisitSummary(String regionName, int visitCount) {
    }

    private record PlaceKey(String placeName, String roadAddress) {
    }

    private static final class PlaceVisitSummary {

        private final String placeName;
        private final String roadAddress;
        private int visitCount;

        private PlaceVisitSummary(String placeName, String roadAddress) {
            this.placeName = placeName;
            this.roadAddress = roadAddress;
        }

        private void increase() {
            visitCount++;
        }

        private String placeName() {
            return placeName;
        }

        private String roadAddress() {
            return roadAddress;
        }

        private int visitCount() {
            return visitCount;
        }
    }
}
