package com.housemanagement.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
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
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.google.gson.Gson;
import com.google.gson.JsonPrimitive;
import com.housemanagement.R;
import com.housemanagement.controllers.ComplaintsAdminFragmentController;
import com.housemanagement.controllers.ComplaintsFragmentController;
import com.housemanagement.db.Dao;
import com.housemanagement.db.MainDb;
import com.housemanagement.interfaces.ApiRequests;
import com.housemanagement.models.tables.Complaint;
import com.housemanagement.models.tables.Flat;
import com.housemanagement.models.tables.House;
import com.housemanagement.otherclasses.GetDataFromServer;
import com.housemanagement.otherclasses.RetrofitService;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComplaintViewAdapter extends RecyclerView.Adapter<ComplaintViewAdapter.MyViewHolder>{

    ArrayList<Complaint> list;
    Context context;
    ComplaintsFragmentController controllerOwner;
    ComplaintsAdminFragmentController controllerAdmin;

    ArrayList<Flat> flats = new ArrayList<>();
    ArrayList<House> houses = new ArrayList<>();


    public ComplaintViewAdapter(ArrayList<Complaint> list, Context context, ComplaintsFragmentController controllerOwner, ComplaintsAdminFragmentController controllerAdmin, ArrayList<Flat> flats, ArrayList<House> houses) {
        this.list = list;
        this.context = context;
        this.controllerOwner = controllerOwner;
        this.controllerAdmin = controllerAdmin;
        this.flats = flats;
        this.houses = houses;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_complaint, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        Complaint currentItem = list.get(position);
        Flat flat = flats.stream().filter(it -> Objects.equals(it.getId(), currentItem.getIdFlat())).findFirst().get();
        holder.id = currentItem.getId();
        holder.flatNumber.setText("Квартира №"+  flat.getFlatNumber());
        if(controllerOwner != null) holder.nameHouse.setVisibility(View.GONE);
        else{
            holder.nameHouse.setText("Дом - "+  houses.stream().filter(it -> Objects.equals(it.getId(), flat.getIdHouse())).findFirst().get().getName());
        }
        holder.dateComplaint.setText("Заявка от " + currentItem.getDate().format(dtf));
        holder.descriptionComplaint.setText(currentItem.getDescription());
        holder.statusComplaint.setText("Статус: " + currentItem.getStatus());

        GetDataFromServer.getStringPhoto(context, holder.id);

        String token = context.getSharedPreferences("HouseManagement", Context.MODE_PRIVATE)
                .getString("token", "");
        ApiRequests apiRequests = new RetrofitService().getRetrofit().create(ApiRequests.class);
        apiRequests.getPhoto("Bearer " + token, holder.id).enqueue(new Callback<JsonPrimitive>() {
            @Override
            public void onResponse(Call<JsonPrimitive> call, Response<JsonPrimitive> response) {
                try {
                    JsonPrimitive item = response.body();
                    if (item != null && response.isSuccessful()) {
                        String base64String =
                                new Gson().fromJson(item.getAsJsonPrimitive(), String.class);
                        if(base64String != null)
                        {
                            //String imageDataBytes = completeImageData.substring(completeImageData.indexOf(",")+1);
                            byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            holder.photoComplaint.setImageBitmap(decodedByte);
                            holder.photoComplaint.setVisibility(View.VISIBLE);
                        }
                    }
                    else holder.photoComplaint.setVisibility(View.GONE);
                } catch (Exception ignored) {
                    String a = ignored.getMessage();
                    Integer sa = 0;
                }
            }

            @Override
            public void onFailure(Call<JsonPrimitive> call, Throwable t) {

            }
        });



        if(!Objects.equals(currentItem.getStatus(), "Отправлена") && controllerOwner != null) holder.button.setVisibility(View.GONE);
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(controllerOwner!= null) controllerOwner.openComplaint(currentItem.getId());
                else controllerAdmin.openComplaint(currentItem.getId());
            }
        });
    }


    private void getStringPhoto(Context contextItem, Integer id) {
        String token = contextItem.getSharedPreferences("HouseManagement", Context.MODE_PRIVATE)
                .getString("token", "");
        ApiRequests apiRequests = new RetrofitService().getRetrofit().create(ApiRequests.class);
        apiRequests.getPhoto("Bearer $token", id).enqueue(new Callback<JsonPrimitive>() {
            @Override
            public void onResponse(Call<JsonPrimitive> call, Response<JsonPrimitive> response) {
                try {
                    JsonPrimitive item = response.body();
                    if (item != null && response.isSuccessful()) {
                        String base64 =
                                new Gson().fromJson(response.body().getAsJsonPrimitive(), String.class);
                    }
                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(Call<JsonPrimitive> call, Throwable t) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView flatNumber, dateComplaint, descriptionComplaint, statusComplaint, nameHouse;
        ImageView photoComplaint;
        AppCompatButton button;
        int id;
        LinearLayout linearLayout;
        ConstraintLayout constraintLayout;
        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            flatNumber = (TextView)itemView.findViewById(R.id.numberFlat);
            nameHouse = (TextView)itemView.findViewById(R.id.nameHouse);
            dateComplaint = (TextView)itemView.findViewById(R.id.dateComplaint);
            descriptionComplaint = (TextView)itemView.findViewById(R.id.descriptionComplaint);
            statusComplaint = (TextView)itemView.findViewById(R.id.statusComplaint);
            photoComplaint = (ImageView) itemView.findViewById(R.id.photoComplaint);
            button = (AppCompatButton) itemView.findViewById(R.id.btn);
            linearLayout = itemView.findViewById(R.id.linearLayout);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);
        }
    }
}
