package com.housemanagement.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.housemanagement.R;
import com.housemanagement.controllers.NewsFragmentController;
import com.housemanagement.models.other.New;

import java.util.ArrayList;
import java.util.Objects;

public class NewViewAdapter extends RecyclerView.Adapter<NewViewAdapter.MyViewHolder>{

    ArrayList<New> list;
    Context context;


    public NewViewAdapter(ArrayList<New> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_new, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        New currentItem = list.get(position);
        holder.id = currentItem.getId();
        holder.nameNew.setText(currentItem.getName());
        holder.descriptionNew.setText(currentItem.getDescription());
        if(Objects.equals(currentItem.getName(), "Напоминание о показаниях"))
            holder.constraintLayout.setBackground(ContextCompat.getDrawable(context, R.color.orangeNew));
        else if(Objects.equals(currentItem.getName(), "Напоминание об оплате"))
            holder.constraintLayout.setBackground(ContextCompat.getDrawable(context, R.color.redNew));
        else holder.constraintLayout.setBackground(ContextCompat.getDrawable(context, R.color.blueNew));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView nameNew, descriptionNew;
        int id;
        LinearLayout linearLayout;
        ConstraintLayout constraintLayout;
        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            nameNew = (TextView)itemView.findViewById(R.id.nameNew);
            descriptionNew = (TextView)itemView.findViewById(R.id.descriptionNew);
            linearLayout = itemView.findViewById(R.id.linearLayout);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);
        }
    }
}
