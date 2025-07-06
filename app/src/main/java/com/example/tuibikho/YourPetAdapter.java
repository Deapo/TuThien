package com.example.tuibikho;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.tuibikho.data.PetEntity;
import com.google.android.material.imageview.ShapeableImageView;

public class YourPetAdapter extends ListAdapter<PetEntity, YourPetAdapter.PetViewHolder> {
    private OnPetClickListener listener;

    public interface OnPetClickListener {
        void onPetClick(PetEntity pet);
    }

    public void setOnPetClickListener(OnPetClickListener listener) {this.listener = listener;}

    public static final DiffUtil.ItemCallback<PetEntity> DIFF_CALLBACK = new DiffUtil.ItemCallback<PetEntity>() {
        @Override
        public boolean areItemsTheSame(@NonNull PetEntity oldItem, @NonNull PetEntity newItem) {
            return oldItem.getPetId() == newItem.getPetId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull PetEntity oldItem, @NonNull PetEntity newItem) {
            return oldItem.getPetName().equals(newItem.getPetName())
                    && oldItem.getPetImage().equals(newItem.getPetImage())
                    && oldItem.getPetAge().equals(newItem.getPetAge())
                    && oldItem.getPetGender().equals(newItem.getPetGender());
        }
    };

    public YourPetAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_info_pet, parent, false);
        return new PetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
        PetEntity pet = getItem(position);
        holder.bind(pet);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onPetClick(pet);
        });
    }

    static class PetViewHolder extends RecyclerView.ViewHolder {
        private final ShapeableImageView petImage;
        private final TextView petName, petAge, petGender;

        public PetViewHolder(@NonNull View itemView) {
            super(itemView);
            petImage = itemView.findViewById(R.id.petImage);
            petName = itemView.findViewById(R.id.petName);
            petAge = itemView.findViewById(R.id.petAge);
            petGender = itemView.findViewById(R.id.petGender);
        }

        public void bind(PetEntity pet) {
            petName.setText(pet.getPetName());
            petAge.setText("Tuổi: " + pet.getPetAge());
            petGender.setText("Giới tính: " + pet.getPetGender());
            if (pet.getPetImage() != null && !pet.getPetImage().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(pet.getPetImage())
                        .placeholder(R.drawable.avatar_pet)
                        .into(petImage);
            } else {
                petImage.setImageResource(R.drawable.avatar_pet);
            }
        }
    }
}
