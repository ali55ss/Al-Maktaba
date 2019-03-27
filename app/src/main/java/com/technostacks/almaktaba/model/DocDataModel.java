package com.technostacks.almaktaba.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by techno-110 on 28/3/18.
 */

public class DocDataModel implements Parcelable {

    private int departmentCourseId;
    private String docTitle;
    private String docDescription;

    public DocDataModel(int departmentCourseId, String docTitle, String docDescription) {
        this.departmentCourseId = departmentCourseId;
        this.docTitle = docTitle;
        this.docDescription = docDescription;
    }

    public int getDepartmentCourseId() {
        return departmentCourseId;
    }

    public void setDepartmentCourseId(int departmentCourseId) {
        this.departmentCourseId = departmentCourseId;
    }

    public String getDocTitle() {
        return docTitle;
    }

    public void setDocTitle(String docTitle) {
        this.docTitle = docTitle;
    }

    public String getDocDescription() {
        return docDescription;
    }

    public void setDocDescription(String docDescription) {
        this.docDescription = docDescription;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.departmentCourseId);
        dest.writeString(this.docTitle);
        dest.writeString(this.docDescription);
    }

    protected DocDataModel(Parcel in) {
        this.departmentCourseId = in.readInt();
        this.docTitle = in.readString();
        this.docDescription = in.readString();
    }

    public static final Parcelable.Creator<DocDataModel> CREATOR = new Parcelable.Creator<DocDataModel>() {
        @Override
        public DocDataModel createFromParcel(Parcel source) {
            return new DocDataModel(source);
        }

        @Override
        public DocDataModel[] newArray(int size) {
            return new DocDataModel[size];
        }
    };
}
