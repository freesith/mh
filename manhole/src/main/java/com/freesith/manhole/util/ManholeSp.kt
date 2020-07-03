package com.freesith.manhole.util

import com.freesith.manhole.core.SpAccessor
import com.freesith.manhole.core.spBoolean
import com.freesith.manhole.core.spString

object ManholeSp : SpAccessor {

    var dbPath by spString()

    var enableHistory by spBoolean()
    var enableHistoryShortcut by spBoolean()
    var enableSummon by spBoolean()

}