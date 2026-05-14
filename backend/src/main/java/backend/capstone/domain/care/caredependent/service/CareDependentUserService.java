package backend.capstone.domain.care.caredependent.service;

import backend.capstone.domain.care.caredependent.dto.CareDayRouteDetailResponse;
import backend.capstone.domain.care.caredependent.dto.CareDependentDayRouteListResponse;
import backend.capstone.domain.care.caredependent.dto.CareDependentUserListResponse;
import backend.capstone.domain.care.caredependent.exception.CareDependentErrorCode;
import backend.capstone.domain.care.caredependent.mapper.CareDependentMapper;
import backend.capstone.domain.care.carerelationship.repository.CareRelationshipRepository;
import backend.capstone.domain.mobility.dayroute.dto.DayRouteDetailResponse;
import backend.capstone.domain.mobility.dayroute.dto.DayRouteSummaryResponse;
import backend.capstone.domain.mobility.dayroute.entity.DayRoute;
import backend.capstone.domain.mobility.dayroute.facade.DayRouteFacade;
import backend.capstone.domain.mobility.dayroute.repository.DayRouteRepository;
import backend.capstone.domain.mobility.latestgpspoint.entity.LatestGpsPoint;
import backend.capstone.domain.mobility.latestgpspoint.repository.LatestGpsPointRepository;
import backend.capstone.domain.mobility.place.dto.PlaceListResponse;
import backend.capstone.domain.mobility.place.facade.PlaceFacade;
import backend.capstone.domain.user.entity.User;
import backend.capstone.global.exception.BusinessException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CareDependentUserService {

    private final CareRelationshipRepository careRelationshipRepository;
    private final LatestGpsPointRepository latestGpsPointRepository;
    private final DayRouteRepository dayRouteRepository;

    private final DayRouteFacade dayRouteFacade;
    private final PlaceFacade placeFacade;

    public CareDependentUserListResponse getDependentUsers(Long guardianUserId) {
        List<User> dependentUsers = careRelationshipRepository.findDependentUsersByGuardianUserId(
            guardianUserId);
        if (dependentUsers.isEmpty()) {
            return CareDependentMapper.toListResponse(List.of(), Map.of());
        }

        List<Long> dependentUserIds = dependentUsers.stream()
            .map(User::getId)
            .toList();
        List<LatestGpsPoint> latestGpsPoints = latestGpsPointRepository.findAllByUserIdIn(
            dependentUserIds);

        return CareDependentMapper.toListResponse(dependentUsers,
            toLatestGpsPointMap(latestGpsPoints));
    }

    public CareDayRouteDetailResponse getDependentUserDayRouteDetail(
        Long guardianUserId, Long dependentUserId, LocalDate date
    ) {
        validateDependentUserAccess(guardianUserId, dependentUserId);

        DayRouteDetailResponse dayRouteDetailResponse = dayRouteFacade.getDayRouteDetail(
            date, dependentUserId);
        return CareDependentMapper.toCareDayRouteDetailResponse(dayRouteDetailResponse);
    }

    public PlaceListResponse getDependentUserPlaces(Long guardianUserId, Long dependentUserId,
        LocalDate date) {
        validateDependentUserAccess(guardianUserId, dependentUserId);
        return placeFacade.getPlaces(date, dependentUserId);
    }

    public DayRouteSummaryResponse getDependentUserDayRouteSummary(
        Long guardianUserId, Long dependentUserId, LocalDate date
    ) {
        validateDependentUserAccess(guardianUserId, dependentUserId);
        return dayRouteFacade.getDayRouteSummary(date, dependentUserId);
    }

    public CareDependentDayRouteListResponse getDependentUserDayRoutes(
        Long guardianUserId, Long dependentUserId, LocalDate cursorDate, int size
    ) {
        validateDependentUserAccess(guardianUserId, dependentUserId);

        List<DayRoute> fetchedDayRoutes = dayRouteRepository.findByUserIdAndCursorDateOrderByDateDesc(
            dependentUserId, cursorDate, PageRequest.of(0, size + 1));

        boolean hasNext = fetchedDayRoutes.size() > size;

        List<DayRoute> dayRoutes = hasNext
            ? fetchedDayRoutes.subList(0, size)
            : fetchedDayRoutes;

        LocalDate nextCursorDate = hasNext ? dayRoutes.get(dayRoutes.size() - 1).getDate() : null;

        return CareDependentMapper.toDayRouteListResponse(dayRoutes, hasNext, nextCursorDate);
    }

    private Map<Long, LatestGpsPoint> toLatestGpsPointMap(List<LatestGpsPoint> latestGpsPoints) {
        Map<Long, LatestGpsPoint> latestGpsPointByUserId = new HashMap<>();
        for (LatestGpsPoint latestGpsPoint : latestGpsPoints) {
            latestGpsPointByUserId.put(latestGpsPoint.getUserId(), latestGpsPoint);
        }
        return latestGpsPointByUserId;
    }

    private void validateDependentUserAccess(Long guardianUserId, Long dependentUserId) {
        if (!careRelationshipRepository.existsByGuardianUserIdAndDependentUserId(
            guardianUserId, dependentUserId)) {
            throw new BusinessException(CareDependentErrorCode.CARE_DEPENDENT_USER_ACCESS_DENIED);
        }
    }
}
