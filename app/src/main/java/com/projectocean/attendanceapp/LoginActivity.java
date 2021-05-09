package com.projectocean.attendanceapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LoginActivity extends AppCompatActivity {

    private ProgressDialog pd;

    private FirebaseAuth mAuth;

    private DatabaseReference checkRef;
    private GoogleSignInClient mGoogleSignInClient;
    private ValueEventListener setUserData;

    private Date currentTime;
    private RelativeLayout google;
    private FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        setViews();

        db = FirebaseDatabase.getInstance();

        //progrress dialogue for loading
        setProgressDialog();

        setGoogleAuth();

        setClickListeners();

        checkRef = db.getReference("users");




        //called after the sign in task is successful to check if existing user or new by Checkref
        setUserData= new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                final String uId = mAuth.getCurrentUser().getUid();

                //If checkref doesn't contain the user id the create the new entry in checkref and in users
                // and call phone auth verification
                if (!dataSnapshot.child(uId).exists()) {

                    checkRef.child( uId ).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {


                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){

                                pd.dismiss();
                                //after adding values to checkRef and UserRef go to phone auth activity
                                Intent courseIntent = new Intent(LoginActivity.this, CommonActivity.class);
                                courseIntent.putExtra("loginFlow","yes");
                                startActivity(courseIntent);
                                finish();

                            }
                            else{
                                pd.dismiss();
                                Toast.makeText(getApplicationContext(),"registerdate task: "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }

                        }
                    });


                    //entry exists for the user in CheckRef, so simply call updateUI to board onto dashboard
                } else {
                    updateUI();
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (pd.isShowing()){
                    pd.dismiss();
                }
                Toast.makeText(LoginActivity.this,"checkref: "+databaseError.toException().toString(),Toast.LENGTH_LONG).show();

            }
        };
    }

    private void setClickListeners() {
        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });


    }

    private void setViews() {

        google = findViewById(R.id.google_layout);

    }

    private void setGoogleAuth() {
        //google sign in options for signInClient
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        //google sign in client
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();

        //account holds the google account, this value can be used if user is previously signed in
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

    }

    private void setProgressDialog() {
        pd = new ProgressDialog(LoginActivity.this);
        pd.setMessage("loading");
        pd.setCancelable(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("updateui","onstart calling updateui for transition");

        updateUI();
    }


    void signIn(){


        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 9001);
        pd.show();

    }


    //when user selects his google account
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        pd.dismiss();
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 9001) {
            // The Task returned from this call is always completed, no need to attach a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            firebaseAuthWithGoogle(account);
            // Signed in successfully, show authenticated UI.

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("abc", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        Log.d("abc", "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        //showProgressDialog();
        // [END_EXCLUDE]
        pd.show();

        //generate credentials form the Google sign in account for firebase sign in with credentials
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        //firebase sign in
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        Log.d("updateui","auth complete listener is being called");

                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Bibliofy Authentication Failed.", Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                            mGoogleSignInClient.signOut();
                        }
                        else{
                            Log.d("updateui","Authentication task done with success");
                            checkRef.addListenerForSingleValueEvent(setUserData);
                        }

                    }
                });
    }


    void anonymousSignIn(){
        pd.show();
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            checkRef.addListenerForSingleValueEvent(setUserData);
                        } else {
                            // If sign in fails, display a message to the user.
                            pd.dismiss();
                            Toast.makeText(LoginActivity.this, "Sorry!!! Cant skip this step right now.", Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }

    //for existing users
    void updateUI(){

        //if firebase auth is not null goto dashboard
        if (mAuth.getCurrentUser() != null ) {

            Log.d("updateui","mauth exists updateUI is getting called");

            if (pd.isShowing())
                pd.dismiss();
            startActivity(new Intent(LoginActivity.this, CommonActivity.class));
            finish();

        }
        else{
            Log.d("updateui","mauth is null updateUI is getting called");
        }

    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    Log.d("focus", "touchevent");
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}
