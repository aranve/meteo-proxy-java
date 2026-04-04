package meteoproxy.connector.openmeteo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.math.BigDecimal;

public interface OpenMeteoClient {

    @GET("forecast")
    Call<GetForecastResponse> getCurrentForecast(
            @Query("latitude") BigDecimal latitude,
            @Query("longitude") BigDecimal longitude,
            @Query(value = "current", encoded = true) String current
    );
}

