package com.example.googlemapdemo;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText etFromLocation;
    private EditText etToLocation;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

//        etFromLocation = findViewById(R.id.et_from_location);
//        etToLocation = findViewById(R.id.et_to_location);
//        button = findViewById(R.id.button);
//
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String userLocation = etFromLocation.getText().toString();
//                String userDestination = etToLocation.getText().toString();
//
//                if(userLocation.equals("") || userDestination.equals("")){
//                    Toast.makeText(MainActivity.this, "Please enter valid address", Toast.LENGTH_SHORT).show();
//                } else{
//                    getDirections(userLocation, userDestination);
//                }
//            }
//        });
    }

    public void getDirections(String userLocation, String userDestination){
        try{
            Uri uri = Uri.parse("http://www.google.com/maps/dir/"+userLocation+"/"+userDestination);
            Intent i = new Intent(Intent.ACTION_VIEW, uri);
            i.setPackage("com.google.android.apps.maps");
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }catch (ActivityNotFoundException e){
            Uri uri = Uri.parse("http://www.google.com/store/apps/details?id=com.google.android.apps.maps");
            Intent i = new Intent(Intent.ACTION_VIEW, uri);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
    }

//    private void direction(){
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        String url = Uri.parse("https://maps.googleapis.com/maps/api/directions/json")  // Use https instead of http
//                .buildUpon()
//                .appendQueryParameter("destination", "-6.9218571, 107.6048254")
//                .appendQueryParameter("origin", "-6.9249233, 107.6345122")
//                .appendQueryParameter("key", API_STRING)
//                .toString();
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                try {
//                    String status = jsonObject.getString("status");
//                    if (status.equals("OK")) {
//                        JSONArray routes = jsonObject.getJSONArray("routes");
//
//                        ArrayList<LatLng> points;
//                        PolylineOptions polylineOptions = null;
//
//                        for (int i = 0; i < routes.length(); i++) {
//                            points = new ArrayList<>();
//                            polylineOptions = new PolylineOptions();
//                            JSONArray legs = routes.getJSONObject(i).getJSONArray("legs");
//
//                            for (int j = 0; j < legs.length(); j++) {
//                                JSONArray steps = legs.getJSONObject(j).getJSONArray("steps");
//
//                                for (int k = 0; k < steps.length(); k++) {
//                                    String polyline = steps.getJSONObject(k).getJSONObject("polyline").getString("points");
//                                    List<LatLng> list = decodePoly(polyline);
//
//                                    for (LatLng point : list) {
//                                        points.add(point);
//                                    }
//                                }
//                            }
//                            polylineOptions.addAll(points);
//                            polylineOptions.width(10);
//                            polylineOptions.color(ContextCompat.getColor(MapsActivity.this, R.color.black));
//                            polylineOptions.geodesic(true);
//                        }
//
//                        if (polylineOptions != null) {
//                            mMap.addPolyline(polylineOptions);
//                            mMap.addMarker(new MarkerOptions().position(new LatLng(-6.9249233, 107.6345122)).title("Marker 1"));
//                            mMap.addMarker(new MarkerOptions().position(new LatLng(-6.9218571, 107.6048254)).title("Marker 2"));
//
//                            LatLngBounds bounds = new LatLngBounds.Builder()
//                                    .include(new LatLng(-6.9249233, 107.6345122))
//                                    .include(new LatLng(-6.9218571, 107.6048254)).build();
//
//                            Point point = new Point();
//                            getWindowManager().getDefaultDisplay().getSize(point);
//                            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, point.x, 150, 30));
//                        } else {
//                            Toast.makeText(MapsActivity.this, "No routes found", Toast.LENGTH_SHORT).show();
//                        }
//                    } else {
//                        Toast.makeText(MapsActivity.this, "Error: " + status, Toast.LENGTH_SHORT).show();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    Toast.makeText(MapsActivity.this, "Parsing error", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//                Toast.makeText(MapsActivity.this, "Request error: " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//        RetryPolicy retryPolicy = new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//        jsonObjectRequest.setRetryPolicy(retryPolicy);
//        requestQueue.add(jsonObjectRequest);
//    }
//
//    private List<LatLng> decodePoly(String encoded){
//        List<LatLng> poly = new ArrayList<>();
//        int index = 0, len = encoded.length();
//        int lat = 0, lng = 0;
//
//        while(index < len){
//            int b, shift = 0, result = 0;
//            do{
//                b = encoded.charAt(index++) - 63;
//                result |= (b & 0x1f) << shift;
//                shift += 5;
//            } while(b >= 0x20);
//            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
//            lat += dlat;
//
//            shift = 0;
//            result = 0;
//            do{
//                b = encoded.charAt(index++) - 63;
//                result |= (b & 0x1f) << shift;
//                shift += 5;
//            } while(b >= 0x20);
//            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
//            lng += dlng;
//
//            LatLng p = new LatLng((((double) lat / 1E5)), (((double) lng / 1E5)));
//            poly.add(p);
//        }
//        return poly;
//    }

    //            if (currentLocation != null) {
//                LatLng origin = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
//                LatLng destination = new LatLng(21.137167, 105.709220); // Coordinates for Ninh Bình
//                calculateDirections(origin, destination);
//            } else {
//                Toast.makeText(MapsActivity.this, "Current location is not available", Toast.LENGTH_SHORT).show();
//            }

//    private void calculateDirections(LatLng origin, LatLng destination) {
//        Log.d(TAG, "calculateDirections: calculating directions.");
//
//        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);
//        directions.alternatives(true);
//        directions.origin(new com.google.maps.model.LatLng(origin.latitude, origin.longitude));
//        directions.destination(new com.google.maps.model.LatLng(destination.latitude, destination.longitude));
//
//        directions.setCallback(new com.google.maps.PendingResult.Callback<DirectionsResult>() {
//            @Override
//            public void onResult(DirectionsResult result) {
//                Log.d(TAG, "onResult: successfully retrieved directions.");
//                addPolylinesToMap(result);
//            }
//
//            @Override
//            public void onFailure(Throwable e) {
//                Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage(), e);
//                Toast.makeText(MapsActivity.this, "Failed to get directions", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void addPolylinesToMap(final DirectionsResult result) {
//        new Handler(Looper.getMainLooper()).post(() -> {
//            Log.d(TAG, "addPolylinesToMap: result routes: " + result.routes.length);
//
//            for (DirectionsRoute route : result.routes) {
//                Log.d(TAG, "addPolylinesToMap: leg: " + route.legs[0].toString());
//                List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());
//
//                List<LatLng> newDecodedPath = new ArrayList<>();
//
//                for (com.google.maps.model.LatLng latLng : decodedPath) {
//                    newDecodedPath.add(new LatLng(latLng.lat, latLng.lng));
//                }
//                Polyline polyline = mMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
//                polyline.setColor(ContextCompat.getColor(MapsActivity.this, android.R.color.holo_blue_dark));
//                polyline.setClickable(true);
//            }
//        });
//    }
}