package gub.foysal.employerslocation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import gub.foysal.employerslocation.Method.Users;
import gub.foysal.employerslocation.Prevelent.Prevelent;
import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {
    private EditText inputphonelogin,inputpasslogin;
    private ProgressDialog loadingbar;
    private DatabaseReference rootref;
    private String parantdbname="Users";
    private CheckBox checkBox;
    private TextView notadminpanel,adminpanel,forgotPass;
    private Button loginbutton;
    private String userId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        notadminpanel=findViewById(R.id.not_admin_panel_LinkId);
        loginbutton=findViewById(R.id.login_main_id);
        adminpanel=findViewById(R.id.admin_panel_LinkId);
        inputphonelogin=findViewById(R.id.phone_login_Id);
        inputpasslogin=findViewById(R.id.password_login_Id);
        forgotPass=findViewById(R.id.forgot_passtId);
        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,ResetPasswordActivity.class);
                intent.putExtra("check","login");
                startActivity(intent);
            }
        });
        loadingbar=new ProgressDialog(this);
        checkBox=findViewById(R.id.checkbox_materialId);
        Paper.init(this);
        userId=getIntent().getStringExtra("prk");
    }

    public void LoginClickToHomepage(View view) {
        String phone=inputphonelogin.getText().toString();
        String password=inputpasslogin.getText().toString();
        if (TextUtils.isEmpty(phone)){
            Toast.makeText(getApplicationContext(),"Please Wite Your Phone Number.....",Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(password)){
            Toast.makeText(getApplicationContext(),"Please Wite Your Password.....",Toast.LENGTH_LONG).show();
        }
        else {
            loadingbar.setTitle("Login Account");
            loadingbar.setMessage("Please Wait!..while we are checking the credentials.");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();
            AllowAccessAccount(phone,password);
        }
    }

    private void AllowAccessAccount(final String phone, final String password) {
        if (checkBox.isChecked())
        {
            Paper.book().write(Prevelent.UserPhoneKey,phone);
            Paper.book().write(Prevelent.UserPasswordKey,password);
        }
        rootref= FirebaseDatabase.getInstance().getReference();
        rootref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(parantdbname).child(phone).exists()){
                    Users userdata=dataSnapshot.child(parantdbname).child(phone).getValue(Users.class);
                    if (userdata.getPhone().equals(phone))
                    {
                        if (userdata.getPassword().equals(password))
                        {
                            try {
                                if(parantdbname.equals("Admins")){
                                    Toast.makeText(LoginActivity.this, "Welcome Admin! you are Logged in Successfully..", Toast.LENGTH_SHORT).show();
                                    loadingbar.dismiss();
                                    Intent intent=new Intent(LoginActivity.this,AdminHomeActivity.class);
                                    intent.putExtra("prk",userId);
                                    startActivity(intent);
                                }
                                else if(parantdbname.equals("Users")){
                                    Toast.makeText(LoginActivity.this, "Login Successfully..", Toast.LENGTH_SHORT).show();
                                    loadingbar.dismiss();
                                    Intent intent=new Intent(LoginActivity.this,FingurprintActivity.class);
                                    Prevelent.currentOnlineusers = userdata;
                                    startActivity(intent);
                                }
                            }catch (Exception e){
                                e.getMessage();
                            }


                        }
                        else {
                            loadingbar.dismiss();
                            Toast.makeText(LoginActivity.this, "Password is incorrect..", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else {
                    Toast.makeText(LoginActivity.this, "Account with this,"+phone+" number do not exists", Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void IamNotAdminLink(View view) {
        loginbutton.setText("Login");
        adminpanel.setVisibility(View.VISIBLE);
        notadminpanel.setVisibility(View.INVISIBLE);
        parantdbname="Users";
    }

    public void IamAdminLink(View view) {
        loginbutton.setText("Login Admin");
        adminpanel.setVisibility(View.INVISIBLE);
        notadminpanel.setVisibility(View.VISIBLE);
        parantdbname="Admins";
    }
}
