package backend.capstone.domain.care.caredependent.sse.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CareSseEventType {
    HEARTBEAT("heartbeat"),
    CONNECTED("connected"),
    LOCATION_UPDATED("location-updated");

    private final String eventName;
}
