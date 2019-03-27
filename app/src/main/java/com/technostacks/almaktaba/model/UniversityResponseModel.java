package com.technostacks.almaktaba.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by techno-110 on 14/3/18.
 */

public class UniversityResponseModel {

    @SerializedName("code")
    @Expose
    private int code;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("Universities")
    @Expose
    private List<University> universities = null;

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

    public List<University> getUniversities() {
        return universities;
    }

    public void setUniversities(List<University> universities) {
        this.universities = universities;
    }

    public class University {

        @SerializedName("id")
        @Expose
        private int id;
        @SerializedName("uni_name")
        @Expose
        private String uniName;
        @SerializedName("uni_name_ar")
        @Expose
        private String uniNameAr;
        @SerializedName("logo")
        @Expose
        private String logo;
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

        public String getUniName() {
            return uniName;
        }

        public void setUniName(String uniName) {
            this.uniName = uniName;
        }

        public String getUniNameAr() {
            return uniNameAr;
        }

        public void setUniNameAr(String uniNameAr) {
            this.uniNameAr = uniNameAr;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getIsDeleted() {
            return isDeleted;
        }

        public void setIsDeleted(int isDeleted) {
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
