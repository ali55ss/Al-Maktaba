package com.technostacks.almaktaba.webservices;

/**
 * Created by techno-110 on 13/3/18.
 */

public class NetworkHelper {


    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final int REGISTER_TYPE_NORMAL = 1;
    public static final int REGISTER_TYPE_FACEBOOK = 2;
    public static final int REGISTER_TYPE_GOOGLE = 3;
    public static final int  ROLE_ID = 2; // 2 is for user and 1 for admin (which is used in admin panel)
    public static final int ROLE_ID_GUEST = 3;
    public static final int SUGGESTION_TYPE_UNI = 1;
    public static final int SUGGESTION_TYPE_COLLEGE = 2;
    public static final int SUGGESTION_TYPE_DEPARTMENT = 3;
    public static final int SUGGESTION_TYPE_COURSE = 4;
    public static final int TYPE_ID_UNI = 0;
    public static final String USER_ID = "user_id";

    /**
     * Response key
     */
    public static final String CODE = "code";
    public static final int CODE200 = 200;
    public static final int CODE100 = 100;
    public static final String MESSAGE = "message";

    // query conditions keys

    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String GET = "get";
    public static final String CONDITIONS = "conditions";
    public static final String TYPE = "type";
    public static final String CONTAIN = "contain";
    public static final String DEVICE_TYPE = "device_type";
    public static final String DEVICETOKEN = "device_token";
    public static final String IS_DELETED = "isDeleted";
    public static final String STATUS = "status";
    public static final String FIELDS  = "fields";
    public static final int STATUS_ACTIVE = 1;
    public static final int STATUS_INACTIVE = 0;
    public static final String GET_ALL = "all";
    public static final String NOT_DELETED = "0";
    public static final String ORDER = "order";
    public static final String ORDER_DESC = "DESC";
    public static final String ORDER_ASC = "ASC";


    // API ENDPOINTS

    public static final String API_LOGIN = "Users/login.json";
    public static final String API_ADD_USER = "Users/add.json";
    public static final String API_FORGOT_PASSWORD = "Users/forgotpassword.json";
    public static final String API_CHANGE_PASSWORD = "Users/changePassword.json";
    public static final String API_LIST_UNIVERSITIES = "universities/index.json";
    //public static final String ADD_UNIVERSITIES = "universities/add.json";
    public static final String API_SUGGEST_UNIVERSITY = "suggestion/add.json";
    public static final String API_UPLOAD_UNIVERSITY_LOGO = "users/uploadImage.json";
    public static final String API_EDIT_USER = "users/edit.json";
    public static final String API_RESEND_CODE = "users/resendcode.json";
    public static final String API_LIST_COLLEGES = "colleges/index.json";
    public static final String API_LIST_DEPARTMENTS = "Collegedepartments.json";
    public static final String API_ADD_DEPARTMENTS = "Userdepartments/add.json";
    public static final String API_EDIT_DEPARTMENTS = "Userdepartments/edit.json";
    public static final String API_LIST_DEPARTMENTCOURSES = "Departmentcourses.json";
    public static final String API_UPLOAD_DOCUMENT = "documents/uploadImage.json";
    public static final String API_ADD_DOCUMENT = "documents/add.json";
    public static final String API_LIST_DOCUMENTS = "documents/index.json";
    public static final String API_DELETE_DOCUMENT = "documents/delete.json";
    public static final String API_ADD_DOCUMENTREPORT = "documentreports/add.json";
    public static final String API_GET_USER = "users/index.json";


    ////////////////////// TABLES functions /////////////////////////

    public static final String TABLE_COLLEGEDEPARTMENTS = "Collegedepartments";
    public static final String TABLE_DEPARTMENTS = "Departments";


    ///////////////////// request params for ADD_USER /////////////////////////

    public static final String ADD_USER_ROLE_ID = "role_id";
    public static final String ADD_USER_EMAIL = "email";
    public static final String ADD_USER_PASSWORD = "password";
    public static final String ADD_USER_REGISTER_TYPE = "register_type";


    ////////////////////// fb/google login request params //////////////////////////////

    public static final String LOGIN_FIRSTNAME = "firstname";
    public static final String LOGIN_LASTNAME = "lastname";
    public static final String LOGIN_PROFILE_URL = "profile_image";

    ////////////////////// add university params /////////////////////////////////////

    public static final String ADD_UNI_NAME = "name";
    public static final String ADD_UNI_LOGO = "logo";
    public static final String UNIVERSITY_IMAGE = "university_image";
    public static final String TYPE_ID = "type_id";
    public static final String SUGGESTION_TYPE = "suggestion_type";


    /////////////////// change password params //////////////////////////////////////////

    public static final String ID = "id";
    public static final String CURRENT_PASS = "cur_password";
    public static final String NEW_PASS = "new_password";


    /////////////////// edit user parama /////////////////////////////////////////////

    public static final String PROFILE_IMAGE = "profile_image";
    public static final String PROFILE_CONTACT_NO = "mobile";

    ////////////////// get college params ////////////////////////////////////////////

    public static final String UNIVERSITY_ID = "university_id";

    ///////////////// list college departments params ///////////////////////////////

    public static final String COLLEGE_ID = "college_id";
    public static final String DEPARTMENT_NAME = "department_name";

    //////////////// add/edit userdepartments api params ////////////////////////////

    public static final String  COLLEGE_DEPARTMENT_ID = "collegedepartment_id";

    //////////////////// DEOARTMENTCOURSES API PARAMS ////////////////////////////

    public static final String DEPARTMENTCOURSES_STATUS = "Departmentcourses.status";
    public static final String CONTAIN_COURSES = "courses";

    //////////////////// add course params //////////////////////////

    public static final String ADD_COURSE_CODE = "course_code";


    /////////////////// upload documents params ////////////////////

    public static final String DOC_FRONT_IMAGE = "front_image";
    public static final String DOC_FILENAME = "filename";
    public static final String DOC_DOCUMENTS = "documents";
    public static final String DOC_DESCRIPTION  = "doc_description";
    public static final String DOC_TITLE = "doc_title";
    public static final String DOC_MIME_TYPE = "mime_type";
    public static final String DOC_DEPARTMENTCOURSE_ID = "departmentcourse_id";


    //////////////////// document listing params //////////////////////

    public static final String DOC_IS_DELETED = "Documents.isDeleted";
    public static final String USERS = "users";
    public static final String DOC_ID = "Documents.id";
    public static final String DOC_DELETE_BY = "deleted_by";


    ////////////////// DOCUMENT REPORT API PARAMS //////////////////

    public static final String DOCUMENT_ID = "document_id";
    public static final String REPORT_TYPE = "report_type";
    public static final String REPORT_DESCRIPTION = "description";

    public static final String VERIFICATION_CODE = "verification_code";


}
