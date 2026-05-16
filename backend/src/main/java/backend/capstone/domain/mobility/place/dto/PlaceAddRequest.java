package backend.capstone.domain.mobility.place.dto;

public record PlaceAddRequest(
    String roadAddress,
    String placeName,
    double latitude,
    double longitude
) {

}
