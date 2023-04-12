package com.example.salatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

//    Button fetchTime;
    TextView fajr_time_data,sunrise_time_data, dhuhr_time_data,asr_time_data,magrib_time_data,sunset_time_data,isha_time_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // assign var
//        fetchTime = findViewById(R.id.btn_fetch_time);

        fajr_time_data= findViewById(R.id.fajr_time_data);
//        sunrise_time_data= findViewById(R.id.sunrise_time_data);
        dhuhr_time_data= findViewById(R.id.dhuhr_time_data);
        asr_time_data= findViewById(R.id.asr_time_data);
        magrib_time_data= findViewById(R.id.magrib_time_data);
//        sunset_time_data= findViewById(R.id.sunset_time_data);
        isha_time_data= findViewById(R.id.isha_time_data);

        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate currentDate = LocalDate.now();
        String date = currentDate.format(formatter);
        String url = "https://api.aladhan.com/v1/timingsByAddress/"+date+"?address=Dhaka,Bangladesh&method=1";

        // call api
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // Assuming the JSON object is stored in a variable called 'response'
                    JSONObject data = response.getJSONObject("data");
                    JSONObject timings = data.getJSONObject("timings");
                    String fajr = timings.getString("Fajr");
                    String sunrise = timings.getString("Sunrise");
                    String dhuhr = timings.getString("Dhuhr");
                    String asr = timings.getString("Asr");
                    String sunset = timings.getString("Sunset");
                    String maghrib = timings.getString("Maghrib");
                    String isha = timings.getString("Isha");



                    SimpleDateFormat timeFormater = new SimpleDateFormat("hh:mm");
                    SimpleDateFormat timeFormaterAsAMPM = new SimpleDateFormat("hh:mm a");

                    Date fazarTime =timeFormater.parse(fajr);
                    Date dhuhrTime =timeFormater.parse(dhuhr);
                    Date asrTime =timeFormater.parse(asr);
                    Date maghribTime =timeFormater.parse(maghrib);
                    Date ishaTime =timeFormater.parse(isha);

                    fajr_time_data.setText(timeFormaterAsAMPM.format(fazarTime).toString());
//                    sunrise_time_data.setText("Sunrise: "+sunrise);
                    dhuhr_time_data.setText(timeFormaterAsAMPM.format(dhuhrTime).toString());
                    asr_time_data.setText(timeFormaterAsAMPM.format(asrTime).toString());
//                    sunset_time_data.setText("Sunset: "+sunset);
                    magrib_time_data.setText(timeFormaterAsAMPM.format(maghribTime).toString());
                    isha_time_data.setText(timeFormaterAsAMPM.format(ishaTime).toString());

                } catch (JSONException e) {
                    // Handle the exception here
                    e.printStackTrace(); // This will print the error stack trace to the console
                    Toast.makeText(MainActivity.this, "Error!!!", Toast.LENGTH_SHORT).show();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("error: "+ error);
                Toast.makeText(MainActivity.this, "Error!!!", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(request);


        // on event click
//        fetchTime.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//
//
//            }
//        });
    }
}