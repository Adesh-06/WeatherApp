package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextView temperatureTextView, humidityTextView, windTextView, textCity,sunriseTextView,sunsetTextView,sealevelTextView,conditionTextView,textMainCond;
    private TextView minTempTextView, maxTempTextView, dayTextView, dateTextView;
    private androidx.appcompat.widget.SearchView searchView;
    private ImageView backgroundImageView; // Background ImageView
    private LottieAnimationView lottieAnimationView;
    String Condition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        backgroundImageView = findViewById(R.id.backgroundImageView); // Initialize background ImageView
        lottieAnimationView = findViewById(R.id.lottieAnimationView);

        // Initialize views
        textCity = findViewById(R.id.textView2);
        temperatureTextView = findViewById(R.id.textView8);
        humidityTextView = findViewById(R.id.humidity);
        windTextView = findViewById(R.id.wind);
        searchView = findViewById(R.id.searchView);

        // For min, max temperature, date and day
        minTempTextView = findViewById(R.id.textView6);  // Min temp TextView
        maxTempTextView = findViewById(R.id.textView5);  // Max temp TextView
        dayTextView = findViewById(R.id.textView7);      // Day TextView
        dateTextView = findViewById(R.id.textView9);// Date TextView
        sunriseTextView=findViewById(R.id.sunrise);
        sunsetTextView=findViewById(R.id.sunset);
        sealevelTextView=findViewById(R.id.sea);
        conditionTextView=findViewById(R.id.main);
        textMainCond=findViewById(R.id.textView4);


        // Set listener for search input
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String capitalizedCity = query.toUpperCase();
                fetchWeatherData(query);
                textCity.setText(capitalizedCity);
                textCity.setTypeface(null, Typeface.BOLD);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // Set current date and day
        setCurrentDateAndDay();
    }

    // Fetch weather data using Retrofit
    private void fetchWeatherData(String cityName) {
        // Initialize Retrofit for API calls
        WeatherApiService apiService = ApiClient.getClient("https://api.openweathermap.org/data/2.5/")
                .create(WeatherApiService.class);

        // Make the API call to fetch weather data
        Call<WeatherApp> call = apiService.getWeather(cityName, "a86f9c3b106f24906717ab6962094016");

        call.enqueue(new Callback<WeatherApp>() {
            @Override
            public void onResponse(Call<WeatherApp> call, Response<WeatherApp> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherApp weather = response.body();
                    updateUI(weather);  // Update UI with weather data
                } else if (response.code() == 404) {
                    // City not found, show a Toast message
                    Toast.makeText(MainActivity.this, "Invalid city name. Please try again.", Toast.LENGTH_SHORT).show();
                    Log.e("WeatherApp", "City not found: " + response.message());
                } else {
                    // Handle other non-successful cases
                    Toast.makeText(MainActivity.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                    Log.e("WeatherApp", "Response not successful: " + response.message());
                }
                changeBackgroundAndAnimation(Condition);

            }

            @Override
            public void onFailure(Call<WeatherApp> call, Throwable t) {
                Log.e("WeatherApp", "Error fetching weather data: " + t.getMessage(), t);
            }
        });
    }

    // Update the UI with weather data
    private void updateUI(WeatherApp weather) {
        if (weather != null && weather.getMain() != null && weather.getWind() != null) {
            // Temperature in Celsius
            double temperatureInCelsius = weather.getMain().getTemp() - 273.15;
            temperatureTextView.setText(String.format("%.1f°C", temperatureInCelsius));

            // Min and Max Temperature in Celsius
            double minTempInCelsius = temperatureInCelsius - 2.0; // Approximate variation
            double maxTempInCelsius = temperatureInCelsius + 2.0;

            if (weather.getWeather() != null && !weather.getWeather().isEmpty()) {
                String condition = weather.getWeather().get(0).getDescription(); // Assuming Weather class has description
                conditionTextView.setText(condition.substring(0, 1).toUpperCase() + condition.substring(1));
                textMainCond.setText(condition.substring(0, 1).toUpperCase() + condition.substring(1));
                // Capitalize first letter
                Condition=condition;
            } else {
                conditionTextView.setText("not available");
            }


            minTempTextView.setText(String.format("Min: %.1f°C", minTempInCelsius));
            maxTempTextView.setText(String.format("Max: %.1f°C", maxTempInCelsius));
            long sunriseTimestamp = weather.getSys().getSunrise();
            long sunsetTimestamp = weather.getSys().getSunset();
            String sunriseTime = convertTimestampToTime(sunriseTimestamp);
            String sunsetTime = convertTimestampToTime(sunsetTimestamp);
            sunriseTextView.setText(String.format(sunriseTime));
            sunsetTextView.setText(String.format(sunsetTime));

            //sea level

            double seaLevel = weather.getMain().getSeaLevel();
            if (seaLevel > 0) { // Assuming sea level > 0 is a valid condition
                sealevelTextView.setText(String.format("Sea Level: %.1f hPa", seaLevel));
            } else {
                sealevelTextView.setText("!");
            }

            // Humidity and wind speed
            humidityTextView.setText(String.format("%d%%", weather.getMain().getHumidity()));
            windTextView.setText(String.format("%.1f m/s", weather.getWind().getSpeed()));
        } else {
            Log.e("WeatherApp", "Weather data is incomplete or null");
        }
    }

    // Set current date and day of the week
    private void setCurrentDateAndDay() {
        // Get current date
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();

        // Format date as "dd MMMM yyyy"
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(date);
        dateTextView.setText(formattedDate);

        // Get day of the week
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        String dayOfTheWeek = dayFormat.format(date);
        dayTextView.setText(dayOfTheWeek);
    }

    private String convertTimestampToTime(long timestamp) {
        // Convert seconds to milliseconds
        Date date = new Date(timestamp * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getDefault());  // Use the device's local time zone
        return sdf.format(date);
    }

    private void changeBackgroundAndAnimation(String condition) {
        if (condition.contains("clear sky") || condition.contains("sunny") || condition.contains("clear")) {
            backgroundImageView.setImageResource(R.drawable.sunny_background);
            lottieAnimationView.setAnimation(R.raw.sun);
        } else if (condition.toLowerCase().contains("partly cloudy") || condition.toLowerCase().contains("clouds") || condition.toLowerCase().contains("overcast") ||
                condition.toLowerCase().contains("mist") || condition.toLowerCase().contains("fog")) {
            backgroundImageView.setImageResource(R.drawable.colud_background);
            lottieAnimationView.setAnimation(R.raw.cloud);
        } else if (condition.toLowerCase().contains("light rain") || condition.toLowerCase().contains("drizzle") || condition.toLowerCase().contains("moderate rain") ||
                condition.toLowerCase().contains("showers") || condition.toLowerCase().contains("heavy rain")|| condition.toLowerCase().contains("heavy intensity rain")) {
            backgroundImageView.setImageResource(R.drawable.rain_background);
            lottieAnimationView.setAnimation(R.raw.rain);
        } else if (condition.toLowerCase().contains("light snow") || condition.toLowerCase().contains("moderate snow") ||
                condition.toLowerCase().contains("heavy snow") || condition.toLowerCase().contains("blizzard")) {
            backgroundImageView.setImageResource(R.drawable.snow_background);
            lottieAnimationView.setAnimation(R.raw.snow);
        } else {
            // Set default or a fallback animation/background
            backgroundImageView.setImageResource(R.drawable.sunny_background); // Define a default background
            lottieAnimationView.setAnimation(R.raw.sun); // Define a default animation
        }

        // Start the Lottie animation
        lottieAnimationView.playAnimation();
    }



}