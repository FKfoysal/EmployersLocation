package gub.foysal.employerslocation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.PowerManager;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import gub.foysal.employerslocation.Method.Comments;
import gub.foysal.employerslocation.Method.GpsTracker;
import gub.foysal.employerslocation.Method.locationdb;
import gub.foysal.employerslocation.Prevelent.Prevelent;
import io.paperdb.Paper;

public class HomePageActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,OnMapReadyCallback, SeekBar.OnSeekBarChangeListener {

    private TextView userProName;
    private CircleImageView userProImage;
    private String type="";

    double latitude, longtitude;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 19;
    private GoogleMap mMap;
    public static final int LOCATION_REQUEST = 999;
    CheckBox checkBox;
    SeekBar seekRed,seekGreen,seekBlue;
    Button draw,clear;
    LinearLayout linearLayout;
    Polygon polygon=null;
    List<LatLng> latLngList=new ArrayList<>();
    List<Marker> markerList=new ArrayList<>();
    int red =0,green=0,blue=0;
    private DatabaseReference mapRef,mapgetRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        checkBox=findViewById(R.id.check_box_id);
        seekRed=findViewById(R.id.seekbar_red_id);
        seekGreen=findViewById(R.id.seekbar_green_id);
        seekBlue=findViewById(R.id.seekbar_blue_id);

        draw=findViewById(R.id.draw_poly_id);
        clear=findViewById(R.id.clear_poly_id);
        linearLayout=findViewById(R.id.linearHomeId);
        Paper.init(this);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (polygon == null) return;

                    polygon.setFillColor(Color.rgb(red, green, blue));
                }else {
                    polygon.setFillColor(Color.TRANSPARENT);
                }
            }
        });
         mapRef=FirebaseDatabase.getInstance().getReference()
                .child("GoogleMap")
                .child(Prevelent.currentOnlineusers.getPhone());

        draw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (polygon!=null) polygon.remove();
//                mapgetRef.keepSynced(true);
//                mapgetRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        HashMap<String, Double> data = (HashMap<String, Double>) snapshot.getValue();
////                        for (DataSnapshot childDataSnapshot : snapshot.getChildren()) {
////
////                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
                PolygonOptions polygonOptions=new PolygonOptions().addAll(latLngList)
                        .clickable(true);
                polygon=mMap.addPolygon(polygonOptions);
                polygon.setStrokeColor(Color.rgb(red, green, blue));
                if (checkBox.isChecked()) {
                    polygon.setFillColor(Color.rgb(red, green, blue));
                }


            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (polygon!=null) polygon.remove();

                for (Marker marker:markerList) marker.remove();

                latLngList.clear();
                markerList.clear();
                checkBox.setChecked(false);
                seekRed.setProgress(0);
                seekGreen.setProgress(0);
                seekBlue.setProgress(0);
            }
        });
        seekRed.setOnSeekBarChangeListener(this);
        seekGreen.setOnSeekBarChangeListener(this);
        seekBlue.setOnSeekBarChangeListener(this);

        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        if (bundle!=null) {
            type = getIntent().getExtras().get("Admin").toString();
        }
        if (type.equals("Admin"))
        {
            linearLayout.setVisibility(View.VISIBLE);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!type.equals("Admin")) {
                    Intent intent = new Intent(HomePageActivity.this, CommentActivity.class);
                    startActivity(intent);
                }

            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        View headerView=navigationView.getHeaderView(0);
        userProName= (TextView) headerView.findViewById(R.id.profilename_show_id);
        userProImage=headerView.findViewById(R.id.profile_image_id);
        try {
            if (!type.equals("Admin")) {
                userProName.setText(Prevelent.currentOnlineusers.getName().trim());
                Picasso.get().load(Prevelent.currentOnlineusers.getImage()).placeholder(R.drawable.profile).into(userProImage);
            }
        }catch (Exception e){
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


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

    @Override
    public void onBackPressed() {
        if (!type.equals("Admin")) {
            AlertDialog.Builder alertdialog = new AlertDialog.Builder(HomePageActivity.this);

            alertdialog.setTitle("are you want to exit??");
            alertdialog.setIcon(R.drawable.ic_help_outline_black_24dp);
            alertdialog.setCancelable(true);

            alertdialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            alertdialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alertDialog = alertdialog.create();
            alertDialog.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_map) {
            HomeMapActivity homeMapActivity=new HomeMapActivity();
            FragmentManager manager=getSupportFragmentManager();
            //manager.beginTransaction().replace(R.id.main_id, onAttachFragment(homeMapActivity);
        } else if (id == R.id.nav_comment) {
            if (!type.equals("Admin")) {
                Intent intent = new Intent(HomePageActivity.this, CommentActivity.class);
                startActivity(intent);
            }

        } else if (id == R.id.nav_fingerprint)
        {

        }

        else if (id == R.id.nav_setting_id) {
            if (!type.equals("Admin")) {
                Intent intent = new Intent(HomePageActivity.this, SettingActivity.class);
                startActivity(intent);
            }

        } else if (id == R.id.nav_logout) {
            if (!type.equals("Admin")) {
                Paper.book().destroy();
                Intent intent = new Intent(HomePageActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
            else {
                Intent intent = new Intent(HomePageActivity.this, AdminHomeActivity.class);
                startActivity(intent);
                finish();
            }
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        DatabaseReference regRef=FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(Prevelent.currentOnlineusers.getPhone());
        regRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    HashMap<String, Double> data = (HashMap<String, Double>) snapshot.getValue();
                    latitude = data.get("lat");
                    longtitude = data.get("lon");
                    LatLng bdbl = new LatLng(latitude, longtitude);
                    mMap.addMarker(new MarkerOptions().position(bdbl)
                            .title("Marker in BD").snippet("phone"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bdbl, 12));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
               // checkBounds();
            }
        });
        if (type.equals("Admin")) {
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    MarkerOptions markerOptions = new MarkerOptions().position(latLng);
                    Marker marker = mMap.addMarker(markerOptions);
                    latLngList.add(latLng);
                    markerList.add(marker);
                    HashMap<String, Object> polyMap = new HashMap<>();
                    //polyMap.put("markerlist", markerList);
                    polyMap.put("latlonlist", latLngList);
                    mapRef.updateChildren(polyMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });
                }
            });
        }

//        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
//            @Override
//            public void onMapLongClick(LatLng latLng) {
//                mMap.addMarker(new MarkerOptions().position(latLng));
//            }
//        });


        try {
            if (checkLocationPermission())
                mMap.setMyLocationEnabled(true);
        }catch (Exception e){
            e.getMessage();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()){
            case R.id.seekbar_red_id:
                red=progress;
                break;
            case R.id.seekbar_green_id:
                green=progress;
                break;
            case R.id.seekbar_blue_id:
                blue=progress;
                break;
        }
        if (polygon!=null) {
            polygon.setStrokeColor(Color.rgb(red, green, blue));
            if (checkBox.isChecked()) {
                polygon.setFillColor(Color.rgb(red, green, blue));
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
