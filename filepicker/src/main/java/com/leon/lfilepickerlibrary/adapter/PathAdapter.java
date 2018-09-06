package com.leon.lfilepickerlibrary.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.leon.lfilepickerlibrary.R;
import com.leon.lfilepickerlibrary.filter.LFileFilter;
import com.leon.lfilepickerlibrary.model.ParamEntity;
import com.leon.lfilepickerlibrary.utils.FileUtils;

import java.io.File;
import java.io.FileFilter;
import java.util.List;

/**
 * 作者：Leon
 * 时间：2017/3/15 15:47
 */
public class PathAdapter extends RecyclerView.Adapter<PathAdapter.PathViewHolder> {
    private final String TAG = "FilePickerLeon";
    private List<File> mListData;
    private Context mContext;
    public OnItemClickListener onItemClickListener;
    private FileFilter mFileFilter;
    private boolean[] mCheckedFlags;
    private ParamEntity mParams;

    public PathAdapter(List<File> files, Context context, ParamEntity params) {
        this.mListData = files;
        this.mContext = context;
        this.mFileFilter = new LFileFilter(params.getFileTypes());
        mParams = params;
        mCheckedFlags = new boolean[files.size()];
    }

    @Override
    public PathViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.lfile_listitem, null);
        PathViewHolder pathViewHolder = new PathViewHolder(view);
        return pathViewHolder;
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    @Override
    public void onBindViewHolder(final PathViewHolder holder, final int position) {
        final File file = mListData.get(position);
        if (file.isFile()) {
            if (mParams.getFileIcon() != 0) {
                holder.ivFileTypeIcon.setBackgroundResource(mParams.getFileIcon());
            } else {
                holder.ivFileTypeIcon.setBackgroundResource(R.mipmap.lfile_file_style_yellow);
            }
            holder.tvName.setText(file.getName());
            holder.tvDetail.setText(mContext.getString(R.string.lfile_file_size) + " " + FileUtils.getReadableFileSize(file.length()));
            holder.cbChoose.setVisibility(View.VISIBLE);
        } else {
            if (mParams.getFolderIcon() != 0) {
                holder.ivFileTypeIcon.setBackgroundResource(mParams.getFolderIcon());
            } else {
                holder.ivFileTypeIcon.setBackgroundResource(R.mipmap.lfile_folder_style_yellow);
            }
            holder.tvName.setText(file.getName());
            //文件大小过滤
            List files = FileUtils.getFileList(file.getAbsolutePath(), mFileFilter, mParams.isGreater(), mParams.getStandardFileSize());
            if (files == null) {
                holder.tvDetail.setText("0 " + mContext.getString(R.string.lfile_item));
            } else {
                holder.tvDetail.setText(files.size() + " " + mContext.getString(R.string.lfile_item));
            }
            holder.cbChoose.setVisibility(View.GONE);
        }
        if (!mParams.isMultiMode()) {
            holder.cbChoose.setVisibility(View.GONE);
        }
        holder.layoutRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (file.isFile()) {
                    holder.cbChoose.setChecked(!holder.cbChoose.isChecked());
                }
                onItemClickListener.onClick(position);
            }
        });
        holder.cbChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //同步复选框和外部布局点击的处理
                onItemClickListener.onClick(position);
            }
        });
        //先设置一次CheckBox的选中监听器，传入参数null
        holder.cbChoose.setOnCheckedChangeListener(null);
        //用数组中的值设置CheckBox的选中状态
        holder.cbChoose.setChecked(mCheckedFlags[position]);
        //再设置一次CheckBox的选中监听器，当CheckBox的选中状态发生改变时，把改变后的状态储存在数组中
        holder.cbChoose.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mCheckedFlags[position] = b;
            }
        });
    }

    /**
     * 设置项目点击监听
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 设置文件列表数据源
     */
    public void setListData(List<File> mListData) {
        this.mListData = mListData;
        mCheckedFlags = new boolean[mListData.size()];
    }

    /**
     * 设置是否全选
     */
    public void updateAllSelected(boolean isAllSelected) {

        for (int i = 0; i < mCheckedFlags.length; i++) {
            mCheckedFlags[i] = isAllSelected;
        }
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }

    class PathViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout layoutRoot;
        private ImageView ivFileTypeIcon;
        private TextView tvName;
        private TextView tvDetail;
        private CheckBox cbChoose;

        private PathViewHolder(View itemView) {
            super(itemView);
            ivFileTypeIcon = itemView.findViewById(R.id.iv_file_type_icon);
            layoutRoot = itemView.findViewById(R.id.layout_item_root);
            tvName = itemView.findViewById(R.id.tv_name);
            tvDetail = itemView.findViewById(R.id.tv_detail);
            cbChoose = itemView.findViewById(R.id.cb_choose);
        }
    }
}


