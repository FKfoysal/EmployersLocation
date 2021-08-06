package gub.foysal.employerslocation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import gub.foysal.employerslocation.Prevelent.Prevelent;

public class ResetPasswordActivity extends AppCompatActivity {
    private String check="";
    private TextView questionMasg,pageTitle;
    private EditText question1,question2,phone;
    private Button verify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        check=getIntent().getStringExtra("check");

        questionMasg=findViewById(R.id.security_mass);
        phone=findViewById(R.id.find_phone_number_Id);
        question1=findViewById(R.id.question1_id);
        question2=findViewById(R.id.question2_id);
        pageTitle=findViewById(R.id.page_title_id);
        verify=findViewById(R.id.varify_btn_id);
    }
    @Override
    protected void onStart() {
        super.onStart();

        phone.setVisibility(View.GONE);
        if (check.equals("setting"))
        {
            pageTitle.setText("Set Question");
            questionMasg.setText("Answer the following Security Question?");
            verify.setText("Set");

            DisplayPreviousQuestion();

            verify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String ans1= question1.getText().toString();
                    String ans2= question2.getText().toString();
                    if (question1.equals("")&&question2.equals(""))
                    {
                        Toast.makeText(ResetPasswordActivity.this, "Please answer both question..", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        DatabaseReference ref= FirebaseDatabase.getInstance().getReference()
                                .child("Users")
                                .child(Prevelent.currentOnlineusers.getPhone());
                        HashMap<String,Object> secQues=new HashMap<>();
                        secQues.put("answer1",ans1);
                        secQues.put("answer2",ans2);
                        ref.child("Security Question").updateChildren(secQues).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(ResetPasswordActivity.this, "Security Question added successfully..", Toast.LENGTH_SHORT).show();
                                    Intent intent=new Intent(ResetPasswordActivity.this,SettingActivity.class);
                                    startActivity(intent);
                                }
                            }
                        });
                    }
                }
            });
        }
        else if (check.equals("login"))
        {
            phone.setVisibility(View.VISIBLE);
            verify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    VerifyUsers();
                }
            });
        }
    }

    private void VerifyUsers() {
        final String verfphone= phone.getText().toString();
        final String answ1= question1.getText().toString();
        final String answ2= question2.getText().toString();
        if (!verfphone.equals("")&&!answ1.equals("")&&!answ2.equals(""))
        {
            final DatabaseReference ref= FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(verfphone);
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        String dphone=dataSnapshot.child("phone").getValue().toString();
                        if (verfphone.equals(dphone))
                        {
                            if (dataSnapshot.hasChild("Security Question"))
                            {
                                String ans1=dataSnapshot.child("Security Question").child("answer1").getValue().toString();
                                String ans2=dataSnapshot.child("Security Question").child("answer2").getValue().toString();

                                if (!ans1.equals(answ1)){
                                    Toast.makeText(ResetPasswordActivity.this, "Your 1st Answer is wrong.", Toast.LENGTH_SHORT).show();
                                }
                                else if (!ans2.equals(answ2)){
                                    Toast.makeText(ResetPasswordActivity.this, "Your 2nd Answer is wrong.", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    AlertDialog.Builder builder=new AlertDialog.Builder(ResetPasswordActivity.this);
                                    builder.setTitle("New Password");
                                    final EditText newPass=new EditText(ResetPasswordActivity.this);
                                    newPass.setHint("Write New Password Here....");
                                    builder.setView(newPass);
                                    builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (!newPass.getText().toString().equals(""))
                                            {
                                                ref.child("password")
                                                        .setValue(newPass.getText().toString())
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){
                                                                    Toast.makeText(ResetPasswordActivity.this, "Password Changed Successfully", Toast.LENGTH_SHORT).show();
                                                                    Intent intent=new Intent(ResetPasswordActivity.this,LoginActivity.class);
                                                                    startActivity(intent);
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                                    builder.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.cancel();
                                        }
                                    });
                                    builder.show();
                                }
                            }

                        }
                        else {
                            Toast.makeText(ResetPasswordActivity.this, "phone number are not exists!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(ResetPasswordActivity.this, "phone number are not exists!", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else {
            Toast.makeText(this, "Please complete the forms", Toast.LENGTH_SHORT).show();
        }


    }

    private void DisplayPreviousQuestion(){
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(Prevelent.currentOnlineusers.getPhone());

        ref.child("Security Question").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    String ans1=dataSnapshot.child("answer1").getValue().toString();
                    String ans2=dataSnapshot.child("answer2").getValue().toString();

                    question1.setText(ans1);
                    question2.setText(ans2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
