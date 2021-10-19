package com.test.test;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;

import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.google.gson.Gson;
import com.test.test.Adapters.ApiCallRequest;
import com.test.test.Adapters.HomeAdapter;
import com.test.test.custom.Constants;
import com.test.test.custom.EndlessRecyclerOnScrollListener;
import com.test.test.network.ApiCallResponse;
import com.test.test.network.ApiCallServiceTask;

import com.test.test.responses.HomeResponse;

import java.util.List;


public class MainActivity extends BaseActivityNew implements ApiCallServiceTask.onApiFinish {
    RecyclerView rvGrid;
    List<HomeResponse.Data> listProducts;
    HomeResponse homeResponse;
    public int width = 0;
    private HomeAdapter adapterOffers;
    private RecyclerView rvOffersNew;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rvGrid = (RecyclerView) findViewById(R.id.rvOffersNew);
        setGridAdapter();
//        setUpOffers();
        getHomeAsync(true,true);
    }

    public void handleResult(ApiCallResponse response) {
        switch (response.ERROR_TYPE) {
            case ApiCallResponse.SUCCESS:
                if (response.FROM.equals(FROM_HOME)) {
                    handleSuccess(response);
                }
                break;

            case ApiCallResponse.JSON_ERROR:
                Log.d(Constants.APP_TAG, "handleResult:JSON_ERROR:" + response.response);
                break;
            default:
                break;
        }
    }

    public void handleSuccess(ApiCallResponse response) {
        try {
            homeResponse = new Gson().fromJson(response.response, HomeResponse.class);
            if (homeResponse.dataList.size() > 0) {
                adapterOffers.setData(homeResponse.dataList);

            } else {
            }


        } catch (Exception e) {
            e.printStackTrace();
            Log.d(Constants.APP_TAG, "handleSuccessEx:" + e.toString());
        }
    }
    public int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    boolean isGrid = true;
    boolean isSet = false;
    public void setGridAdapter() {
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        rvGrid.setLayoutManager(mLayoutManager);
        if (!isSet) {
            rvGrid.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(4), true));
            rvGrid.setItemAnimator(new DefaultItemAnimator());
            isSet = true;
        }
        rvGrid.setAdapter(adapterOffers);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (adapterOffers.getItemViewType(position)) {
                    case 1:
                        return 1;
                    case 0:
                        return 2; //number of columns of the grid
                    default:
                        return -1;
                }
            }
        });
    }
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

//    public void setUpOffers() {
//        rvOffersNew = findViewById(R.id.rvOffersNew);
//        rvOffersNew.setHasFixedSize(true);
//        adapterOffers = new HomeAdapter(MainActivity.this, width);
//        rvOffersNew.setAdapter(adapterOffers);
//        SnapHelper snapHelper = new PagerSnapHelper();
//        snapHelper.attachToRecyclerView(rvOffersNew);
//    }
    final String FROM_HOME = "FROM_HOME";
    private void getHomeAsync(boolean showProgress, boolean isFromSwipe) {

        String URL = "restapi.php?token=Y2F0ZWdvcnk=";
        new ApiCallServiceTask(this).requestApi(new ApiCallRequest(FROM_HOME, URL,
                null, homeResponse == null, ApiCallRequest.WHITE));
    }
    @Override
    public void onApiFinished(ApiCallResponse response) {

        handleResult(response);
    }
}