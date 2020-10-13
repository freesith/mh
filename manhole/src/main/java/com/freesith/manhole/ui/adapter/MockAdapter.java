package com.freesith.manhole.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.freesith.manhole.R;
import com.freesith.manhole.bean.Mock;
import com.freesith.manhole.ui.adapter.base.BaseAdapter;
import com.freesith.manhole.ui.adapter.base.BaseViewHolder;

public class MockAdapter extends BaseAdapter<Mock> {

    public MockAdapter(Context context) {
        super(context);
    }

    private MockListener mockListener;

    public void setMockListener(MockListener mockListener) {
        this.mockListener = mockListener;
    }

    @Override
    protected int getLayoutId(int viewType) {
        return R.layout.item_mock;
    }

    @Override
    protected void bindView(BaseViewHolder<Mock> holder, final Mock mock, final int position) {
        String method = mock.request.method;

        holder.setText(R.id.manhole_tvName, mock.name);
        holder.setText(R.id.manhole_tvTitle, mock.title);
        holder.setEmptyGoneText(R.id.manhole_tvDesc, mock.desc);
        holder.setText(R.id.manhole_tvMethod, method.toUpperCase());
        holder.setText(R.id.manhole_tvPath, mock.request.path);

        TextView tvPath = holder.getView(R.id.manhole_tvPath);
        //only support get & post for now
        if ("get".equalsIgnoreCase(method)) {
            holder.getView(R.id.manhole_tvMethod).setBackgroundResource(R.drawable.manhole_left_circle_get);
            tvPath.setBackgroundResource(R.drawable.manhole_right_circle_stroke_get);
        } else {
            holder.getView(R.id.manhole_tvMethod).setBackgroundResource(R.drawable.manhole_left_circle_post);
            tvPath.setBackgroundResource(R.drawable.manhole_right_circle_stroke_post);
        }
        if (mock.passive) {
            holder.getItemView().setBackgroundColor(getContext().getResources().getColor(R.color.manhole_mock33));
        } else {
            holder.getItemView().setBackgroundColor(getContext().getResources().getColor(R.color.manhole_white));
        }

        Switch switchMock = holder.getView(R.id.manhole_switchMock);
        switchMock.setOnCheckedChangeListener(null);
        switchMock.setChecked(mock.enable);
        switchMock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mockListener != null) {
                    mockListener.onMockEnableChanged(mock.name, isChecked, position);
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mockListener != null) {
                    mockListener.onMockClick(mock);
                }
            }
        });

    }

    public interface MockListener {

        void onMockEnableChanged(String name, boolean enable, int position);

        void onMockClick(Mock mock);
    }

}
