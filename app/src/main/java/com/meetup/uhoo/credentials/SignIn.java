package com.meetup.uhoo.credentials;

import android.app.ProgressDialog;
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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.meetup.uhoo.Enum;
import com.meetup.uhoo.R;
import com.meetup.uhoo.core.User;
import com.meetup.uhoo.core.UserDataFetchListener;
import com.meetup.uhoo.service_layer.user_services.CurrentUserDataService;
import com.meetup.uhoo.util.FindLocation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignIn extends AppCompatActivity {


    Button signUpButton;
    Button loginButton;
    EditText emailEditText;
    EditText passwordEditText;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;

    private CallbackManager mCallbackManager;

    private CurrentUserDataService currentUserDataService;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Enable Facebook Authentication
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_sign_in);

        // Enable Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Log out of anonymous user
        FirebaseAuth.getInstance().signOut();

        // Init User Data
        currentUserDataService = new CurrentUserDataService(getApplicationContext());

        // Inflate variables
        loginButton = (Button) findViewById(R.id.loginButton);
        signUpButton = (Button) findViewById(R.id.signUpButton);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        pd = new ProgressDialog(SignIn.this);

        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.btnFacebookLogin);
        loginButton.setReadPermissions("email", "public_profile", "user_about_me");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                Log.i("Facebook Login", "facebook:onSuccess:" + loginResult);


                // Set Auth Type and pass in Access token
                currentUserDataService.setAuthType(Enum.AuthType.FACEBOOK, loginResult.getAccessToken().getToken());


                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {


                                Log.i("Facebook Graph", "data:" + response.toString());

                                try {
                                    String url = "https://graph.facebook.com/" + object.getString("id") +  "/picture?height=500";

                                    // Get user shared prefs and save photo url locally
                                    SharedPreferences.Editor editor = getSharedPreferences("currentUser", MODE_PRIVATE).edit();
                                    editor.putString("facebookId", object.getString("id"));
                                    editor.putString("photoUrl", url);
                                    editor.apply();

                                    currentUserDataService.setPhotoUrl(url);




                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }




                                handleFacebookAccessToken(loginResult.getAccessToken());
                            }
                        });
                Bundle parameters = new Bundle();
                request.setParameters(parameters);
                request.executeAsync();


            }

            @Override
            public void onCancel() {
                Log.i("Facebook Login", "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.i("Facebook Login", "facebook:onError", error);
                // ...
            }
        });

        // Login through email and password
        // mAuthListener will catch the pass/fail of user login
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Validation Check
                if (!ValidationCheck()) {
                    return;
                }

                // Log into email
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
                                    Toast.makeText(SignIn.this, "Sign In Failed", Toast.LENGTH_SHORT).show();
                                    return;
                                }


                                // Get user shared prefs and save account type
                                SharedPreferences.Editor editor = getSharedPreferences("currentUser", MODE_PRIVATE).edit();
                                editor.putString("authType", "EMAIL");
                                editor.apply();

                                Intent intent = new Intent(SignIn.this, FindLocation.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
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
                FirebaseUser mUser = firebaseAuth.getCurrentUser();
                if (mUser != null) {
                    // User is signed in
                    Log.i("auth", "onAuthStateChanged:signed_in:" + mUser.getUid());

                    // Get user shared prefs and save account uid
                    SharedPreferences.Editor editor = getSharedPreferences("currentUser", MODE_PRIVATE).edit();
                    editor.putString("uid", mUser.getUid());
                    editor.apply();

                    pd.setMessage("Fetching Data");

                    // Save profile url on database
                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("/photoUrl/", currentUserDataService.getCurrentUser().getPhotoUrl());
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {

                                currentUserDataService.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
                                currentUserDataService.getFirebaseUserData(new UserDataFetchListener() {
                                    @Override
                                    public void onUserFetch(User user) {
                                        currentUserDataService.saveUserDataLocally(getApplicationContext());

                                        // Sign Up Process is Done
                                        Intent intent = new Intent(SignIn.this, FindLocation.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });

                            }
                        }
                    });





                } else {
                    // User is signed out
                    Log.i("auth", "onAuthStateChanged:signed_out");
                }
            }
        };


    }


    // Checks if email and password lengths are 0
    Boolean ValidationCheck() {
        if (emailEditText.getText().length() == 0) {
            //Toast.makeText(SignIn.this, "Please Enter an Email", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (passwordEditText.getText().length() == 0) {
            //Toast.makeText(SignIn.this, "Please Enter a Password", Toast.LENGTH_SHORT).show();
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

        pd.dismiss();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), FindLocation.class);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode,
                resultCode, data);
    }


    private void handleFacebookAccessToken(final AccessToken token) {
        Log.d("Facebook Login", "handleFacebookAccessToken:" + token);


        pd.setMessage("Logging In");
        pd.show();

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("Facebook Login", "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            pd.hide();

                            Log.w("Facebook Login", "signInWithCredential", task.getException());
                            Toast.makeText(SignIn.this, "Facebook Authentication Failed",
                                    Toast.LENGTH_SHORT).show();

                            return;
                        }


                    }
                });


    }
}
