package com.freesith.manhole.ui.adapter;

import android.content.Context;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.mox.R;
import com.freesith.manhole.Util;
import com.freesith.manhole.db.bean.Case;
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

        holder.setText(R.id.tvMocks, Util.join(aCase.mocks, ","));

        Switch switchCase = holder.getView(R.id.switchCase);
        switchCase.setOnCheckedChangeListener(null);
        switchCase.setChecked(aCase.enable);
        switchCase.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if  (caseListener != null) {
                    caseListener.onCaseEnableChanged(aCase.name, isChecked, position);
                }
            }
        });
        holder.itemView.setBackgroundColor(context.getResources().getColor(aCase.passive? R.color.manhole_case33 : R.color.manhole_white));
    }


    public interface CaseListener {
        void onCaseEnableChanged(String name, boolean enable, int position);
    }
}
