package com.test.test.network;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import androidx.core.content.ContextCompat;
import com.test.test.Adapters.ApiCallRequest;
import com.test.test.R;
import com.test.test.custom.Constants;
import org.json.JSONObject;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;


public class ApiCallServiceTask {
    private onApiFinish onApiFinishListener;
    private Context context;
    private boolean isWantToShowToast = true;

    private RelativeLayout rlProgressBarMain;
    private ProgressBar progressBar;

    private final OkHttpClient client;

    public ApiCallServiceTask(Context context) {
        this.context = context;
        client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        onApiFinishListener = (onApiFinish) context;
        isWantToShowToast = true;

        rlProgressBarMain = ((Activity) context).findViewById(R.id.rlProgressBarMain);
        progressBar = ((Activity) context).findViewById(R.id.progressBar);
    }

    public void requestApi(final ApiCallRequest apiCallRequest) {
        if (apiCallRequest.showProgress) {
//            showProgress(apiCallRequest.title);
            showProgressBar(apiCallRequest.progressBg);
        } else {
//            closeProgress();
            hideProgressBar(false, apiCallRequest.progressBg);
        }

        isWantToShowToast = true;
//        if (apiCallRequest.from.equals("SEARCH_API")){
//            isWantToShowToast = false;
//            cancelApiCallWithTag(client, apiCallRequest.from);
//        }


        cancelApiCallWithTag(client, apiCallRequest.from);

        final Request request;//= new Request.Builder().url(url).post(body).build();

        Request.Builder builder = new Request.Builder();
        String URL = apiCallRequest.url;
        if (!URL.contains(Constants.BASE_URL)) {
            URL = Constants.BASE_URL + apiCallRequest.url;
        }

        Log.d(Constants.APP_TAG, "api url:" + URL);
        if (apiCallRequest.requestBody != null) {
            request = builder.url(URL)
                    .post(apiCallRequest.requestBody)
                    .tag(apiCallRequest.from)
                    .build();
        } else {
            request = builder.url(URL)
                    .tag(apiCallRequest.from)
                    .build();
        }

        try {
            if (apiCallRequest.requestBody != null) {
                Request requestCopy = request;
                final Buffer buffer = new Buffer();
                requestCopy.body().writeTo(buffer);
                Log.d(Constants.APP_TAG, "api parameters:" + buffer.readUtf8());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                try {
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ApiCallResponse apiCallResponse = new ApiCallResponse();
                            apiCallResponse.FROM = apiCallRequest.from;
                            apiCallResponse.ERROR_TYPE = ApiCallResponse.FAILED;
                            apiCallResponse.response = null;
                            apiCallResponse.apiCallRequest = apiCallRequest;

                            onApiFinishListener.onApiFinished(apiCallResponse);

                        }
                    });
                    Log.e(Constants.APP_TAG, "ApiCallServiceTask: Connection Failed, exception : " + e.getMessage());
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

//                closeProgress();
                hideProgressBar(true, apiCallRequest.progressBg);
                connectioFailesTaost();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String response2 = response.body().string();
                Log.d(Constants.APP_TAG, apiCallRequest.from + " response: " + response2);
                try {
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ApiCallResponse apiCallResponse = new ApiCallResponse();
                            apiCallResponse.FROM = apiCallRequest.from;
                            apiCallResponse.ERROR_TYPE = ApiCallResponse.SUCCESS;
                            apiCallResponse.response = response2;
                            apiCallResponse.apiCallRequest = apiCallRequest;
                            try {
                                if (response2 == null || response2.trim().length() < 1) {
//
                                    apiCallResponse.ERROR_TYPE = ApiCallResponse.JSON_ERROR;
//
                                } else {
//
                                    JSONObject json = new JSONObject(response2);
                                    if (json.getString("success").equals("1")) {
                                        apiCallResponse.ERROR_TYPE = ApiCallResponse.SUCCESS;
                                    }
                                    else if (json.getString("success").equals("2"));
                                }
                            } catch (Exception e) {
                                apiCallResponse.ERROR_TYPE = ApiCallResponse.JSON_ERROR;
                                e.printStackTrace();
                            }
                            onApiFinishListener.onApiFinished(apiCallResponse);

//                            closeProgress();
                            hideProgressBar(apiCallResponse.ERROR_TYPE != ApiCallResponse.SUCCESS, apiCallRequest.progressBg);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void connectioFailesTaost() {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isWantToShowToast) {
                    if (isNetworkAvailable(context)) {
//                        Toast.makeText(context, "Please connect internet", Toast.LENGTH_SHORT).show();
//                        showToast("Please connect internet");
//                        showToast("Please check your network connection");
                        return;
                    }
//                    Toast.makeText(context, "Connection Failed", Toast.LENGTH_SHORT).show();
//                    showToast("Connection Failed");
//                    showToast("Please check your network connection");
                }
            }
        });
    }

    private void cancelApiCallWithTag(OkHttpClient client, Object tag) {
        try {
            for (Call call : client.dispatcher().queuedCalls()) {
                if (call.request().tag().equals(tag)) {
                    call.cancel();
                    isWantToShowToast = false;
                    Log.e(Constants.APP_TAG, "ApiCall isCanceled:" + call.isCanceled());
                }
            }
            for (Call call : client.dispatcher().runningCalls()) {
                if (call.request().tag().equals(tag)) {
                    isWantToShowToast = false;
                    call.cancel();
                    Log.e(Constants.APP_TAG, "ApiCall isCanceled:" + call.isCanceled());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface onApiFinish {
        void onApiFinished(ApiCallResponse apiCallResponse);
    }

    public void hideProgressBar(final boolean isApiFailed, String progressBg) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
//                    if (rlProgressBarMain != null) {
//                        rlProgressBarMain.setVisibility(View.GONE);
//                    }
                    if ((!isApiFailed && progressBg.equals(ApiCallRequest.WHITE)) ||
                            progressBg.equals(ApiCallRequest.TRANSPARENT) && rlProgressBarMain != null) {
                        rlProgressBarMain.setVisibility(View.GONE);
                    }
                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }

//                    if (isApiFailed && progressBar!=null && progressBar.getVisibility() == View.VISIBLE
//                            && rlProgressBar!=null && rlProgressBar.getVisibility() == View.VISIBLE){
//                        progressBar.setVisibility(View.GONE);
//                    }else {
//                        if (rlProgressBarMain!=null)
//                            rlProgressBarMain.setVisibility(View.GONE);
//                        if (progressBarWithBgTransparent!=null)
//                            progressBarWithBgTransparent.setVisibility(View.GONE);
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void showProgressBar(final String progressBg) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (rlProgressBarMain != null) {
                        if (progressBg != null && progressBg.equals(ApiCallRequest.TRANSPARENT)) {
                            rlProgressBarMain.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));
                        } else {
                            rlProgressBarMain.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                        }
                        rlProgressBarMain.setVisibility(View.VISIBLE);
                        if (progressBar != null) {
                            progressBar.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (Exception e) {
                    Log.d(Constants.APP_TAG, "showProgressBar excep: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    public boolean isNetworkAvailable(Context context) {
        if (context == null) {
            return false;
        }
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = null;
        if (cm != null) {
            info = cm.getActiveNetworkInfo();
        }
        if (info != null && info.isAvailable() && info.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

//    public void showToast(String message) {
//        try {
//            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
//            View layout = inflater.inflate(R.layout.layout_toast_error,
//                    (ViewGroup) ((Activity) context).findViewById(R.id.toast_layout_root));
//            MaterialButton text = layout.findViewById(R.id.buttonToast);
//            text.setText(message);
//            Toast toast = new Toast(context);
//            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 0);
//            toast.setDuration(Toast.LENGTH_SHORT);
//            toast.setView(layout);
//            toast.show();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


}
