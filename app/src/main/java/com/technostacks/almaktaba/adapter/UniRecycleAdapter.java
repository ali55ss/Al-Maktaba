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
import com.technostacks.almaktaba.model.UniversityResponseModel;
import com.technostacks.almaktaba.webservices.WebService;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by techno-110 on 17/5/17.
 */
public class UniRecycleAdapter extends RecyclerView.Adapter<UniRecycleAdapter.UniversityHolder> {

    Context context;
    List<UniversityResponseModel.University> listUni;
    List<CollegeResponseModel.College> listColleges;
    RecyclerItemClick listener;

    public UniRecycleAdapter(Context context, List<UniversityResponseModel.University> listUni, List<CollegeResponseModel.College> listColleges , RecyclerItemClick listener) {
        this.context = context;
        this.listUni = listUni;
        this.listener = listener;
        this.listColleges = listColleges;
    }

    @Override
    public UniversityHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_row_university_layout, parent, false);
        UniversityHolder holder = new UniversityHolder(view);

        return holder;
    }

    @Override
    public int getItemCount() {
        if (listUni!=null)
            return listUni.size();
        else
            return listColleges.size();
    }


    @Override
    public void onBindViewHolder(UniversityHolder holder, int position) {

        if (listUni!=null){

            UniversityResponseModel.University university = listUni.get(position);
            holder.tvUni.setText(university.getUniName());

            if ( university.getLogo()!=null && !university.getLogo().isEmpty())
                Picasso.with(context).load(WebService.UNIVERSITY_BASE_URL+university.getLogo()).placeholder(R.drawable.ic_profile_placeholder).fit().into(holder.ivUni);
            else
                holder.ivUni.setImageResource(R.drawable.ic_placeholder);

        }else {

            CollegeResponseModel.College college = listColleges.get(position);
            holder.tvUni.setText(college.getCollegeName());

        }


    }

    public class UniversityHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.iv_single_uni)
        ImageView ivUni;
        @BindView(R.id.tv_single_uni)
        TextView tvUni;

        public UniversityHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            if (listUni!=null)
                ivUni.setVisibility(View.VISIBLE);
            else
                ivUni.setVisibility(View.GONE);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onRecyclerItemClick(getAdapterPosition());
        }
    }

    public void setSearchData(List<UniversityResponseModel.University> listUni){
        this. listUni = listUni;
        notifyDataSetChanged();
    }

    public void setSearchCollegeData(List<CollegeResponseModel.College> listColleges){
        this. listColleges = listColleges;
        notifyDataSetChanged();
    }
}
