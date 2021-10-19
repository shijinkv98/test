package com.test.test.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.RequestOptions;
import com.test.test.MainActivity;
import com.test.test.R;
import com.test.test.custom.GlideApp;
import com.test.test.responses.HomeResponse;

import java.util.ArrayList;
import java.util.List;

public class HomeAdapter  extends RecyclerView.Adapter<com.test.test.Adapters.HomeAdapter.ViewHolder> {

    private List<HomeResponse.Data> list = new ArrayList<>();
    MainActivity activity;
    int width = 0;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout llProductGrid;
        public TextView tvTitle, tvDescription, tvPrice, tvDiscount, tvDiscountPercentage;//, tvRatingCount;
        public ImageView ivProduct;
        public MyViewHolder(View view) {
            super(view);
            tvTitle = (TextView) view.findViewById(R.id.tvGrid);
            ivProduct = (ImageView) view.findViewById(R.id.ivProduct);
            llProductGrid = (LinearLayout) view.findViewById(R.id.llProductGrid);
        }
    }
    public HomeAdapter(MainActivity activity, int width) {
        this.activity = activity;
        this.width = width;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View view = LayoutInflater.from(activity)
                .inflate(R.layout.row_product_grid, parent, false);
        return new ViewHolder(view);

    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final HomeResponse.Data data = list.get(position);
        holder.tvTitle.setText(data.CAT_Name);
        GlideApp.with(activity)
                .load(data.CAT_Image)
                .apply(new RequestOptions().centerCrop())
                .into(holder.iv);

    }

    public List<HomeResponse.Data> getData() {
        return list;
    }

    public void setData(List<HomeResponse.Data> listThumbs) {
        this.list = listThumbs;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView tvTitle;
        public final ImageView iv;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            tvTitle = (TextView) view.findViewById(R.id.tvGrid);
            iv = (ImageView) view.findViewById(R.id.ivProduct);
        }
    }
}
