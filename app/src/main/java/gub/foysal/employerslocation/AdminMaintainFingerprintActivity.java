package gub.foysal.employerslocation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import gub.foysal.employerslocation.Method.FingerViewHolder;
import gub.foysal.employerslocation.Method.Fingerprint;
import gub.foysal.employerslocation.Prevelent.Prevelent;

public class AdminMaintainFingerprintActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference fingerRef;
    private String fingerId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maintain_fingerprint);

        fingerRef = FirebaseDatabase.getInstance().getReference()
                .child("Fingerprint")
                .child(Prevelent.currentOnlineusers.getPhone());

        recyclerView = findViewById(R.id.finger_list_recyler_view_id);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Fingerprint> options=new FirebaseRecyclerOptions.Builder<Fingerprint>()
                .setQuery(fingerRef,Fingerprint.class)
                .build();
        FirebaseRecyclerAdapter<Fingerprint, FingerViewHolder> adapter
                = new FirebaseRecyclerAdapter<Fingerprint, FingerViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FingerViewHolder holder, int i, @NonNull final Fingerprint finger) {
                holder.textAdminName.setText("Name: "+finger.getName());
                holder.textAdminPhone.setText("Phone: "+finger.getPhone());
                holder.textAdminDate.setText("Date And Time : "+finger.getPrk());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence options[]=new CharSequence[]
                                {
                                        "Delete"
                                };
                        AlertDialog.Builder builder1=new AlertDialog.Builder(AdminMaintainFingerprintActivity.this);
                        builder1.setTitle("Finger Options: ");
                        builder1.setItems(options,new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0)
                                {
                                    fingerRef.child(Prevelent.currentOnlineusers.getPhone())
                                            .child(finger.getPrk())
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(AdminMaintainFingerprintActivity.this, "Item Delete Successfully", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(AdminMaintainFingerprintActivity.this, AdminMaintainFingerprintActivity.class);
                                                    startActivity(intent);
                                                }
                                            });
                                }
                            }
                        });
                        builder1.show();
                    }
                });
            }

            @NonNull
            @Override
            public FingerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.finger_samble_view,parent,false);
                FingerViewHolder holder=new FingerViewHolder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}
