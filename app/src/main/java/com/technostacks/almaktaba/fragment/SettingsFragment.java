package com.technostacks.almaktaba.fragment;

import android.app.Activity;
import android.app.job.JobInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.util.Util;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.technostacks.almaktaba.R;
import com.technostacks.almaktaba.activity.ChangePassActivity;
import com.technostacks.almaktaba.activity.LoginActivity;
import com.technostacks.almaktaba.activity.MainActivity;
import com.technostacks.almaktaba.dialog.DepartmentAlertDialog;
import com.technostacks.almaktaba.listener.RecyclerItemClick;
import com.technostacks.almaktaba.util.Const;
import com.technostacks.almaktaba.util.SharedPrefsUtils;
import com.technostacks.almaktaba.util.Utils;
import com.technostacks.almaktaba.webservices.NetworkHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SettingsFragment extends Fragment {

    public static final String TAG = SettingsFragment.class.getSimpleName();
    @BindView(R.id.tv)
    TextView tv;
    @BindView(R.id.rl_my_department)
    RelativeLayout rlMyDepartment;
    @BindView(R.id.rl_change_password)
    RelativeLayout rlChangePassword;
    @BindView(R.id.btn_logout)
    Button btnLogout;
    private Context mContext;

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
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
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, view);

        if (Utils.getLoggedInUser(mContext).getRegisterType() == NetworkHelper.REGISTER_TYPE_NORMAL)
            rlChangePassword.setVisibility(View.VISIBLE);
        else rlChangePassword.setVisibility(View.GONE);


        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @OnClick({R.id.rl_change_password, R.id.btn_logout,R.id.rl_my_department})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_change_password:
                startActivity(new Intent(mContext, ChangePassActivity.class));
                break;
            case R.id.btn_logout:

                DepartmentAlertDialog dialog = new DepartmentAlertDialog(mContext);
                dialog.createDepartmentChooserDialog(getString(R.string.are_you_suer_you_want_to_logout_from_almaktaba),"" ,"", new RecyclerItemClick() {
                    @Override
                    public void onRecyclerItemClick(int position) {
                        switch (position){
                            case 0:
                                break;
                            case 1:

                                if (Utils.getLoggedInUser(mContext).getRegisterType() == NetworkHelper.REGISTER_TYPE_FACEBOOK)
                                    LoginManager.getInstance().logOut();

                                Utils.clearLoggedInPreferences();
                                SharedPrefsUtils.setBooleanPreference(mContext, Const.IS_LOGIN,false);
                                SharedPrefsUtils.setStringPreference(mContext, Const.LOGIN_RESPONSE, null);
                                SharedPrefsUtils.setStringPreference(mContext,Const.DEPARTMENT_RESPONSE,null);
                                startActivity(new Intent(getActivity(),LoginActivity.class));
                                ((Activity)mContext).finishAffinity();

                                break;
                        }
                    }
                });

                break;
            case R.id.rl_my_department:
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra(Const.IS_FROM_SETTINGS, true);
                startActivity(intent);
                break;
        }
    }
}
