package com.cxb.myfamilytree.ui.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cxb.myfamilytree.R;
import com.cxb.myfamilytree.model.ThemeBean;
import com.cxb.myfamilytree.utils.DisplayUtil;
import com.cxb.myfamilytree.utils.PrefUtils;

import java.util.List;

/**
 * 主题列表
 */

public class ThemeListAdapter extends RecyclerView.Adapter {

    private OnListClickListener mListClick;
    private Context mContext;
    private List<ThemeBean> mList;
    private LayoutInflater mInflater;

    private int mRadiusDP;
    private int mStrokeWidthDP;

    public ThemeListAdapter(Context context, List<ThemeBean> list) {
        mContext = context;
        mList = list;
        mInflater = LayoutInflater.from(context);

        mRadiusDP = DisplayUtil.dip2px(4);
        mStrokeWidthDP = DisplayUtil.dip2px(1);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ListViewHolder(mInflater.inflate(R.layout.item_theme_list, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        bindItem((ListViewHolder) holder, position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private void bindItem(ListViewHolder holder, final int position) {
        final ThemeBean model = mList.get(position);
        final String name = model.getName();
        final int color = model.getColor();
        final String selectTheme = PrefUtils.getTheme();
        final boolean isSelect = selectTheme.equals(name);
        TypedValue typedValue = new TypedValue();
        mContext.getTheme().resolveAttribute(android.R.attr.textColorSecondary, typedValue, true);
        final int normalColor = ContextCompat.getColor(mContext, typedValue.resourceId);
        final int selectColor = ContextCompat.getColor(mContext, color);

        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(selectColor);

        holder.ivSelect.setImageResource(isSelect ? R.drawable.ic_done : 0);
        holder.ivSelect.setBackground(drawable);
        holder.tvName.setText(name);
        holder.tvName.setTextColor(selectColor);

        GradientDrawable tagDrawable = new GradientDrawable();
        tagDrawable.setCornerRadius(mRadiusDP);
        tagDrawable.setStroke(mStrokeWidthDP, isSelect ? selectColor : normalColor);

        holder.tvTag.setTextColor(isSelect ? selectColor : normalColor);
        holder.tvTag.setBackground(tagDrawable);
        holder.tvTag.setText(isSelect ? "使用中" : "使用");

        holder.itemView.setTag(holder.getAdapterPosition());
        holder.itemView.setOnClickListener(mClick);
    }

    private class ListViewHolder extends RecyclerView.ViewHolder {

        private View itemView;
        private ImageView ivSelect;
        private TextView tvName;
        private TextView tvTag;

        private ListViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            ivSelect = (ImageView) itemView.findViewById(R.id.iv_select);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvTag = (TextView) itemView.findViewById(R.id.tv_tag);
        }
    }

    public void setListClick(OnListClickListener listClick) {
        this.mListClick = listClick;
    }

    private View.OnClickListener mClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mListClick != null) {
                final int position = (int) v.getTag();
                mListClick.onItemClick(position);
            }
        }
    };

}
