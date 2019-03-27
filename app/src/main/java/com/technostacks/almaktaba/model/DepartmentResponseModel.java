package com.technostacks.almaktaba.model;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DepartmentResponseModel {

    @SerializedName("code")
    @Expose
    private int code;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("Collegedepartments")
    @Expose
    private List<Collegedepartment> collegedepartments = null;

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

    public List<Collegedepartment> getCollegedepartments() {
        return collegedepartments;
    }

    public void setCollegedepartments(List<Collegedepartment> collegedepartments) {
        this.collegedepartments = collegedepartments;
    }

    @Override
    public String toString() {
        return "DepartmentResponseModel{" +
                "code=" + code +
                ", url='" + url + '\'' +
                ", message='" + message + '\'' +
                ", collegedepartments=" + collegedepartments +
                '}';
    }

    public class Collegedepartment {

        @SerializedName("id")
        @Expose
        private int id;
        @SerializedName("Departments")
        @Expose
        private Departments departments;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public Departments getDepartments() {
            return departments;
        }

        public void setDepartments(Departments departments) {
            this.departments = departments;
        }

        @Override
        public String toString() {
            return "Collegedepartment{" +
                    "id=" + id +
                    ", departments=" + departments +
                    '}';
        }

        public class Departments {

            @SerializedName("department_name")
            @Expose
            private String departmentName;
            @SerializedName("id")
            @Expose
            private int id;

            public String getDepartmentName() {
                return departmentName;
            }

            public void setDepartmentName(String departmentName) {
                this.departmentName = departmentName;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            @Override
            public String toString() {
                return "Departments{" +
                        "departmentName='" + departmentName + '\'' +
                        ", id=" + id +
                        '}';
            }
        }
    }

}