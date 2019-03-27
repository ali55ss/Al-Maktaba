package com.technostacks.almaktaba.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.technostacks.almaktaba.R;
import com.technostacks.almaktaba.listener.RecyclerItemClick;
import com.technostacks.almaktaba.model.CollegeResponseModel;
import com.technostacks.almaktaba.model.CoursesResponseModel;
import com.technostacks.almaktaba.model.DepartmentResponseModel;
import com.technostacks.almaktaba.model.UniversityResponseModel;
import com.technostacks.almaktaba.webservices.WebService;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by techno-110 on 17/5/17.
 */
public class DepartmentRecycleAdapter extends RecyclerView.Adapter<DepartmentRecycleAdapter.UniversityHolder> {

    Context context;
    List<DepartmentResponseModel.Collegedepartment> listDepartment;
    List<CoursesResponseModel.Departmentcourse> listCourses;
    RecyclerItemClick listener;

    public DepartmentRecycleAdapter(Context context, List<DepartmentResponseModel.Collegedepartment> listDepartment, List<CoursesResponseModel.Departmentcourse> listCourses , RecyclerItemClick listener) {
        this.context = context;
        this.listDepartment = listDepartment;
        this.listener = listener;
        this.listCourses = listCourses;
    }

    @Override
    public UniversityHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_row_university_layout, parent, false);
        UniversityHolder holder = new UniversityHolder(view);

        return holder;
    }

    @Override
    public int getItemCount() {
        if (listDepartment!=null)
            return listDepartment.size();
        else
            return listCourses.size();
    }


    @Override
    public void onBindViewHolder(UniversityHolder holder, int position) {

        if (listDepartment!=null){

            DepartmentResponseModel.Collegedepartment collegedepartment = listDepartment.get(position);
            holder.tvUni.setText(collegedepartment.getDepartments().getDepartmentName());

        }else {

            CoursesResponseModel.Departmentcourse.Courses courses = listCourses.get(position).getCourses();
            holder.tvUni.setText(courses.getCourseName());
            holder.tvCourseId.setText(courses.getCourseCode());

        }

    }

    public class UniversityHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.iv_single_uni)
        ImageView ivUni;
        @BindView(R.id.tv_single_uni)
        TextView tvUni;
        @BindView(R.id.tv_single_course_code)
        TextView tvCourseId;

        public UniversityHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            ivUni.setVisibility(View.GONE);

            if (listCourses!=null)
                tvCourseId.setVisibility(View.VISIBLE);
            else
                tvCourseId.setVisibility(View.GONE);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            listener.onRecyclerItemClick(getAdapterPosition());
        }
    }

    public void setSearchDepartmentData(List<DepartmentResponseModel.Collegedepartment> listDepartment){
        this. listDepartment = listDepartment;
        notifyDataSetChanged();
    }

    public void setSearchCourseData(List<CoursesResponseModel.Departmentcourse> listCourses){
        this. listCourses = listCourses;
        notifyDataSetChanged();
    }
}

