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
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.technostacks.almaktaba.R;
import com.technostacks.almaktaba.activity.CollegesActivity;
import com.technostacks.almaktaba.activity.LoginActivity;
import com.technostacks.almaktaba.activity.SuggestUniActivity;
import com.technostacks.almaktaba.adapter.UniRecycleAdapter;
import com.technostacks.almaktaba.listener.RecyclerItemClick;
import com.technostacks.almaktaba.model.UniversityResponseModel;
import com.technostacks.almaktaba.dialog.AlertDialogHelper;
import com.technostacks.almaktaba.util.Const;
import com.technostacks.almaktaba.util.ProgressDialogHelper;
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

public class UniversityFragment extends Fragment implements RecyclerItemClick, SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = UniversityFragment.class.getSimpleName();
    @BindView(R.id.search_uni)
    SearchView searchUni;
    @BindView(R.id.rcv_universities)
    RecyclerView rcvUniversities;
    @BindView(R.id.fab_add_uni)
    FloatingActionButton fabAddUni;
    @BindView(R.id.fab_uni_guest_user)
    FloatingActionButton fabGuest;
    @BindView(R.id.ll_empty_vw)
    LinearLayout llEmptyVw;
    @BindView(R.id.swipe_universities)
    SwipeRefreshLayout swipeUni;
    private Context mContext;
    Gson gson;
    UniRecycleAdapter adapter;
    List<UniversityResponseModel.University> searchUniList, listUnis;

    public UniversityFragment() {

    }

    public static UniversityFragment newInstance() {
        UniversityFragment fragment = new UniversityFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "oncreate of fragment !");
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG, "oncreateview of fragment !");

        View view = inflater.inflate(R.layout.fragment_universities, container, false);
        ButterKnife.bind(this, view);

        rcvUniversities.setLayoutManager(new LinearLayoutManager(mContext));
        rcvUniversities.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));

        gson = Utils.getGson();
        listUnis = new ArrayList<>();
        getListOfUniversities(true);

        adapter = new UniRecycleAdapter(mContext, listUnis, null, this);
        rcvUniversities.setAdapter(adapter);

        //searchUni.setIconifiedByDefault(false);
        searchUni.clearFocus();
        swipeUni.setOnRefreshListener(this);
        searchUni.setOnQueryTextListener(this);

        if (isGuestUser) {
            fabGuest.setVisibility(View.VISIBLE);
            fabAddUni.setVisibility(View.GONE);
        } else {
            fabAddUni.setVisibility(View.VISIBLE);
            fabGuest.setVisibility(View.GONE);
        }


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @OnClick({R.id.fab_add_uni, R.id.fab_uni_guest_user})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fab_add_uni:
                Utils.hidekeyBoard(getActivity());
                Intent intent = new Intent(mContext, SuggestUniActivity.class);
                intent.putExtra(Const.SUGGEST_SCREEN, Const.SUGGEST_UNI);
                startActivity(intent);
                break;
            case R.id.fab_uni_guest_user:
                Utils.clearLoggedInPreferences();
                startActivity(new Intent(mContext, LoginActivity.class));
                break;
        }
    }

    @Override
    public void onRecyclerItemClick(int position) {

        Intent intent = new Intent(mContext, CollegesActivity.class);
        intent.putExtra(Const.UNI_ID, listUnis.get(position).getId());
        startActivity(intent);
    }

    private void getListOfUniversities(boolean showProgress) {

        if (showProgress)
            Utils.showProgressDialog(mContext, "");
        try {
            JSONObject jsonObject = new JSONObject();
            JSONObject conditionJson = new JSONObject();
            conditionJson.put(NetworkHelper.IS_DELETED, NetworkHelper.NOT_DELETED);
            conditionJson.put(NetworkHelper.STATUS, NetworkHelper.STATUS_ACTIVE);
            jsonObject.put(NetworkHelper.CONDITIONS, conditionJson);
            jsonObject.put(NetworkHelper.GET, NetworkHelper.GET_ALL);


            Log.e(TAG, "json obj = " + jsonObject);

            StringEntity stringEntity = new StringEntity(jsonObject.toString());

            WebService.postWithAuth(mContext, NetworkHelper.API_LIST_UNIVERSITIES, stringEntity, NetworkHelper.CONTENT_TYPE_JSON, swipeUni, responseHandler);
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

                    UniversityResponseModel universityResponseModel = gson.fromJson(response, UniversityResponseModel.class);

                    listUnis = universityResponseModel.getUniversities();
                    handleUniResponse();

                } else {
                    AlertDialogHelper.showDialog(mContext, jsonObject.getString(NetworkHelper.MESSAGE), null);
                    handleUniResponse();
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

        if (swipeUni.isRefreshing())
            swipeUni.setRefreshing(false);

        Utils.hideProgressDialog();


    }


    private void handleUniResponse() {

        if (listUnis != null && !listUnis.isEmpty()) {

            adapter.setSearchData(listUnis);
            rcvUniversities.setVisibility(View.VISIBLE);
            llEmptyVw.setVisibility(View.GONE);
        } else {
            rcvUniversities.setVisibility(View.GONE);
            llEmptyVw.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        searchUniList = new ArrayList<>();
        if (listUnis != null) {
            for (int i = 0; i < listUnis.size(); i++) {
                if (listUnis.get(i).getUniName().toLowerCase().contains(newText.toLowerCase()))
                    searchUniList.add(listUnis.get(i));
            }

            if (!searchUniList.isEmpty()) {
                rcvUniversities.setVisibility(View.VISIBLE);
                llEmptyVw.setVisibility(View.GONE);
                adapter.setSearchData(searchUniList);
            } else {
                rcvUniversities.setVisibility(View.GONE);
                llEmptyVw.setVisibility(View.VISIBLE);
            }

        }

        return false;
    }

    @Override
    public void onRefresh() {
        listUnis.clear();
        getListOfUniversities(false);
    }
}
