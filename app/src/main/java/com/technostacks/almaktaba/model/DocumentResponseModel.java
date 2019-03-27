package com.technostacks.almaktaba.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DocumentResponseModel implements Parcelable {

    @SerializedName("code")
    @Expose
    private int code;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("Documents")
    @Expose
    private List<Document> documents = null;

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

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    @Override
    public String toString() {
        return "DocumentResponseModel{" +
                "code=" + code +
                ", url='" + url + '\'' +
                ", message='" + message + '\'' +
                ", documents=" + documents +
                '}';
    }

    public static class Document implements Parcelable {

        @SerializedName("id")
        @Expose
        private int id;
        @SerializedName("departmentcourse_id")
        @Expose
        private int departmentcourseId;
        @SerializedName("user_id")
        @Expose
        private int userId;
        @SerializedName("doc_title")
        @Expose
        private String docTitle;
        @SerializedName("doc_description")
        @Expose
        private String docDescription;
        @SerializedName("filename")
        @Expose
        private String filename;
        @SerializedName("front_image")
        @Expose
        private String frontImage;
        @SerializedName("mime_type")
        @Expose
        private String mimeType;
        @SerializedName("status")
        @Expose
        private int status;
        @SerializedName("isDeleted")
        @Expose
        private int isDeleted;
        @SerializedName("deleted_by")
        @Expose
        private int deletedBy;
        @SerializedName("created")
        @Expose
        private String created;
        @SerializedName("modified")
        @Expose
        private String modified;
        @SerializedName("Users")
        @Expose
        private Users users;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getDepartmentcourseId() {
            return departmentcourseId;
        }

        public void setDepartmentcourseId(int departmentcourseId) {
            this.departmentcourseId = departmentcourseId;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
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

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public String getFrontImage() {
            return frontImage;
        }

        public void setFrontImage(String frontImage) {
            this.frontImage = frontImage;
        }

        public String getMimeType() {
            return mimeType;
        }

        public void setMimeType(String mimeType) {
            this.mimeType = mimeType;
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

        public int getDeletedBy() {
            return deletedBy;
        }

        public void setDeletedBy(int deletedBy) {
            this.deletedBy = deletedBy;
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

        public Users getUsers() {
            return users;
        }

        public void setUsers(Users users) {
            this.users = users;
        }

        @Override
        public String toString() {
            return "Document{" +
                    "id=" + id +
                    ", departmentcourseId=" + departmentcourseId +
                    ", userId=" + userId +
                    ", docTitle='" + docTitle + '\'' +
                    ", docDescription='" + docDescription + '\'' +
                    ", filename='" + filename + '\'' +
                    ", frontImage='" + frontImage + '\'' +
                    ", mimeType='" + mimeType + '\'' +
                    ", status=" + status +
                    ", isDeleted=" + isDeleted +
                    ", deletedBy=" + deletedBy +
                    ", created='" + created + '\'' +
                    ", modified='" + modified + '\'' +
                    ", users=" + users +
                    '}';
        }

        public static class Users implements Parcelable {

            @SerializedName("id")
            @Expose
            private int id;
            @SerializedName("role_id")
            @Expose
            private int roleId;
            @SerializedName("email")
            @Expose
            private String email;
            @SerializedName("password")
            @Expose
            private String password;
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

            public String getPassword() {
                return password;
            }

            public void setPassword(String password) {
                this.password = password;
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

            @Override
            public String toString() {
                return "Users{" +
                        "id=" + id +
                        ", roleId=" + roleId +
                        ", email='" + email + '\'' +
                        ", password='" + password + '\'' +
                        ", apiKey='" + apiKey + '\'' +
                        ", apiPlainKey='" + apiPlainKey + '\'' +
                        ", firstname='" + firstname + '\'' +
                        ", lastname='" + lastname + '\'' +
                        ", profileImage='" + profileImage + '\'' +
                        ", mobile='" + mobile + '\'' +
                        ", address='" + address + '\'' +
                        ", dateOfBirth=" + dateOfBirth +
                        ", verificationCode='" + verificationCode + '\'' +
                        ", status=" + status +
                        ", registerType=" + registerType +
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
                dest.writeInt(this.roleId);
                dest.writeString(this.email);
                dest.writeString(this.password);
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
            }

            public Users() {
            }

            protected Users(Parcel in) {
                this.id = in.readInt();
                this.roleId = in.readInt();
                this.email = in.readString();
                this.password = in.readString();
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
            }

            public static final Creator<Users> CREATOR = new Creator<Users>() {
                @Override
                public Users createFromParcel(Parcel source) {
                    return new Users(source);
                }

                @Override
                public Users[] newArray(int size) {
                    return new Users[size];
                }
            };
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.id);
            dest.writeInt(this.departmentcourseId);
            dest.writeInt(this.userId);
            dest.writeString(this.docTitle);
            dest.writeString(this.docDescription);
            dest.writeString(this.filename);
            dest.writeString(this.frontImage);
            dest.writeString(this.mimeType);
            dest.writeInt(this.status);
            dest.writeInt(this.isDeleted);
            dest.writeInt(this.deletedBy);
            dest.writeString(this.created);
            dest.writeString(this.modified);
            dest.writeParcelable(this.users, flags);
        }

        public Document() {
        }

        protected Document(Parcel in) {
            this.id = in.readInt();
            this.departmentcourseId = in.readInt();
            this.userId = in.readInt();
            this.docTitle = in.readString();
            this.docDescription = in.readString();
            this.filename = in.readString();
            this.frontImage = in.readString();
            this.mimeType = in.readString();
            this.status = in.readInt();
            this.isDeleted = in.readInt();
            this.deletedBy = in.readInt();
            this.created = in.readString();
            this.modified = in.readString();
            this.users = in.readParcelable(Users.class.getClassLoader());
        }

        public static final Parcelable.Creator<Document> CREATOR = new Parcelable.Creator<Document>() {
            @Override
            public Document createFromParcel(Parcel source) {
                return new Document(source);
            }

            @Override
            public Document[] newArray(int size) {
                return new Document[size];
            }
        };
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
        dest.writeTypedList(this.documents);
    }

    public DocumentResponseModel() {
    }

    protected DocumentResponseModel(Parcel in) {
        this.code = in.readInt();
        this.url = in.readString();
        this.message = in.readString();
        this.documents = in.createTypedArrayList(Document.CREATOR);
    }

    public static final Parcelable.Creator<DocumentResponseModel> CREATOR = new Parcelable.Creator<DocumentResponseModel>() {
        @Override
        public DocumentResponseModel createFromParcel(Parcel source) {
            return new DocumentResponseModel(source);
        }

        @Override
        public DocumentResponseModel[] newArray(int size) {
            return new DocumentResponseModel[size];
        }
    };
}