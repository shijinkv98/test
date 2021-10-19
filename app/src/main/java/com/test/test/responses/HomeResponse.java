package com.test.test.responses;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class HomeResponse {
    @SerializedName("code")
    public int code;
    @SerializedName("status")
    public int status;
    @SerializedName("data")
    public List<Data> dataList;

    public static class Data implements Serializable{
        @SerializedName("CAT_Id")
        public int CAT_Id;
        @SerializedName("CAT_Name")
        public int CAT_Name;
        @SerializedName("CAT_Image")
        public int CAT_Image;
    }

}
