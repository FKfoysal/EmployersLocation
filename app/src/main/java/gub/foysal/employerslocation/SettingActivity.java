package gub.foysal.employerslocation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import gub.foysal.employerslocation.Prevelent.Prevelent;

public class SettingActivity extends AppCompatActivity {
    private CircleImageView circleProfileImage;
    private TextView profileChangebtn,closeSettingtext,updateSettingtext;
    private EditText fullnameSettingtext,phoneSettingText,addressSettingText;
    private Uri imageUri;
    private String myUri="";
    private StorageTask uploadTask;
    private StorageReference profileStorageRef;
    private String checker="";
    private Button securityQues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        profileStorageRef= FirebaseStorage.getInstance().getReference().child("Profile Pictures");

        circleProfileImage=findViewById(R.id.image_setting_id);
        profileChangebtn=findViewById(R.id.change_profile_btn_id);
        closeSettingtext=findViewById(R.id.close_settings_id);
        updateSettingtext=findViewById(R.id.update_settings_id);
        fullnameSettingtext=findViewById(R.id.change_name_id);
        phoneSettingText=findViewById(R.id.change_phone_id);
        addressSettingText=findViewById(R.id.change_address_id);
        securityQues=findViewById(R.id.security_question_id);

        securityQues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SettingActivity.this,ResetPasswordActivity.class);
                intent.putExtra("check","setting");
                startActivity(intent);
            }
        });

        UpdateProfileInfo(circleProfileImage,fullnameSettingtext,phoneSettingText,addressSettingText);
        closeSettingtext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        updateSettingtext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checker.equals("clicked"))
                {
                    UserInfoUpdated();
                }
                else {
                    UpdateOnlyUserInfo();
                }
            }
        });

        profileChangebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checker="clicked";
                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(SettingActivity.this);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                circleProfileImage.setImageURI(imageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(this, "Error, Try Again..", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SettingActivity.this,SettingActivity.class));
            finish();
        }
    }

    private void UpdateOnlyUserInfo() {
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("Users");
        HashMap<String,Object> userMap=new HashMap<>();
        userMap.put("name",fullnameSettingtext.getText().toString());
        userMap.put("phone",phoneSettingText.getText().toString());
        userMap.put("address",addressSettingText.getText().toString());
        databaseReference.child(Prevelent.currentOnlineusers.getPhone()).updateChildren(userMap);

        startActivity(new Intent(SettingActivity.this,HomePageActivity.class));
        Toast.makeText(SettingActivity.this, "Profile Information Update Successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void UserInfoUpdated() {
        if (TextUtils.isEmpty(fullnameSettingtext.getText().toString()))
        {
            Toast.makeText(this, "Name is Mandatory", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(phoneSettingText.getText().toString()))
        {
            Toast.makeText(this, "Phone is Mandatory", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(addressSettingText.getText().toString()))
        {
            Toast.makeText(this, "Address is Mandatory", Toast.LENGTH_SHORT).show();
        }
        else if (checker.equals("clicked"))
        {
            ImageUploaded();
        }
    }

    private void ImageUploaded() {
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Please Waite! while we are updating your profile information");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (imageUri != null)
        {
            final StorageReference fileRef=profileStorageRef
                    .child(Prevelent.currentOnlineusers.getPhone()+".jpg");
            uploadTask=fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful())
                    {
                        throw  task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful())
                    {
                        Uri downloadUri=task.getResult();
                        myUri=downloadUri.toString();

                        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("Users");
                        HashMap<String,Object> userMap=new HashMap<>();
                        userMap.put("name",fullnameSettingtext.getText().toString());
                        userMap.put("phone",phoneSettingText.getText().toString());
                        userMap.put("address",addressSettingText.getText().toString());
                        userMap.put("image",myUri);
                        databaseReference.child(Prevelent.currentOnlineusers.getPhone()).updateChildren(userMap);

                        progressDialog.dismiss();
                        startActivity(new Intent(SettingActivity.this,HomePageActivity.class));
                        Toast.makeText(SettingActivity.this, "Profile Information Update Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(SettingActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else {
            Toast.makeText(this, "Image is not selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void UpdateProfileInfo(final CircleImageView circleProfileImage, final EditText fullnameSettingtext, final EditText phoneSettingText, final EditText addressSettingText)
    {
        DatabaseReference userRef= FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(Prevelent.currentOnlineusers.getPhone());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.child("image").exists())
                    {
                        String imageget=dataSnapshot.child("image").getValue().toString();
                        String nameget=dataSnapshot.child("name").getValue().toString();
                        String phoneget=dataSnapshot.child("phone").getValue().toString();
                        String addressget=dataSnapshot.child("address").getValue().toString();

                        Picasso.get().load(imageget).into(circleProfileImage);
                        fullnameSettingtext.setText(nameget);
                        phoneSettingText.setText(phoneget);
                        addressSettingText.setText(addressget);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
