package com.freesith.manhole.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.freesith.manhole.R;
import com.freesith.manhole.bean.Case;
import com.freesith.manhole.bean.MockChoice;
import com.freesith.manhole.ui.adapter.base.BaseViewHolder;

import java.util.List;

public class CaseChoiceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_CASE = 1;
    public static final int TYPE_CHOICE= 2;

    private Context context;

    private List<Case> caseList;
    private List<MockChoice> choiceList;

    private CaseAdapter.CaseListener caseListener;

    public void setCaseListener(CaseAdapter.CaseListener caseListener) {
        this.caseListener = caseListener;
    }


    public CaseChoiceAdapter(Context context) {
        this.context = context;
    }

    public void setCaseList(List<Case> list) {
        this.caseList = list;
    }
    public void setChoiceList(List<MockChoice> list) {
        this.choiceList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_CASE) {
            return new CaseHolder(LayoutInflater.from(context).inflate(R.layout.item_case, parent, false));
        } else {
            return new ChoiceHolder(LayoutInflater.from(context).inflate(R.layout.item_enable_choice, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CaseHolder) {
            final Case caze = caseList.get(position);
            ((CaseHolder) holder).setText(R.id.manhole_tvName, caze.name);
            ((CaseHolder) holder).setText(R.id.manhole_tvTitle, caze.title);
            ((CaseHolder) holder).setEmptyGoneText(R.id.manhole_tvDesc, caze.desc);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (caseListener != null) {
                        caseListener.onCaseClick(caze.name);
                    }
                }
            });
        } else {
            int caseSize = caseList == null ? 0 : caseList.size();
            MockChoice choice = choiceList.get(position - caseSize);
            ChoiceHolder choiceHolder = (ChoiceHolder)holder;
            String method = choice.method;

            choiceHolder.setText(R.id.manhole_tvMockName, choice.mockName);
            choiceHolder.setText(R.id.manhole_tvName, choice.name);
            choiceHolder.setText(R.id.manhole_tvTitle, choice.title);
            choiceHolder.setEmptyGoneText(R.id.manhole_tvDesc, choice.desc);
            choiceHolder.setText(R.id.manhole_tvMethod, method.toUpperCase());
            choiceHolder.setText(R.id.manhole_tvPath, choice.path);

            TextView tvPath = choiceHolder.getView(R.id.manhole_tvPath);
            //only support get & post for now
            if ("get".equalsIgnoreCase(method)) {
                choiceHolder.getView(R.id.manhole_tvMethod).setBackgroundResource(R.drawable.manhole_left_circle_get);
                tvPath.setBackgroundResource(R.drawable.manhole_right_circle_stroke_get);
            } else {
                choiceHolder.getView(R.id.manhole_tvMethod).setBackgroundResource(R.drawable.manhole_left_circle_post);
                tvPath.setBackgroundResource(R.drawable.manhole_right_circle_stroke_post);
            }
            if (choice.passive) {
                choiceHolder.getItemView().setBackgroundColor(context.getResources().getColor(R.color.manhole_mock33));
            } else {
                choiceHolder.getItemView().setBackgroundColor(context.getResources().getColor(R.color.manhole_white));
            }

            Switch switchMock = choiceHolder.getView(R.id.manhole_switchMock);
            switchMock.setOnCheckedChangeListener(null);
            switchMock.setChecked(choice.enable);

        }
    }

    @Override
    public int getItemViewType(int position) {
        int caseSize = caseList == null ? 0 : caseList.size();
        if (position < caseSize) {
            return TYPE_CASE;
        } else {
            return TYPE_CHOICE;
        }
    }

    @Override
    public int getItemCount() {
        int caseSize = caseList == null ? 0 : caseList.size();
        int choiceSize = choiceList == null ? 0 : choiceList.size();
        return caseSize + choiceSize;
    }


    class CaseHolder extends BaseViewHolder<Case> {

        public CaseHolder(@NonNull View itemView) {
            super(itemView);
        }
    }


    class ChoiceHolder extends BaseViewHolder<MockChoice> {

        public ChoiceHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
