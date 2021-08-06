package gub.foysal.employerslocation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import gub.foysal.employerslocation.Method.CommentViewHolder;
import gub.foysal.employerslocation.Method.Comments;
import gub.foysal.employerslocation.Method.GpsTracker;
import gub.foysal.employerslocation.Prevelent.Prevelent;

public class CommentActivity extends AppCompatActivity {

    private EditText commentText;
    private Button commentBtn;
    double latitude, longtitude;
    private GpsTracker gpsTracker;
    private String location, commentTextDoc, currentDateSave, currentTimeSave;
    public String commentRandomKey;
    public List<Address> addresses;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference commentRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        commentText = findViewById(R.id.comment_id);
        commentBtn = findViewById(R.id.comment_btn_id);

        commentRef = FirebaseDatabase.getInstance().getReference()
                .child("Comments")
                .child(Prevelent.currentOnlineusers.getPhone());
        recyclerView = findViewById(R.id.recyler_view_id);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValiditiCommentInfo();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Comments> options=new FirebaseRecyclerOptions.Builder<Comments>()
                .setQuery(commentRef, Comments.class)
                .build();

        FirebaseRecyclerAdapter<Comments, CommentViewHolder> adapter
                = new FirebaseRecyclerAdapter<Comments, CommentViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CommentViewHolder holder, int positon, @NonNull final Comments comments) {
                holder.textComment.setText(""+comments.getComment());
                holder.textAddress.setText("Name: "+comments.getName());
                holder.textTime.setText("Time: "+comments.getPrk());
            }

            @NonNull
            @Override
            public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.comment_sample_view,parent,false);
                CommentViewHolder holder=new CommentViewHolder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void ValiditiCommentInfo() {
        commentTextDoc = commentText.getText().toString();
        if (TextUtils.isEmpty(commentTextDoc)) {
            Toast.makeText(this, "Comment Box is Emply", Toast.LENGTH_SHORT).show();
        } else {
            UpdateOnlyCommentsInfo();
        }
    }

    private void UpdateOnlyCommentsInfo() {
        gpsTracker = new GpsTracker(CommentActivity.this);
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

        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("MMM dd, yyyy");
        currentDateSave=currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a");
        currentTimeSave=currentTime.format(calendar.getTime());

        commentRandomKey=currentDateSave+currentTimeSave;
        final DatabaseReference commentRef=FirebaseDatabase.getInstance().getReference()
                .child("Comments")
                .child(Prevelent.currentOnlineusers.getPhone());

        HashMap<String,Object> commentMap=new HashMap<>();
        commentMap.put("prk",commentRandomKey);
        commentMap.put("comment",commentTextDoc);
        commentMap.put("date",currentDateSave);
        commentMap.put("time",currentTimeSave);
        commentMap.put("location",location);
        commentMap.put("name",Prevelent.currentOnlineusers.getName());

        commentRef.child(commentRandomKey).updateChildren(commentMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(CommentActivity.this, "Comment Successful", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
}
