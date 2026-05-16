package backend.capstone.domain.mobility.place.dto;

public record PlaceUpdateRequest(
    String roadAddress,
    String placeName,
    double latitude,
    double longitude
) {

}
