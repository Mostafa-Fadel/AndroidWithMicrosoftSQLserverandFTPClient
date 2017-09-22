package com.mfadel.angel.firebaseauth;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Auth extends AppCompatActivity {

    private FirebaseAuth Auth;

    private String mCustomToken;

    EditText signupInputEmail, signupInputPassword;
    Button btnSignUp, btnLinkToLogIn;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);


        Auth = FirebaseAuth.getInstance();
        // signupInputLayoutEmail = (EditText) findViewById(R.id.signup_input_email);
        // signupInputLayoutPassword = (TextInputLayout) findViewById(R.id.signup_input_layout_password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        signupInputEmail = (EditText) findViewById(R.id.signup_input_email);
        signupInputPassword = (EditText) findViewById(R.id.signup_input_password);

        btnSignUp = (Button) findViewById(R.id.btn_signup);
        btnLinkToLogIn = (Button) findViewById(R.id.btn_link_login);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();

            }
        });

        btnLinkToLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Auth.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
        private void submitForm() {

            String email = signupInputEmail.getText().toString().trim();
            String password = signupInputPassword.getText().toString().trim();

            if(!checkEmail()) {
                return;
            }
            if(!checkPassword()) {
                return;
            }
            //signupInputLayoutEmail.setErrorEnabled(false);
            //signupInputLayoutPassword.setErrorEnabled(false);

            progressBar.setVisibility(View.VISIBLE);
            //create user
            Auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(Auth.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Toast.makeText(getApplicationContext(), "You are successfully Registered !!", Toast.LENGTH_SHORT).show();
                           // Log.d(TAG,"createUserWithEmail:onComplete:" + task.isSuccessful());
                            progressBar.setVisibility(View.GONE);
                            // If sign in fails, Log the message to the LogCat. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                              //  Log.d(TAG,"Authentication failed." + task.getException());

                            } else {
                               // startActivity(new Intent(Auth.this, UserActivity.class));
                                startActivity(new Intent(Auth.this, Viewdata.class));
                                finish();
                            }
                        }

                    });

        }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = Auth.getCurrentUser();
     //   updateUI(currentUser);
    }


    private boolean checkEmail() {
        String email = signupInputEmail.getText().toString().trim();
        if (email.isEmpty() || !isEmailValid(email)) {

          //  signupInputLayoutEmail.setErrorEnabled(true);
          //  signupInputLayoutEmail.setError(getString(R.string.err_msg_email));
            signupInputEmail.setError(getString(R.string.err_msg_required));
            requestFocus(signupInputEmail);
            return false;
        }
       // signupInputLayoutEmail.setErrorEnabled(false);
        return true;
    }

    private boolean checkPassword() {

        String password = signupInputPassword.getText().toString().trim();
        if (password.isEmpty() || !isPasswordValid(password)) {

           // signupInputLayoutPassword.setError(getString(R.string.err_msg_password));
            signupInputPassword.setError(getString(R.string.err_msg_required));
            requestFocus(signupInputPassword);
            return false;
        }
       // signupInputLayoutPassword.setErrorEnabled(false);
        return true;
    }

    private static boolean isEmailValid(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private static boolean isPasswordValid(String password){
        return (password.length() >= 6);
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}
