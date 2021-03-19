package com.invincible.khidalagse.ui.home;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.invincible.khidalagse.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class HomeFragment extends Fragment {

    //private HomeViewModel homeViewModel;
    private TextView days, months;
    private RelativeLayout alldate;
    private DatePickerDialog datePickerDialog;
    private ParentAdapter adapter2;
    private ArrayList<String> listOfTime = new ArrayList<>();
    private ArrayList<String> childs = new ArrayList<>();
    private DatabaseReference menu_ref;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        //final TextView textView = root.findViewById(R.id.text_home);
        //homeViewModel.getText().observe(this, new Observer<String>() {
        //   @Override
        //    public void onChanged(@Nullable String s) {
        //        textView.setText(s);
        //    }
        //});
        //textView.setText("mmm");

        days = (TextView) root.findViewById(R.id.date_text_view);
        months = (TextView) root.findViewById(R.id.month_text_view);
        menu_ref = FirebaseDatabase.getInstance().getReference().child("food_menu");

        alldate = (RelativeLayout) root.findViewById(R.id.date_keep);

        Calendar c = Calendar.getInstance();
        int day0 = c.get(Calendar.DAY_OF_MONTH);
        int month0 = c.get(Calendar.MONTH);
        final String[] mnths = {"JAN   ", "FEB   ", "MAR  ", "APRIL ", "MAY   ", "JUNE  ", "JULY  ", "AUG   ", "SEP   ", "OCT   ", "NOV   ", "DEC   "};
        String date_str;
        if(day0<10){
           date_str = "0"+day0;
        }
        else {
            date_str = ""+day0;
        }
        days.setText(date_str);
        months.setText(mnths[month0]);

        menu_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> set = new ArrayList<>();
                ArrayList<Integer> set2 = new ArrayList<>();
                ArrayList<String> set3 = new ArrayList<>();
                listOfTime.clear();
                childs.clear();

                Iterator iterator = dataSnapshot.getChildren().iterator();
                int childCount = (int)dataSnapshot.getChildrenCount();
                int counter = 0;

                while(iterator.hasNext()){
                    counter += 1;
                    String chld = ((DataSnapshot)iterator.next()).getKey();
                    set3.add(chld);
                    //Toast.makeText(getContext(), "t  : "+chld, Toast.LENGTH_SHORT).show();
                    int finalCounter = counter;

                    String name = dataSnapshot.child(chld).child("name").getValue().toString();
                    int priority = dataSnapshot.child(chld).child("priority").getValue(Integer.class);
                    //Toast.makeText(getContext(), "np  : "+name+","+priority, Toast.LENGTH_SHORT).show();
                    listOfTime.add("");
                    childs.add("");
                    set.add(name);
                    set2.add(priority);



                    if(finalCounter == childCount){
                        for (int i = 0; i < set2.size(); i++) {
                            int prior = set2.get(i).intValue();
                            //Toast.makeText(getContext(), "np  : "+set2.size()+","+prior+","+set.get(i), Toast.LENGTH_SHORT).show();
                            listOfTime.set((prior-1),set.get(i));
                            childs.set((prior-1),set3.get(i));

                        }
                        adapter2.notifyDataSetChanged();
                    }

                    /*menu_ref.child(chld).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild("name") && dataSnapshot.hasChild("priority")){

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });*/
                }

                //listOfTime.clear();
                //priorityOfTime.clear();
                //listOfTime.addAll(set);
                //priorityOfTime.addAll(set2);

                //Toast.makeText(getContext(), "t  "+listOfTime+"p  "+priorityOfTime, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        RecyclerView recyclerView1 = root.findViewById(R.id.main_recycler);
        LinearLayoutManager vertivalLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView1.setLayoutManager(vertivalLayoutManager);
        adapter2 = new ParentAdapter(childs, listOfTime);
        recyclerView1.setAdapter(adapter2);


        alldate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                String date_str;
                                if(dayOfMonth<10){
                                    date_str = "0"+dayOfMonth;
                                }
                                else {
                                    date_str = ""+dayOfMonth;
                                }
                                days.setText(date_str);
                                months.setText(mnths[month]);
                            }
                        },  mYear, mMonth, mDay);
                        datePickerDialog.show();
            }
        });

        return root;
    }

}