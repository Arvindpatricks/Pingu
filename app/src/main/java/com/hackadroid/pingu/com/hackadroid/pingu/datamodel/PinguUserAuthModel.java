package com.hackadroid.pingu.com.hackadroid.pingu.datamodel;

import java.text.SimpleDateFormat;
import java.util.Date;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;
import com.amazonaws.services.dynamodbv2.*;
/**
 * Created by arvind on 05/03/17.
 */

@DynamoDBTable(tableName = "PinguUserAuth")
public class PinguUserAuthModel {


    String email_id;
    String last_logged_in;
    String mobile_number;

    @DynamoDBAttribute(attributeName = "_mobile_Number")
    public String getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }


    @DynamoDBHashKey(attributeName = "_email_Id")
    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id=email_id;
    }

    @DynamoDBAttribute(attributeName = "_last_logged_in")
    public String getLastLoggedIn() {
        return last_logged_in;
    }

    public void setLastLoggedIn(String last_logged_in){

        this.last_logged_in=last_logged_in;

    }

    @Override
    public String toString() {
        return "PinguUserAuthModel{" +
                "email_id='" + email_id + '\'' +
                ", last_logged_in='" + last_logged_in + '\'' +
                ", mobile_number='" + mobile_number + '\'' +
                '}';
    }

}
