package com.example.salaaplicatie.workout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.salaaplicatie.R;
import com.example.salaaplicatie.helpers.VerticalDecorationHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class SelectExercise extends AppCompatActivity {
    private ImageButton btnBack;
    private RecyclerView rvExercitii;
    private CustomAdapterExercitiiPrestabilite adapter;
    private List<ExercitiiPrestabilite>listaExercitii=new ArrayList<>();
    private FirebaseFirestore db;
    private List<ExercitiiPrestabilite> listaExercitiiOriginal = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_exercise);

        btnBack=findViewById(R.id.imgBtnBack);

        rvExercitii=findViewById(R.id.rvExercitii);
        adapter=new CustomAdapterExercitiiPrestabilite(this,listaExercitii,exercitiu -> {
            Intent intent=new Intent();
            intent.putExtra("SelectedExNume",exercitiu.getNume());
            intent.putExtra("SelectedExEquipment",exercitiu.getEquipment());
            Toast.makeText(this,"S-a trimis:"+exercitiu.getNume(),Toast.LENGTH_LONG).show();
            setResult(Activity.RESULT_OK,intent);
            finish();
        });
        rvExercitii.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        rvExercitii.setAdapter(adapter);

        int verticalSpace=getResources().getDimensionPixelOffset(R.dimen.vertical_space_height);
        rvExercitii.addItemDecoration(new VerticalDecorationHelper(verticalSpace));

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),LogWorkout.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        db = FirebaseFirestore.getInstance();
        fetchExercises();

        SearchView searchView = findViewById(R.id.searchExercise);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
    }

    private void fetchExercises() {
        db.collection("TabelaExercitii")
                .get()
                .addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                listaExercitii.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    ExercitiiPrestabilite exercitiu = document.toObject(ExercitiiPrestabilite.class);
                    listaExercitii.add(exercitiu);
                }
                listaExercitiiOriginal.addAll(listaExercitii);
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this,"Error loading exercises."+task.getException(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filter(String text) {
        List<ExercitiiPrestabilite> filteredList = new ArrayList<>();
        if (text.isEmpty()) {
            filteredList.addAll(listaExercitiiOriginal); // Adaugă înapoi toate exercițiile
        } else {
            for (ExercitiiPrestabilite exercitiu : listaExercitiiOriginal) { // Folosește lista originală pentru filtrare
                if (exercitiu.getNume().toLowerCase().contains(text.toLowerCase()) ||
                        exercitiu.getEquipment().toLowerCase().contains(text.toLowerCase()) ||
                        exercitiu.getMuscles().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(exercitiu);
                }
            }
        }
        adapter.updateList(filteredList);
    }

}