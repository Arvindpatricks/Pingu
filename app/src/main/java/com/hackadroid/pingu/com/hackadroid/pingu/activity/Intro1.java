package com.hackadroid.pingu.com.hackadroid.pingu.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.*;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;
import com.hackadroid.pingu.R;
import com.hackadroid.pingu.com.hackadroid.pingu.datamodel.PinguUserAuthModel;

import java.sql.Timestamp;

import static android.R.attr.duration;


public class Intro1 extends AppCompatActivity {
    private Button saveButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro1);

        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(), // get the context for the current activity
                "302190334691", // your AWS Account id
                "us-west-2:785a9d24-4114-46d9-ba63-4608ad084552", // your identity pool id
                "arn:aws:iam::302190334691:role/Cognito_PinguUnauth_Role",// an unauthenticated role ARN
                "arn:aws:iam::302190334691:role/Cognito_PinguAuth_Role", // an authenticated role ARN
                Regions.US_WEST_2 //Region
        );


        AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
        ddbClient.setRegion(Region.getRegion(Regions.US_WEST_2));

        final DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);
        Log.d("INFO","Mapper initialised !!");
        this.saveButton = (Button)this.findViewById(R.id.save);
        this.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                Toast.makeText(getApplicationContext(), "Saving in DynamoDB",Toast.LENGTH_LONG).show();

                Runnable runnable = new Runnable() {
                    public void run() {
                        //DynamoDB calls go here
                        PinguUserAuthModel pinguUserAuthModel = new PinguUserAuthModel();
                        pinguUserAuthModel.setEmail_id("subaramesh95@gmail.com");
                        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                        pinguUserAuthModel.setLast_logged_in(timestamp.toString());
                        mapper.save(pinguUserAuthModel);
                    }
                };
                Thread mythread = new Thread(runnable);
                mythread.start();


            }catch (Exception e){
                    Log.d("Error:",e.toString());
                }
            }
        });


    }
}
