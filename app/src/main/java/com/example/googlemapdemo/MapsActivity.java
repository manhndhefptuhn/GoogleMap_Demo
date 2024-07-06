package com.example.googlemapdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private final int FINE_PERMISSION_CODE = 1;
    private GoogleMap mMap;
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private SearchView mapSearchView;
    private static final String TAG = "MapsActivity";
    private DrawerLayout drawerLayout;
    private Marker currentMarker;
    private Polyline currentPolyline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_maps);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();

        // Setup SearchView
        mapSearchView = findViewById(R.id.mapSearch);
        mapSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchLocation(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        setupNavigationDrawer();
    }

    private void setSupportActionBar(Toolbar toolbar) {
        if (toolbar != null) {
            toolbar.setTitle("Google Map Demo");
            toolbar.inflateMenu(R.menu.menu_main);
            toolbar.setOnMenuItemClickListener(this::onOptionsItemSelected);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.map_normal) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        } else if (id == R.id.map_satellite) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        } else if (id == R.id.map_hybrid) {
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        } else if (id == R.id.map_terrain) {
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        } else {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(this, "Permission is denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void checkAndRequestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
        }
    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        checkAndRequestLocationPermission();
        mMap.setMyLocationEnabled(true);
        mMap.setOnMapClickListener(this::handleMapClick);
        setupMapUI();
    }
    private void handleMapClick(LatLng latLng) {
        if (currentMarker != null) {
            currentMarker.remove();
        }
        currentMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Vị trí chọn"));
        String toastMsg = String.format("Kinh độ: %s, Vĩ độ: %s", latLng.longitude, latLng.latitude);
        Toast.makeText(MapsActivity.this, toastMsg, Toast.LENGTH_LONG).show();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
    }
    private void setupMapUI() {
        if (currentLocation != null) {
            LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 20));
        }
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
    }
    private void searchLocation(String location) {
        if (location != null && !location.isEmpty()) {
            List<Address> addressList = new ArrayList<>();
            try {
                addressList = new Geocoder(MapsActivity.this).getFromLocationName(location, 1);
                if (!addressList.isEmpty()) {
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    updateMapLocation(latLng, location);
                } else {
                    Toast.makeText(MapsActivity.this, "Địa điểm không được tìm thấy", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void updateMapLocation(LatLng latLng, String location) {
        if (currentMarker != null) {
            currentMarker.remove();
        }
        currentMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(location));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }
    private void setupNavigationDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, findViewById(R.id.toolbar), R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        View headerView = navigationView.getHeaderView(0);
        if (headerView != null) {
            Button buttonGetDirectionsDrawer = headerView.findViewById(R.id.button_get_directions);
            if (buttonGetDirectionsDrawer != null) {
                buttonGetDirectionsDrawer.setOnClickListener(v -> {
                    String fromLocation = ((EditText) headerView.findViewById(R.id.et_from_location)).getText().toString();
                    String toLocation = ((EditText) headerView.findViewById(R.id.et_to_location)).getText().toString();
                    direction(fromLocation, toLocation);
                });
            }
        }
    }
    private void getLastLocation() {
        checkAndRequestLocationPermission();
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            currentLocation = location;
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            if (mapFragment != null) {
                mapFragment.getMapAsync(MapsActivity.this);
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Failed to get last location: " + e.getMessage());
            Toast.makeText(this, "Failed to get location", Toast.LENGTH_SHORT).show();
        });
    }
    private void direction(String locationFrom, String locationTo) {
        LatLng point1 = getPosition(locationFrom);
        LatLng point2 = getPosition(locationTo);

        // Check if either point is null and notify the user if so
        if (point1 == null || point2 == null) {
            Toast.makeText(this, "Một hoặc hai vị trí không được tìm thấy", Toast.LENGTH_SHORT).show();
            return; // Exit the method early to prevent NullPointerException
        }

        // Xóa đường thẳng cũ nếu có
        if (currentPolyline != null) {
            currentPolyline.remove();
        }

        // Vẽ đường thẳng giữa hai điểm và đặt màu cho đường thẳng
        currentPolyline = mMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .add(point1, point2)
                .color(Color.parseColor("#9e4f34"))); // Đổi màu đường thẳng

        // Di chuyển camera đến điểm đầu tiên và zoom
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point1, 15));
    }
    public LatLng getPosition(String location) {
        List<Address> addressList = new ArrayList<>();
        if (location != null) {
            Geocoder geocoder = new Geocoder(MapsActivity.this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!addressList.isEmpty()) {
                Address address = addressList.get(0);
                return new LatLng(address.getLatitude(), address.getLongitude());
            }
        }
        return null;
    }

    //    private void direction(String locationFrom, String locationTo) {
//        LatLng point1 = null;
//        LatLng point2 = null;
//        if (!locationFrom.contains(",") || !locationTo.contains(",")) {
//            // Thêm hai điểm LatLng
//            point1 = getPosition(locationFrom); // Ví dụ: Tọa độ điểm 1
//            point2 = getPosition(locationTo); // Ví dụ: Tọa độ điểm 2
//        } else {
//            String[] pointOne = locationFrom.split(",");
//            String[] pointTwo = locationTo.split(",");
//
//            // Thêm hai điểm LatLng
//            point1 = new LatLng(Double.parseDouble(pointOne[0].trim()), Double.parseDouble(pointOne[1].trim())); // Ví dụ: Tọa độ điểm 1
//            point2 = new LatLng(Double.parseDouble(pointTwo[0].trim()), Double.parseDouble(pointTwo[1].trim())); // Ví dụ: Tọa độ điểm 2
//        }
//
//        // Vẽ đường thẳng giữa hai điểm và đặt màu cho đường thẳng
//        Polyline polyline = mMap.addPolyline(new PolylineOptions()
//                .clickable(true)
//                .add(point1, point2)
//                .color(Color.parseColor("#9e4f34"))); // Đổi màu đường thẳng
//
//        // Di chuyển camera đến điểm đầu tiên và zoom
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point1, 15));
//    }
}