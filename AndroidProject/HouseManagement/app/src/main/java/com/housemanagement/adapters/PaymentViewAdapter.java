package com.housemanagement.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.housemanagement.R;
import com.housemanagement.models.tables.Flat;
import com.housemanagement.models.tables.Payment;
import com.housemanagement.models.tables.Service;
import com.housemanagement.models.tables.SettingsService;

import java.util.ArrayList;
import java.util.Objects;

public class PaymentViewAdapter extends RecyclerView.Adapter<PaymentViewAdapter.MyViewHolder>{

    ArrayList<Payment> list;
    Context context;
    ArrayList<Service> services;
    ArrayList<Flat> flats;
    ArrayList<SettingsService> settingsServices;


    public PaymentViewAdapter(ArrayList<Payment> list, Context context, ArrayList<Service> services, ArrayList<Flat> flats, ArrayList<SettingsService> settingsServices) {
        this.list = list;
        this.context = context;
        this.settingsServices = settingsServices;
        this.services = services;
        this.flats = flats;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_payment, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Payment currentItem = list.get(position);
        Service currentService = services.stream().filter (it -> Objects.equals(it.getId(), currentItem.getIdService())).findFirst().get();
        holder.id = currentItem.getId();
        holder.flat_number.setText("Квартира №"+ flats.stream().filter (it -> Objects.equals(it.getId(), currentItem.getIdFlat())).findFirst().get().getFlatNumber());
        holder.period.setText(currentItem.getPeriod());
        holder.service.setText(currentService.getNameService());
        holder.amount.setText("Начислено: " + currentItem.getAmount() + " руб");
        holder.penalties.setText("Пени: " + currentItem.getPenalties() + " руб");
        holder.summa.setText("Всего к оплате: " + (currentItem.getAmount() + currentItem.getPenalties()) + "руб");
        switch (currentService.getNameService()){
            case "Холодное водоснабжение":
                holder.img.setImageResource(R.drawable.cold_water);
                break;
            case "Горячее водоснабжение":
                holder.img.setImageResource(R.drawable.hot_water);
                break;
            case "Электроэнергия":
                holder.img.setImageResource(R.drawable.electricity);
                break;
            case "Газ":
                holder.img.setImageResource(R.drawable.gas);
                break;
            case "Отопление":
                holder.img.setImageResource(R.drawable.warm);
                break;
            default:
                holder.img.setImageResource(R.drawable.other);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView flat_number, service, period, amount, penalties, summa;
        ImageView img;
        int id;
        LinearLayout linearLayout;
        ConstraintLayout constraintLayout;
        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            flat_number = (TextView)itemView.findViewById(R.id.numberFlat);
            img = (ImageView)itemView.findViewById(R.id.img);
            service = (TextView)itemView.findViewById(R.id.service);
            period = (TextView)itemView.findViewById(R.id.period);
            amount = (TextView)itemView.findViewById(R.id.amount);
            summa = (TextView)itemView.findViewById(R.id.summa);
            penalties = (TextView)itemView.findViewById(R.id.penalties);
            linearLayout = itemView.findViewById(R.id.linearLayout);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);
        }
    }
}
