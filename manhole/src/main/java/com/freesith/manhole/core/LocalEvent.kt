package com.freesith.manhole.core

import kotlin.collections.HashSet


fun String.happen(obj: Any) {
    Broadcast.postEvent(this, obj)
}

fun Any.registerReceiver(action: String, callback: (t: Any) -> Unit) {
    Broadcast.register(this, action, callback)
}

fun Any.unregister(action: String) {
    Broadcast.unregister(this, action)
}

fun Any.unregisterAll() {
    Broadcast.unregisterAll(this)
}

object Broadcast {
    private val instanceMap: MutableMap<Any, Receiver> = mutableMapOf()
    private val receiverMap: MutableMap<String, HashSet<Receiver>> = mutableMapOf()
    internal fun register(obj: Any, action: String, callback: (t: Any) -> Unit) {
        var receiver = instanceMap[obj]
        if (receiver != null) {
            receiver.addAction(action, callback)
        } else {
            receiver = Receiver()
            receiver.addAction(action, callback)
            instanceMap[obj] = receiver
        }
        addReceiver(action, receiver)
    }

    private fun addReceiver(action: String, receiver: Receiver) {
        val set = receiverMap.get(action)
        if (set != null) {
            set.add(receiver)
        } else {
            val hashSet = HashSet<Receiver>()
            hashSet.add(receiver)
            receiverMap[action] = hashSet
        }
    }

    fun unregister(obj: Any, action: String) {
        instanceMap[obj]?.run {
            removeAction(action)
            if (isEmpty()) {
                instanceMap.remove(obj)
            }
        }
    }

    internal fun unregisterAll(obj: Any) {
        instanceMap.remove(obj)?.run { clear() }

    }

    internal fun postEvent(action: String, data: Any) {
        receiverMap[action]?.forEach {
            it.onEvent(action, data)
        }
    }
}

class Receiver() {

    private val actionMap: MutableMap<String, (t: Any) -> Unit> = mutableMapOf()
    internal fun addAction(action: String, callback: (t: Any) -> Unit) {
        actionMap.put(action, callback)
    }

    internal fun clear() {
        actionMap.clear()
    }

    internal fun removeAction(action: String) {
        actionMap.remove(action)
    }

    internal fun isEmpty(): Boolean {
        return actionMap.isEmpty()
    }

    internal fun onEvent(action: String, data: Any) {
        actionMap[action]?.invoke(data)
    }


}