package com.meetup.uhoo.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v7.widget.PopupMenu;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.meetup.uhoo.Enum;
import com.meetup.uhoo.R;
import com.meetup.uhoo.core.User;
import com.meetup.uhoo.profile.ProfileActivity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by sultankhan on 11/22/16.
 */
public class ProfileRowView extends FrameLayout {

    private String firstName;
    private String lastName;
    private String oneLiner;
    private String userId;
    private String profileUrl;
    private int visibilityPermission;
    private int type;
    private Enum.CheckinVisibilityState checkinVisibilityState;

    private Context context;
    private SharedPreferences sharedPrefs;
    private DatabaseReference mDatabase;

    private TextView tvOneLiner;
    private TextView tvCheckinState;
    private ImageView ivCheckingState;
    private CircleImageView profileImage;
    private LinearLayout llCheckinStatus;


    public ProfileRowView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public ProfileRowView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Get Attributes
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ProfileRowView,
                0, 0
        );

        // Save attribute values
        try {
            firstName = a.getString(R.styleable.ProfileRowView_userFirstName);
            lastName = a.getString(R.styleable.ProfileRowView_userLastName);
            visibilityPermission = a.getInt(R.styleable.ProfileRowView_visibilityPermission,2);
            type = a.getInt(R.styleable.ProfileRowView_type,1);

            if(type == 1){
                userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            }
        } finally {
            a.recycle();
        }

        initView(context);
    }

    public ProfileRowView(Context context) {
        super(context);
        initView(context);
    }

    private void initView(final Context context) {
        View view = inflate(getContext(), R.layout.custom_view_profile_row, null);

        this.context = context;
        sharedPrefs = context.getSharedPreferences("name", 0);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        tvOneLiner = (TextView) view.findViewById(R.id.miniBio);
        tvCheckinState = (TextView) view.findViewById(R.id.tvCheckinState);
        ivCheckingState = (ImageView) view.findViewById(R.id.ivCheckinState);
        profileImage = (CircleImageView) view.findViewById(R.id.profileImage);
        llCheckinStatus = (LinearLayout) view.findViewById(R.id.llCheckinStatus);


        // If type is Edit, then load current user data
        if(type == 2){
            RefreshCurrentUserData();

            // Setup checkin status
            llCheckinStatus.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Creating the instance of PopupMenu
                    PopupMenu popup = new PopupMenu(context, ivCheckingState);
                    //Inflating the Popup using xml file
                    popup.getMenuInflater()
                            .inflate(R.menu.popup_availability, popup.getMenu());

                    //registering popup with OnMenuItemClickListener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            Toast.makeText(context,
                                    "Status Changed: " + item.getTitle(),
                                    Toast.LENGTH_SHORT
                            ).show();

                            if(item.getTitle().equals("Busy")){
                                setCheckinVisibilityState(Enum.CheckinVisibilityState.BUSY);
                            }
                            else{
                                setCheckinVisibilityState(Enum.CheckinVisibilityState.AVAILABLE);
                            }

                            SaveProfileDataToDatabase();


                            return true;
                        }
                    });

                    popup.show(); //showing popup menu
                }
            });

        }

        // If the type is Self, then set listeners
        if(type == 1) {

            RefreshCurrentUserData();

            profileImage.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), ProfileActivity.class);
                    getContext().startActivity(intent);
                }
            });

            tvOneLiner.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), ProfileActivity.class);
                    getContext().startActivity(intent);
                }
            });

            llCheckinStatus.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Creating the instance of PopupMenu
                    PopupMenu popup = new PopupMenu(context, ivCheckingState);
                    //Inflating the Popup using xml file
                    popup.getMenuInflater()
                            .inflate(R.menu.popup_availability, popup.getMenu());

                    //registering popup with OnMenuItemClickListener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            Toast.makeText(context,
                                    "Status Changed: " + item.getTitle(),
                                    Toast.LENGTH_SHORT
                            ).show();

                            if(item.getTitle().equals("Busy")){
                                setCheckinVisibilityState(Enum.CheckinVisibilityState.BUSY);
                            }
                            else{
                                setCheckinVisibilityState(Enum.CheckinVisibilityState.AVAILABLE);
                            }

                            SaveProfileDataToDatabase();


                            return true;
                        }
                    });

                    popup.show(); //showing popup menu
                }
            });
        }





        addView(view);
    }


    public void RefreshCurrentUserData(){

        // Get User Data if it Exists
        sharedPrefs = context.getSharedPreferences("currentUser", 0);
        oneLiner = sharedPrefs.getString("oneLiner", "");
        firstName = sharedPrefs.getString("firstName","");
        lastName = sharedPrefs.getString("lastName", "");
        profileUrl = sharedPrefs.getString("photoUrl","");

        Log.d("photoUrl", profileUrl);

        setCheckinVisibilityState (Enum.CheckinVisibilityState.values()[ sharedPrefs.getInt("checkinVisibilityState",0)]);




        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {

                URL img_value = null;
                Bitmap mIcon1 =  null;

                try {
                    img_value = new URL(profileUrl);
                    mIcon1 = BitmapFactory.decodeStream(img_value.openConnection().getInputStream());

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                final Bitmap finalMIcon = mIcon1;
                handler.post(new Runnable(){
                    public void run() {
                        profileImage.setImageBitmap(finalMIcon);
                    }
                });

            }
        };
        new Thread(runnable).start();

        // Populate user data
        tvOneLiner.setText(oneLiner);
    }




    public void SaveProfileDataToDatabase(){

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/firstName/", firstName);
        childUpdates.put("/lastName/", lastName);
        childUpdates.put("/oneLiner/", oneLiner);
        childUpdates.put("/checkinVisibilityState/", checkinVisibilityState.ordinal());

        mDatabase.child("users").child(userId).updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {

                }
            }
        });

        SaveProfileDataToLocalCurrentUser();

    }

    public void SaveProfileDataToLocalCurrentUser(){
        // Save Data Locally
        SharedPreferences.Editor editor = context.getSharedPreferences("currentUser", 0).edit();
        editor.putString("firstName", firstName);
        editor.putString("lastName", lastName);
        editor.putString("oneLiner",oneLiner);
        editor.putInt("checkinVisibilityState", checkinVisibilityState.getValue());
        editor.apply();
    }


    /* Setter Functions
     * Sets different values and updates the view objects
     **********************************************************************************************/
    public void setUserId(String userId){
        this.userId = userId;
    }


    public void setOneLiner(String oneLiner){
        this.oneLiner = oneLiner;
        this.tvOneLiner.setText(oneLiner);
    }

    public void setProfileUrl(final String profileUrl){
        this.profileUrl = profileUrl;

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {

                URL img_value = null;
                Bitmap mIcon1 =  null;

                try {
                    Log.i("profileUrl", profileUrl);
                    img_value = new URL(profileUrl);
                    mIcon1 = BitmapFactory.decodeStream(img_value.openConnection().getInputStream());
                    final Bitmap finalMIcon = mIcon1;
                    handler.post(new Runnable(){
                        public void run() {


                            profileImage.setImageBitmap(finalMIcon);


                            // Get user auth type. If anon user not looking at themself, then tell mask profile image
                            SharedPreferences prefs = context.getSharedPreferences("currentUser", context.MODE_PRIVATE);
                            String authType = prefs.getString("authType", null);
                            if (authType != null && authType.equals("ANON") && type != 1) {

                                blurProfile();
                            }


                        }
                    });

                } catch (MalformedURLException e) {
                    Log.e("MalformedURLException", profileUrl);
                    e.printStackTrace();

                    profileImage.setImageBitmap(null);
                } catch (IOException e) {
                    Log.e("IOException", profileUrl);
                    e.printStackTrace();

                    profileImage.setImageBitmap(null);
                }



            }
        };
        new Thread(runnable).start();
    }

    public void setCheckinVisibilityState(Enum.CheckinVisibilityState checkinVisibilityState){
        this.checkinVisibilityState = checkinVisibilityState;

        switch (checkinVisibilityState){
            case AVAILABLE:
                ivCheckingState.setImageResource(R.drawable.fab_checkin_status_available);
                tvCheckinState.setText("Approach Me");
                break;
            /*
            case CHECK:
                ivCheckingState.setImageResource(R.drawable.fab_checkin_status_check);
                tvCheckinState.setText("Check");
                break;
                */
            case BUSY:
                ivCheckingState.setImageResource(R.drawable.fab_checkin_status_busy);
                tvCheckinState.setText("Busy");
                break;
            default:
                break;
        }

    }

    public void setData(User user){
        setOneLiner( user.getOneLiner());
        setCheckinVisibilityState(Enum.CheckinVisibilityState.values()[ user.getCheckinVisibilityState()]);
        setProfileUrl(user.getPhotoUrl());
    }


    public Enum.CheckinVisibilityState getCheckinVisibilityState(){
        return checkinVisibilityState;
    }


    public Drawable getProfileDrawable(){
        return profileImage.getBackground();
    }


    @SuppressLint("NewApi")
    public static Bitmap blurRenderScript(Context context,Bitmap smallBitmap, int radius) {
        try {
            smallBitmap = RGB565toARGB888(smallBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bitmap bitmap = Bitmap.createBitmap(
                smallBitmap.getWidth(), smallBitmap.getHeight(),
                Bitmap.Config.ARGB_8888);

        RenderScript renderScript = RenderScript.create(context);

        Allocation blurInput = Allocation.createFromBitmap(renderScript, smallBitmap);
        Allocation blurOutput = Allocation.createFromBitmap(renderScript, bitmap);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript,
                Element.U8_4(renderScript));
        blur.setInput(blurInput);
        blur.setRadius(radius); // radius must be 0 < r <= 25
        blur.forEach(blurOutput);

        blurOutput.copyTo(bitmap);
        renderScript.destroy();

        return bitmap;

    }

    private static Bitmap RGB565toARGB888(Bitmap img) throws Exception {
        int numPixels = img.getWidth() * img.getHeight();
        int[] pixels = new int[numPixels];

        //Get JPEG pixels.  Each int is the color values for one pixel.
        img.getPixels(pixels, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());

        //Create a Bitmap of the appropriate format.
        Bitmap result = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);

        //Set RGB pixels.
        result.setPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());
        return result;
    }

    private void blurProfile(){
        Log.i("ProfileRowView", "blurProfile");
        Bitmap bm=((BitmapDrawable) profileImage.getDrawable()).getBitmap();
        profileImage.setImageBitmap(blurRenderScript(context,bm, 25));
    }


}
