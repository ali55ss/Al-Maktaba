package com.technostacks.almaktaba.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CoursesResponseModel implements Parcelable {

    @SerializedName("code")
    @Expose
    private int code;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("Departmentcourses")
    @Expose
    private List<Departmentcourse> departmentcourses = null;

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

    public List<Departmentcourse> getDepartmentcourses() {
        return departmentcourses;
    }

    public void setDepartmentcourses(List<Departmentcourse> departmentcourses) {
        this.departmentcourses = departmentcourses;
    }

    @Override
    public String toString() {
        return "CoursesResponseModel{" +
                "code=" + code +
                ", url='" + url + '\'' +
                ", message='" + message + '\'' +
                ", departmentcourses=" + departmentcourses +
                '}';
    }

    public static class Departmentcourse implements Parcelable {

        @SerializedName("id")
        @Expose
        private int id;
        @SerializedName("collegedepartment_id")
        @Expose
        private int collegedepartmentId;
        @SerializedName("course_id")
        @Expose
        private int courseId;
        @SerializedName("status")
        @Expose
        private int status;
        @SerializedName("created")
        @Expose
        private String created;
        @SerializedName("modified")
        @Expose
        private String modified;
        @SerializedName("Courses")
        @Expose
        private Courses courses;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getCollegedepartmentId() {
            return collegedepartmentId;
        }

        public void setCollegedepartmentId(int collegedepartmentId) {
            this.collegedepartmentId = collegedepartmentId;
        }

        public int getCourseId() {
            return courseId;
        }

        public void setCourseId(int courseId) {
            this.courseId = courseId;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
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

        public Courses getCourses() {
            return courses;
        }

        public void setCourses(Courses courses) {
            this.courses = courses;
        }


        public static class Courses implements Parcelable {

            @SerializedName("id")
            @Expose
            private int id;
            @SerializedName("course_name")
            @Expose
            private String courseName;
            @SerializedName("course_name_ar")
            @Expose
            private String courseNameAr;
            @SerializedName("course_code")
            @Expose
            private String courseCode;
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

            public String getCourseName() {
                return courseName;
            }

            public void setCourseName(String courseName) {
                this.courseName = courseName;
            }

            public String getCourseNameAr() {
                return courseNameAr;
            }

            public void setCourseNameAr(String courseNameAr) {
                this.courseNameAr = courseNameAr;
            }

            public String getCourseCode() {
                return courseCode;
            }

            public void setCourseCode(String courseCode) {
                this.courseCode = courseCode;
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
                return "Courses{" +
                        "id=" + id +
                        ", courseName='" + courseName + '\'' +
                        ", courseNameAr='" + courseNameAr + '\'' +
                        ", courseCode='" + courseCode + '\'' +
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
                dest.writeValue(this.id);
                dest.writeString(this.courseName);
                dest.writeString(this.courseNameAr);
                dest.writeString(this.courseCode);
                dest.writeValue(this.status);
                dest.writeValue(this.isDeleted);
                dest.writeString(this.created);
                dest.writeString(this.modified);
            }

            public Courses() {
            }

            protected Courses(Parcel in) {
                this.id = (Integer) in.readValue(Integer.class.getClassLoader());
                this.courseName = in.readString();
                this.courseNameAr = in.readString();
                this.courseCode = in.readString();
                this.status = (Integer) in.readValue(Integer.class.getClassLoader());
                this.isDeleted = (Integer) in.readValue(Integer.class.getClassLoader());
                this.created = in.readString();
                this.modified = in.readString();
            }

            public final static Parcelable.Creator<Courses> CREATOR = new Parcelable.Creator<Courses>() {
                @Override
                public Courses createFromParcel(Parcel source) {
                    return new Courses(source);
                }

                @Override
                public Courses[] newArray(int size) {
                    return new Courses[size];
                }
            };
        }

        @Override
        public String toString() {
            return "Departmentcourse{" +
                    "id=" + id +
                    ", collegedepartmentId=" + collegedepartmentId +
                    ", courseId=" + courseId +
                    ", status=" + status +
                    ", created='" + created + '\'' +
                    ", modified='" + modified + '\'' +
                    ", courses=" + courses +
                    '}';
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeValue(this.id);
            dest.writeValue(this.collegedepartmentId);
            dest.writeValue(this.courseId);
            dest.writeValue(this.status);
            dest.writeString(this.created);
            dest.writeString(this.modified);
            dest.writeParcelable(this.courses, flags);
        }

        public Departmentcourse() {
        }

        protected Departmentcourse(Parcel in) {
            this.id = (Integer) in.readValue(Integer.class.getClassLoader());
            this.collegedepartmentId = (Integer) in.readValue(Integer.class.getClassLoader());
            this.courseId = (Integer) in.readValue(Integer.class.getClassLoader());
            this.status = (Integer) in.readValue(Integer.class.getClassLoader());
            this.created = in.readString();
            this.modified = in.readString();
            this.courses = in.readParcelable(Courses.class.getClassLoader());
        }

        public final static Parcelable.Creator<Departmentcourse> CREATOR = new Parcelable.Creator<Departmentcourse>() {
            @Override
            public Departmentcourse createFromParcel(Parcel source) {
                return new Departmentcourse(source);
            }

            @Override
            public Departmentcourse[] newArray(int size) {
                return new Departmentcourse[size];
            }
        };
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
        dest.writeTypedList(this.departmentcourses);
    }

    public CoursesResponseModel() {
    }

    protected CoursesResponseModel(Parcel in) {
        this.code = (Integer) in.readValue(Integer.class.getClassLoader());
        this.url = in.readString();
        this.message = in.readString();
        this.departmentcourses = in.createTypedArrayList(Departmentcourse.CREATOR);
    }

    public final static Parcelable.Creator<CoursesResponseModel> CREATOR = new Parcelable.Creator<CoursesResponseModel>() {
        @Override
        public CoursesResponseModel createFromParcel(Parcel source) {
            return new CoursesResponseModel(source);
        }

        @Override
        public CoursesResponseModel[] newArray(int size) {
            return new CoursesResponseModel[size];
        }
    };
}