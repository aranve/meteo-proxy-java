package meteoproxy.domain.utils;

import retrofit2.Response;

public class RetrofitUtils {

    private RetrofitUtils() {}

    public static boolean isFailed(Response<?> response) {
        return !response.isSuccessful();
    }
}

