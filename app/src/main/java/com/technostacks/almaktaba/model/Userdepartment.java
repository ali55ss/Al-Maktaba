package com.technostacks.almaktaba.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Userdepartment implements Parcelable {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("user_id")
    @Expose
    private int userId;
    @SerializedName("collegedepartment_id")
    @Expose
    private int collegedepartmentId;
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCollegedepartmentId() {
        return collegedepartmentId;
    }

    public void setCollegedepartmentId(int collegedepartmentId) {
        this.collegedepartmentId = collegedepartmentId;
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

    @Override
    public String toString() {
        return "Userdepartment{" +
                "id=" + id +
                ", userId=" + userId +
                ", collegedepartmentId=" + collegedepartmentId +
                ", status=" + status +
                ", isDeleted=" + isDeleted +
                ", created='" + created + '\'' +
                ", modified='" + modified + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.userId);
        dest.writeInt(this.collegedepartmentId);
        dest.writeInt(this.status);
        dest.writeInt(this.isDeleted);
        dest.writeString(this.created);
        dest.writeString(this.modified);
    }

    public Userdepartment() {
    }

    protected Userdepartment(Parcel in) {
        this.id = in.readInt();
        this.userId = in.readInt();
        this.collegedepartmentId = in.readInt();
        this.status = in.readInt();
        this.isDeleted = in.readInt();
        this.created = in.readString();
        this.modified = in.readString();
    }

    public static final Parcelable.Creator<Userdepartment> CREATOR = new Parcelable.Creator<Userdepartment>() {
        @Override
        public Userdepartment createFromParcel(Parcel source) {
            return new Userdepartment(source);
        }

        @Override
        public Userdepartment[] newArray(int size) {
            return new Userdepartment[size];
        }
    };
}