package com.freesith.manhole.ui

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.freesith.manhole.R
import com.freesith.manhole.bean.MockChoice
import kotlinx.android.synthetic.main.layout_choice_detail.view.*
import kotlinx.android.synthetic.main.layout_choice_header.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

class ChoiceLayout(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
    ConstraintLayout(context, attrs, defStyleAttr),CoroutineScope by MainScope() {

    constructor(context: Context?) : this(context, null, 0)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    val headerView = LayoutInflater.from(context).inflate(R.layout.layout_choice_header, null)

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_choice_detail, this)
        findViewById<View>(R.id.tvClose).setOnClickListener { (parent as ViewGroup).removeView(this@ChoiceLayout) }
        setBackgroundColor(Color.WHITE)
    }


    fun showChoice(mockChoice: MockChoice) {
        headerView.tvName.text = mockChoice.name
        headerView.tvTitle.text = mockChoice.title
        if (mockChoice.desc.isNullOrEmpty()) {
            headerView.tvDesc.visibility = View.GONE
        } else {
            headerView.tvDesc.text = mockChoice.desc
        }
        if (!mockChoice.urlQuery.isNullOrEmpty()) {
            val stringBuilder = StringBuilder()
            mockChoice.urlQuery.forEach {
                stringBuilder.append(it.key).append(" = ").append(it.value).append("\n")
            }
            headerView.compare1.visibility = View.VISIBLE
            headerView.tvQuery.visibility = View.VISIBLE
            headerView.tvQuery.text = stringBuilder.toString().trim()
        }
        if (!mockChoice.requestBody.isNullOrEmpty()) {
            val stringBuilder = StringBuilder()
            mockChoice.requestBody.forEach {
                stringBuilder.append(it).append("\n")
            }
            headerView.compare2.visibility = View.VISIBLE
            headerView.tvRequestBody.visibility = View.VISIBLE
            headerView.tvRequestBody.text = stringBuilder.toString().trim()
        }
        v_response_json.setHeaderView(headerView)
        v_response_json.setJson(mockChoice.data)
    }
}