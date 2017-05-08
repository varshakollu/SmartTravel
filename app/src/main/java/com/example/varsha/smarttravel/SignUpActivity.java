package com.example.varsha.smarttravel;

import android.support.v7.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Varsha on 4/2/2017.
 */

public class SignUpActivity extends AppCompatActivity {

    private Firebase mRef;
    private User user;
    private EditText name;
    private EditText phoneNumber;
    private EditText email;
    private EditText password;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
    }

    protected void onStart() {

        super.onStart();
        name = (EditText) findViewById(R.id.editName);
        email = (EditText) findViewById(R.id.editEmail);
        phoneNumber = (EditText) findViewById(R.id.editPhoneNumber);
        password = (EditText) findViewById(R.id.editPassword);
    }

    public void onSignUpClicked(View view) {

        mRef = new Firebase(com.example.varsha.smarttravel.Config.FIREBASE_URL);

        showProgressDialog();
        onAuthenticationSucess();
    }

    private void onAuthenticationSucess() {

        setUpUser();
        signOut();
        hideProgressDialog();
        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
        finish();
    }

    protected void setUpUser() {

        String id = String.valueOf((int) Math.random());
        user = new User();

        user.setId(id.toString());
        user.setName(name.getText().toString());
        user.setPhoneNumber(phoneNumber.getText().toString());
        user.setEmail(email.getText().toString());
        user.setPassword(password.getText().toString());

        mRef.child("Users").child(name.getText().toString()).setValue(user);
    }

    private void signOut() {

        mAuth.signOut();
    }

    public void showProgressDialog() {

        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {

        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}