package com.hackadroid.pingu.com.hackadroid.pingu.dao;

import com.hackadroid.pingu.com.hackadroid.pingu.datamodel.PinguUserAuthModel;

/**
 * Created by arvind on 05/03/17.
 */

public interface PinguUserAuthDao {
    public PinguUserAuthModel getData(String email_id);
    public PinguUserAuthModel setData(String email_id);

}
