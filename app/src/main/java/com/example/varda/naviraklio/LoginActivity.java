package com.example.varda.naviraklio;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
//import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.AppCompatButton;
//import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.varda.naviraklio.helpers.InputValidation;
import com.example.varda.naviraklio.sql.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;




/**
 * A login screen that offers login via username/password.
 */
public class LoginActivity extends AppCompatActivity {

    private final AppCompatActivity mActivity = LoginActivity.this;

    /**
     * Id to identity READ_CONTACTS permission request.
     */


    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    /*private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world", "johnis89:55555"
    };*/

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
//    private UserLoginTask mAuthTask = null;

    // UI references.
    private ScrollView mLoginFormView;
    private ProgressBar mProgressView;
    private TextInputLayout mTextInputLayoutUsername;
    private TextInputLayout mTextInputLayoutPassword;

    private AutoCompleteTextView mUsernameView;
    private TextInputEditText mPasswordView;
    private Button mSignInButton, mSignUpBtn;

    // Class references.
    private InputValidation mInputValidation;
    private DatabaseHelper mDatabaseHelper;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    //private GoogleApiClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        // Set up the login form.
        initViews();
        initListeners();
        initObjects();
    }

    /**
     * This method is to initialize views
     */
    private void initViews() {

        mLoginFormView = (ScrollView) findViewById(R.id.login_form);
        mProgressView = (ProgressBar) findViewById(R.id.login_progress);

        mTextInputLayoutUsername = (TextInputLayout) findViewById(R.id.textInputLayoutUsername);
        mTextInputLayoutPassword = (TextInputLayout) findViewById(R.id.textInputLayoutPassword);
        mUsernameView = (AutoCompleteTextView) findViewById(R.id.textInputEditTextUsername);
       // populateAutoComplete();
        mPasswordView = (TextInputEditText) findViewById(R.id.textInputEditTextPassword);

        mSignInButton = (Button) findViewById(R.id.sign_in_button);

        mSignUpBtn = (Button) findViewById(R.id.sign_up_btn);

    }

    /**
     * This method is to initialize listeners
     */
    private void initListeners() {
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


                verifyFromSQLite();

            }
        });
        mSignUpBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSignup = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intentSignup);
            }
        });
    }

    /**
     * This method is to initialize objects to be used
     */
    private void initObjects() {
        mDatabaseHelper = new DatabaseHelper(mActivity);
        mInputValidation = new InputValidation(mActivity);

    }
    /**
     * This implemented method is to listen the click on view
     *
     * @param v
     */


    /**
     * This method is to validate the input text fields and verify login credentials from SQLite
     */
    private void verifyFromSQLite() {
        boolean usernameFilledCheck, usernameShortCheck = false, passwordFilledCheck, passwordShortCheck = false, loginValidCheck;
        usernameFilledCheck = mInputValidation.isAutoCompleteTextViewFilled(mUsernameView, mTextInputLayoutUsername, getString(R.string.error_empty_username));
        if (usernameFilledCheck) {
            usernameShortCheck = mInputValidation.isInputEditTextValidUsername(mUsernameView, mTextInputLayoutUsername, getString(R.string.error_invalid_username));
        }
        passwordFilledCheck = mInputValidation.isInputEditTextFilled(mPasswordView, mTextInputLayoutPassword, getString(R.string.error_empty_password));
        if (passwordFilledCheck) {
            passwordShortCheck = mInputValidation.isInputEditTextValidPassword(mPasswordView, mTextInputLayoutPassword, getString(R.string.error_invalid_password));
        }

        if (usernameFilledCheck && usernameShortCheck && passwordFilledCheck && passwordShortCheck) {
            if (mDatabaseHelper.checkUser(mUsernameView.getText().toString().trim()
                    , mPasswordView.getText().toString().trim())) {
                emptyInputEditText();
                loginValidCheck = true;
            } else {
                loginValidCheck = false;
                mTextInputLayoutUsername.setErrorEnabled(true);
                mTextInputLayoutPassword.setErrorEnabled(true);
                mTextInputLayoutUsername.setError(getString(R.string.error_incorrect_password));
                mTextInputLayoutPassword.setError(getString(R.string.error_incorrect_password));
            }
            if (loginValidCheck) {
                showProgress(true);
                Intent goHome = new Intent(LoginActivity.this, Home.class);
                startActivity(goHome);
            }
        }


    }

    /**
     * This method is to empty all input edit text
     */
    private void emptyInputEditText() {
        mUsernameView.setText(null);
        mPasswordView.setText(null);
    }

    /**
     * mUsernameView = (AutoCompleteTextView) findViewById(R.id.textInputEditTextUsername);
     * populateAutoComplete();
     * <p>
     * mPasswordView = (TextInputEditText) findViewById(R.id.textInputEditTextPassword);
     * mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
     *
     * @Override public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
     * if (id == R.id.login || id == EditorInfo.IME_NULL) {
     * attemptLogin();
     * return true;
     * }
     * return false;
     * }
     * });
     * <p>
     * Button mSignInButton = (Button) findViewById(R.id.sign_in_button);
     * mSignInButton.setOnClickListener(new OnClickListener() {
     * @Override public void onClick(View view) {
     * attemptLogin();
     * }
     * });
     * <p>
     * TextView mSignUpBtn = (TextView) findViewById(R.id.sign_up_link);
     * mSignUpBtn.setOnClickListener(new OnClickListener() {
     * @Override public void onClick(View view) {
     * signup();
     * }
     * });
     * <p>
     * mLoginFormView = (ScrollView) findViewById(R.id.login_form);
     * mProgressView = (ProgressBar) findViewById(R.id.login_progress);
     * // ATTENTION: This was auto-generated to implement the App Indexing API.
     * // See https://g.co/AppIndexing/AndroidStudio for more information.
     * client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
     * }
     */

 /*   private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mUsernameView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }
*/
    /**
     * Callback received when a permissions request has been completed.
     */
  /*  @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }*/


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    /*private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid username.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        } else if (!isUsernameValid(username)) {
            mUsernameView.setError(getString(R.string.error_invalid_username));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(username, password);
            mAuthTask.execute((Void) null);
            Intent goHome = new Intent(LoginActivity.this, Home.class);
            startActivity(goHome);
        }
    }

    private boolean isUsernameValid(String username) {
        //TODO: Replace this with your own logic
        return username.length() > 6;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }*/

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     *//*
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setFirstName("Login Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }*/
    @Override
    protected void onResume() {
        super.onResume();
        showProgress(false);
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    /*public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUsername;
        private final String mPassword;

        UserLoginTask(String username, String password) {
            mUsername = username;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mUsername)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    public void signup() {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }*/
}

