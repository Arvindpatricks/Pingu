package com.hackadroid.pingu.com.hackadroid.pingu.dao;

import android.accounts.NetworkErrorException;

import com.hackadroid.pingu.com.hackadroid.pingu.datamodel.PinguUserAuthModel;

/**
 * Created by arvind on 05/03/17.
 */

public interface PinguUserAuthDao {
    public PinguUserAuthModel getData(String email_id);
    public PinguUserAuthModel setData(String email_id);
    public boolean isNewUser (String email_id) throws NetworkErrorException;
    public boolean save(PinguUserAuthModel record);


    }
