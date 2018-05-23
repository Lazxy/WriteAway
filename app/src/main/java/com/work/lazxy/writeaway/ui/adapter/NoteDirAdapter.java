package com.work.lazxy.writeaway.ui.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupWindow;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.work.lazxy.writeaway.R;
import com.work.lazxy.writeaway.common.Constant;
import com.work.lazxy.writeaway.entity.NoteEntity;
import com.work.lazxy.writeaway.event.EventDeleteNote;
import com.work.lazxy.writeaway.ui.activity.NoteActivity;
import com.work.lazxy.writeaway.utils.CalendarUtils;
import com.work.lazxy.writeaway.utils.UIUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by Lazxy on 2017/4/29.
 */

public class NoteDirAdapter extends BaseQuickAdapter<NoteEntity,BaseViewHolder> {

    public NoteDirAdapter(int layoutResId, List<NoteEntity> data) {
        super(layoutResId,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, NoteEntity item) {
        String title = TextUtils.isEmpty(item.getTitle())?mContext.getResources().getString(R.string.no_title):item.getTitle();
        helper.setText(R.id.tv_note_dir_title,title);
        helper.setText(R.id.tv_note_dir_preview,item.getPreview());
        helper.setText(R.id.tv_note_dir_last_edit, CalendarUtils.getTimeFromTimestamp(item.getLastEditTime()));
        View options = helper.getView(R.id.iv_note_dir_options);
        options.setTag(item);
        options.setOnClickListener(mOptionsListener);
        View card = helper.getView(R.id.card_item_note);
        card.setTag(item);
        card.setOnClickListener(mCardListener);
    }

    private View.OnClickListener mOptionsListener = new View.OnClickListener(){

        @Override
        public void onClick(final View v) {
            /*显示一个PopupMenu*/
            PopupMenu popupMenu = new PopupMenu(v.getContext(), v, Gravity.BOTTOM);
            popupMenu.inflate(R.menu.popup_note_dir);

            UIUtils.setIconEnable(popupMenu.getMenu(), true);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int id = item.getItemId();
                    switch(id){
                        case R.id.menu_note_dir_delete:
                            UIUtils.showSimpleAlertDialog(v.getContext(),
                                    null, "确认删除吗？", "删除", "取消",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String path = ((NoteEntity)v.getTag()).getFilePath();
                                            EventBus.getDefault().post(new EventDeleteNote(path));
                                        }
                                    },null);
                            break;
                        default:break;
                    }
                    return true;
                }
            });
            popupMenu.show();
        }
    };

    private View.OnClickListener mCardListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, NoteActivity.class);
            intent.putExtra(Constant.Extra.EXTRA_NOTE,(NoteEntity)v.getTag());
            ((Activity)mContext).startActivityForResult(intent,Constant.Common.REQUEST_CODE_UPDATE_NOTE);
        }
    };
}
