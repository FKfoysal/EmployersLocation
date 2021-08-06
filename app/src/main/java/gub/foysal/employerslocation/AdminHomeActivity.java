package gub.foysal.employerslocation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

import gub.foysal.employerslocation.Method.AdminViewHolder;
import gub.foysal.employerslocation.Method.CommentViewHolder;
import gub.foysal.employerslocation.Method.Comments;
import gub.foysal.employerslocation.Method.GpsTracker;
import gub.foysal.employerslocation.Method.Users;
import gub.foysal.employerslocation.Prevelent.Prevelent;

public class AdminHomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference adminRef,commentdbRef;
    private String type="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        adminRef = FirebaseDatabase.getInstance().getReference()
                .child("Users");

        recyclerView = findViewById(R.id.employ_list_recyler_view_id);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Users> options=new FirebaseRecyclerOptions.Builder<Users>()
                .setQuery(adminRef, Users.class)
                .build();

        FirebaseRecyclerAdapter<Users, AdminViewHolder> adapter
                = new FirebaseRecyclerAdapter<Users, AdminViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AdminViewHolder holder, int i, @NonNull final Users users) {
                holder.textAdminName.setText("Name: "+users.getName());
                holder.textAdminPhone.setText("Phone: "+users.getPhone());
                holder.textAdminAddress.setText("Address: "+users.getAddress());
                Picasso.get().load(users.getImage()).into(holder.imageAdmin);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(AdminHomeActivity.this,AdminEmployerMaintainActivity.class);
                        intent.putExtra("prk",users.getPhone());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public AdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.admin_sample_view,parent,false);
                AdminViewHolder holder=new AdminViewHolder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public void NotificationsClicked(View view) {

    }

    public void CloseClicked(View view) {
        Intent intent = new Intent(AdminHomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
