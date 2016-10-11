package com.meetup.uhoo.credentials;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.meetup.uhoo.R;
import com.meetup.uhoo.util.FindLocation;

public class SignIn extends AppCompatActivity {



    Button signUpButton;
    Button loginButton;
    EditText emailEditText;
    EditText passwordEditText;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Enable Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Inflate variables
        loginButton = (Button) findViewById(R.id.loginButton);
        signUpButton = (Button) findViewById(R.id.signUpButton);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        // Login through email and password
        // mAuthListener will catch the pass/fail of user login
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Validation Check
                if(!ValidationCheck()){
                    return;
                }


                mAuth.signInWithEmailAndPassword(emailEditText.getText().toString(), passwordEditText.getText().toString())
                        .addOnCompleteListener(SignIn.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d("auth", "signInWithEmail:onComplete:" + task.isSuccessful());


                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Log.w("auth", "signInWithEmail:failed", task.getException());
                                    Toast.makeText(SignIn.this, "Sign In Failed",Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                // Get user shared prefs and save account type
                                SharedPreferences.Editor editor = getSharedPreferences("currentUser", MODE_PRIVATE).edit();
                                editor.putString("authType", "EMAIL");
                                editor.apply();

                                Intent intent = new Intent(SignIn.this, FindLocation.class);
                                startActivity(intent);

                            }

                        });
            }
        });

        // Take user to Email/Password sign up
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignIn.this, CreateNewAccount.class);
                startActivity(intent);
            }
        });




        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("auth", "onAuthStateChanged:signed_in:" + user.getUid());

                } else {
                    // User is signed out
                    Log.d("auth", "onAuthStateChanged:signed_out");
                }
            }
        };



    }


    // Checks if email and password lengths are 0
    Boolean ValidationCheck(){
        if(emailEditText.getText().length() == 0 ){
            Toast.makeText(SignIn.this, "Please Enter an Email", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(passwordEditText.getText().length() == 0 ){
            Toast.makeText(SignIn.this, "Please Enter a Password", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
