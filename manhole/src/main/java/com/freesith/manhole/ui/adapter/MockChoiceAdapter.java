package com.freesith.manhole.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.freesith.manhole.R;
import com.freesith.manhole.bean.MockChoice;
import com.freesith.manhole.ui.adapter.base.BaseAdapter;
import com.freesith.manhole.ui.adapter.base.BaseViewHolder;

public class MockChoiceAdapter extends BaseAdapter<MockChoice> {

    public MockChoiceAdapter(Context context) {
        super(context);
    }

    private EnableChoiceAdapter.ChoiceListener choiceListener;

    public void setChoiceListener(EnableChoiceAdapter.ChoiceListener choiceListener) {
        this.choiceListener = choiceListener;
    }


    @Override
    protected int getLayoutId(int viewType) {
        return R.layout.item_mock_choice;
    }

    @Override
    protected void bindView(BaseViewHolder<MockChoice> holder, final MockChoice mock, final int position) {
        holder.setText(R.id.manhole_tvName, mock.name);
        holder.setText(R.id.manhole_tvTitle, mock.title);
        holder.setEmptyGoneText(R.id.manhole_tvDesc, mock.desc);

        Switch switchMock = holder.getView(R.id.manhole_switchMock);
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
        if (mock.passive) {
            holder.getItemView().setBackgroundColor(getContext().getResources().getColor(R.color.manhole_mock33));
        } else {
            holder.getItemView().setBackgroundColor(getContext().getResources().getColor(R.color.manhole_white));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (choiceListener != null) {
                    choiceListener.onChoiceClick(mock);
                }
            }
        });

    }
}
