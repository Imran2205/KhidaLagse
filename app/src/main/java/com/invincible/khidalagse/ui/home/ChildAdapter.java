package com.invincible.khidalagse.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.invincible.khidalagse.R;

import java.util.List;

public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ViewHolder> {

    private List<Integer> mViewColors;
    private List<String> mAnimals;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    ChildAdapter(List<Integer> colors, List<String> animals) {
        this.mViewColors = colors;
        this.mAnimals = animals;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_recycler, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int color = mViewColors.get(position);
        String animal = mAnimals.get(position);
        holder.text.setText(animal);


    }

    @Override
    public int getItemCount() {
        return mAnimals.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView img;
        TextView text;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.child_imageView);
            text = itemView.findViewById(R.id.child_textView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mClickListener != null) mClickListener.onItemClick(v, getAdapterPosition());
        }
    }

    public String getItem(int id) {
        return mAnimals.get(id);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
