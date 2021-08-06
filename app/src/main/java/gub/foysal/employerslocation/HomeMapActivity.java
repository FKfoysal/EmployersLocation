package gub.foysal.employerslocation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import gub.foysal.employerslocation.Method.GpsTracker;
import gub.foysal.employerslocation.Method.Users;
import gub.foysal.employerslocation.Prevelent.Prevelent;

public class HomeMapActivity extends FragmentActivity implements OnMapReadyCallback {
    double latitude, longtitude;
    private GpsTracker gpsTracker;
    String location;
    public List<Address> addresses;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 19;
    private GoogleMap mMap;
    public static final int LOCATION_REQUEST = 999;
    private DatabaseReference dref;
    private String parantdbname="Users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void searchPlace(View view) {
        try {
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setCountry("BD")
                    .build();

            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .setFilter(typeFilter)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        gpsTracker = new GpsTracker(HomeMapActivity.this);
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            if(gpsTracker.canGetLocation()){
                latitude = gpsTracker.getLatitude();
                longtitude = gpsTracker.getLongitude();
                addresses = geocoder.getFromLocation(latitude, longtitude, 1);
                location = addresses.get(0).getAddressLine(0);

            }else{
                gpsTracker.showSettingsAlert();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        dref= FirebaseDatabase.getInstance().getReference();
        dref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(parantdbname).child("name").exists()){

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        LatLng bdbl = new LatLng(latitude, longtitude);
        mMap.addMarker(new MarkerOptions().position(bdbl)
                .title("Marker in BD").snippet("phone"));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bdbl, 14));


        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mMap.addMarker(new MarkerOptions().position(latLng));
            }
        });
        try {
            if (checkLocationPermission())
                mMap.setMyLocationEnabled(true);
        }catch (Exception e){
            e.getMessage();
        }


    }

    private boolean checkLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    permissions,
                    LOCATION_REQUEST
            );
            return false;
        }
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE &&
                resultCode == RESULT_OK) {
            Place place = PlaceAutocomplete.getPlace(this, data);
            String name = place.getName().toString();
            String address = place.getAddress().toString();
            LatLng latLng = place.getLatLng();

            mMap.addMarker(new MarkerOptions()
                    .position(latLng).title(name).snippet(address));

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
        }
    }
}
