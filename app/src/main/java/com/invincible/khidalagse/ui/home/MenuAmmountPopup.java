package com.invincible.khidalagse.ui.home;

import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.invincible.khidalagse.R;
import com.invincible.khidalagse.UserInfoActivity;

import java.util.HashMap;

public class MenuAmmountPopup {
    private FirebaseAuth mAuth;
    private DatabaseReference dRef;
    private String uid;
    public void showPopup(final View view, final String menuName){
        LayoutInflater inflater = (LayoutInflater)view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_menu_ammount_select, null);

        int width = RelativeLayout.LayoutParams.MATCH_PARENT;
        int height = (RelativeLayout.LayoutParams.MATCH_PARENT);

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        dRef = FirebaseDatabase.getInstance().getReference();

        Toast.makeText(view.getContext(), ""+height, Toast.LENGTH_SHORT).show();

        boolean focusable = true;

        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        popupWindow.showAtLocation(view, Gravity.CENTER, 0,0);
        TextView select_amnt = popupView.findViewById(R.id.select_amnt_popup_textView);
        select_amnt.setText("Select Amount");

        TextView menu_name = popupView.findViewById(R.id.menu_name_textView);
        menu_name.setText(menuName);

        Button add_cart_btn = popupView.findViewById(R.id.add_at_cart_btn);

        NumberPicker picker = popupView.findViewById(R.id.menu_amnt_number_picker);
        picker.setMaxValue(100);
        picker.setMinValue(1);


        add_cart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num = picker.getValue();
                Toast.makeText(view.getContext(), ""+num, Toast.LENGTH_SHORT).show();

                //Toast.makeText(view.getContext(), number, Toast.LENGTH_SHORT).show();
                HashMap<String, Integer> cartMap =  new HashMap<>();
                cartMap.put("amount", num);

                String cartKey = dRef.child("cart").child(uid).push().getKey();

                dRef.child("cart").child(uid).child(menuName).setValue(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(view.getContext(), "added to cart", Toast.LENGTH_SHORT).show();
                            popupWindow.dismiss();
                        }
                        else {
                            String msg = task.getException().toString();
                            Toast.makeText(view.getContext(), "Error:"+msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });




        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //popupWindow.dismiss();
                return true;
            }
        });

        /*LayoutInflater inflater = (LayoutInflater)view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        //final LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.popup_menu_ammount_select, null);
        View popupView = inflater.inflate(R.layout.popup_menu_ammount_select, null);
        NumberPicker numberpicker = (NumberPicker) popupView.findViewById(R.id.menu_amnt_number_picker);

        numberpicker.setMinValue(1950);
        numberpicker.setMaxValue(2018);
        numberpicker.setValue(2017);
        //Finally building an AlertDialog
        final AlertDialog builder = new AlertDialog.Builder(view.getContext())
                .setPositiveButton("Add", null)
                .setNegativeButton("Cancel", null)
                .setView(popupView)
                .setCancelable(false)
                .create();
        builder.show();
        //Setting up OnClickListener on positive button of AlertDialog
        builder.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Code on submit
            }
        });*/
    }
}
