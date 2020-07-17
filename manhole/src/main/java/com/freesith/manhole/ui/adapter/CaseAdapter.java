package com.freesith.manhole.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.freesith.manhole.R;
import com.freesith.manhole.Util;
import com.freesith.manhole.bean.Case;
import com.freesith.manhole.ui.adapter.base.BaseAdapter;
import com.freesith.manhole.ui.adapter.base.BaseViewHolder;

public class CaseAdapter extends BaseAdapter<Case> {

    public CaseAdapter(Context context) {
        super(context);
    }

    private CaseListener caseListener;

    public void setCaseListener(CaseListener caseListener) {
        this.caseListener = caseListener;
    }

    @Override
    protected int getLayoutId(int viewType) {
        return R.layout.item_case;
    }

    @Override
    protected void bindView(BaseViewHolder<Case> holder, final Case aCase, final int position) {
        holder.setText(R.id.tvName, aCase.name);
        holder.setText(R.id.tvDesc, aCase.desc);
        holder.setText(R.id.tvTitle, aCase.title);
        Switch switchCase = holder.getView(R.id.switchCase);
        switchCase.setOnCheckedChangeListener(null);
        switchCase.setChecked(aCase.enable);
        switchCase.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (caseListener != null) {
                    caseListener.onCaseEnableChanged(aCase.name, isChecked, position);
                }
            }
        });
        holder.itemView.setBackgroundColor(getContext().getResources().getColor(aCase.passive ? R.color.manhole_case33 : R.color.manhole_white));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (caseListener != null) {
                    caseListener.onCaseClick(aCase.name);
                }
            }
        });
    }


    public interface CaseListener {

        void onCaseEnableChanged(String name, boolean enable, int position);

        void onCaseClick(String name);
    }
}
