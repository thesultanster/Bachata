package com.meetup.uhoo.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.meetup.uhoo.R;
import com.meetup.uhoo.core.User;
import com.meetup.uhoo.service_layer.user_services.CurrentUserDataService;
import com.rey.material.widget.Switch;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sultankhan on 11/23/16.
 */
public class ProfilePictureFragment extends Fragment {

    private Button btnSetProfilePicture;
    private ImageView ivProfilePicture;

    private StorageReference storageRef;
    private StorageReference photoRef;
    private DatabaseReference mDatabase;
    private Activity activity;

    public static final int GET_FROM_GALLERY = 3;
    private User user;
    private CurrentUserDataService currentUserDataService;

    public ProfilePictureFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentUserDataService = new CurrentUserDataService(getContext());
        user = currentUserDataService.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Create a storage reference from our app
        storageRef = storage.getReference();

        // Create a reference to profile picture
        photoRef = storageRef.child("images/user_profile_image/" + user.getUid() + "/profile_image.png");



    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity activity;

        if (context instanceof Activity) {
            activity = (Activity) context;
            this.activity = activity;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile_picture, container, false);

        ivProfilePicture = (ImageView) view.findViewById(R.id.ivProfilePicture);

        btnSetProfilePicture = (Button) view.findViewById(R.id.btnSetProfilePicture);
        btnSetProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);

            }
        });



        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {

                URL img_value = null;
                Bitmap mIcon1 = null;

                try {
                    Log.i("profileUrl", user.getPhotoUrl());
                    img_value = new URL(user.getPhotoUrl());
                    mIcon1 = BitmapFactory.decodeStream(img_value.openConnection().getInputStream());
                    final Bitmap finalMIcon = mIcon1;
                    handler.post(new Runnable() {
                        public void run() {
                            ivProfilePicture.setImageBitmap(finalMIcon);
                        }
                    });

                } catch (MalformedURLException e) {
                    Log.e("MalformedURLException", user.getPhotoUrl());
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.e("IOException", user.getPhotoUrl());
                    e.printStackTrace();
                }


            }
        };
        new Thread(runnable).start();


        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //Detects request codes
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;

            // Set image to bitmap
            try {
                bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), selectedImage);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] pictureData = baos.toByteArray();

                float aspectRatio = bitmap.getWidth() / (float) bitmap.getHeight();

                int height = 500;
                int width = Math.round(height * aspectRatio);

                bitmap = Bitmap.createScaledBitmap(
                        bitmap, width, height, false);

                final Handler handler = new Handler();
                final Bitmap finalBitmap = bitmap;
                Runnable runnable = new Runnable() {
                    public void run() {

                        URL img_value = null;
                        Bitmap mIcon1 = null;

                        try {
                            handler.post(new Runnable() {
                                public void run() {
                                    ivProfilePicture.setImageBitmap(finalBitmap);
                                }
                            });

                        } catch (Exception e) {
                            Log.e("IOException", "onActivityResult");
                            e.printStackTrace();
                        }


                    }
                };
                new Thread(runnable).start();

                // Upload photo to firebase
                UploadTask uploadTask = photoRef.putBytes(pictureData);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Log.e("onActivityResult","Upload Faliure: " + exception.toString());
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();

                        currentUserDataService.setPhotoUrl(downloadUrl.toString());
                        currentUserDataService.saveUserDataLocally(getContext());
                        currentUserDataService.saveUserToDatabase();

                        // Trigger Interface
                        ((ProfileActivity) activity).onProfilePictureDataChanged();

                    }
                });

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
