package com.example.salatapp;

import static com.example.salatapp.R.id.fajr_time_data;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // Get the last known location
    private FusedLocationProviderClient fusedLocationClient;

    //    Button fetchTime;
    TextView fajr_time_data, sunrise_time_data, dhuhr_time_data, asr_time_data, magrib_time_data, sunset_time_data, isha_time_data, current_city, current_country;

    LocalDate currentDate = LocalDate.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    String date = currentDate.format(formatter);
    final String[] url = {"https://api.aladhan.com/v1/timingsByAddress/" + date + "?address=Mumbai,India&method=1"};
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 6969) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                new AlertDialog.Builder(this)
                        .setTitle("Location permission needed!")
                        .setMessage("Please provide location permission from app settings")
                        .setPositiveButton("Grant Permission", (dialogInterface, i) -> {
                            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                                ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 6969);
                            } else {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                startActivityForResult(intent.setData(uri), 420);
                            }

                        }).show();
            }
            else{
                Toast.makeText(MainActivity.this, "Successfully accessed location.", Toast.LENGTH_SHORT).show();
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the last known location
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 6969);
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        current_country = findViewById(R.id.current_country);
        current_city = findViewById(R.id.current_city);



        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                            try {
                                List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                if(!addressList.isEmpty()) {
                                    Log.d("LOCATION", "onSuccess: " + addressList.get(0).getLocality());
                                    Log.d("LOCATION", "onSuccess: " + addressList.get(0).getCountryName());

                                    current_country.setText(addressList.get(0).getCountryName());
                                    current_city.setText(addressList.get(0).getLocality());

                                    url[0] = "https://api.aladhan.com/v1/timingsByAddress/"+date+"?address="+addressList.get(0).getLocality()+","+addressList.get(0).getCountryName()+"&method=1";
                                    callAPI();
                                }
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                });


    }

    public void callAPI() {
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

        // call api
        Log.d("LOCATION", "onSuccess: " + url[0]);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url[0], null, new Response.Listener<JSONObject>() {
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
    }
}