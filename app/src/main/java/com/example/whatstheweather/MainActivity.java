package com.example.whatstheweather;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    EditText cityName;
    TextView weatherDetails,weatherTitle,tempDetail;
    String city;
    static float temp,feelsLike,tempMin,tempMax;
    int humidity;

    public void getWeatherDetails(View view){

        weatherDetails = findViewById(R.id.weatherDetails);
        weatherTitle=findViewById(R.id.weatherTitle);
        tempDetail=findViewById(R.id.tempDetail);
        cityName = findViewById(R.id.cityName);
        DownloadContents task = new DownloadContents();
        city = cityName.getText().toString().trim();
        task.execute("https://api.openweathermap.org/data/2.5/weather?q="+city+"&appid=5ed4a3f2992cbc2cad53e3020dd211f6");
    }
    public class DownloadContents extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpsURLConnection urlConnection = null;

            try {

                url = new URL(urls[0]);

                urlConnection = (HttpsURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data!=-1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            }

            catch (Exception e){
                e.printStackTrace();
                return result;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {

                JSONObject jsonObject = new JSONObject(result);

                String weatherInfo = jsonObject.getString("main");

                String desc=jsonObject.getString("weather");
                String description= "";

                String coun=jsonObject.getString("sys");
                String country="";

                coun=coun.substring(0,coun.length()-1)+"}]";
                coun="[{" + coun.substring(1,coun.length());

                weatherInfo =weatherInfo.substring(0,weatherInfo.length()-1)+"}]";
                weatherInfo="[{" + weatherInfo.substring(1,weatherInfo.length());

//                Log.i("Weather Content", weatherInfo);

                JSONArray arr=new JSONArray(weatherInfo);
                JSONArray arr2=new JSONArray(desc);
                JSONArray arr3=new JSONArray(coun);

                for (int i=0; i<arr.length();i++){
                    JSONObject jsonPart = arr.getJSONObject(i);
                    temp=Float.parseFloat(jsonPart.getString("temp"));
                    feelsLike=Float.parseFloat(jsonPart.getString("feels_like"));
                    humidity=Integer.parseInt(jsonPart.getString("humidity"));
                }

                for(int i=0;i<arr2.length();i++){
                    JSONObject jsonPart=arr2.getJSONObject(i);
                    description=jsonPart.getString("description");
                }

                for(int i=0;i<arr3.length();i++){
                    JSONObject jsonPart=arr3.getJSONObject(i);
                    country=jsonPart.getString("country");
                }

                temp= (float) (temp-273.15);
                feelsLike=(float) (feelsLike-273.15);

                description=description.substring(0, 1).toUpperCase() + description.substring(1);
                weatherTitle.setText(city+", "+country);
                tempDetail.setText(String.format("%.2f",temp)+" \u2103");
                weatherDetails.setText(description+"\nFeels like "+String.format("%.2f",feelsLike)+" \u2103\n"+humidity+"% Humidity");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}