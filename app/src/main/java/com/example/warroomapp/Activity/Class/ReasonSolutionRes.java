package com.example.warroomapp.Activity.Class;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ReasonSolutionRes implements Serializable {

    @SerializedName("data")
    public List<DataItem> data;
    @SerializedName("detail")
    public String detail;
    public List<DataItem> getData() {
        return data;
    }
    public String getDetail() {
        return detail;
    }

    public static class DataItem {
        @SerializedName("pk")
        private Integer pk;

        @SerializedName("errorCode")
        private String errorCode;

        @SerializedName("en_description")
        private String enDescription;

        @SerializedName("th_description")
        private String thDescription;

        @SerializedName("update_emp")
        private String updateEmp;

        @SerializedName("update_date")
        private String updateDate;

        @SerializedName("rating")
        private String rating;

        public Integer getPk() {
            return pk;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public String getEnDescription() {
            return enDescription;
        }

        public String getThDescription() {
            return thDescription;
        }

        public String getUpdateEmp() {
            return updateEmp;
        }

        public String getUpdateDate() {
            return updateDate;
        }
        public String getRating() {
            return rating;
        }
    }
}