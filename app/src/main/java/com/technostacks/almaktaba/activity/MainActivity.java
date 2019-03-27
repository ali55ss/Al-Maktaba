package com.technostacks.almaktaba.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.util.Util;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.gson.Gson;
import com.specyci.residemenu.ResideMenu;
import com.squareup.picasso.Picasso;
import com.technostacks.almaktaba.R;
import com.technostacks.almaktaba.dialog.DepartmentAlertDialog;
import com.technostacks.almaktaba.fragment.CoursesFragment;
import com.technostacks.almaktaba.fragment.UniversityFragment;
import com.technostacks.almaktaba.fragment.ProfileFragment;
import com.technostacks.almaktaba.fragment.SettingsFragment;
import com.technostacks.almaktaba.listener.RecyclerItemClick;
import com.technostacks.almaktaba.model.User;
import com.technostacks.almaktaba.model.Userdepartment;
import com.technostacks.almaktaba.util.CircleImageView;
import com.technostacks.almaktaba.util.Const;
import com.technostacks.almaktaba.util.SharedPrefsUtils;
import com.technostacks.almaktaba.util.Utils;
import com.technostacks.almaktaba.webservices.WebService;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements View.OnClickListener, ResideMenu.OnMenuListener, RecyclerItemClick {

    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String FRAGMENT_DEPARTMENT = "FRAGMENT_DEPARTMENT";
    public static final String FRAGMENT_PROFILE = "FRAGMENT_PROFILE";
    public static final String FRAGMENT_SETTINGS = "FRAGMENT_SETTINGS";
    public static final String FRAGMENT_COURSES = "FRAGMENT_COURSES";
    public static boolean isGuestUser = false;

    private Context mContext;
    private ResideMenu resideMenu;
    private String FRAGMENT_TAG = "";
    TextView tvMenuDepartment, tvMenuProfile, tvMenuSettings;
    View vwDepartment, vwProfile, vwSettings;
    UniversityFragment myDepartmentFragment;
    CoursesFragment coursesFragment;
    List<String> arabicLangs;
    String currentLang;
    private Gson gson;
    Userdepartment userdepartment;
    boolean isFromSettings = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_main);

        mContext = this;
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_main, container);
        ButterKnife.bind(this, view);


        if (getIntent().getExtras() != null) {
            isFromSettings = getIntent().getExtras().getBoolean(Const.IS_FROM_SETTINGS);
            isGuestUser = getIntent().getExtras().getBoolean(Const.IS_GUEST_USER);
        }

        myDepartmentFragment = UniversityFragment.newInstance();
        coursesFragment = CoursesFragment.newInstance();

        userdepartment = Utils.getUserdepartment(mContext);


        if (isGuestUser) {
            changeFragment(myDepartmentFragment, FRAGMENT_DEPARTMENT);
            ivMenu.setVisibility(View.VISIBLE);
            tvForgotBack.setVisibility(View.GONE);
            setToolbarTitle(getString(R.string.universities));
        } else
            handleFragmentNavigation();

        arabicLangs = Arrays.asList(getResources().getStringArray(R.array.rtl_lang));
        ivMenu.setOnClickListener(this);
        tvForgotBack.setOnClickListener(this);
        gson = Utils.getGson();
        setUpMenu();
        handleMenuCilckBackground(1);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // check for current device language to set up menu in rtl

        currentLang = Locale.getDefault().getLanguage();
        SharedPrefsUtils.setStringPreference(MainActivity.this, Const.CURRENT_LANGUAGE, Locale.getDefault().getLanguage());

    }

    private void handleFragmentNavigation() {

        if (isFromSettings) {

            changeFragment(myDepartmentFragment, FRAGMENT_DEPARTMENT);
            setToolbarTitle(getString(R.string.universities));
            ivMenu.setVisibility(View.GONE);
            tvForgotBack.setVisibility(View.VISIBLE);

        } else {

            ivMenu.setVisibility(View.VISIBLE);
            tvForgotBack.setVisibility(View.GONE);
            if (userdepartment != null) {
                changeFragment(coursesFragment, FRAGMENT_COURSES);
                setToolbarTitle(getString(R.string.courses));
            } else {
                changeFragment(myDepartmentFragment, FRAGMENT_DEPARTMENT);
                setToolbarTitle(getString(R.string.universities));
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_menu_icon:

                /*currentLang = Locale.getDefault().getLanguage();
                SharedPrefsUtils.setStringPreference(MainActivity.this, Const.CURRENT_LANGUAGE, Locale.getDefault().getLanguage());*/
                if (arabicLangs.contains(currentLang)) {
                    resideMenu.openMenu(ResideMenu.DIRECTION_RIGHT);
                } else {
                    resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
                }

                break;
            case R.id.tv_menu_my_departments:


                resideMenu.closeMenu();
                handleMenuCilckBackground(1);
                handleFragmentNavigation();

                break;
            case R.id.tv_menu_profile:

                if (isGuestUser) {
                    DepartmentAlertDialog dialog = new DepartmentAlertDialog(mContext);
                    dialog.createDepartmentChooserDialog(getString(R.string.login_or_register_to_proces),getString(R.string.teake_me_there),getString(R.string.close),this);

                } else {
                    resideMenu.closeMenu();
                    Fragment profileFragment = ProfileFragment.newInstance();
                    handleMenuCilckBackground(2);
                    changeFragment(profileFragment, FRAGMENT_PROFILE);
                    setToolbarTitle(getString(R.string.str_profile));
                }
                break;
            case R.id.tv_menu_settings:

                if (isGuestUser) {

                    DepartmentAlertDialog dialog = new DepartmentAlertDialog(mContext);
                    dialog.createDepartmentChooserDialog(getString(R.string.login_or_register_to_proces),getString(R.string.teake_me_there),getString(R.string.close),this);

                } else {

                    resideMenu.closeMenu();
                    handleMenuCilckBackground(3);
                    Fragment settingsFragment = SettingsFragment.newInstance();
                    changeFragment(settingsFragment, FRAGMENT_SETTINGS);
                    setToolbarTitle(getString(R.string.settings));
                }

                break;
            case R.id.tv_forgot_back:
                onBackPressed();
                break;

            case R.id.ll_close_menu:
                resideMenu.closeMenu();
                break;
        }

    }

    private void handleMenuCilckBackground(int menuPosition) {
        switch (menuPosition) {
            case 1:
                tvMenuDepartment.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                tvMenuProfile.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                tvMenuSettings.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                vwDepartment.setVisibility(View.VISIBLE);
                vwProfile.setVisibility(View.GONE);
                vwSettings.setVisibility(View.GONE);
                break;
            case 2:
                tvMenuProfile.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                tvMenuDepartment.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                tvMenuSettings.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                vwDepartment.setVisibility(View.GONE);
                vwProfile.setVisibility(View.VISIBLE);
                vwSettings.setVisibility(View.GONE);
                break;
            case 3:
                tvMenuSettings.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                tvMenuDepartment.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                tvMenuProfile.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                vwDepartment.setVisibility(View.GONE);
                vwProfile.setVisibility(View.GONE);
                vwSettings.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void setUpMenu() {

        RelativeLayout parentLayout;
        resideMenu = new ResideMenu(this, R.layout.single_left_menu_layout, R.layout.single_left_menu_layout);
        resideMenu.attachToActivity(this);
        resideMenu.setMenuListener(this);
        //resideMenu.setScaleValue(0.5f);
        resideMenu.setScaleValue(0.45f);


        currentLang = Locale.getDefault().getLanguage();
        SharedPrefsUtils.setStringPreference(MainActivity.this, Const.CURRENT_LANGUAGE, Locale.getDefault().getLanguage());

        if (arabicLangs.contains(currentLang)) {
            parentLayout = (RelativeLayout) resideMenu.getRightMenuView().findViewById(R.id.ll_left_menu_parent);

        } else {
            parentLayout = (RelativeLayout) resideMenu.getLeftMenuView().findViewById(R.id.ll_left_menu_parent);
        }

        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_LEFT);
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);

        TextView tvUserName = (TextView) parentLayout.findViewById(R.id.tv_user_name);
        TextView tvUserEmail = (TextView) parentLayout.findViewById(R.id.tv_user_email);
        parentLayout.findViewById(R.id.ll_close_menu).setOnClickListener(this);

        CircleImageView ivUserProfile = (CircleImageView) parentLayout.findViewById(R.id.iv_residemenu_user_profile);
        ivUserProfile.setImageResource(R.drawable.ic_profile_placeholder);

        vwDepartment = parentLayout.findViewById(R.id.vw_my_dept);
        vwProfile = parentLayout.findViewById(R.id.vw_my_profile);
        vwSettings = parentLayout.findViewById(R.id.vw_settings);
        tvMenuDepartment = parentLayout.findViewById(R.id.tv_menu_my_departments);
        tvMenuProfile = parentLayout.findViewById(R.id.tv_menu_profile);
        tvMenuSettings = parentLayout.findViewById(R.id.tv_menu_settings);
        parentLayout.findViewById(R.id.rl_user_profile).setOnClickListener(this);


        if (userdepartment != null)
            tvMenuDepartment.setText(R.string.department_courses);
        else
            tvMenuDepartment.setText(R.string.my_university);

        tvMenuDepartment.setOnClickListener(this);
        tvMenuProfile.setOnClickListener(this);
        tvMenuSettings.setOnClickListener(this);

        User user = Utils.getLoggedInUser(mContext);

        if (user != null) {

            if(user.getFirstname()!=null)
                tvUserName.setText(user.getFirstname() + " " + user.getLastname());
            tvUserEmail.setText(user.getEmail());
            if (user.getProfileImage() != null && !user.getProfileImage().isEmpty())
                Picasso.with(mContext).load(WebService.PROFILE_BASE_URL + user.getProfileImage()).placeholder(R.drawable.ic_profile_placeholder).fit().error(R.drawable.ic_profile_placeholder).into(ivUserProfile);
            else
                ivUserProfile.setImageResource(R.drawable.ic_profile_placeholder);
        }
    }

    @Override
    public void openMenu() {

    }

    @Override
    public void closeMenu() {

    }

    private void changeFragment(Fragment fragment, String fragemnt_tag) {

        try {

            if (!FRAGMENT_TAG.equalsIgnoreCase(fragemnt_tag)) {
                FRAGMENT_TAG = fragemnt_tag;

                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, fragment, fragemnt_tag)
                        .commit();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRecyclerItemClick(int position) {

        switch (position){
            case 0:
                break;
            case 1:
                startActivity(new Intent(mContext,LoginActivity.class));
                break;
        }
    }
}
