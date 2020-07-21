package com.freesith.manhole.ui.adapter.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.freesith.manhole.R
import java.util.*

abstract class BaseAdapter<T>(protected var context: Context) :
    RecyclerView.Adapter<BaseViewHolder<T>>(), View.OnClickListener {
    var mList: MutableList<T> = ArrayList()
    private var onItemClickListener: OnItemClickListener<T>? =
        null

    fun setOnItemClickListener(itemClickListener: OnItemClickListener<T>?) {
        onItemClickListener = itemClickListener
    }

    fun setList(list: List<T>?) {
        list?.let {
            mList.clear()
            mList.addAll(it)
        }
    }

    fun addList(list: List<T>?) {
        list?.let {
            mList.addAll(it)
        }
    }

    fun add(t: T) {
        mList.add(t)
    }

    fun add(t: T, index: Int) {
        mList.add(index, t)
    }


    operator fun get(position: Int): T? {
        return if (position >= 0 && position < mList!!.size) {
            mList!![position]
        } else null
    }

    protected abstract fun getLayoutId(viewType: Int): Int
    protected abstract fun bindView(holder: BaseViewHolder<T>?, t: T, position: Int)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<T> {
        val view =
            LayoutInflater.from(context).inflate(getLayoutId(viewType), parent, false)
        view.setOnClickListener(this)
        return BaseViewHolder(view)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) {
        val t = mList!![position]
        holder.itemView.setTag(R.id.tag_id_position, position)
        bindView(holder, t, position)
    }

    override fun getItemCount(): Int {
        return if (mList == null) 0 else mList!!.size
    }

    fun clear() {
        mList!!.clear()
    }

    override fun onClick(v: View) {
        val tag = v.getTag(R.id.tag_id_position)
        if (tag is Int) {
            val position = tag
            if (onItemClickListener != null && position >= 0 && position < mList!!.size) {
                onItemClickListener!!.onItemClick(mList!![position], position)
            }
        }
    }

    interface OnItemClickListener<T> {
        fun onItemClick(t: T, position: Int)
    }

}