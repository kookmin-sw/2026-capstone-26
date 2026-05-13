package backend.capstone.domain.care.service;

import backend.capstone.domain.care.carerelationship.repository.CareRelationshipRepository;
import backend.capstone.domain.care.dto.CareDayRouteDetailResponse;
import backend.capstone.domain.care.exception.CareErrorCode;
import backend.capstone.domain.care.mapper.CareDayRouteMapper;
import backend.capstone.domain.mobility.dayroute.dto.DayRouteDetailResponse;
import backend.capstone.domain.mobility.dayroute.facade.DayRouteFacade;
import backend.capstone.global.exception.BusinessException;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CareDayRouteService {

    private final CareRelationshipRepository careRelationshipRepository;
    private final DayRouteFacade dayRouteFacade;

    public CareDayRouteDetailResponse getDependentUserDayRouteDetail(
        Long guardianUserId, Long dependentUserId, LocalDate date
    ) {
        validateDependentUserAccess(guardianUserId, dependentUserId);

        DayRouteDetailResponse dayRouteDetailResponse = dayRouteFacade.getDayRouteDetail(
            date, dependentUserId);
        return CareDayRouteMapper.toCareDayRouteDetailResponse(dayRouteDetailResponse);
    }

    private void validateDependentUserAccess(Long guardianUserId, Long dependentUserId) {
        if (!careRelationshipRepository.existsByGuardianUserIdAndDependentUserId(
            guardianUserId, dependentUserId)) {
            throw new BusinessException(CareErrorCode.CARE_DEPENDENT_USER_ACCESS_DENIED);
        }
    }
}
