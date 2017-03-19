package com.hackadroid.pingu.com.hackadroid.pingu.dao;

/**
 * Created by krvind on 3/19/17.
 */

import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedParallelScanList;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedQueryList;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.model.ResourceInUseException;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;

/**
 * @author Arvind
 * Defines the common DAO object for FCL Tables.
 * @param <T> - Generic parameter that can except any DynamoDB data model
 */
public class DynamoDBDao<T> {

    private static final Integer MAX_ATTEMPTS = 3;
    private DynamoDBMapper mapper;
    private Class<T> clazz;

    /**
     * Public constructor
     * @param dbHandlerClient -  DynamoDB Handler Client
     * @param clazz - target class
     */
    public DynamoDBDao(AmazonDynamoDBClient dbHandlerClient, Class<T> clazz)
    {
        this.mapper = new DynamoDBMapper(dbHandlerClient);
        this.clazz = clazz;
    }

    /**
     * Write a record in DynamoDB Table
     * @param record -  record to be written
     * @return -  returns true for success  and false for failure
     */
    public boolean writeRecord(T record)
    {

        boolean written = false;
        boolean failed = false;
        int attempts = 0;

        // Return if the record is null
        if(record == null)
            return false;

        while (!written && !failed) {
            try {
                try {
                    mapper.save(record);
                    written = true;
                }
                catch (ResourceNotFoundException e) {
                    //Log error here
                    failed = true;
                    throw e;
                }
            }
            catch (ResourceInUseException e) {
                if(attempts == MAX_ATTEMPTS) {
                    //Log Error
                    failed = true;
                    throw e;
                }
                try {
                    Thread.sleep(15 * 1000);
                }
                catch (InterruptedException e1) {
                }
                attempts ++;

            }
            catch (ConditionalCheckFailedException e) {
                // Throw the ConditionalCheckException as it is.
                // updateDemandTrendAtomically() catches the exception and
                // performs a retry
                //log.debug("Conditional check failed while writing: " + record,e);
                failed = true;
                throw e;
            }
            catch (Exception e) {
                // Catch any other exception and log it.
                //log.error("Error while writing record " + record + " error: ",e);
                throw new RuntimeException(e);
            }
        }
        return !failed;
    }


    /**
     * write a list of records in DynamoDB tables
     * @param records -  List of records to be written
     * @return - true if success anf false if failure
     */
    public boolean writeRecordBatch(List<T> records)
    {
        boolean written = false;
        boolean failed = false;
        int attempts = 0;

        if(records == null)
            return false;

        while (!written && !failed) {
            try {
                try {
                    mapper.batchWrite(records, new LinkedList<Object>());
                    written = true;
                }
                catch (ResourceNotFoundException e) {
                    //Log error here
                    failed = true;
                    throw e;
                }
            }
            catch (ResourceInUseException e)
            {
                if(attempts == MAX_ATTEMPTS) {
                    //Log Error
                    failed = true;
                    throw e;
                }
                try {
                    Thread.sleep(15 * 1000);
                }
                catch (InterruptedException e1) {
                }
                attempts ++;

            }
            catch (ConditionalCheckFailedException e) {
                // Throw the ConditionalCheckException as it is.
                // updateDemandTrendAtomically() catches the exception and
                // performs a retry
                //log.debug("Conditional check failed while writing: " + record,e);
                failed = true;
                throw e;
            }
            catch (Exception e) {
                // Catch any other exception and log it.
                //log.error("Error while writing record " + record + " error: ",e);
                throw new RuntimeException(e);
            }
        }
        return !failed;
    }


    /**
     * read a single record based from DynamoDB Table
     * @param hashKey - haskkey object
     * @param rangeKey - rangekey object
     * @return -  read record based on haskey and range key
     */
    public T readRecord(Object hashKey, Object rangeKey)
    {
        if (rangeKey != null)
        {
            return mapper.load(clazz, hashKey, rangeKey);
        }
        else
        {
            return mapper.load(clazz, hashKey);
        }
    }


    /**
     * Read a list of records based on query
     * @param query - query expression
     * @return - PaginatedQueryLits containing list of result records
     */
    public PaginatedQueryList<T> readList(DynamoDBQueryExpression<T> query)
    {
        PaginatedQueryList<T> result = mapper.query(clazz, query);
        return result;
    }

    /**
     * delete a record from DynamoDB Table
     * @param record -  record to be deleted
     * @return - true if succeeded and false for failure
     */
    public boolean deleteObject(T record)
    {
        if(record == null)
            return false;
        mapper.delete(record);
        return true;
    }


    /**
     * Scan all the records from DynamoDB Table based on scanExpression
     * @param scanExpression -  scan Expression
     * @return -  OasginatedScanList containing list of all the records matching the Expression
     */
    public PaginatedScanList<T> getAll(DynamoDBScanExpression scanExpression)
    {
        PaginatedScanList<T> result = mapper.scan(clazz, scanExpression);
        return result;
    }


    /**
     * Scan all the records parallely based on segments from DynamoDBVTables
     * @param scanExpression -  scan Expression
     * @param segments -  segments
     * @return -  PaginatedParallel list of result records
     */
    public PaginatedParallelScanList<T> getAll(DynamoDBScanExpression scanExpression, int segments)
    {
        PaginatedParallelScanList<T> result = mapper.parallelScan(clazz,scanExpression, segments);
        return result;
    }


}
