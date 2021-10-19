package com.test.test.network;

import com.test.test.Adapters.ApiCallRequest;

/**
 * Created by Alisons on 5/9/2018.
 */

public class ApiCallResponse {
    public static final int SUCCESS = 1;
    public static final int FAILED = 2;
    public static final int JSON_ERROR = 3;

    public String FROM = "";
    public String response = "";
    public int ERROR_TYPE;
    public ApiCallRequest apiCallRequest;
}
