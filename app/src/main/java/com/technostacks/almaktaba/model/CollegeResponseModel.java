package com.technostacks.almaktaba.model;

/**
 * Created by techno-110 on 16/3/18.
 */

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CollegeResponseModel {

    @SerializedName("code")
    @Expose
    private int code;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("Colleges")
    @Expose
    private List<College> colleges = null;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<College> getColleges() {
        return colleges;
    }

    public void setColleges(List<College> colleges) {
        this.colleges = colleges;
    }

    public class College {

        @SerializedName("id")
        @Expose
        private int id;
        @SerializedName("university_id")
        @Expose
        private int universityId;
        @SerializedName("college_name")
        @Expose
        private String collegeName;
        @SerializedName("college_name_ar")
        @Expose
        private String collegeNameAr;
        @SerializedName("status")
        @Expose
        private int status;
        @SerializedName("isDeleted")
        @Expose
        private int isDeleted;
        @SerializedName("created")
        @Expose
        private String created;
        @SerializedName("modified")
        @Expose
        private String modified;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public Integer getUniversityId() {
            return universityId;
        }

        public void setUniversityId(Integer universityId) {
            this.universityId = universityId;
        }

        public String getCollegeName() {
            return collegeName;
        }

        public void setCollegeName(String collegeName) {
            this.collegeName = collegeName;
        }

        public String getCollegeNameAr() {
            return collegeNameAr;
        }

        public void setCollegeNameAr(String collegeNameAr) {
            this.collegeNameAr = collegeNameAr;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public Integer getIsDeleted() {
            return isDeleted;
        }

        public void setIsDeleted(Integer isDeleted) {
            this.isDeleted = isDeleted;
        }

        public String getCreated() {
            return created;
        }

        public void setCreated(String created) {
            this.created = created;
        }

        public String getModified() {
            return modified;
        }

        public void setModified(String modified) {
            this.modified = modified;
        }

    }

}
