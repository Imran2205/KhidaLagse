package com.invincible.khidalagse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private Button sendVerCodeBtn, verifyBtn;
    private EditText inPhoneNo, inVerCode;
    private TextView login_ins_top, login_ins2_top, login_phone;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private DatabaseReference usersRef, rootRef;

    CountryCodePicker ccp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initFields();

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        rootRef = FirebaseDatabase.getInstance().getReference();
        loadingBar = new ProgressDialog(this);

        login_ins_top = (TextView) findViewById(R.id.login_ins);
        login_ins2_top = (TextView) findViewById(R.id.login_req);

        login_phone = (TextView) findViewById(R.id.login_phone_textview);


        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(inPhoneNo);

        sendVerCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ccp.isValidFullNumber()){
                    String phnNo = ccp.getFullNumberWithPlus();
                    Toast.makeText(LoginActivity.this, "phone no: "+phnNo, Toast.LENGTH_SHORT).show();
                    loadingBar.setTitle("Phone verification");
                    loadingBar.setMessage("Please wait");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phnNo,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            LoginActivity.this,               // Activity (for callback binding)
                            callbacks);        // OnVerificationStateChangedCallbacks
                }
                else {
                    Toast.makeText(LoginActivity.this, "enter valid phone no", Toast.LENGTH_SHORT).show();
                }
            }
        });

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerCodeBtn.setVisibility(View.INVISIBLE);
                inPhoneNo.setVisibility(View.INVISIBLE);

                String verCode = inVerCode.getText().toString();

                if (TextUtils.isEmpty(verCode)){
                    Toast.makeText(LoginActivity.this,"plrase enter verification code", Toast.LENGTH_SHORT).show();;

                }
                else{
                    loadingBar.setTitle("Code verification");
                    loadingBar.setMessage("Please wait");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verCode);
                    signInWithPhoneAuthCredential(credential);
                }

            }
        });


        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                loadingBar.dismiss();
                Toast.makeText(LoginActivity.this, ""+e, Toast.LENGTH_SHORT).show();
                sendVerCodeBtn.setVisibility(View.VISIBLE);
                inPhoneNo.setVisibility(View.VISIBLE);

                verifyBtn.setVisibility(View.INVISIBLE);
                inVerCode.setVisibility(View.INVISIBLE);
                ccp.setVisibility(View.VISIBLE);
            }
            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                loadingBar.dismiss();
                mVerificationId = verificationId;
                mResendToken = token;

                Toast.makeText(LoginActivity.this, "Code sent", Toast.LENGTH_SHORT).show();;

                sendVerCodeBtn.setVisibility(View.INVISIBLE);
                inPhoneNo.setVisibility(View.INVISIBLE);

                verifyBtn.setVisibility(View.VISIBLE);
                inVerCode.setVisibility(View.VISIBLE);
                ccp.setVisibility(View.INVISIBLE);

                login_ins_top.setText("Enter Verfication Code");
                login_ins2_top.setText("enter the code that you have received via sms");
                login_phone.setText("Verification code");

            }

        };

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String device_token = FirebaseInstanceId.getInstance().getToken();
                            String currentUser = mAuth.getCurrentUser().getUid();
                            rootRef.child("device_token").child(currentUser)
                                    .setValue(device_token);
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this, "You are logged in successfully", Toast.LENGTH_SHORT).show();
                            sendUsertoMainActivity();
                        }
                        else {
                            loadingBar.dismiss();
                            String msg = task.getException().toString();
                            Toast.makeText(LoginActivity.this, "Error:"+msg, Toast.LENGTH_SHORT).show();;

                        }
                    }
                });
    }

    private void sendUsertoMainActivity() {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    public void initFields(){
        sendVerCodeBtn = (Button)  findViewById(R.id.sendButton);
        verifyBtn = (Button) findViewById(R.id.verifyButton);
        inPhoneNo = (EditText) findViewById(R.id.phoneText);
        inVerCode = (EditText) findViewById(R.id.codeText);
    }
}
