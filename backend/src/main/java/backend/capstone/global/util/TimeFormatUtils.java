package backend.capstone.global.util;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

//시간을 분 단위로 변환
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TimeFormatUtils {

    private static final ZoneId KST_ZONE_ID = ZoneId.of("Asia/Seoul");

    public static Long toKstMinutesOfDay(Instant instant) {
        if (instant == null) {
            return null;
        }

        LocalTime localTime = instant.atZone(KST_ZONE_ID).toLocalTime();
        return (long) (localTime.getHour() * 60 + localTime.getMinute());
    }

    public static String formatHourMinute(int totalMinutes) {
        int hour = totalMinutes / 60;
        int minute = totalMinutes % 60;
        return String.format("%02d:%02d", hour, minute);
    }
}
