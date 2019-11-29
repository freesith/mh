package com.freesith.manhole.ui.adapter.base;

import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class BaseViewHolder<T> extends RecyclerView.ViewHolder {

    private SparseArray<View> viewCache = new SparseArray<>();

    public BaseViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public <V extends View> V getView(int id) {
        View view = viewCache.get(id);
        if (view == null) {
            view = itemView.findViewById(id);
            viewCache.put(id, view);
        }
        return (V) view;
    }

    public View getItemView() {
        return itemView;
    }

    public void setText(int viewId, String text) {
        TextView textView = getView(viewId);
        if (textView != null) {
            textView.setText(text);
        }
    }

}
