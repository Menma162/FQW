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
import androidx.recyclerview.widget.RecyclerView;

import com.housemanagement.R;
import com.housemanagement.controllers.AccountFragmentController;
import com.housemanagement.models.tables.Flat;

import java.util.ArrayList;

public class FlatViewAdapter extends RecyclerView.Adapter<FlatViewAdapter.MyViewHolder>{

    ArrayList<Flat> list;
    Context context;
    AccountFragmentController controller;


    public FlatViewAdapter(ArrayList<Flat> list, Context context, AccountFragmentController controller) {
        this.list = list;
        this.context = context;
        this.controller = controller;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_flat, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Flat currentItem = list.get(position);
        holder.id = currentItem.getId();
        holder.numberFlat.setText("Квартира №"+ currentItem.getFlatNumber());
        holder.personalAccountFlat.setText("Лицевой счет: " + currentItem.getPersonalAccount());
        holder.totalAreaFlat.setText("Общая площадь: " + currentItem.getTotalArea() + " м²");
        holder.usableAreaFlat.setText("Полезная площадь: " + currentItem.getUsableArea() + " м²");
        holder.numberOfRegisteredResidentsFlat.setText("Зарегистрированных жителей: " + currentItem.getNumberOfRegisteredResidents());
        holder.numberOfOwnersFlat.setText("Владельцев жилья: " + currentItem.getNumberOfOwners());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView numberFlat, personalAccountFlat, totalAreaFlat, usableAreaFlat, numberOfRegisteredResidentsFlat, numberOfOwnersFlat;
        int id;
        LinearLayout linearLayout;
        ConstraintLayout constraintLayout;
        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            numberFlat = (TextView)itemView.findViewById(R.id.numberFlat);
            personalAccountFlat = (TextView)itemView.findViewById(R.id.personalAccountFlat);
            totalAreaFlat = (TextView)itemView.findViewById(R.id.totalAreaFlat);
            usableAreaFlat = (TextView)itemView.findViewById(R.id.usableAreaFlat);
            numberOfRegisteredResidentsFlat = (TextView)itemView.findViewById(R.id.numberOfRegisteredResidentsFlat);
            numberOfOwnersFlat = (TextView)itemView.findViewById(R.id.numberOfOwnersFlat);
            linearLayout = itemView.findViewById(R.id.linearLayout);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);
        }
    }
}
