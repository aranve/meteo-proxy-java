package meteoproxy.domain.meteo.utils;

import meteoproxy.domain.utils.DateTimeUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DateTimeUtilsUnitTest {

    @Test
    void convertsLocalDatetimeFromNewYorkToUtc() {
        String datetime = "2023-08-01T10:00:00";
        String timezone = "America/New_York";

        String result = DateTimeUtils.convertToZuluDateTime(datetime, timezone);

        // 10:00 AM EDT (UTC-4) -> 14:00 UTC
        assertEquals("2023-08-01T14:00:00Z", result);
    }

    @Test
    void convertsLocalDatetimeFromTokyoToUtc() {
        String datetime = "2023-08-01T10:00:00";
        String timezone = "Asia/Tokyo";

        String result = DateTimeUtils.convertToZuluDateTime(datetime, timezone);

        // 10:00 AM JST (UTC+9) -> 01:00 UTC
        assertEquals("2023-08-01T01:00:00Z", result);
    }

    @Test
    void convertsLocalDatetimeFromUtcToUtc() {
        String datetime = "2023-08-01T10:00:00";
        String timezone = "UTC";

        String result = DateTimeUtils.convertToZuluDateTime(datetime, timezone);

        assertEquals("2023-08-01T10:00:00Z", result);
    }

    @Test
    void throwsExceptionForInvalidTimezone() {
        String datetime = "2023-08-01T10:00:00";
        String invalidTimezone = "NotARealTimezone";

        assertThrows(Exception.class, () ->
                DateTimeUtils.convertToZuluDateTime(datetime, invalidTimezone)
        );
    }

    @Test
    void throwsExceptionForInvalidDatetimeFormat() {
        String invalidDatetime = "2023-08-01 10:00:00";
        String timezone = "UTC";

        assertThrows(Exception.class, () ->
                DateTimeUtils.convertToZuluDateTime(invalidDatetime, timezone)
        );
    }
}

