package com.technostacks.almaktaba.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResendCodeResponse implements Parcelable {

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
    private User users;

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

    public User getUsers() {
        return users;
    }

    public void setUsers(User users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "ResendCodeResponse{" +
                "code=" + code +
                ", url='" + url + '\'' +
                ", message='" + message + '\'' +
                ", users=" + users +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.code);
        dest.writeString(this.url);
        dest.writeString(this.message);
        dest.writeParcelable(this.users, flags);
    }

    public ResendCodeResponse() {
    }

    protected ResendCodeResponse(Parcel in) {
        this.code =  in.readInt();
        this.url = in.readString();
        this.message = in.readString();
        this.users = in.readParcelable(User.class.getClassLoader());
    }

    public static final Parcelable.Creator<ResendCodeResponse> CREATOR = new Parcelable.Creator<ResendCodeResponse>() {
        @Override
        public ResendCodeResponse createFromParcel(Parcel source) {
            return new ResendCodeResponse(source);
        }

        @Override
        public ResendCodeResponse[] newArray(int size) {
            return new ResendCodeResponse[size];
        }
    };
}