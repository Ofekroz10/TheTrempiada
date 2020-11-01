package com.example.thetrempiada;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


public class MainActivity extends AppCompatActivity {

    static final int GOOGLE_SIGN_IN = 123;
    //public static FirebaseAuth mAuth;
    Button btn_login;
    //public static GoogleSignInClient mGoogleSignInClient;
    private GoogleLogin googleLogin;
    private FirebaseAuthentication firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_login = findViewById(R.id.loginGoogle);
        googleLogin = GoogleLogin.getInstance();
        firebaseAuth = FirebaseAuthentication.getInstance();

        //connect android studio to FirebaseAuth via getInstance()
        //mAuth = FirebaseAuth.getInstance();

        //
        googleLogin.setOptions(this);
        googleLogin.getClient(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(firebaseAuth.mAuth.getCurrentUser() != null)
            updateUI(firebaseAuth.mAuth.getCurrentUser());
        btn_login.setOnClickListener(v->signIn());
    }

    private void updateUI(FirebaseUser account){
        boolean login = account == null ? false: true;
        if(login){
            // move to other activity
            Intent i = new Intent(MainActivity.this,AfterLogin.class);
            //Helpers.sendParametersToActivity(i,new Pair<String,Parcelable>("account",account));
            startActivity(i);
        }
    }

    private void signIn(){
        Intent signInIntent = googleLogin.getGoogleSighInIntent();
        startActivityForResult(signInIntent, googleLogin.getGoogleSignInCode());
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == googleLogin.getGoogleSignInCode()) {
            Task<GoogleSignInAccount> task = googleLogin.getSignedInAccountFromIntent(data);
            task.addOnSuccessListener(x->handleSignInSuccess(x));
            task.addOnFailureListener(x->handleSighInFail(x));
        }
    }

    private void handleSignInSuccess(GoogleSignInAccount account) {
        if(account != null) {
            firebaseAuth.sighInFirebase(account)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void handleSighInFail(Exception e){
        Toast.makeText(MainActivity.this, e.toString(),
                Toast.LENGTH_SHORT).show();
    }
}