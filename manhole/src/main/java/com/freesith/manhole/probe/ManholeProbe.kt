package com.freesith.manhole.probe

object ManholeProbe {

    //MainTabActivity#presenter#isGetNoonAction#set(false)
    //MainTabActivity#presenter#noonTimerComplete()
    fun probe(probe: Probe) {
        probe.routes.forEach {
            ////TODO 2020/8/10 by WangChao 需要考虑参数中传的字符串中包含#的情况
            var obj: Any? = null
            val split = it.split("#")
            split.forEach { part ->
                if (obj == null) {

                }

            }
        }
    }
}