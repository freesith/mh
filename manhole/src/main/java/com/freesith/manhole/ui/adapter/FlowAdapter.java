package com.freesith.manhole.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.freesith.manhole.R;
import com.freesith.manhole.bean.Flow;
import com.freesith.manhole.ui.adapter.base.BaseAdapter;
import com.freesith.manhole.ui.adapter.base.BaseViewHolder;

public class FlowAdapter extends BaseAdapter<Flow> {

    public FlowAdapter(Context context) {
        super(context);
    }

    private FlowListener flowListener;


    public void setFlowListener(FlowListener flowListener) {
        this.flowListener = flowListener;
    }

    @Override
    protected int getLayoutId(int viewType) {
        return R.layout.manhole_item_flow;
    }

    @Override
    protected void bindView(BaseViewHolder<Flow> holder, final Flow flow, final int position) {
        holder.setText(R.id.manhole_tvName, flow.name);
        holder.setEmptyGoneText(R.id.manhole_tvDesc, flow.desc);
        holder.setText(R.id.manhole_tvTitle, flow.title);

//        String caseText = Util.join(flow.cases, ", ");
//        if(!TextUtils.isEmpty(caseText)) {
//            holder.setText(R.id.tvCases, caseText);
//            holder.getView(R.id.tvCases).setVisibility(View.VISIBLE);
//        } else {
//            holder.getView(R.id.tvCases).setVisibility(View.GONE);
//        }

//        String mockText = Util.join(flow.mocks, ", ");
//        if (!TextUtils.isEmpty(mockText)) {
//            holder.setText(R.id.tvMocks, mockText);
//            holder.getView(R.id.tvMocks).setVisibility(View.VISIBLE);
//        } else {
//            holder.getView(R.id.tvMocks).setVisibility(View.GONE);
//        }

        Switch switchFlow = holder.getView(R.id.manhole_switchFlow);
        switchFlow.setOnCheckedChangeListener(null);
        switchFlow.setChecked(flow.enable);
        switchFlow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (flowListener != null) {
                    flowListener.onFlowEnableChanged(flow.name, isChecked, position);
                }
            }
        });
        holder.getItemView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flowListener != null) {
                    flowListener.onFlowClick(flow.name);
                }
            }
        });
    }

    public interface FlowListener {

        void onFlowEnableChanged(String name, boolean enable, int position);

        void onFlowClick(String name);
    }

}
