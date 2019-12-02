package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    String city = "seoul,kr";
    String api = "8118ed6ee68db2debfaaa5a44c832918";

    TextView addressTxt, updated_atTxt, statusTxt, tempTxt, temp_minTxt, temp_maxTxt, windTxt, humidityTxt;
    ImageView statusImg;
    ImageButton settingBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addressTxt = findViewById(R.id.address);
        updated_atTxt = findViewById(R.id.updated_at);
        statusTxt = findViewById(R.id.status);
        tempTxt = findViewById(R.id.temp);
        temp_minTxt = findViewById(R.id.temp_min);
        temp_maxTxt = findViewById(R.id.temp_max);
        windTxt = findViewById(R.id.wind);
        humidityTxt = findViewById(R.id.humidity);
        statusImg = findViewById(R.id.statusImg);
        settingBtn = findViewById(R.id.SettingBtn);
        settingBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(intent);
            }
        });

        new weatherTask().execute();
    }

    class weatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /* Showing the ProgressBar, Making the main design GONE */
            findViewById(R.id.loader).setVisibility(View.VISIBLE);
            findViewById(R.id.mainContainer).setVisibility(View.GONE);
            findViewById(R.id.errorText).setVisibility(View.GONE);
        }

        protected String doInBackground(String... args) {
            String response = HttpRequest.ExecuteGet("https://api.openweathermap.org/data/2.5/weather?q=" + city + "&units=metric&appid=" + api);
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObj = new JSONObject(result);
                JSONObject main = jsonObj.getJSONObject("main");
                JSONObject sys = jsonObj.getJSONObject("sys");
                JSONObject wind = jsonObj.getJSONObject("wind");
                JSONObject weather = jsonObj.getJSONArray("weather").getJSONObject(0);

                Long updatedAt = jsonObj.getLong("dt");
                String updatedAtText = new SimpleDateFormat("yyyy.MM.dd a hh:mm", Locale.KOREAN).format(new Date(updatedAt * 1000)) + " 에 업데이트됨";
                String temp = main.getString("temp") + "°C";
                String tempMin = "최소 기온: " + main.getString("temp_min") + "°C";
                String tempMax = "최대 기온: " + main.getString("temp_max") + "°C";
                String windSpeed = wind.getString("speed") + "m/s";
                String humidity = main.getString("humidity") + "%";

                String weatherDescription = weather.getString("description");

                String address = jsonObj.getString("name") + ", " + sys.getString("country");

                /* Populating extracted data into our views */
                addressTxt.setText(address);
                updated_atTxt.setText(updatedAtText);
                statusTxt.setText(weatherDescription);
                tempTxt.setText(temp);
                temp_minTxt.setText(tempMin);
                temp_maxTxt.setText(tempMax);
                windTxt.setText(windSpeed);
                humidityTxt.setText(humidity);

                if (weatherDescription.contains("clear")) {
                    statusImg.setImageResource(R.drawable.clear_sky);
                    statusTxt.setText("맑음");
                }
                else if (weatherDescription.contains("cloud")) {
                    statusImg.setImageResource(R.drawable.cloud);
                    statusTxt.setText("흐림");
                }
                else if (weatherDescription.contains("rain")) {
                    statusImg.setImageResource(R.drawable.rainy);
                    statusTxt.setText("비");
                }

                /* Views populated, Hiding the loader, Showing the main design */
                findViewById(R.id.loader).setVisibility(View.GONE);
                findViewById(R.id.mainContainer).setVisibility(View.VISIBLE);

            } catch (JSONException e) {
                Log.d("shj", "API ERROR : " + e);
                findViewById(R.id.loader).setVisibility(View.GONE);
                findViewById(R.id.errorText).setVisibility(View.VISIBLE);
            }
        }
    }
}
