package com.freesith.jsonview

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.freesith.jsonview.bean.JsonElement

class JsonAdapter (private val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var jsonElements: MutableList<JsonElement<*>>? = null
    private var headerView: View? = null

    companion object {
        const val TYPE_HEADER = 1
        const val TYPE_JSON = 2
    }

    override fun getItemCount(): Int {
        val jsonSize = if (jsonElements.isNullOrEmpty()) 0 else jsonElements!!.size
        val headerSize = if (headerView != null) 1 else 0
        return jsonSize + headerSize
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0 && headerView != null) {
            return TYPE_HEADER
        } else {
            return TYPE_JSON
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is JsonViewHolder) {
            if (headerView == null) {
                holder.bindJsonLine(jsonElements!!.get(position))
            } else {
                holder.bindJsonLine(jsonElements!!.get(position - 1))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {

            TYPE_HEADER -> {
                return HeaderViewHolder(headerView!!)
            }

            else -> {
                val textView = TextView(context)
                textView.setTextColor(Color.parseColor("#1e194d"))
                return JsonViewHolder(textView)
            }
        }
    }

    fun setElements(jsonElements: MutableList<JsonElement<*>>) {
        this.jsonElements = jsonElements
    }

    fun setHeaderView(view: View) {
        headerView = view
    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


    inner class JsonViewHolder(private val itemText: TextView) : RecyclerView.ViewHolder(itemText) {

        fun bindJsonLine(jsonElement: JsonElement<*>) {
            val level = jsonElement.level
            val spannableStringBuilder = SpannableStringBuilder()
            (0 .. level).forEach {
                spannableStringBuilder.append("  ")
            }
            spannableStringBuilder.append(jsonElement.name).append(":")
            val headLength = spannableStringBuilder.length
            spannableStringBuilder.append(jsonElement.value.toString())
            spannableStringBuilder.setSpan(ForegroundColorSpan(jsonElement.valueColor), headLength, spannableStringBuilder.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            itemText.typeface = Typeface.MONOSPACE
            itemText.setText(spannableStringBuilder)
            itemText.setPadding(0 ,0 ,0, 12)
        }
    }
}