package com.technostacks.almaktaba.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginResponseModel implements Parcelable {

    @SerializedName("code")
    @Expose
    private int code;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("Users")
    @Expose
    private List<User> users = null;

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

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.code);
        dest.writeString(this.url);
        dest.writeString(this.message);
        dest.writeList(this.users);
    }

    public LoginResponseModel() {
    }

    protected LoginResponseModel(Parcel in) {
        this.code = in.readInt();
        this.url = in.readString();
        this.message = in.readString();
        this.users = new ArrayList<User>();
        in.readList(this.users, User.class.getClassLoader());
    }

    public static final Parcelable.Creator<LoginResponseModel> CREATOR = new Parcelable.Creator<LoginResponseModel>() {
        @Override
        public LoginResponseModel createFromParcel(Parcel source) {
            return new LoginResponseModel(source);
        }

        @Override
        public LoginResponseModel[] newArray(int size) {
            return new LoginResponseModel[size];
        }
    };
}