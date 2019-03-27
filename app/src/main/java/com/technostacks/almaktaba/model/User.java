package com.technostacks.almaktaba.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class User implements Parcelable {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("role_id")
    @Expose
    private int roleId;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("api_key")
    @Expose
    private String apiKey;
    @SerializedName("api_plain_key")
    @Expose
    private String apiPlainKey;
    @SerializedName("firstname")
    @Expose
    private String firstname;
    @SerializedName("lastname")
    @Expose
    private String lastname;
    @SerializedName("profile_image")
    @Expose
    private String profileImage;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("date_of_birth")
    @Expose
    private String dateOfBirth;
    @SerializedName("verification_code")
    @Expose
    private String verificationCode;
    @SerializedName("status")
    @Expose
    private int status;
    @SerializedName("register_type")
    @Expose
    private int registerType;
    @SerializedName("isDeleted")
    @Expose
    private int isDeleted;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("modified")
    @Expose
    private String modified;
    @SerializedName("userdepartments")
    @Expose
    private List<Userdepartment> userdepartments = null;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiPlainKey() {
        return apiPlainKey;
    }

    public void setApiPlainKey(String apiPlainKey) {
        this.apiPlainKey = apiPlainKey;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getRegisterType() {
        return registerType;
    }

    public void setRegisterType(int registerType) {
        this.registerType = registerType;
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

    public List<Userdepartment> getUserdepartments() {
        return userdepartments;
    }

    public void setUserdepartments(List<Userdepartment> userdepartments) {
        this.userdepartments = userdepartments;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.roleId);
        dest.writeString(this.email);
        dest.writeString(this.apiKey);
        dest.writeString(this.apiPlainKey);
        dest.writeString(this.firstname);
        dest.writeString(this.lastname);
        dest.writeString(this.profileImage);
        dest.writeString(this.mobile);
        dest.writeString(this.address);
        dest.writeString(this.dateOfBirth);
        dest.writeString(this.verificationCode);
        dest.writeInt(this.status);
        dest.writeInt(this.registerType);
        dest.writeInt(this.isDeleted);
        dest.writeString(this.created);
        dest.writeString(this.modified);
        dest.writeList(this.userdepartments);
    }

    public User() {
    }

    protected User(Parcel in) {
        this.id = in.readInt();
        this.roleId = in.readInt();
        this.email = in.readString();
        this.apiKey = in.readString();
        this.apiPlainKey = in.readString();
        this.firstname = in.readString();
        this.lastname = in.readString();
        this.profileImage = in.readString();
        this.mobile = in.readString();
        this.address = in.readString();
        this.dateOfBirth = in.readString();
        this.verificationCode = in.readString();
        this.status = in.readInt();
        this.registerType = in.readInt();
        this.isDeleted = in.readInt();
        this.created = in.readString();
        this.modified = in.readString();
        this.userdepartments = new ArrayList<Userdepartment>();
        in.readList(this.userdepartments, Userdepartment.class.getClassLoader());
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", roleId=" + roleId +
                ", email='" + email + '\'' +
                ", apiKey='" + apiKey + '\'' +
                ", apiPlainKey='" + apiPlainKey + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", profileImage='" + profileImage + '\'' +
                ", mobile='" + mobile + '\'' +
                ", address='" + address + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", verificationCode='" + verificationCode + '\'' +
                ", status=" + status +
                ", registerType=" + registerType +
                ", isDeleted=" + isDeleted +
                ", created='" + created + '\'' +
                ", modified='" + modified + '\'' +
                ", userdepartments=" + userdepartments +
                '}';
    }
}