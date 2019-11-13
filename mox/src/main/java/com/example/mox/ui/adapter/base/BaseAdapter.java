package com.example.mox.ui.adapter.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseAdapter<T> extends RecyclerView.Adapter<BaseViewHolder<T>> {

    public BaseAdapter(Context context) {
        this.context = context;
    }

    protected Context context;
    protected List<T> mList = new ArrayList<>();


    public void setList(List<T> list) {
        mList = list;
    }

    public void addList(List<T> list) {
        mList.addAll(list);
    }

    public void add(T t) {
        mList.add(t);
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
        return new BaseViewHolder<>(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<T> holder, int position) {
        T t = mList.get(position);
        bindView(holder, t, position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
