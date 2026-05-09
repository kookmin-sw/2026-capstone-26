package backend.capstone.domain.mobility.dayroute.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public record DayRouteBookmarkBatchRequest(
    @NotEmpty
    @Schema(example = "[\"2026-05-09\", \"2026-05-10\"]")
    List<@NotNull LocalDate> dates
) {

}
