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
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.housemanagement.R;
import com.housemanagement.controllers.IndicationsFragmentController;
import com.housemanagement.models.tables.Counter;
import com.housemanagement.models.tables.Flat;
import com.housemanagement.models.tables.Service;
import com.housemanagement.models.tables.SettingsService;
import com.housemanagement.otherclasses.GetDataFromServer;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

public class CounterViewAdapter extends RecyclerView.Adapter<CounterViewAdapter.MyViewHolder>{

    ArrayList<Counter> list;
    Context context;
    IndicationsFragmentController controller;
    ArrayList<Service> services;
    ArrayList<Flat> flats;
    ArrayList<SettingsService> settingsServices;

    LocalDate dateNow;


    public CounterViewAdapter(ArrayList<Counter> list, Context context, IndicationsFragmentController controller, ArrayList<Flat> flats, ArrayList<Service> services, ArrayList<SettingsService> settingsServices, LocalDate dateNow) {
        this.list = list;
        this.context = context;
        this.controller = controller;
        this.settingsServices = settingsServices;
        this.services = services;
        this.flats = flats;
        this.dateNow = dateNow;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_counter, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Counter currentItem = list.get(position);
        Service currentService = services.stream().filter (it -> Objects.equals(it.getId(), currentItem.getIdService())).findFirst().get();
        SettingsService settingsService =  settingsServices.stream().filter(it -> Objects.equals(it.getIdService(), currentService.getId())).findFirst().get();
        holder.id = currentItem.getId();
        holder.flat_number.setText("Квартира №"+ flats.stream().filter (it -> Objects.equals(it.getId(), currentItem.getIdFlat())).findFirst().get().getFlatNumber());
        holder.type.setText(currentService.getNameCounter());//тут вывести тип
        holder.number.setText("Счетчик №" + currentItem.getNumber());
        holder.dateOfTransfer.setText("Передать показания с " + settingsService.getStartDateTransfer() + " по " + settingsService.getEndDateTransfer() + " число");
        switch (currentService.getNameCounter()){
            case "Счетчик холодной воды":
                holder.img.setImageResource(R.drawable.cold_water);
                break;
            case "Счетчик горячей воды":
                holder.img.setImageResource(R.drawable.hot_water);
                break;
            case "Счетчик электроэнергии":
                holder.img.setImageResource(R.drawable.electricity);
                break;
            case "Счетчик газа":
                holder.img.setImageResource(R.drawable.gas);
                break;
            case "Счетчик отопления":
                holder.img.setImageResource(R.drawable.warm);
                break;
            default:
                break;
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        if(!(dateNow.getDayOfMonth() >= settingsService.getStartDateTransfer() && dateNow.getDayOfMonth() <= settingsService.getEndDateTransfer())) holder.button.setVisibility(View.GONE);
        holder.dateOfNextVerification.setText("Дата поверки: " + currentItem.getDateNextVerification().format(dtf));
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controller.openIndication(list.get(position).getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView flat_number, type, number, dateOfNextVerification, dateOfTransfer;
        ImageView img;
        AppCompatButton button;
        int id;
        LinearLayout linearLayout;
        ConstraintLayout constraintLayout;
        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            flat_number = (TextView)itemView.findViewById(R.id.numberFlat);
            img = (ImageView)itemView.findViewById(R.id.img);
            type = (TextView)itemView.findViewById(R.id.typeCounter);
            number = (TextView)itemView.findViewById(R.id.numberCounter);
            dateOfNextVerification = (TextView)itemView.findViewById(R.id.dateOfNextVerification);
            dateOfTransfer = (TextView)itemView.findViewById(R.id.dateOfTransfer);
            button = (AppCompatButton)itemView.findViewById(R.id.btn);
            linearLayout = itemView.findViewById(R.id.linearLayout);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);
        }
    }
}
