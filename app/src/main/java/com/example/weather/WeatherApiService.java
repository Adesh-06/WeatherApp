package com.example.weather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApiService {
    @GET("weather")
    Call<WeatherApp> getWeather(@Query("q") String cityName, @Query("appid")String apiKey);

}
