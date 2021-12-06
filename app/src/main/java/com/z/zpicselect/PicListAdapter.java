package com.z.zpicselect;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.z.zpicselect.recyclerview.adapter.CommonRecyclerAdapter;
import com.z.zpicselect.recyclerview.adapter.ViewHolder;

import java.util.List;

public class PicListAdapter extends CommonRecyclerAdapter<PicEntity> {

    private List<PicEntity> mResultList;
    private int mMaxCount;

    public PicListAdapter(Context context, List<PicEntity> data, List<PicEntity> resultList, int maxCount) {
        super(context, data, R.layout.media_chooser_item);
        mResultList = resultList;
        mMaxCount = maxCount;
    }

    @Override
    public void convert(ViewHolder holder, PicEntity item) {
        if (item == null) {
            // 显示拍照
            holder.setViewVisibility(R.id.camera_ll, View.VISIBLE);
            holder.setViewVisibility(R.id.media_selected_indicator, View.INVISIBLE);
            holder.setViewVisibility(R.id.image, View.INVISIBLE);
            //holder.setViewVisibility(R.id.mask, View.INVISIBLE);
        } else {
            // 显示图片
            holder.setViewVisibility(R.id.camera_ll, View.INVISIBLE);
            holder.setViewVisibility(R.id.media_selected_indicator, View.VISIBLE);
            holder.setViewVisibility(R.id.image, View.VISIBLE);
            // 利用glide显示图片
            ImageView imageView = holder.getView(R.id.image);
            Glide.with(mContext)
                    .load(item.path)
                    .placeholder(R.drawable.ic_discovery_default_channel)
                    .centerCrop().into(imageView);

            // 选中的图片右上角显示勾选颜色
            ImageView selectedIndicatorIv = holder.getView(R.id.media_selected_indicator);
            if (mResultList.contains(item)) {
                selectedIndicatorIv.setSelected(true);
            } else {
                selectedIndicatorIv.setSelected(false);
            }

            // 给条目设置点击事件
            holder.setOnIntemClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // 无则选中，有则取消选中
                    if (mResultList.contains(item)) {
                        mResultList.remove(item);
                    } else {
                        // 不能大于最大选择张数
                        if (mResultList.size() >= mMaxCount) {
                            Toast.makeText(mContext, "最多选择: " + mMaxCount + "张图片", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        mResultList.add(item);
                    }
                    // 更新列表
                    notifyDataSetChanged();
                    // 通知更新状态信息updateSelectedStateInfo
                    if (mSelectListener != null) {
                        mSelectListener.onSelect();
                    }
                }
            });
        }
    }

    private PicSelectListener mSelectListener;

    public void setOnPicSelectListener(PicSelectListener listener) {
        mSelectListener = listener;
    }

}
