package com.freesith.manhole.ui.adapter.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.freesith.manhole.R;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseAdapter<T> extends RecyclerView.Adapter<BaseViewHolder<T>> implements View.OnClickListener {

    public BaseAdapter(Context context) {
        this.context = context;
    }

    protected Context context;
    protected List<T> mList = new ArrayList<>();
    private OnItemClickListener<T> onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener<T> itemClickListener) {
        this.onItemClickListener = itemClickListener;
    }

    public void setList(List<T> list) {
        mList = list;
    }

    public void addList(List<T> list) {
        mList.addAll(list);
    }

    public void add(T t) {
        mList.add(t);
    }
    public void add(T t, int index) {
        mList.add(index, t);
    }

    public List<T> getList() {
        return mList;
    }

    public T get(int position) {
        if (position >= 0 && position < mList.size()) {
            return mList.get(position);
        }
        return null;
    }

    protected abstract int getLayoutId(int viewType);

    protected abstract void bindView(BaseViewHolder<T> holder, T t, int position);

    @NonNull
    @Override
    public BaseViewHolder<T> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(getLayoutId(viewType), parent, false);
        view.setOnClickListener(this);
        return new BaseViewHolder<>(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<T> holder, int position) {
        T t = mList.get(position);
        holder.itemView.setTag(R.id.tag_id_position, position);
        bindView(holder, t, position);
    }

    @Override
    public int getItemCount() {
        return mList == null  ? 0 : mList.size();
    }

    public void clear() {
        mList.clear();
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag(R.id.tag_id_position);
        if (tag instanceof Integer) {
            int position = (int) tag;
            if (onItemClickListener != null && position >= 0 && position < mList.size()) {
                onItemClickListener.onItemClick(mList.get(position), position);
            }
        }
    }

    public interface OnItemClickListener<T> {
        void onItemClick(T t, int position);
    }
}
