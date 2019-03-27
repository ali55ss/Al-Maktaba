package com.technostacks.almaktaba.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.technostacks.almaktaba.R;
import com.technostacks.almaktaba.listener.RecyclerPostItemClick;
import com.technostacks.almaktaba.model.DocumentResponseModel;
import com.technostacks.almaktaba.util.CircleImageView;
import com.technostacks.almaktaba.util.Const;
import com.technostacks.almaktaba.util.Utils;
import com.technostacks.almaktaba.webservices.WebService;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.technostacks.almaktaba.activity.MainActivity.isGuestUser;

/**
 * Created by techno-110 on 17/5/17.
 */
public class PostRecycleAdapter extends RecyclerView.Adapter<PostRecycleAdapter.UniversityHolder> {

    Context mContext;
    List<DocumentResponseModel.Document> listDocs;
    RecyclerPostItemClick listener;
    Animation animation;

    public PostRecycleAdapter(Context context, List<DocumentResponseModel.Document> listDocs, RecyclerPostItemClick listener) {
        this.mContext = context;
        this.listDocs = listDocs;
        this.listener = listener;

        animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
    }

    @Override
    public UniversityHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_post_layout, parent, false);
        UniversityHolder holder = new UniversityHolder(view);

        return holder;
    }

    @Override
    public int getItemCount() {
        return listDocs.size();
    }


    @Override
    public void onBindViewHolder(final UniversityHolder holder, int position) {

        DocumentResponseModel.Document document = listDocs.get(position);

        if (document.getUsers()!=null && document.getUsers().getProfileImage()!=null)
            Picasso.with(mContext).load(WebService.PROFILE_BASE_URL+document.getUsers().getProfileImage()).placeholder(R.drawable.ic_profile_placeholder).fit().error(R.drawable.ic_profile_placeholder).into(holder.ivPostUserPic);
        else
            holder.ivPostUserPic.setImageResource(R.drawable.ic_profile_placeholder);

        holder.tvPostUserName.setText(document.getUsers().getFirstname()+" "+document.getUsers().getLastname());
        holder.tvPostTime.setText(Utils.convertUtcToLocalTime(mContext,document.getCreated()));
        holder.tvPostTitle.setText(document.getDocTitle());
        holder.tvPostDetails.setText(document.getDocDescription());

        if (document.getMimeType().equalsIgnoreCase(Const.MIME_TYPE_MP4) || document.getMimeType().equalsIgnoreCase(Const.MIME_TYPE_MOV))
            holder.ivActionPlay.setVisibility(View.VISIBLE);
        else
            holder.ivActionPlay.setVisibility(View.GONE);
        /*Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                int width = (int) (Utils.getScreenWidth());
                int diw = bitmap.getWidth();
                int height = bitmap.getHeight();
                if (diw > 0) {
                    height = width * bitmap.getHeight() / diw;
                    bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);

                }
                holder.ivPostImage.setImageBitmap(bitmap);
                holder.ivPostImage.setLayoutParams(new LinearLayout.LayoutParams(width, height));
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };*/

        if (document.getFrontImage()!=null && !document.getFrontImage().isEmpty())
            Picasso.with(mContext).load(WebService.DOCUMENT_BASE_URL+document.getFrontImage()).placeholder(R.drawable.ic_placeholder).centerCrop().fit().into(holder.ivPostImage);
        else
            holder.ivPostImage.setImageResource(R.drawable.ic_profile_placeholder);

        holder.itemView.startAnimation(animation);
    }

    public class UniversityHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvUni;
        @BindView(R.id.iv_post_user_pic)
        CircleImageView ivPostUserPic;
        @BindView(R.id.tv_post_user_name)
        TextView tvPostUserName;
        @BindView(R.id.tv_post_time)
        TextView tvPostTime;
        @BindView(R.id.tv_post_title)
        TextView tvPostTitle;
        @BindView(R.id.iv_post_image)
        ImageView ivPostImage;
        @BindView(R.id.tv_post_details)
        TextView tvPostDetails;
        @BindView(R.id.iv_post_report)
        ImageView ivPostComment;
        @BindView(R.id.iv_post_share)
        ImageView ivPostShare;
        @BindView(R.id.iv_post_edit)
        ImageView ivPostEdit;
        @BindView(R.id.iv_action_video_play)
        ImageView ivActionPlay;

        public UniversityHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            if (isGuestUser){
                ivPostEdit.setVisibility(View.GONE);
                ivPostShare.setVisibility(View.GONE);
                ivPostComment.setVisibility(View.GONE);
            }else {
                ivPostEdit.setVisibility(View.VISIBLE);
                ivPostShare.setVisibility(View.VISIBLE);
                ivPostComment.setVisibility(View.VISIBLE);
            }

            ivPostComment.setOnClickListener(this);
            ivPostShare.setOnClickListener(this);
            ivPostEdit.setOnClickListener(this);
            ivPostImage.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            //listener.onRecyclerItemClick(getAdapterPosition());

            switch (v.getId()){
                case R.id.iv_post_report:
                    listener.onRecyclerItemClick(Const.POST_REPORT_CLICK,getAdapterPosition());
                    break;
                case R.id.iv_post_share:
                    listener.onRecyclerItemClick(Const.POST_DOWNLOAD_CLICK,getAdapterPosition());
                    break;
                case R.id.iv_post_edit:
                    listener.onRecyclerItemClick(Const.POST_EDIT_CLICK,getAdapterPosition());
                    break;
                case R.id.iv_post_image:
                    listener.onRecyclerItemClick(0,getAdapterPosition());
                    break;
            }
        }

    }

    public void setPostData(List<DocumentResponseModel.Document> listDocs) {
        this.listDocs = listDocs;
        notifyDataSetChanged();
    }

}
