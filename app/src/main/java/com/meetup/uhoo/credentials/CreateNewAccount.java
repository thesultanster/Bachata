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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.meetup.uhoo.R;
import com.meetup.uhoo.util.FindLocation;

public class CreateNewAccount extends AppCompatActivity {

    Button signUpButton;
    EditText emailEditText;
    EditText passwordEditText;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_account);

        // Get Current user Authentication
        mAuth = FirebaseAuth.getInstance();

        // Inflate variables
        signUpButton = (Button) findViewById(R.id.signUpButton);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Validation Check
                if(!ValidationCheck()){
                    return;
                }


                // Create new credential for Email/Password
                AuthCredential credential = EmailAuthProvider.getCredential(
                        emailEditText.getText().toString(),
                        passwordEditText.getText().toString());

                // Link Credential with existing anonymous user
                mAuth.getCurrentUser().linkWithCredential(credential)
                        .addOnCompleteListener(CreateNewAccount.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d("auth", "linkWithCredential:onComplete:" + task.isSuccessful());

                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(CreateNewAccount.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(CreateNewAccount.this, "Profile Created!", Toast.LENGTH_SHORT).show();

                                    // Get user shared prefs and save account type
                                    SharedPreferences.Editor editor = getSharedPreferences("currentUser", MODE_PRIVATE).edit();
                                    editor.putString("authType", "EMAIL");
                                    editor.apply();

                                    Intent intent = new Intent(CreateNewAccount.this, FindLocation.class);
                                    startActivity(intent);
                                    finish();

                                }

                            }
                        });

            }



        });


        // Check authentication states if user is signed in or out
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("auth", "CreateNewAccount onAuthStateChanged:signed_in:" + user.getUid());
                    //Intent intent = new Intent(CreateNewAccount.this, ProfileActivity.class);
                    //startActivity(intent);
                } else {
                    // User is signed out
                    Log.d("auth", "CreateNewAccount onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
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

    // Checks if email and password lengths are 0
    Boolean ValidationCheck(){
        if(emailEditText.getText().length() == 0 ){
            Toast.makeText(CreateNewAccount.this, "Please Enter an Email", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(passwordEditText.getText().length() == 0 ){
            Toast.makeText(CreateNewAccount.this, "Please Enter a Password", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


}
