package com.example.salaaplicatie.workout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.salaaplicatie.R;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapterExercitiiSet extends RecyclerView.Adapter<CustomAdapterExercitiiSet.WorkoutViewHolder> {

    private List<ExercitiiAntrenament> exercitiiAntrenament = new ArrayList<>();
    private OnDataChangedListener onDataChangedListener;

    public void setOnDataChangedListener(OnDataChangedListener listener) {
        this.onDataChangedListener = listener;
    }

    public interface OnDataChangedListener {
        void onDataChanged();
    }
    public CustomAdapterExercitiiSet(List<ExercitiiAntrenament> exercitiiAntrenament) {
        this.exercitiiAntrenament = exercitiiAntrenament;
    }

    public List<ExercitiiAntrenament> getExercitiiAntrenament() {
        return exercitiiAntrenament;
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_adapter_exercitii_antrenament, parent, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        ExercitiiAntrenament detail = exercitiiAntrenament.get(position);
        holder.bind(detail);
    }

    @Override
    public int getItemCount() {
        return exercitiiAntrenament.size();
    }

    class WorkoutViewHolder extends RecyclerView.ViewHolder {
        TextView tvExerciseName, tvEquipment;
        LinearLayout layoutSets;
        Button btnAddSet;
        ImageButton btnRemoveExercise;

        public WorkoutViewHolder(View itemView) {
            super(itemView);
            tvExerciseName = itemView.findViewById(R.id.tvExerciseName);
            tvEquipment = itemView.findViewById(R.id.tvEquipment);
            layoutSets = itemView.findViewById(R.id.layoutSets);
            btnAddSet = itemView.findViewById(R.id.btnAddSet);
            btnRemoveExercise=itemView.findViewById(R.id.btnRemoveExercise);

        }

        void bind(ExercitiiAntrenament detail) {
            tvExerciseName.setText(detail.getExerciseName());
            tvEquipment.setText(detail.getEquipment());
            layoutSets.removeAllViews();

            for (int i = 0; i < detail.sets.size(); i++) {
                final int currentIndex=i;
                ExercitiiSet set = detail.sets.get(currentIndex);
                View setView = LayoutInflater.from(itemView.getContext()).inflate(R.layout.layout_exercitiu, layoutSets, false);
                EditText etWeight = setView.findViewById(R.id.etWeight);
                EditText etRep = setView.findViewById(R.id.etRep);
                EditText etRpe = setView.findViewById(R.id.etRpe);

                Button btnConfirmSet = setView.findViewById(R.id.btnConfirmSet);
                btnConfirmSet.setSelected(set.isConfirmed());

                Button btnDeleteSet=setView.findViewById(R.id.btnDeleteSet);
                btnDeleteSet.setOnClickListener(v->{
                    if(getBindingAdapterPosition()!=RecyclerView.NO_POSITION){
                        detail.sets.remove(currentIndex);
                        notifyItemChanged(getBindingAdapterPosition());
                        Toast.makeText(v.getContext(), "Set deleted", Toast.LENGTH_SHORT).show();
                        if (onDataChangedListener != null) {
                            onDataChangedListener.onDataChanged();
                        }
                    }
                });

                etWeight.setText(String.format("%.1f", set.getWeight()));
                etRep.setText(String.format("%d", set.getReps()));
                etRpe.setText(String.format("%.1f", set.getRpe()));

                TextView tvSetNumber = setView.findViewById(R.id.tvSetNumber);
                tvSetNumber.setText("Set " + (i + 1));

                layoutSets.addView(setView);

                btnConfirmSet.setOnClickListener(v -> {
                    try {
                        double weight = Double.parseDouble(etWeight.getText().toString());
                        int reps = Integer.parseInt(etRep.getText().toString());
                        double rpe = Double.parseDouble(etRpe.getText().toString());

                        if (rpe < 6.0 || rpe > 10.0) {
                            set.setRpe(6);
                            Toast.makeText(v.getContext(), "RPE must be between 6 and 10.", Toast.LENGTH_SHORT).show();
                        } else {
                            set.setWeight(weight);
                            set.setReps(reps);
                            set.setRpe(rpe);
                            btnConfirmSet.setSelected(true);
                            set.setConfirmed(true);

                            if (onDataChangedListener != null) {
                                onDataChangedListener.onDataChanged();
                            }
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(v.getContext(), "Please enter valid numbers.", Toast.LENGTH_SHORT).show();
                    }
                });
            }


            btnAddSet.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    if(layoutSets.getChildCount()>0){
                    View lastSetView = layoutSets.getChildAt(layoutSets.getChildCount() - 1);
                    EditText etWeight = lastSetView.findViewById(R.id.etWeight);
                    EditText etReps = lastSetView.findViewById(R.id.etRep);
                    EditText etRpe = lastSetView.findViewById(R.id.etRpe);

                    double weight = etWeight.getText().toString().isEmpty() ? 0 : Double.parseDouble(etWeight.getText().toString());
                    int rep = etReps.getText().toString().isEmpty() ? 0 : Integer.parseInt(etReps.getText().toString());
                    double rpe = etRpe.getText().toString().isEmpty() ? 0 : Double.parseDouble(etRpe.getText().toString());

                    ExercitiiAntrenament currentExercise = exercitiiAntrenament.get(position);
                    currentExercise.sets.add(new ExercitiiSet(weight, rep, rpe));
                    notifyItemChanged(position);
                    Toast.makeText(v.getContext(), "New set added", Toast.LENGTH_SHORT).show();
                        if (onDataChangedListener != null) {
                            onDataChangedListener.onDataChanged();
                        }
                }else {
                    ExercitiiAntrenament currentExercise = exercitiiAntrenament.get(position);
                    currentExercise.sets.add(new ExercitiiSet(0, 0, 0));
                    notifyItemChanged(position);
                    Toast.makeText(v.getContext(), "First set added", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            btnRemoveExercise.setOnClickListener(v->{
                int position=getBindingAdapterPosition();
                if(position!=RecyclerView.NO_POSITION){
                    exercitiiAntrenament.remove(position);
                    notifyItemRemoved(position);
                    notifyItemChanged(position,exercitiiAntrenament.size()-position);
                    Toast.makeText(v.getContext(), "Exercise removed", Toast.LENGTH_SHORT).show();
                    if (onDataChangedListener != null) {
                        onDataChangedListener.onDataChanged();
                    }
                }
            });
        }
    }

    public void addExercise(ExercitiiAntrenament newExercise) {
        exercitiiAntrenament.add(newExercise);
        notifyItemInserted(exercitiiAntrenament.size() - 1);
        if (onDataChangedListener != null) {
            onDataChangedListener.onDataChanged();
        }
    }


    private void notifyDataChanged() {
        if (onDataChangedListener != null) {
            onDataChangedListener.onDataChanged();
        }
    }


}
