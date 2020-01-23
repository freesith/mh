package com.freesith.jsonview

import android.content.Context
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
        return JsonViewHolder(textView)
    }

    fun setElements(jsonElements: MutableList<JsonElement<*>>) {
        this.jsonElements = jsonElements
    }


    class JsonViewHolder(private val itemText: TextView) : RecyclerView.ViewHolder(itemText) {

        fun bindJsonLine(jsonElement: JsonElement<*>) {
            val level = jsonElement.level
            val builder = StringBuilder()
            (0 .. level).forEach {
                builder.append("     ")
            }
            builder.append(jsonElement.name).append(" : ").append(jsonElement.value.toString())
            itemText.setText(builder.toString())
            itemText.setPadding(0 ,0 ,0, 12)
        }
    }
}