package edu.uw.abourn.audioroam;

import android.content.Intent;
import android.support.annotation.NonNull;
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
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {
    private EditText emailInput;
    private EditText passwordInput;
    private EditText confirmPasswordInput;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        emailInput = (EditText) findViewById(R.id.emailInput);
        passwordInput = (EditText) findViewById(R.id.passwordInput);
        confirmPasswordInput = (EditText) findViewById(R.id.confirmPasswordInput);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // make sure to sign out the current user?? Might not be necessary--how else will they navigate to this screen since you have to be signed out to see the login?
    }

    public void createAccountSignIn(final View v) {
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        String passwordConfirm = confirmPasswordInput.getText().toString();
        boolean formValid = validateForm(email, password, passwordConfirm);
        if (formValid) {
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            sendEmailVerification(v);
                            Intent mainIntent = new Intent(SignUpActivity.this, MapActivity.class);
                            startActivity(mainIntent);
                        } else {
                            Snackbar.make(v, "Error: " + task.getException().getMessage(), Snackbar.LENGTH_SHORT).show(); // ugly, but works
                            /*
                            try {
                                Log.v("SIGN UP:", task.getException().toString());
                                throw task.getException();
                            } catch(FirebaseAuthWeakPasswordException e) {
                                Snackbar.make(v, "Password must be greater than six characters", Snackbar.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Snackbar.make(v, "An error occurred, please try again", Snackbar.LENGTH_SHORT).show();
                            }
                            */
                        }
                    }
                });

        }
    }

    public boolean validateForm(String email, String password, String passwordConfirm) {
        if (email.isEmpty() || !password.equals(passwordConfirm)) {
            return false;
        }
        return true;
        // return !email.isEmpty() || password.equals(passwordConfirm)
    }

    public void sendEmailVerification(final View v) {
        FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
            .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Snackbar.make(v, "Welcome! Please check your email to verify your account!", Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
    }
}
