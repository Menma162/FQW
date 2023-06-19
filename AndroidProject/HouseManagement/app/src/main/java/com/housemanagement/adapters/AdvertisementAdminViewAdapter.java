package com.housemanagement.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.housemanagement.R;
import com.housemanagement.controllers.AdvertisementsAdminFragmentController;
import com.housemanagement.models.tables.Advertisement;
import com.housemanagement.models.tables.House;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

public class AdvertisementAdminViewAdapter extends RecyclerView.Adapter<AdvertisementAdminViewAdapter.MyViewHolder>{

    ArrayList<Advertisement> list;
    Context context;
    AdvertisementsAdminFragmentController controller;
    ArrayList<House> houses = new ArrayList<>();


    public AdvertisementAdminViewAdapter(ArrayList<Advertisement> list, Context context, AdvertisementsAdminFragmentController controller, ArrayList<House> houses) {
        this.list = list;
        this.context = context;
        this.controller = controller;
        this.houses = houses;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_advertisement_admin, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        Advertisement currentItem = list.get(position);
        holder.id = currentItem.getId();
        holder.nameNew.setText("Объявление от " + currentItem.getDate().format(dtf));
        holder.nameHouse.setText("Дом - " + houses.stream().filter(it -> Objects.equals(it.getId(), currentItem.getIdHouse())).findFirst().get().getName());
        holder.descriptionNew.setText(currentItem.getDescription());
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controller.openAdvertisement(list.get(position).getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView nameNew, descriptionNew, nameHouse;
        AppCompatButton button;
        int id;
        LinearLayout linearLayout;
        ConstraintLayout constraintLayout;
        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            nameNew = (TextView)itemView.findViewById(R.id.nameNew);
            nameHouse = (TextView)itemView.findViewById(R.id.nameHouse);
            descriptionNew = (TextView)itemView.findViewById(R.id.descriptionNew);
            button = (AppCompatButton)itemView.findViewById(R.id.btn);
            linearLayout = itemView.findViewById(R.id.linearLayout);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);
        }
    }
}
