package com.technostacks.almaktaba.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.bumptech.glide.util.Util;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.technostacks.almaktaba.R;
import com.technostacks.almaktaba.adapter.DepartmentRecycleAdapter;
import com.technostacks.almaktaba.adapter.UniRecycleAdapter;
import com.technostacks.almaktaba.dialog.AlertDialogHelper;
import com.technostacks.almaktaba.dialog.DepartmentAlertDialog;
import com.technostacks.almaktaba.listener.RecyclerItemClick;
import com.technostacks.almaktaba.model.ChangeDepartmentModel;
import com.technostacks.almaktaba.model.CollegeResponseModel;
import com.technostacks.almaktaba.model.DepartmentResponseModel;
import com.technostacks.almaktaba.model.LoginResponseModel;
import com.technostacks.almaktaba.model.User;
import com.technostacks.almaktaba.model.Userdepartment;
import com.technostacks.almaktaba.util.Const;
import com.technostacks.almaktaba.util.ProgressDialogHelper;
import com.technostacks.almaktaba.util.SharedPrefsUtils;
import com.technostacks.almaktaba.util.Utils;
import com.technostacks.almaktaba.webservices.NetworkHelper;
import com.technostacks.almaktaba.webservices.WebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

import static com.technostacks.almaktaba.activity.MainActivity.isGuestUser;

public class DepartmentsActivity extends BaseActivity implements RecyclerItemClick, SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = DepartmentsActivity.class.getSimpleName();
    public static final String DEPARTMENT_ID = "DEPARTMENT_ID";
    @BindView(R.id.rcv_department)
    RecyclerView rcvDepartment;
    @BindView(R.id.fab_add_departments)
    FloatingActionButton fabAddDepartments;
    @BindView(R.id.fab_department_guest_user)
    FloatingActionButton fabGuest;
    @BindView(R.id.ll_empty_departments)
    LinearLayout llEmptyDepartments;
    @BindView(R.id.search_departments)
    SearchView searchDepartments;
    @BindView(R.id.swipe_departments)
    SwipeRefreshLayout swipeDepartments;
    private Context mContext;
    Gson gson;
    int collegeId;
    int currentClickPosition;
    DepartmentRecycleAdapter adapter;
    List<DepartmentResponseModel.Collegedepartment> departmentsList, searchDepartmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_departments);
        mContext = this;
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_departments, container);
        ButterKnife.bind(this, view);

        if (getIntent().getExtras() != null) {
            collegeId = getIntent().getExtras().getInt(Const.COLLEGE_ID);
        }

        rcvDepartment.setLayoutManager(new LinearLayoutManager(mContext));
        rcvDepartment.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));

        gson = Utils.getGson();

        setToolbarTitle(getString(R.string.departments));
        ivMenu.setVisibility(View.GONE);
        tvForgotBack.setVisibility(View.VISIBLE);
        tvForgotBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hidekeyBoard(DepartmentsActivity.this);
                onBackPressed();
            }
        });

        callGetDepartmentsApi(true);

        departmentsList = new ArrayList<>();
        adapter = new DepartmentRecycleAdapter(mContext, departmentsList, null, this);
        rcvDepartment.setAdapter(adapter);

        if (isGuestUser) {
            fabGuest.setVisibility(View.VISIBLE);
            fabAddDepartments.setVisibility(View.GONE);
        } else {
            fabAddDepartments.setVisibility(View.VISIBLE);
            fabGuest.setVisibility(View.GONE);
        }
        searchDepartments.setOnQueryTextListener(this);
        swipeDepartments.setOnRefreshListener(this);
    }

    private void callGetDepartmentsApi(boolean showProgress) {

        if (showProgress)
            Utils.showProgressDialog(mContext, "");
        try {

            JSONObject jsonObject = new JSONObject();
            JSONObject conditionJson = new JSONObject();
            conditionJson.put(NetworkHelper.TABLE_COLLEGEDEPARTMENTS + "." + NetworkHelper.IS_DELETED, 0);
            conditionJson.put(NetworkHelper.COLLEGE_ID, collegeId);
            jsonObject.put(NetworkHelper.CONDITIONS, conditionJson);

            jsonObject.put(NetworkHelper.CONTAIN, new JSONArray().put("departments"));
            JSONArray jsonArray = new JSONArray();
            jsonArray.put("Departments.department_name");
            jsonArray.put("Departments.id");
            jsonArray.put("Collegedepartments.id");
            jsonObject.put(NetworkHelper.FIELDS, jsonArray);
            jsonObject.put(NetworkHelper.GET, NetworkHelper.GET_ALL);

            Log.e(TAG, "json obj = " + jsonObject);

            StringEntity stringEntity = new StringEntity(jsonObject.toString());

            WebService.postWithAuth(mContext, NetworkHelper.API_LIST_DEPARTMENTS, stringEntity, NetworkHelper.CONTENT_TYPE_JSON, swipeDepartments, responseHandler);

        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

            try {

                String response = new String(responseBody);
                Log.e(TAG, "response = " + response);
                JSONObject jsonObject = new JSONObject(response);

                if (jsonObject.has(NetworkHelper.CODE) && jsonObject.getInt(NetworkHelper.CODE) == NetworkHelper.CODE200) {

                    DepartmentResponseModel departmentResponseModel = gson.fromJson(response, DepartmentResponseModel.class);


                    departmentsList = departmentResponseModel.getCollegedepartments();
                    handleDepartmenResponse();

                } else {
                    handleDepartmenResponse();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

            handleProgressVisibility();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            if (responseBody != null) {
                Log.e(TAG, "response = " + new String(responseBody));
            }
            handleProgressVisibility();
        }
    };


    @OnClick({R.id.fab_add_departments, R.id.fab_department_guest_user})
    public void onViewClicked(View view) {

        switch (view.getId()) {
            case R.id.fab_add_departments:
                Intent intent = new Intent(mContext, SuggestUniActivity.class);
                intent.putExtra(Const.SUGGEST_SCREEN, Const.SUGGEST_DEPARTMENT);
                intent.putExtra(Const.COLLEGE_ID, collegeId);
                startActivity(intent);
                break;
            case R.id.fab_department_guest_user:
                Utils.clearLoggedInPreferences();
                startActivity(new Intent(mContext, LoginActivity.class));
                break;
        }

    }

    private void handleProgressVisibility() {

        if (swipeDepartments.isRefreshing())
            swipeDepartments.setRefreshing(false);

        Utils.hideProgressDialog();

    }

    private void handleDepartmenResponse() {

        if (departmentsList != null && !departmentsList.isEmpty()) {

            adapter.setSearchDepartmentData(departmentsList);
            rcvDepartment.setVisibility(View.VISIBLE);
            llEmptyDepartments.setVisibility(View.GONE);
        } else {

            llEmptyDepartments.setVisibility(View.VISIBLE);
            rcvDepartment.setVisibility(View.GONE);
        }

    }

    @Override
    public void onRecyclerItemClick(final int recyclerItemPosition) {

        currentClickPosition = recyclerItemPosition;
        if (isGuestUser) {

            Intent intent = new Intent(mContext, CoursesActivity.class);
            intent.putExtra(DEPARTMENT_ID, departmentsList.get(recyclerItemPosition).getId());
            startActivity(intent);

        } else {

            DepartmentAlertDialog dialog = new DepartmentAlertDialog(mContext);
            dialog.createDepartmentChooserDialog(getString(R.string.do_you_want_to_enroll_the_department_you_have_chosen), "", "", new RecyclerItemClick() {
                @Override
                public void onRecyclerItemClick(int position) {
                    switch (position) {
                        case 0:
                            Intent intent = new Intent(mContext, CoursesActivity.class);
                            intent.putExtra(DEPARTMENT_ID, departmentsList.get(recyclerItemPosition).getId());
                            startActivity(intent);

                            break;
                        case 1:
                            handleDepartmentAddEditApiCall(recyclerItemPosition);

                            break;
                    }
                }
            });
        }

    }

    private void handleDepartmentAddEditApiCall(int position) {

        Utils.showProgressDialog(mContext,"");

        Userdepartment userdepartment = Utils.getUserdepartment(mContext);
        try {

            JSONObject jsonObject = new JSONObject();
            StringEntity stringEntity;
            if (userdepartment != null) {  // call edit departments api

                jsonObject.put(NetworkHelper.ID, userdepartment.getId());
                jsonObject.put(NetworkHelper.USER_ID, userdepartment.getUserId());
                jsonObject.put(NetworkHelper.COLLEGE_DEPARTMENT_ID, departmentsList.get(position).getId());

                Log.e(TAG, "request json = " + jsonObject);
                stringEntity = new StringEntity(jsonObject.toString());
                WebService.postWithAuth(mContext, NetworkHelper.API_EDIT_DEPARTMENTS, stringEntity, NetworkHelper.CONTENT_TYPE_JSON, swipeDepartments, addEditDeaprtmentResponseHandler);

            } else {                                                                           // call add departments api

                jsonObject.put(NetworkHelper.USER_ID, Utils.getLoggedInUser(mContext).getId());
                jsonObject.put(NetworkHelper.COLLEGE_DEPARTMENT_ID, departmentsList.get(position).getId());

                Log.e(TAG, "request json = " + jsonObject);
                stringEntity = new StringEntity(jsonObject.toString());
                WebService.postWithAuth(mContext, NetworkHelper.API_ADD_DEPARTMENTS, stringEntity, NetworkHelper.CONTENT_TYPE_JSON, swipeDepartments, addEditDeaprtmentResponseHandler);

            }

        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }

    }


    AsyncHttpResponseHandler addEditDeaprtmentResponseHandler = new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {


            try {

                String response = new String(responseBody);
                Log.e(TAG, "response = " + response);
                JSONObject jsonObject = null;

                jsonObject = new JSONObject(response);

                if (jsonObject.has(NetworkHelper.CODE) && jsonObject.getInt(NetworkHelper.CODE) == NetworkHelper.CODE200) {

                    ChangeDepartmentModel departmentModel = gson.fromJson(response, ChangeDepartmentModel.class);
                    ArrayList<Userdepartment> userdepartmentArrayList = new ArrayList<>();
                    userdepartmentArrayList.add(departmentModel.getUserdepartments());

                    String departmentJson = gson.toJson(userdepartmentArrayList.get(0));
                    SharedPrefsUtils.setStringPreference(mContext, Const.DEPARTMENT_RESPONSE, departmentJson);
                    //  Utils.saveNewUserDepartmentDataToPreference(mContext,userdepartmentArrayList);
                    Utils.clearLoggedInPreferences();
                    Utils.hideProgressDialog();

                    Intent intent = new Intent(mContext, MainActivity.class);
                    startActivity(intent);
                    finishAffinity();

                } else {

                    AlertDialogHelper.showDialog(mContext, response, null);
                    Utils.hideProgressDialog();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Utils.hideProgressDialog();
            }

        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            if (responseBody != null)
                AlertDialogHelper.showDialog(mContext, new String(responseBody), null);
            Utils.hideProgressDialog();
        }
    };

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        searchDepartmentList = new ArrayList<>();

        if (departmentsList != null) {
            for (int i = 0; i < departmentsList.size(); i++) {
                if (departmentsList.get(i).getDepartments().getDepartmentName().toLowerCase().contains(newText.toLowerCase()))
                    searchDepartmentList.add(departmentsList.get(i));
            }

            if (!searchDepartmentList.isEmpty()) {
                rcvDepartment.setVisibility(View.VISIBLE);
                llEmptyDepartments.setVisibility(View.GONE);
                adapter.setSearchDepartmentData(searchDepartmentList);
            } else {
                rcvDepartment.setVisibility(View.GONE);
                llEmptyDepartments.setVisibility(View.VISIBLE);
            }
        }

        return false;
    }

    @Override
    public void onRefresh() {
        departmentsList.clear();
        callGetDepartmentsApi(false);
    }
}
