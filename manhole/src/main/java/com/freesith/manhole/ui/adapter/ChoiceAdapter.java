package com.freesith.manhole.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.mox.R;
import com.freesith.manhole.db.bean.MockChoice;
import com.freesith.manhole.ui.adapter.base.BaseAdapter;
import com.freesith.manhole.ui.adapter.base.BaseViewHolder;

public class ChoiceAdapter extends BaseAdapter<MockChoice> {

    public ChoiceAdapter(Context context) {
        super(context);
    }

    private ChoiceListener  choiceListener;

    public void setChoiceListener(ChoiceListener choiceListener) {
        this.choiceListener = choiceListener;
    }

    @Override
    protected int getLayoutId(int viewType) {
        return R.layout.item_mock;
    }

    @Override
    protected void bindView(BaseViewHolder<MockChoice> holder, final MockChoice mock, final int position) {
        String method = mock.method;

        holder.setText(R.id.tvName, mock.name);
        holder.setText(R.id.tvTitle, mock.title);
        holder.setText(R.id.tvDesc, mock.desc);
        holder.setText(R.id.tvMethod, method.toUpperCase());
        holder.setText(R.id.tvPath, mock.path);

        TextView tvPath = holder.getView(R.id.tvPath);
        //only support get & post for now
        if ("get".equalsIgnoreCase(method)) {
            holder.getView(R.id.tvMethod).setBackgroundResource(R.drawable.manhole_left_circle_get);
            tvPath.setBackgroundResource(R.drawable.manhole_right_circle_stroke_get);
        } else {
            holder.getView(R.id.tvMethod).setBackgroundResource(R.drawable.manhole_left_circle_post);
            tvPath.setBackgroundResource(R.drawable.manhole_right_circle_stroke_post);
        }
        if (mock.passive) {
            holder.getItemView().setBackgroundColor(context.getResources().getColor(R.color.manhole_mock33));
        } else {
            holder.getItemView().setBackgroundColor(context.getResources().getColor(R.color.manhole_white));
        }

        Switch switchMock = holder.getView(R.id.switchMock);
        switchMock.setOnCheckedChangeListener(null);
        switchMock.setChecked(mock.enable);
        switchMock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (choiceListener != null) {
                    choiceListener.onChoiceEnableChanged(mock, isChecked, position);
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (choiceListener != null) {
                    choiceListener.onChoiceClick(mock);
                }
            }
        });

    }

    public interface ChoiceListener {

        void onChoiceEnableChanged(MockChoice choice, boolean enable, int position);

        void onChoiceClick(MockChoice mock);
    }
}
