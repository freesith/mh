package com.freesith.jsonview

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.freesith.jsonview.bean.JsonElement

class JsonAdapter (private val context: Context): RecyclerView.Adapter<JsonAdapter.JsonViewHolder>() {

    private var jsonElements: MutableList<JsonElement<*>>? = null

    override fun getItemCount(): Int {
        return if (jsonElements.isNullOrEmpty()) 0 else jsonElements!!.size
    }

    override fun onBindViewHolder(holder: JsonViewHolder, position: Int) {
        holder.bindJsonLine(jsonElements!!.get(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JsonViewHolder {
        val textView = TextView(context)
        textView.setTextColor(Color.parseColor("#1e194d"))
        return JsonViewHolder(textView)
    }

    fun setElements(jsonElements: MutableList<JsonElement<*>>) {
        this.jsonElements = jsonElements
    }


    class JsonViewHolder(private val itemText: TextView) : RecyclerView.ViewHolder(itemText) {

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