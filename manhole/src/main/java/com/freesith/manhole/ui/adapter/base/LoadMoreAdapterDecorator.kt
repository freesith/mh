package com.freesith.manhole.ui.adapter.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.freesith.manhole.R

class LoadMoreAdapterDecorator<T, V: BaseAdapter<T>>(
        private val context: Context, private val adapter: V, private val loadMoreCallback: () -> Unit) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var status: Int = STATE_NO_MORE

    companion object {
        //避免和type重复
        private const val LOAD_MORE_VIEW = 2267

        const val STATE_NO_MORE = 0
        const val STATE_HAS_MORE = 1
        const val STATE_LOADING = 2
        const val STATE_LOAD_FAIL = 3
    }

    fun loadSuccess(hasMore: Boolean) {
        setStatus(if (hasMore) STATE_HAS_MORE else STATE_NO_MORE)
    }

    fun loadMoreFail() {
        setStatus(STATE_LOAD_FAIL)
    }

    fun hasMore(): Boolean {
        return status == STATE_HAS_MORE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // 通过判断显示类型，来创建不同的View
        return if (viewType == LOAD_MORE_VIEW) {
            val view = LayoutInflater.from(context).inflate(R.layout.item_list_load_more, parent, false)
            view.setOnClickListener {
                if (status == STATE_LOAD_FAIL) {
                    setStatus(STATE_LOADING)
                    loadMoreCallback.invoke()
                }
            }
            return LoadMoreHolder(view)
        } else {
            adapter.onCreateViewHolder(parent, viewType)
        }
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        val layoutParams = holder.itemView.layoutParams;
        if (layoutParams != null && layoutParams is StaggeredGridLayoutManager.LayoutParams) {
            if (getItemViewType(holder.layoutPosition) == LOAD_MORE_VIEW) {
                layoutParams.isFullSpan = true
            }
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LoadMoreAdapterDecorator<*, *>.LoadMoreHolder) {
            holder.status(status)
        } else {
            adapter.onBindViewHolder(holder as BaseViewHolder<T>, position)
        }
    }

    /**
     * 状态变化，只会刷新最后一排
     */
    private fun setStatus(status: Int) {
        if (this.status != status) {
            this.status = status
            notifyItemChanged(adapter.itemCount)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (holder is LoadMoreAdapterDecorator<*, *>.LoadMoreHolder) {
            holder.status(status)
        } else {
            adapter.onBindViewHolder(holder as BaseViewHolder<T>, position, payloads)
        }
    }

    override fun getItemCount(): Int {
        //如果列表是空的,就不显示"没有更多了"
        return if (adapter.itemCount == 0) {
            0
        } else adapter.itemCount + 1
    }

    override fun getItemViewType(position: Int): Int {

        return if (position == adapter.itemCount) {
            //最后一个是加载更多
            LOAD_MORE_VIEW
        } else adapter.getItemViewType(position)
    }

        inner class LoadMoreHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val pbLoad: ProgressBar = view.findViewById(R.id.pbLoad)
        private val tvLoad: TextView = view.findViewById(R.id.tvLoad)

        fun status(status: Int) {
            when (status) {
                STATE_HAS_MORE -> {
                    itemView.visibility = View.VISIBLE
                    itemView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    pbLoad.visibility = View.VISIBLE
                    tvLoad.text = "加载中..."
                    this@LoadMoreAdapterDecorator.status = STATE_LOADING
                    loadMoreCallback.invoke()
                }
                STATE_LOADING -> {
                    itemView.visibility = View.VISIBLE
                    itemView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    pbLoad.visibility = View.VISIBLE
                    tvLoad.text = "加载中..."
                }
                STATE_NO_MORE -> {
                    itemView.visibility = View.GONE
                    itemView.layoutParams.height = 0
                }
                STATE_LOAD_FAIL -> {
                    itemView.visibility = View.VISIBLE
                    itemView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    pbLoad.visibility = View.GONE
                    tvLoad.text = "加载失败, 点击重试"
                }
            }
        }
    }


}
