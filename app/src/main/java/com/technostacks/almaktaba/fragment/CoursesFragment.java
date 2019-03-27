package com.technostacks.almaktaba.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bumptech.glide.util.Util;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.technostacks.almaktaba.R;
import com.technostacks.almaktaba.activity.DepartmentsActivity;
import com.technostacks.almaktaba.activity.DocumentListingActivity;
import com.technostacks.almaktaba.activity.MainActivity;
import com.technostacks.almaktaba.activity.SuggestUniActivity;
import com.technostacks.almaktaba.adapter.DepartmentRecycleAdapter;
import com.technostacks.almaktaba.dialog.AlertDialogHelper;
import com.technostacks.almaktaba.listener.RecyclerItemClick;
import com.technostacks.almaktaba.model.CoursesResponseModel;
import com.technostacks.almaktaba.model.LoginResponseModel;
import com.technostacks.almaktaba.model.User;
import com.technostacks.almaktaba.model.Userdepartment;
import com.technostacks.almaktaba.util.Const;
import com.technostacks.almaktaba.util.ProgressDialogHelper;
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


public class CoursesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener {

    public static final String TAG = CoursesFragment.class.getSimpleName();
    private Context mContext;
    @BindView(R.id.rcv_courses)
    RecyclerView rcvCourses;
    @BindView(R.id.swipe_courses)
    SwipeRefreshLayout swipeCourses;
    @BindView(R.id.fab_add_courses)
    FloatingActionButton fabAddCourses;
    @BindView(R.id.ll_empty_courses)
    LinearLayout llEmptyCourses;
    @BindView(R.id.search_courses)
    SearchView searchCourses;

    Userdepartment userdepartment;
    Gson gson;
    List<CoursesResponseModel.Departmentcourse> coursesList, searchCoursesList;
    DepartmentRecycleAdapter adapter;

    public CoursesFragment() {
        // Required empty public constructor
    }

    public static CoursesFragment newInstance() {
        CoursesFragment fragment = new CoursesFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_courses, container, false);
        ButterKnife.bind(this, view);

        rcvCourses.setLayoutManager(new LinearLayoutManager(mContext));
        rcvCourses.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));

        gson = Utils.getGson();

        coursesList = new ArrayList<>();
        userdepartment = Utils.getUserdepartment(getActivity());
        getListOfCourses(true);

        adapter = new DepartmentRecycleAdapter(mContext, null, coursesList, recyclerItemClick);
        rcvCourses.setAdapter(adapter);

        searchCourses.setOnQueryTextListener(this);
        swipeCourses.setOnRefreshListener(this);

        return view;
    }


    private void getListOfCourses(boolean showProgress) {

        if (showProgress)
            Utils.showProgressDialog(mContext, "");

        try {
            JSONObject jsonObject = new JSONObject();
            JSONObject conditionJson = new JSONObject();
            conditionJson.put(NetworkHelper.IS_DELETED, NetworkHelper.NOT_DELETED);
            conditionJson.put(NetworkHelper.COLLEGE_DEPARTMENT_ID, userdepartment.getCollegedepartmentId());
            conditionJson.put(NetworkHelper.DEPARTMENTCOURSES_STATUS, 1);
            jsonObject.put(NetworkHelper.CONDITIONS, conditionJson);
            jsonObject.put(NetworkHelper.CONTAIN, new JSONArray().put(NetworkHelper.CONTAIN_COURSES));
            jsonObject.put(NetworkHelper.GET, NetworkHelper.GET_ALL);

            Log.e(TAG, "json obj = " + jsonObject);

            StringEntity stringEntity = new StringEntity(jsonObject.toString());

            WebService.postWithAuth(mContext, NetworkHelper.API_LIST_DEPARTMENTCOURSES, stringEntity, NetworkHelper.CONTENT_TYPE_JSON, swipeCourses, responseHandler);
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

                    CoursesResponseModel coursesResponseModel = gson.fromJson(response, CoursesResponseModel.class);

                    coursesList = coursesResponseModel.getDepartmentcourses();


                } else {
                    AlertDialogHelper.showDialog(mContext, jsonObject.getString(NetworkHelper.MESSAGE), null);
                }
                handleUniResponse();
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

    private void handleProgressVisibility() {

        if (swipeCourses.isRefreshing())
            swipeCourses.setRefreshing(false);

        Utils.hideProgressDialog();


    }

    private void handleUniResponse() {

        if (coursesList != null && !coursesList.isEmpty()) {

            adapter.setSearchCourseData(coursesList);
            rcvCourses.setVisibility(View.VISIBLE);
            llEmptyCourses.setVisibility(View.GONE);
        } else {
            rcvCourses.setVisibility(View.GONE);
            llEmptyCourses.setVisibility(View.VISIBLE);
        }

    }

    RecyclerItemClick recyclerItemClick = new RecyclerItemClick() {
        @Override
        public void onRecyclerItemClick(int position) {
            Intent intent = new Intent(mContext, DocumentListingActivity.class);
            intent.putExtra(Const.COURSE_DATA, coursesList.get(position));
            startActivity(intent);
        }
    };

    @OnClick({R.id.fab_add_courses})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fab_add_courses:
                Intent intent = new Intent(mContext, SuggestUniActivity.class);
                intent.putExtra(Const.SUGGEST_SCREEN, Const.SUGGEST_COURSE);
                intent.putExtra(DepartmentsActivity.DEPARTMENT_ID, userdepartment.getCollegedepartmentId());
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onRefresh() {
        getListOfCourses(false);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        searchCoursesList = new ArrayList<>();
        if (coursesList != null) {
            for (int i = 0; i < coursesList.size(); i++) {
                if (coursesList.get(i).getCourses().getCourseName().toLowerCase().contains(newText.toLowerCase()))
                    searchCoursesList.add(coursesList.get(i));
            }

            if (!searchCoursesList.isEmpty()) {
                rcvCourses.setVisibility(View.VISIBLE);
                llEmptyCourses.setVisibility(View.GONE);
                adapter.setSearchCourseData(searchCoursesList);
            } else {
                rcvCourses.setVisibility(View.GONE);
                llEmptyCourses.setVisibility(View.VISIBLE);
            }

        }

        return false;
    }
}
