package gub.foysal.employerslocation.Method;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import gub.foysal.employerslocation.R;

public class AdminViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView textAdminName,textAdminPhone,textAdminAddress;
    public ImageView imageAdmin;
    private itemClickLiseaner listener;

    public AdminViewHolder(@NonNull View itemView) {
        super(itemView);
        textAdminName=itemView.findViewById(R.id.sample_employ_list_name_id);
        textAdminPhone=itemView.findViewById(R.id.sample_employ_list_phone_id);
        textAdminAddress=itemView.findViewById(R.id.sample_employ_list_address_id);
        imageAdmin=itemView.findViewById(R.id.sample_employ_list_image_id);
    }

    public void setListener(itemClickLiseaner listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        listener.onClick(view, getAdapterPosition(),false);
    }
}
