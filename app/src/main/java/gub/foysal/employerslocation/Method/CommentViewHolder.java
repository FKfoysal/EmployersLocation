package gub.foysal.employerslocation.Method;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import gub.foysal.employerslocation.R;

public class CommentViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView textComment,textAddress,textTime;
    private itemClickLiseaner listener;

    public CommentViewHolder(@NonNull View itemView) {
        super(itemView);
        textComment=itemView.findViewById(R.id.sample_comment_id);
        textAddress=itemView.findViewById(R.id.sample_comment_address_id);
        textTime=itemView.findViewById(R.id.sample_comment_time_id);
    }

    public void setListener(itemClickLiseaner listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        listener.onClick(view, getAdapterPosition(),false);
    }
}
