package backend.capstone.domain.care.sse.dto;

public record CareSseMessagePayload(
    String message
) {

    public static CareSseMessagePayload of(String message) {
        return new CareSseMessagePayload(message);
    }
}
