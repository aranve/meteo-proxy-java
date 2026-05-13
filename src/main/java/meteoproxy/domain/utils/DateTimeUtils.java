package meteoproxy.domain.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class DateTimeUtils {

    private DateTimeUtils() {}

    public static String convertToZuluDateTime(String datetimeString, String timezone) {
        Objects.requireNonNull(datetimeString, "datetimeString cannot be null");
        Objects.requireNonNull(timezone, "timezone cannot be null");

        LocalDateTime localDateTime = LocalDateTime.parse(datetimeString);
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.of(timezone));
        return zonedDateTime
                .withZoneSameInstant(ZoneId.of("UTC"))
                .format(DateTimeFormatter.ISO_INSTANT);
    }
}

