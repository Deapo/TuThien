package com.example.tuibikho;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.*;

public class PetTypeAdapter extends RecyclerView.Adapter<PetTypeAdapter.ViewHolder> {
    private final List<PetType> petTypeItems;
    private OnItemClickListener listener;

    public interface OnItemClickListener{
        void onItemClick(PetType petType);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public PetTypeAdapter(List<PetType> petTypeItems){
        this.petTypeItems = petTypeItems; this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_pet_types, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PetType petType = petTypeItems.get(position);
        holder.img.setImageResource(petType.getImgResId());
        holder.name.setText(petType.getName());

       holder.itemPet.setOnClickListener(v -> {
        if (listener != null)   
        {
            listener.onItemClick(petType);
        }
       });
    }

    @Override
    public int getItemCount() {
        return petTypeItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView img;
        TextView name;
        LinearLayout itemPet;
        ViewHolder(View itemView){
            super(itemView);
            img = itemView.findViewById(R.id.imgPet);
            name = itemView.findViewById(R.id.petName);
            itemPet = itemView.findViewById(R.id.itemPet);
        }
    }

}
