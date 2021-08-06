package gub.foysal.employerslocation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import gub.foysal.employerslocation.Method.GpsTracker;

public class RegistrationActivity extends AppCompatActivity {

    EditText name,phone,password;
    private ProgressDialog loadingbar;
    private DatabaseReference rootref;
    double latitude, longtitude;
    private GpsTracker gpsTracker;
    private String location, commentTextDoc, currentDateSave, currentTimeSave;
    public String regRandomKey;
    public List<Address> addresses;
    private FusedLocationProviderClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        name=findViewById(R.id.user_name_reg_id);
        phone=findViewById(R.id.phone_reg_Id);
        password=findViewById(R.id.pass_reg_Id);
        loadingbar=new ProgressDialog(this);
    }

    public void RegistrationCreateAccount(View view) {
        String inputName=name.getText().toString();
        String inputPhone=phone.getText().toString();
        String inputPassword=password.getText().toString();
        if (TextUtils.isEmpty(inputName)){
            Toast.makeText(getApplicationContext(),"Please Wite Your Name.....",Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(inputPhone)){
            Toast.makeText(getApplicationContext(),"Please Wite Your Phone Number.....",Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(inputPassword)){
            Toast.makeText(getApplicationContext(),"Please Wite Your Password.....",Toast.LENGTH_LONG).show();
        }
        else {
            loadingbar.setTitle("Create Account");
            loadingbar.setMessage("Please Wite!..while we are checking the credentials.");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();

            ValidtoPhoneNumber(inputName,inputPhone,inputPassword);
        }
    }

    private void ValidtoPhoneNumber(final String inputName, final String inputPhone, final String inputPassword) {

        RequestPermission();
        client= LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(RegistrationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            return;
        }
        client.getLastLocation().addOnSuccessListener(RegistrationActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location!=null) {
                    longtitude = location.getLongitude();
                    latitude = location.getLatitude();
                }
            }
        });


        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("MMM dd, yyyy");
        currentDateSave=currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a");
        currentTimeSave=currentTime.format(calendar.getTime());
        regRandomKey=currentDateSave+currentTimeSave;

        rootref= FirebaseDatabase.getInstance().getReference();
        rootref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child("Users").child(inputPhone).exists()){
                    HashMap<String,Object> userdatamap=new HashMap<>();
                    userdatamap.put("prk",regRandomKey);
                    userdatamap.put("phone",inputPhone);
                    userdatamap.put("password",inputPassword);
                    userdatamap.put("name",inputName);
                    //userdatamap.put("address",location);
                    userdatamap.put("lat",latitude);
                    userdatamap.put("lon",longtitude);
                    rootref.child("Users").child(inputPhone).updateChildren(userdatamap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(RegistrationActivity.this, "Congratulation! your account has been created", Toast.LENGTH_SHORT).show();
                                        loadingbar.dismiss();
                                        Intent intent=new Intent(RegistrationActivity.this,LoginActivity.class);
                                        intent.putExtra("prk",regRandomKey);
                                        startActivity(intent);
                                    }
                                    else {
                                        loadingbar.dismiss();
                                        Toast.makeText(RegistrationActivity.this, "Netword Error! please try again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else {
                    Toast.makeText(getApplicationContext(),"This"+inputPhone+" already exists.",Toast.LENGTH_LONG).show();
                    loadingbar.dismiss();
                    Toast.makeText(RegistrationActivity.this, "Please try again using another phone number", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(RegistrationActivity.this,MainActivity.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void RequestPermission(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
    }
}
