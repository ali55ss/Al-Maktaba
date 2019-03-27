package com.technostacks.almaktaba.util;

import android.os.Environment;

/**
 * Created by techno-110 on 13/3/18.
 */

public class Const {

    public static final String CURRENT_LANGUAGE = "language";
    public static final String ARABIC_LANG_CODE = "ar";
    public static final String DV_LANG_CODE = "dv";
    public static final String FA_LANG_CODE = "fa";


    public static final String LOGIN_EMAIL = "email";
    public static final String LOGIN_PASSWORD = "password";
    public static final String IS_LOGIN = "is_login";
    public static final String LOGIN_RESPONSE = "login_response";
    public static final String DOCUMENT_RESPONSE = "document_response";
    public static final String VERIFICATION_CODE = "verification";
    public static final String DEPARTMENT_RESPONSE = "dept_response";

    public static final int OPEN_GALLERY_REQUESTCODE = 41;
    public static final int OPEN_CAMERA_REQUESTCODE = 42;
    public static final String SUGGEST_SCREEN = "suggest";
    public static final int SUGGEST_UNI = 11;
    public static final int SUGGEST_COLLEGE = 12;
    public static final int SUGGEST_DEPARTMENT = 13;
    public static final int SUGGEST_COURSE = 14;
    public static final String UNI_ID = "uni_id";
    public static final String COLLEGE_ID = "college_id";
    public static final String IS_FROM_SETTINGS = "IS_SETTINGS";
    public static final String IS_GUEST_USER = "IS_GUEST";
    public static final String IS_FROM_UPLOAD_DOC = "IS_FROM_UPLOAD_DOC";
    public static final String COURSE_DATA = "COURSE_DATA";
    public static final int POST_REPORT_CLICK = 1;
    public static final int POST_DOWNLOAD_CLICK = 2;
    public static final int POST_EDIT_CLICK = 3;
    public static final String MIME_TYPE_MP4 = "mp4";
    public static final String MIME_TYPE_MOV = "mov";
    public static final String MIME_TYPE_PDF = "pdf";
    public static final String FOLDER_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String DOC_URL = "DOC_URL";

    public static final int REQUEST_CROP_IMAGE = 131;
    public static final int RESULT_CROP_IMAGE = 132;

    public final static String FROM = "from";
    public final static String POSITION = "position";
    final static public int CROP_IMAGE = 100;
    public static final int REQUEST_SCAN_DOC = 121;
    public static final int RESULT_SCAN_DOC = 122;

    public static final String TEMP_FOLDER_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String TEMP_FILE_NAME = "FILE_NAME";
    public static final String DIRECTORY_NAME = "pdfdemo";
    public static final String DOC_DATA = "DOC_DATA";
    public static String dirpath = Const.TEMP_FOLDER_PATH + "/" + Const.DIRECTORY_NAME;

    public static final String EXT_JPG = "jpg";
    public static final String EXT_PNG = "png";
    public static final String EXT_TXT = "txt";
    public static final String EXT_DOC = "doc";
    public static final String EXT_DOCX = "docx";

}
