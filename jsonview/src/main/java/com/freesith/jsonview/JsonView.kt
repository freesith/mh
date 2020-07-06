package com.freesith.jsonview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_json_view.view.*

class JsonView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    FrameLayout(context, attrs, defStyleAttr) {

    lateinit var jsonRecycler: RecyclerView
    lateinit var jsonAdapter: JsonAdapter

    constructor(context: Context) : this (context, null, 0)

    constructor(context: Context, attrs: AttributeSet?) : this (context, attrs, 0)


    init {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_json_view, this)
        jsonRecycler = view.findViewById(R.id.json_recycler)
        jsonRecycler.layoutManager = LinearLayoutManager(context)
        jsonAdapter = JsonAdapter(context)
        jsonRecycler.adapter = jsonAdapter


    }

    fun setHeaderView(view: View) {
        jsonAdapter.setHeaderView(view)
    }

    fun setJson(json: String?) {
        if (json.isNullOrEmpty()) {
            return
        }
        val jsonElements = parseJson(json)
        if (!jsonElements.isNullOrEmpty()) {
            jsonAdapter.setElements(jsonElements)
            jsonAdapter.notifyDataSetChanged()
            json_pointer.setJson(jsonElements!!)
        }
    }
}