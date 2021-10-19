package com.test.test.Adapters;

import okhttp3.RequestBody;

public class ApiCallRequest {
    public boolean showProgress = false;
    public String url = "", from = "", progressBg= "";
    public RequestBody requestBody;
    public static final String WHITE = "WHITE";
    public static final String TRANSPARENT = "TRANSPARENT";

    public ApiCallRequest(String from, String url, RequestBody requestBody,
                          boolean showProgress, String progressBg) {
        this.from = from;
        this.showProgress = showProgress;
        this.url = url;
        this.progressBg = progressBg;
        this.requestBody = requestBody;
    }
}
