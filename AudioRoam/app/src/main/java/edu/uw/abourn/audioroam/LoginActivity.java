package edu.uw.abourn.audioroam;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.R.attr.password;

public class LoginActivity extends AppCompatActivity {
    private EditText emailInput;
    private EditText passwordInput;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        emailInput = (EditText) findViewById(R.id.emailInput);
        passwordInput = (EditText) findViewById(R.id.passwordInput);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // user is signed in, should sent intent to show main activity
                    Intent mainIntent = new Intent(LoginActivity.this, MapActivity.class);
                    startActivity(mainIntent);
                } else {
                    // User is signed out;
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void signIn(final View v) {
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        boolean valid = validateLogin(v, email, password);
        if (valid) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent mainIntent = new Intent(LoginActivity.this, MapActivity.class);
                                startActivity(mainIntent);
                            } else {
                                Snackbar.make(v, "Incorrect email or password!", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    public boolean validateLogin(View v, String email, String password) {
        if (email.isEmpty()) {
            Snackbar.make(v, "Email cannot be blank!", Snackbar.LENGTH_SHORT).show();
        }else if (password.isEmpty()) {
            Snackbar.make(v, "Password cannot be blank!", Snackbar.LENGTH_SHORT).show();
        }
        return !email.isEmpty() && !password.isEmpty();
    }

    public void forgotPasswordCallback(final View v) {
        String email = emailInput.getText().toString();
        if (email.isEmpty()) {
            Snackbar.make(v, "Specify the email we should send password reset to", Snackbar.LENGTH_SHORT).show();
        } else {
            mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Snackbar.make(v, "Password reset email sent!", Snackbar.LENGTH_SHORT).show();
                        } else {
                            Snackbar.make(v, "An error occurred sending your reset email", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
        }
    }

    public void signUpCallback(View v) {
        // send intent to the SignUp Activity
    }
}
