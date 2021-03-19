package com.invincible.khidalagse.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.invincible.khidalagse.R;

import java.util.List;

public class ParentAdapter extends RecyclerView.Adapter<ParentAdapter.ViewHolder> {

    private final List<String> mMenu_times;
    private List<String> mChilds;
    private DatabaseReference menu_ref;

    // data is passed into the constructor
    ParentAdapter(List<String> childs, List<String> menu_times) {


        this.mChilds = childs;
        this.mMenu_times = menu_times;
    }

    @NonNull
    @Override
    public ParentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.parent_recycler, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ParentAdapter.ViewHolder holder, int position) {
        String menu = mMenu_times.get(position);
        holder.text.setText(menu);

        menu_ref = FirebaseDatabase.getInstance().getReference().child("food_menu");


            //Toast.makeText(holder.recyclerView.getContext(),""+ menu_time_child+","+mChilds.size(), Toast.LENGTH_SHORT).show();
            String menu_time_child = mChilds.get(position);
            FirebaseRecyclerOptions options_firebase = new FirebaseRecyclerOptions.Builder<MenuInfo>()
                    .setQuery(menu_ref.child(menu_time_child).child("items"),MenuInfo.class)
                    .build();
            //Toast.makeText(holder.recyclerView.getContext(),""+ menu_time_child+","+position, Toast.LENGTH_SHORT).show();
            final FirebaseRecyclerAdapter<MenuInfo, FoodMenuHolder> adapter = new FirebaseRecyclerAdapter<MenuInfo, FoodMenuHolder>(options_firebase) {
                @Override
                protected void onBindViewHolder(@NonNull FoodMenuHolder foodMenuHolder, int i, @NonNull MenuInfo menuInfo) {
                    final String item_num = getRef(i).getKey();
                    final String[] foodImg = {"default_image"};
                    final String[] listFoodName = new String[1];

                    menu_ref.child(menu_time_child).child("items").child(item_num).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                if(dataSnapshot.hasChild("image_link")){
                                    String foodPic = dataSnapshot.child("image_link").getValue().toString();
                                    String foodName = dataSnapshot.child("name").getValue().toString();
                                    int price = dataSnapshot.child("price").getValue(Integer.class);
                                    float ratings = dataSnapshot.child("ratings").getValue(Float.class);
                                    listFoodName[0] = foodName;
                                    foodImg[0] = foodPic;
                                    foodMenuHolder.food_name.setText(foodName);
                                }
                                else {
                                    String foodName = dataSnapshot.child("name").getValue().toString();
                                    int price = dataSnapshot.child("price").getValue(Integer.class);
                                    float ratings = dataSnapshot.child("ratings").getValue(Float.class);
                                    listFoodName[0] = foodName;
                                    foodImg[0] = "default image";
                                    foodMenuHolder.food_name.setText(foodName);
                                }

                                foodMenuHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //Toast.makeText(holder.recyclerView.getContext(),"this is "+listFoodName[0] , Toast.LENGTH_SHORT).show();
                                        MenuAmmountPopup menuAmmountPopup = new MenuAmmountPopup();
                                        menuAmmountPopup.showPopup(v, listFoodName[0]);
                                    }
                                });

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

                @NonNull
                @Override
                public FoodMenuHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_recycler, parent, false);
                    FoodMenuHolder viewHolder = new FoodMenuHolder(view);
                    return viewHolder;
                }
            };

            LinearLayoutManager horizontalLayoutManager
                    = new LinearLayoutManager(holder.recyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false);
            holder.recyclerView.setLayoutManager(horizontalLayoutManager);
            holder.recyclerView.setAdapter(adapter);
            adapter.startListening();


    }

    @Override
    public int getItemCount() {
        return mMenu_times.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView text;

        RecyclerView recyclerView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.textView);
            recyclerView = itemView.findViewById(R.id.rv_child);

        }
    }

    public String getItem(int id) {
        return mMenu_times.get(id);
    }

    public static class FoodMenuHolder extends RecyclerView.ViewHolder{
        TextView food_name;
        ImageView food_image;

        public FoodMenuHolder(@NonNull View itemView) {
            super(itemView);

            food_name = itemView.findViewById(R.id.child_textView);
            food_image = itemView.findViewById(R.id.child_imageView);
        }
    }
}
