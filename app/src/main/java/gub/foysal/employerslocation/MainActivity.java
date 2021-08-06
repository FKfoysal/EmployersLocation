package gub.foysal.employerslocation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import gub.foysal.employerslocation.Method.Users;
import gub.foysal.employerslocation.Prevelent.Prevelent;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    private String userphonePrimaryKey,userPasswordKey;
    private DatabaseReference rootref;
    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Paper.init(this);
        loadingbar=new ProgressDialog(this);
        try {
            userphonePrimaryKey= Paper.book().read(Prevelent.UserPhoneKey);
            userPasswordKey=Paper.book().read(Prevelent.UserPasswordKey);
            if (userphonePrimaryKey!=""&& userPasswordKey!=""){
                if (!TextUtils.isEmpty(userphonePrimaryKey)&& !TextUtils.isEmpty(userPasswordKey)){
                    Allowaccess(userphonePrimaryKey,userPasswordKey);

                    loadingbar.setTitle("Already Logged in");
                    loadingbar.setMessage("Please wite....");
                    loadingbar.setCanceledOnTouchOutside(false);
                    loadingbar.show();
                }
            }
        }catch (Exception e)
        {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void Allowaccess(final String phone, final String password) {
        rootref= FirebaseDatabase.getInstance().getReference();
        rootref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Users").child(phone).exists()){
                    Users userdata=dataSnapshot.child("Users").child(phone).getValue(Users.class);
                    if (userdata.getPhone().equals(phone))
                    {
                        if (userdata.getPassword().equals(password))
                        {
                            Toast.makeText(MainActivity.this, "Login Successfully..", Toast.LENGTH_SHORT).show();
                            loadingbar.dismiss();
                            Intent intent=new Intent(MainActivity.this,HomePageActivity.class);
                            Prevelent.currentOnlineusers=userdata;
                            startActivity(intent);
                        }
                        else {
                            loadingbar.dismiss();
                            Toast.makeText(MainActivity.this, "Password is incorrect..", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else {
                    Toast.makeText(MainActivity.this, "Account with this,"+phone+" number do not exists", Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void RegistrationClick(View view) {
        Intent intent=new Intent(this,RegistrationActivity.class);
        startActivity(intent);
    }

    public void LoginClick(View view) {
        Intent intent=new Intent(this,LoginActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(checkLocationPermission()){
        }
    }

    private boolean checkLocationPermission(){
        if(ActivityCompat
                .checkSelfPermission(
                        this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    555
            );
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 555
                && grantResults[0] == PackageManager.PERMISSION_GRANTED){
        }else{
            Toast.makeText(
                    this,
                    "Permission denied",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
