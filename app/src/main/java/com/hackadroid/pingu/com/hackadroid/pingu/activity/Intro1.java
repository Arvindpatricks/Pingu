package com.hackadroid.pingu.com.hackadroid.pingu.activity;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.*;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.hackadroid.pingu.R;
import com.hackadroid.pingu.com.hackadroid.pingu.daoimpl.PinguUserAuthImpl;
import com.hackadroid.pingu.com.hackadroid.pingu.datamodel.PinguUserAuthModel;
import com.google.android.gms.auth.api.signin.*;

import org.apache.log4j.Logger;

import java.sql.Timestamp;

public class Intro1 extends AppCompatActivity  {
    private  GoogleApiClient mGoogleApiClient;
    private SignInButton signInButton;
    private static final int RC_SIGN_IN = 0;
    private static DynamoDBMapper mapper;
    PinguUserAuthImpl pinguUserAuthimpl;
    private static final Logger log = Logger.getLogger(Intro1.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_intro1);
        this.signInButton=(SignInButton)findViewById(R.id.sign_in_button);
        this.signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(), // get the context for the current activity
                "302190334691", // your AWS Account id
                "us-west-2:785a9d24-4114-46d9-ba63-4608ad084552", // your identity pool id
                "arn:aws:iam::302190334691:role/Cognito_PinguUnauth_Role",// an unauthenticated role ARN
                "arn:aws:iam::302190334691:role/Cognito_PinguAuth_Role", // an authenticated role ARN
                Regions.US_WEST_2 //Region
        );
        pinguUserAuthimpl=new PinguUserAuthImpl(credentialsProvider);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            log.info("SignInResult:"+result.isSuccess());
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {

        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            if(pinguUserAuthimpl.isNewUser(acct.getEmail())){
                Toast.makeText(getApplicationContext(),"NEW USER..",Toast.LENGTH_LONG).show();
                //TODO: Move to User Registration Page
            }
            else{
                log.info("User already exists.. Saving data..");
                updateLastLoggedIn(acct);
                //TODO: Move to next page with user credentials
            }

        } else {
            Toast.makeText(getApplicationContext(),"Login Failed !!", Toast.LENGTH_LONG).show();
        }
    }

    private void updateLastLoggedIn(final GoogleSignInAccount googleSignInAccount){
        try{

            Toast.makeText(getApplicationContext(), "Saving in DynamoDB",Toast.LENGTH_LONG).show();
            PinguUserAuthModel pinguUserAuthModel = new PinguUserAuthModel();
            pinguUserAuthModel.setEmail_id(googleSignInAccount.getEmail());
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            pinguUserAuthModel.setLastLoggedIn(timestamp.toString());
            if(pinguUserAuthimpl.save(pinguUserAuthModel)){
                log.info("Updated last logged in Successfully !!");
            }
        }catch (Exception e){
            log.error(e.toString());
        }
    }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
}
