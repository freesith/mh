package com.freesith.manhole.ui

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.freesith.manhole.ManholeMock
import com.freesith.manhole.R
import com.freesith.manhole.bean.Mock
import com.freesith.manhole.bean.MockChoice
import com.freesith.manhole.ui.adapter.CaseAdapter
import com.freesith.manhole.ui.adapter.CaseAdapter.CaseListener
import com.freesith.manhole.ui.adapter.EnableChoiceAdapter
import com.freesith.manhole.ui.adapter.EnableChoiceAdapter.ChoiceListener
import com.freesith.manhole.ui.adapter.FlowAdapter
import com.freesith.manhole.ui.adapter.FlowAdapter.FlowListener
import com.freesith.manhole.ui.adapter.MockAdapter
import com.freesith.manhole.ui.adapter.MockAdapter.MockListener
import com.freesith.manhole.ui.interfaces.MonitorListener
import com.freesith.manhole.ui.util.ViewUtil
import kotlinx.android.synthetic.main.layout_mock_list.view.*
import java.util.*

class MockListLayout : LinearLayout, View.OnClickListener, FlowListener, CaseListener,
    MockListener, ChoiceListener {
    private var thisContext: Context? = null
    private var mockAdapter: MockAdapter? = null
    private var caseAdapter: CaseAdapter? = null
    private var flowAdapter: FlowAdapter? = null
    private var enableAdapter: EnableChoiceAdapter? = null
    private var monitorListener: MonitorListener? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        init(context)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context)
    }

    fun setMonitorListener(monitorListener: MonitorListener?) {
        this.monitorListener = monitorListener
    }

    private fun init(context: Context) {
        this.thisContext = context
        val view =
            LayoutInflater.from(context).inflate(R.layout.layout_mock_list, this)
        tvFlow.setOnClickListener(this)
        tvCase.setOnClickListener(this)
        tvMock.setOnClickListener(this)
        tvEnable.setOnClickListener(this)
        rvMock.setLayoutManager(LinearLayoutManager(context))
    }

    fun show() {
        showEnable()
        updateButtons(tvEnable)
    }

    private fun showEnable() {
        if (enableAdapter == null) {
            enableAdapter = EnableChoiceAdapter(thisContext)
            enableAdapter!!.setChoiceListener(this)
        }
        val enableMockMap = ManholeMock.enableMockMap
        val choiceList: MutableList<MockChoice> =
            ArrayList()
        if (!enableMockMap.isEmpty()) {
            for (linkedList in enableMockMap.values) {
                choiceList.addAll(linkedList)
            }
        }
        enableAdapter!!.setList(choiceList)
        rvMock!!.adapter = enableAdapter
        enableAdapter!!.notifyDataSetChanged()
    }

    private fun showFlow() {
        if (flowAdapter == null) {
            flowAdapter = FlowAdapter(thisContext)
            flowAdapter!!.setFlowListener(this)
        }
        val flows = ManholeMock.flows
        flowAdapter!!.setList(flows)
        rvMock!!.adapter = flowAdapter
        flowAdapter!!.notifyDataSetChanged()
    }

    private fun showCase() {
        if (caseAdapter == null) {
            caseAdapter = CaseAdapter(thisContext)
            caseAdapter!!.setCaseListener(this)
        }
        val cases = ManholeMock.cases
        caseAdapter!!.setList(cases)
        rvMock!!.adapter = caseAdapter
        caseAdapter!!.notifyDataSetChanged()
    }

    private fun showMock() {
        if (mockAdapter == null) {
            mockAdapter = MockAdapter(thisContext)
            mockAdapter!!.setMockListener(this)
        }
        val mocks = ManholeMock.mocks
        mockAdapter!!.setList(mocks)
        rvMock!!.adapter = mockAdapter
        mockAdapter!!.notifyDataSetChanged()
    }

    override fun onClick(v: View) {
        if (v.id == R.id.tvFlow) {
            showFlow()
            updateButtons(v)
        } else if (v.id == R.id.tvCase) {
            showCase()
            updateButtons(v)
        } else if (v.id == R.id.tvMock) {
            showMock()
            updateButtons(v)
        } else if (v.id == R.id.tvEnable) {
            showEnable()
            updateButtons(v)
        }
    }

    private fun updateButtons(v: View?) {
        tvCase!!.setBackgroundColor(if (v!!.id == R.id.tvCase) Color.WHITE else Color.TRANSPARENT)
        tvEnable!!.setBackgroundColor(if (v.id == R.id.tvEnable) Color.WHITE else Color.TRANSPARENT)
        tvFlow!!.setBackgroundColor(if (v.id == R.id.tvFlow) Color.WHITE else Color.TRANSPARENT)
        tvMock!!.setBackgroundColor(if (v.id == R.id.tvMock) Color.WHITE else Color.TRANSPARENT)
    }

    override fun onFlowEnableChanged(
        name: String,
        enable: Boolean,
        position: Int
    ) {
        val flow = flowAdapter!![position]
        if (flow != null) {
            ManholeMock.updateFlowEnable(name, enable)
            flow.enable = enable
        }
    }

    override fun onFlowClick(name: String) {
        val caseLayout = CaseLayout(thisContext)
        ViewUtil.findCoverLayout(this).addView(
            caseLayout,
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        caseLayout.showFlow(name)
    }

    override fun onCaseEnableChanged(
        name: String,
        enable: Boolean,
        position: Int
    ) {
        val caze = caseAdapter!![position]
        if (caze != null) {
            ManholeMock.updateCaseEnable(name, enable)
            caze.enable = enable
        }
    }

    override fun onCaseClick(name: String) {
        val caseLayout = CaseLayout(thisContext)
        ViewUtil.findCoverLayout(this).addView(
            caseLayout,
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        caseLayout.showCase(name)
    }

    override fun onMockEnableChanged(
        name: String,
        enable: Boolean,
        position: Int
    ) {
        val mock = mockAdapter!![position]
        if (mock != null) {
            ManholeMock.updateMockEnable(name, enable)
            mock.enable = enable
        }
    }

    override fun onMockClick(mock: Mock) {
        if (monitorListener != null) {
            monitorListener!!.onShowSingleMock(mock)
        }
    }

    override fun onChoiceEnableChanged(
        choice: MockChoice,
        enable: Boolean,
        position: Int
    ) {
        ManholeMock.updateMockChoiceEnable(choice.mockName, choice.index, enable)
    }

    override fun onMockNameClick(name: String) {
        val mockView = MockView(thisContext)
        val mock = ManholeMock.findMockByName(name)
        if (mock != null) {
            ViewUtil.findCoverLayout(this).addView(
                mockView,
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            mockView.showMock(mock)
        }
    }

    override fun onChoiceClick(mock: MockChoice) {}
}