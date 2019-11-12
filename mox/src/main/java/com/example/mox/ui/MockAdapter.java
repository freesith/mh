package com.example.mox.ui;

import com.example.mox.R;
import com.example.mox.db.bean.Mock;
import com.example.mox.ui.base.BaseAdater;
import com.example.mox.ui.base.BaseViewHolder;

public class MockAdapter extends BaseAdater<Mock> {

    @Override
    protected int getLayoutId(int viewType) {
        return R.layout.item_mock;
    }

    @Override
    protected void bindView(BaseViewHolder<Mock> holder, Mock mock) {
        holder.setText(R.id.tvName, mock.name);
        holder.setText(R.id.tvDesc, mock.desc);
        holder.setText(R.id.tvMethod, mock.request.method);
        holder.setText(R.id.tvPath, mock.request.path);
    }

}
