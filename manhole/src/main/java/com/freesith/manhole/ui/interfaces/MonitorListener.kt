package com.freesith.manhole.ui.interfaces

import com.freesith.manhole.bean.Mock

interface MonitorListener {
    fun onShowSingleMock(mock: Mock?)
    fun onShowHistoryDetail(historyId: Int)
}