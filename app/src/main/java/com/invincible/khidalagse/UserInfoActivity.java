package com.invincible.khidalagse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import org.apache.commons.lang3.ArrayUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class UserInfoActivity extends AppCompatActivity {

    private EditText name, email, addressLine1, addressLine2, addressZip, addressDist;
    private Button save_btn;
    private String username, useremail, useraddlin1, useraddlin2, useraddzip, useradddist;
    private String currentUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        name = (EditText) findViewById(R.id.in_name);
        email = (EditText)findViewById(R.id.in_email);
        addressLine1 = (EditText)findViewById(R.id.in_add_line1);
        addressLine2 = (EditText)findViewById(R.id.in_add_line2);
        addressZip = (EditText)findViewById(R.id.in_add_zip);
        addressDist = (EditText)findViewById(R.id.in_add_dist);
        save_btn = (Button)findViewById(R.id.user_info_save_btn);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        loadingBar = new ProgressDialog(this);

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = name.getText().toString();
                useremail = email.getText().toString();
                useraddlin1 = addressLine1.getText().toString();
                useraddlin2 = addressLine2.getText().toString();
                useraddzip = addressZip.getText().toString();
                useradddist = addressDist.getText().toString();
                String error[] = {"name","email","address","address","zip","district"};
                int[] errorIndex = {};
                if(username.isEmpty()){
                    errorIndex = (int []) ArrayUtils.add(errorIndex, 0);
                }

                if(!isValidEmail(useremail)){
                    errorIndex = (int []) ArrayUtils.add(errorIndex, 1);
                }

                if(useraddlin1.isEmpty()){
                    errorIndex = (int []) ArrayUtils.add(errorIndex, 2);
                }

                if(useraddlin2.isEmpty()){
                    errorIndex = (int []) ArrayUtils.add(errorIndex, 3);
                }

                if(useraddzip.isEmpty()){
                    errorIndex = (int []) ArrayUtils.add(errorIndex, 4);
                }

                if(useradddist.isEmpty()){
                    errorIndex = (int []) ArrayUtils.add(errorIndex, 5);
                }

                String errorMsg = "";
                int z = errorIndex.length;
                for(int i=0; i<z; i++){
                    errorMsg = errorMsg+error[errorIndex[i]];
                    if(i<(z-1)){
                        errorMsg=errorMsg+",";
                    }
                }

                if(z>0){
                    Toast.makeText(UserInfoActivity.this,"Please insert valid "+errorMsg, Toast.LENGTH_SHORT).show();
                }
                else{
                    saveDataInDB();
                }

            }


        });


    }

    private void saveDataInDB() {
        HashMap<String, String> profileMap =  new HashMap<>();
        profileMap.put("uid", currentUserID);
        profileMap.put("name", username);
        profileMap.put("email", useremail);
        profileMap.put("address_line1", useraddlin1);
        profileMap.put("address_line2", useraddlin2);
        profileMap.put("address_zip-zode", useraddzip);
        profileMap.put("address_district", useradddist);
        rootRef.child("Users").child(currentUserID).setValue(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(UserInfoActivity.this, "profile updated", Toast.LENGTH_SHORT).show();
                    sendUserToMainActivity();
                }
                else {
                    String msg = task.getException().toString();
                    Toast.makeText(UserInfoActivity.this, "Error:"+msg, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void sendUserToMainActivity(){
        Intent mainIntent = new Intent(UserInfoActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }



}
