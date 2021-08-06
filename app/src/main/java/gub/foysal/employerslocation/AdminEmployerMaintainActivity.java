package gub.foysal.employerslocation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import gub.foysal.employerslocation.Method.Users;
import gub.foysal.employerslocation.Prevelent.Prevelent;

public class AdminEmployerMaintainActivity extends AppCompatActivity {
    private TextView adminViewName,adminViewPhone,adminViewAddress;
    private Button  deleteEmployee,locationTrack;
    private ImageView adminViewImage;
    private String employID = "";
    private DatabaseReference employeeRef;
    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_employer_maintain);
        deleteEmployee=findViewById(R.id.delete_this_employ_id);
        locationTrack=findViewById(R.id.Location_track_employ_id);

        adminViewName=findViewById(R.id.admin_edit_employ_name_id);
        adminViewPhone=findViewById(R.id.admin_edit_employ_phone_id);
        adminViewAddress=findViewById(R.id.admin_edit_employ_address_id);
        adminViewImage=findViewById(R.id.admin_edit_employ_image_id);
        loadingbar=new ProgressDialog(this);

        employID=getIntent().getStringExtra("prk");
        employeeRef= FirebaseDatabase.getInstance().getReference().child("Users").child(employID);
        DisplayEmployeeInfo();
        deleteEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteThisEmployee();
            }
        });
        locationTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginCurrentUsers();
            }
        });
    }

    private void LoginCurrentUsers() {
        loadingbar.setTitle("Login Account");
        loadingbar.setMessage("Please Wait!..while we are checking the credentials.");
        loadingbar.setCanceledOnTouchOutside(false);
        loadingbar.show();

        employeeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Users userdata=snapshot.getValue(Users.class);

                        Toast.makeText(AdminEmployerMaintainActivity.this, "Successfully login", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(AdminEmployerMaintainActivity.this,HomePageActivity.class);
                        intent.putExtra("Admin","Admin");
                        Prevelent.currentOnlineusers = userdata;
                        startActivity(intent);
                        loadingbar.dismiss();

                }
                else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void DeleteThisEmployee() {
        employeeRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(AdminEmployerMaintainActivity.this, "This Employee is deleted Successfully", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(AdminEmployerMaintainActivity.this,AdminHomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void DisplayEmployeeInfo() {
        employeeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String name=snapshot.child("name").getValue().toString();
                    String phone=snapshot.child("phone").getValue().toString();
                    String address=snapshot.child("address").getValue().toString();
                    String image=snapshot.child("image").getValue().toString();

                    adminViewName.setText(name);
                    adminViewPhone.setText(phone);
                    adminViewAddress.setText(address);
                    Picasso.get().load(image).into(adminViewImage);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
