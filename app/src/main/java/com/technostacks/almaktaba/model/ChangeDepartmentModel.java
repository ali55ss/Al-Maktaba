package com.technostacks.almaktaba.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChangeDepartmentModel {

    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("Userdepartments")
    @Expose
    private Userdepartment userdepartments;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
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

    public Userdepartment getUserdepartments() {
        return userdepartments;
    }

    public void setUserdepartments(Userdepartment userdepartments) {
        this.userdepartments = userdepartments;
    }

    @Override
    public String toString() {
        return "ChangeDepartmentModel{" +
                "code=" + code +
                ", url='" + url + '\'' +
                ", message='" + message + '\'' +
                ", userdepartments=" + userdepartments +
                '}';
    }
}