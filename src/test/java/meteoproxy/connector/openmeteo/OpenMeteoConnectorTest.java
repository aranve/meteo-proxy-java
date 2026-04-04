package meteoproxy.connector.openmeteo;

import meteoproxy.domain.exception.ExternalApiException;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.Test;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class OpenMeteoConnectorTest {

    private static final BigDecimal LATITUDE = new BigDecimal("52.52");
    private static final BigDecimal LONGITUDE = new BigDecimal("13.41");

    private final OpenMeteoClient openMeteoClient = mock(OpenMeteoClient.class);

    private final Retry retry = Retry.of(
            "open-meteo",
            RetryConfig.<Response<?>>custom()
                    .maxAttempts(2)
                    .waitDuration(Duration.ZERO)
                    .retryOnResult(response -> !response.isSuccessful())
                    .build()
    );

    private final OpenMeteoConnector sut = new OpenMeteoConnector(openMeteoClient, retry);

    @Test
    void shouldReturnForecast() throws IOException {
        // given
        GetForecastResponse forecast = mock(GetForecastResponse.class);
        Call<GetForecastResponse> call = mock(Call.class);

        when(openMeteoClient.getCurrentForecast(any(), any(), anyString())).thenReturn(call);
        when(call.execute()).thenReturn(Response.success(forecast));

        // when
        GetForecastResponse result = sut.getCurrentForecast(LATITUDE, LONGITUDE);

        // then
        assertEquals(forecast, result);
        verify(openMeteoClient, times(1)).getCurrentForecast(any(), any(), anyString());
    }

    @Test
    void shouldRetryOnceAndSucceedOnSecondAttempt() throws IOException {
        // given
        GetForecastResponse forecast = mock(GetForecastResponse.class);
        Call<GetForecastResponse> call = mock(Call.class);

        ResponseBody errorBody = ResponseBody.create(MediaType.get("application/json"), "error");

        when(openMeteoClient.getCurrentForecast(any(), any(), anyString())).thenReturn(call);
        when(call.execute())
                .thenReturn(Response.error(500, errorBody))
                .thenReturn(Response.success(forecast));

        // when
        GetForecastResponse result = sut.getCurrentForecast(LATITUDE, LONGITUDE);

        // then
        assertEquals(forecast, result);
        verify(openMeteoClient, times(2)).getCurrentForecast(any(), any(), anyString());
    }

    @Test
    void shouldThrowExternalApiExceptionAfterRetryIsExhausted() throws IOException {
        // given
        Call<GetForecastResponse> call = mock(Call.class);

        ResponseBody errorBody = ResponseBody.create(MediaType.get("application/json"), "error");

        when(openMeteoClient.getCurrentForecast(any(), any(), anyString())).thenReturn(call);
        when(call.execute()).thenReturn(Response.error(500, errorBody));

        // when + then
        assertThrows(ExternalApiException.class, () -> sut.getCurrentForecast(LATITUDE, LONGITUDE));

        verify(openMeteoClient, times(2)).getCurrentForecast(any(), any(), anyString());
    }
}

