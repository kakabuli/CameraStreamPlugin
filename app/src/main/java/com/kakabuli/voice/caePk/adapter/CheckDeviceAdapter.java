package com.kakabuli.voice.caePk.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import androidx.core.content.ContextCompat;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kakabuli.camerastream.MyApplication;
import com.kakabuli.camerastream.R;
import com.kakabuli.voice.caePk.bean.CheckDeviceBean;
import com.kakabuli.voice.caePk.bean.ItemCheckType;

import java.util.List;

public class CheckDeviceAdapter extends BaseMultiItemQuickAdapter<CheckDeviceBean> {
    private List<CheckDeviceBean> dataList;

    public CheckDeviceAdapter(List<CheckDeviceBean> data) {
        super(data);
        this.dataList = data;
        addItemType(ItemCheckType.ITEM_TEXT.ordinal(), R.layout.layout_item_check);
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, CheckDeviceBean checkDeviceBean) {
        TextView number = (TextView) viewHolder.itemView.findViewById(R.id.number);
        number.setText(dataList.indexOf(checkDeviceBean) + 1 + "");

        TextView checkTitle = (TextView) viewHolder.itemView.findViewById(R.id.checkTitle);
        checkTitle.setText(checkDeviceBean.getCheckTitle() + ": ");

        TextView checkResult = (TextView) viewHolder.itemView.findViewById(R.id.checkResult);
        checkResult.setText(checkDeviceBean.getCheckResult());

        int checkResultStatus = checkDeviceBean.getCheckResultStatus();
        ImageView imageStatus = viewHolder.itemView.findViewById(R.id.statusImg);
        ProgressBar progressBar = viewHolder.itemView.findViewById(R.id.checkProgressBar);
        if (checkResultStatus == -1) {
            imageStatus.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            imageStatus.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            if (checkResultStatus == 0) {
                imageStatus.setBackgroundResource(R.drawable.img_check_error);
            } else {
                imageStatus.setBackgroundResource(R.drawable.img_check_success);
            }
        }
    }


    public void updateData() {
        notifyDataSetChanged();
    }

    public void updateData(CheckDeviceBean checkDeviceBean) {
        for (CheckDeviceBean it : dataList) {
            if (it.getCheckType() == checkDeviceBean.getCheckType()) {
                it.setCheckResultStatus(checkDeviceBean.getCheckResultStatus());
                it.setCheckResult(checkDeviceBean.getCheckResult());
                break;
            }
        }
        notifyDataSetChanged();
    }

    public void addFooterViewTo(boolean checkSuccess, boolean noRootNoCard) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_item_footer, null);
        TextView findViewById = view.findViewById(R.id.footerView);
        if (checkSuccess) {
            findViewById.setText(MyApplication.getInstance().getString(R.string.check_success));
            findViewById
                    .setTextColor(ContextCompat.getColor(mContext, R.color.green));
        } else {
            if (!noRootNoCard) {
                findViewById.setText(MyApplication.getInstance().getString(R.string.check_error));
            } else {
                findViewById.setText(MyApplication.getInstance().getString(R.string.system_no_root_check_error));
            }
            findViewById
                    .setTextColor(ContextCompat.getColor(mContext, R.color.red));
        }
        addFooterView(view);
    }
}
