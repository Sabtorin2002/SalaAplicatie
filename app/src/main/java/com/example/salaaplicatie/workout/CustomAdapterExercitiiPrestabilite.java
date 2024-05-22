package com.example.salaaplicatie.workout;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.salaaplicatie.R;
import com.example.salaaplicatie.helpers.OnItemClickListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class CustomAdapterExercitiiPrestabilite extends RecyclerView.Adapter<CustomAdapterExercitiiPrestabilite.ExercitiiViewHolder> {
    private Context context;
    private List<ExercitiiPrestabilite>listaExercitii;
    private FirebaseStorage storage;
    private OnItemClickListener listener;

    public CustomAdapterExercitiiPrestabilite(Context context, List<ExercitiiPrestabilite> listaExercitii,OnItemClickListener listener) {
        this.context = context;
        this.listaExercitii = listaExercitii;
        this.storage = FirebaseStorage.getInstance();
        this.listener=listener;
    }

    @NonNull
    @Override
    public ExercitiiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_adapter_exercitii_prestabilitate, parent, false);
        return new ExercitiiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExercitiiViewHolder holder, int position) {
        ExercitiiPrestabilite exercitiu = listaExercitii.get(position);
        holder.tvNumeExercitiu.setText(exercitiu.getNume());
        holder.tvEquipment.setText(exercitiu.getEquipment());
        holder.tvMuscles.setText(exercitiu.getMuscles());

        Glide.with(context)
                .load(exercitiu.getImgUrl())
                .into(holder.imgExercitiu);
        StorageReference ref = storage.getReferenceFromUrl(exercitiu.getImgUrl());
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Folosirea URL-ului pentru încărcarea imaginii
                Glide.with(context)
                        .load(uri.toString())
                        .into(holder.imgExercitiu);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Firebase", "Eroare la încărcarea imaginii", e);
            }
        });
        holder.itemView.setOnClickListener(v->{
            if(listener!=null){
                listener.onItemClick(exercitiu);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listaExercitii.size();
    }

    public void updateList(List<ExercitiiPrestabilite>newList){
        listaExercitii.clear();
        listaExercitii.addAll(newList);
        notifyDataSetChanged();
    }

    public static class ExercitiiViewHolder extends RecyclerView.ViewHolder {
        public TextView tvNumeExercitiu;
        public TextView tvEquipment;
        public TextView tvMuscles;
        public ImageView imgExercitiu;

        public ExercitiiViewHolder(View itemView) {
            super(itemView);
            tvNumeExercitiu = itemView.findViewById(R.id.tvNumeExercitiu);
            tvEquipment = itemView.findViewById(R.id.tvEquipment);
            tvMuscles = itemView.findViewById(R.id.tvMuscles);
            imgExercitiu = itemView.findViewById(R.id.imgExercitiu);
        }
    }

}
