package com.technostacks.almaktaba.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.technostacks.almaktaba.R;
import com.technostacks.almaktaba.adapter.UniRecycleAdapter;
import com.technostacks.almaktaba.listener.RecyclerItemClick;
import com.technostacks.almaktaba.model.CollegeResponseModel;
import com.technostacks.almaktaba.util.Const;
import com.technostacks.almaktaba.util.Utils;
import com.technostacks.almaktaba.webservices.NetworkHelper;
import com.technostacks.almaktaba.webservices.WebService;

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

public class CollegesActivity extends BaseActivity implements RecyclerItemClick, SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = CollegesActivity.class.getSimpleName();
    @BindView(R.id.rcv_colleges)
    RecyclerView rcvColleges;
    @BindView(R.id.fab_add_colleges)
    FloatingActionButton fabColleges;
    @BindView(R.id.fab_college_guest_user)
    FloatingActionButton fabGuest;
    @BindView(R.id.ll_empty_colleges)
    LinearLayout llEmptyColleges;
    @BindView(R.id.search_colleges)
    SearchView searchColleges;
    @BindView(R.id.swipe_colleges)
    SwipeRefreshLayout swipeColleges;
    private Context mContext;

    Gson gson;
    int universityId;
    UniRecycleAdapter adapter;
    List<CollegeResponseModel.College> collegeList, searchCollegeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        //    setContentView(R.layout.activity_colleges);

        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_colleges, container);
        ButterKnife.bind(this, view);

        if (getIntent().getExtras() != null) {
            universityId = getIntent().getExtras().getInt(Const.UNI_ID);
        }
        rcvColleges.setLayoutManager(new LinearLayoutManager(mContext));
        rcvColleges.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));

        gson = Utils.getGson();

        setToolbarTitle(getString(R.string.colleges));
        ivMenu.setVisibility(View.GONE);
        tvForgotBack.setVisibility(View.VISIBLE);
        tvForgotBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hidekeyBoard(CollegesActivity.this);
                onBackPressed();
            }
        });

        callGetCollegesApi(true);

        collegeList = new ArrayList<>();
        adapter = new UniRecycleAdapter(mContext, null, collegeList, this);
        rcvColleges.setAdapter(adapter);

        if (isGuestUser) {
            fabGuest.setVisibility(View.VISIBLE);
            fabColleges.setVisibility(View.GONE);
        } else {
            fabColleges.setVisibility(View.VISIBLE);
            fabGuest.setVisibility(View.GONE);
        }

        searchColleges.setOnQueryTextListener(this);
        swipeColleges.setOnRefreshListener(this);
    }

    private void callGetCollegesApi(boolean showProgress) {

        if (showProgress)
            Utils.showProgressDialog(mContext, "");
        try {

            JSONObject jsonObject = new JSONObject();
            JSONObject conditionJson = new JSONObject();
            conditionJson.put(NetworkHelper.IS_DELETED, 0);
            conditionJson.put(NetworkHelper.STATUS, NetworkHelper.STATUS_ACTIVE);
            conditionJson.put(NetworkHelper.UNIVERSITY_ID, universityId);
            jsonObject.put(NetworkHelper.CONDITIONS, conditionJson);
            jsonObject.put(NetworkHelper.GET, NetworkHelper.GET_ALL);

            Log.e(TAG, "json obj = " + jsonObject);

            StringEntity stringEntity = new StringEntity(jsonObject.toString());

            WebService.postWithAuth(mContext, NetworkHelper.API_LIST_COLLEGES, stringEntity, NetworkHelper.CONTENT_TYPE_JSON, swipeColleges, responseHandler);

        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onRecyclerItemClick(int position) {
        Intent intent = new Intent(mContext, DepartmentsActivity.class);
        intent.putExtra(Const.COLLEGE_ID, collegeList.get(position).getId());
        startActivity(intent);
    }

    @OnClick({R.id.fab_add_colleges, R.id.fab_college_guest_user})
    public void onViewClicked(View view) {

        switch (view.getId()) {
            case R.id.fab_add_colleges:
                Intent intent = new Intent(mContext, SuggestUniActivity.class);
                intent.putExtra(Const.SUGGEST_SCREEN, Const.SUGGEST_COLLEGE);
                intent.putExtra(Const.UNI_ID, universityId);
                startActivity(intent);
                break;
            case R.id.fab_college_guest_user:
                Utils.clearLoggedInPreferences();
                startActivity(new Intent(mContext, LoginActivity.class));
                break;
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

                    CollegeResponseModel collegeResponseModel = gson.fromJson(response, CollegeResponseModel.class);

                    collegeList = collegeResponseModel.getColleges();
                    handleCollegeResponse();

                } else {

                    handleCollegeResponse();
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

    private void handleProgressVisibility() {

        if (swipeColleges.isRefreshing())
            swipeColleges.setRefreshing(false);

        Utils.hideProgressDialog();

    }

    private void handleCollegeResponse() {

        if (collegeList != null && !collegeList.isEmpty()) {

            adapter.setSearchCollegeData(collegeList);
            rcvColleges.setVisibility(View.VISIBLE);
            llEmptyColleges.setVisibility(View.GONE);
        } else {

            rcvColleges.setVisibility(View.GONE);
            llEmptyColleges.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        searchCollegeList = new ArrayList<>();

        if (collegeList != null) {
            for (int i = 0; i < collegeList.size(); i++) {
                if (collegeList.get(i).getCollegeName().toLowerCase().contains(newText.toLowerCase()))
                    searchCollegeList.add(collegeList.get(i));
            }

            if (!searchCollegeList.isEmpty()) {
                rcvColleges.setVisibility(View.VISIBLE);
                llEmptyColleges.setVisibility(View.GONE);
                adapter.setSearchCollegeData(searchCollegeList);
            } else {
                rcvColleges.setVisibility(View.GONE);
                llEmptyColleges.setVisibility(View.VISIBLE);
            }


        }

        return false;
    }

    @Override
    public void onRefresh() {
        collegeList.clear();
        callGetCollegesApi(false);
    }
}
