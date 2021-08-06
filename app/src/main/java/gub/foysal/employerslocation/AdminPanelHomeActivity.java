package gub.foysal.employerslocation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import gub.foysal.employerslocation.Method.Users;
import gub.foysal.employerslocation.Prevelent.Prevelent;

public class AdminPanelHomeActivity extends AppCompatActivity {
    Users users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel_home);
    }

    public void EmployersListClicked(View view) {
        Intent intent=new Intent(AdminPanelHomeActivity.this,AdminHomeActivity.class);
        startActivity(intent);
    }

    public void FingerprintMaintainClicked(View view) {

        Intent intent=new Intent(AdminPanelHomeActivity.this,AdminMaintainFingerprintActivity.class);
        //intent.putExtra("prk",users.getPhone());
        startActivity(intent);
    }
}
