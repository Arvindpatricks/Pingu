package com.hackadroid.pingu.com.hackadroid.pingu.daoimpl;

import android.accounts.NetworkErrorException;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.hackadroid.pingu.com.hackadroid.pingu.constants.DDBTableNames;
import com.hackadroid.pingu.com.hackadroid.pingu.dao.DynamoDBDao;
import com.hackadroid.pingu.com.hackadroid.pingu.dao.PinguUserAuthDao;
import com.hackadroid.pingu.com.hackadroid.pingu.datamodel.PinguUserAuthModel;


import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by arvind on 05/03/17.
 */

public class PinguUserAuthImpl implements PinguUserAuthDao {
    CognitoCachingCredentialsProvider credentialsProvider;
    AmazonDynamoDBClient ddbClient;
    private static DynamoDBMapper userAuthmapper;
     Map<String, AttributeValue> item;
    Map<String, AttributeValue> key;
    private DynamoDBDao<PinguUserAuthModel> dynamoDBDao;
    boolean isNewUser= false;
    boolean status = false;

    public PinguUserAuthImpl(CognitoCachingCredentialsProvider credentialsProvider) {
        this.credentialsProvider=credentialsProvider;
        ddbClient= new AmazonDynamoDBClient(credentialsProvider);
        ddbClient.setRegion(Region.getRegion(Regions.US_WEST_2));
        userAuthmapper = new DynamoDBMapper(ddbClient);
        this.dynamoDBDao = new DynamoDBDao<PinguUserAuthModel>(ddbClient,PinguUserAuthModel.class);


    }


    @Override
    public PinguUserAuthModel getData(String email_id) {
        return null;
    }

    @Override
    public PinguUserAuthModel setData(String email_id) {
        return null;
    }

    @Override
    public boolean isNewUser(String email_id) {

        PinguUserAuthModel model = new PinguUserAuthModel();
        model.setEmail_id(email_id);
        final DynamoDBQueryExpression<PinguUserAuthModel> queryExpression = new DynamoDBQueryExpression<PinguUserAuthModel>()
                .withHashKeyValues(model);
        Runnable runnable = new Runnable() {
            public void run() {

                if(dynamoDBDao.readList(queryExpression)==null){
                    isNewUser=true;
                    Log.d("INFO:","NEW User");

                }

            }
        };
        Thread mythread = new Thread(runnable);
        mythread.start();
        Log.d("INFO:","Not new User");

        return isNewUser;

    }

    @Override
    public boolean save(PinguUserAuthModel record) {
        if(record==null){
            return status;
        }
        final PinguUserAuthModel my_record=record;
        new Thread() {
            public void run() {

                try {
                        status = dynamoDBDao.writeRecord(my_record);
                }catch (Exception e1) {
                    e1.printStackTrace();
                }

            }
        }.start();
        return status;
    }



}
