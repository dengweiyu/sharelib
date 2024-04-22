package com.gdiwing.baselib.utils

import android.app.Activity
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gdiwing.baselib.net.event.BaseViewTagEvent
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.*
import kotlin.collections.HashMap

enum class TrackType {
    CLICK, SHOW, DISAPPEAR
}

fun Fragment.onTrackPageView(map: HashMap<String, String?>): HashMap<String, String?> {
    val bundle = arguments
    bundle?.let {
        val refMap = it.getSerializable("ref_map")
        if (refMap is HashMap<*, *>) {
            kotlin.runCatching {
                map.putAll(refMap as HashMap<String, String?>)
            }
        }
    }
    if (!map.contains("ref_page_type")) {
        map["ref_page_type"] = ""
    }
    if (!map.contains("ref_page_id")) {
        map["ref_page_id"] = ""
    }
    if (!map.contains("ref_object_type")) {
        map["ref_object_type"] = ""
    }
    if (!map.contains("ref_object_id")) {
        map["ref_object_id"] = ""
    }
    if (!map.contains("ref_page_inst_id")) {
        map["ref_page_inst_id"] = ""
    }
    if (!map.contains("ref_object_inst_id")) {
        map["ref_object_inst_id"] = ""
    }
    if (!map.contains("ref_event_id")) {
        map["ref_event_id"] = ""
    }
    val newMap = HashMap<String, String?>().apply { putAll(map) }
    return newMap
}

fun Activity.onTrackPageView(map: HashMap<String, String?>) : HashMap<String, String?>{
    val bundle = intent.extras
    bundle?.let {
        val refMap = it.getSerializable("ref_map")
        if (refMap is HashMap<*, *>) {
            kotlin.runCatching {
                map.putAll(refMap as HashMap<String, String?>)
            }
        }
    }

    if (!map.contains("ref_page_type")) {
        map["ref_page_type"] = ""
    }
    if (!map.contains("ref_page_id")) {
        map["ref_page_id"] = ""
    }
    if (!map.contains("ref_object_type")) {
        map["ref_object_type"] = ""
    }
    if (!map.contains("ref_object_id")) {
        map["ref_object_id"] = ""
    }
    if (!map.contains("ref_page_inst_id")) {
        map["ref_page_inst_id"] = ""
    }
    if (!map.contains("ref_object_inst_id")) {
        map["ref_object_inst_id"] = ""
    }
    if (!map.contains("ref_event_id")) {
        map["ref_event_id"] = ""
    }
    val newMap = HashMap<String, String?>().apply { putAll(map) }
    return newMap
}


fun Fragment.hookStar() {
    view?.let { view ->
        if (view is ViewGroup) {
            hookStart(view, false)
        }
    }
}

fun Activity.hookStar() {
    Log.d("${this.javaClass.simpleName}", " hookStart")
    window.decorView?.let { view ->
        if (view is ViewGroup) {
            hookStart(view, false)
        }
    }
}

/**
 * hook掉viewGroup
 *
 * @param viewGroup
 */
fun hookStart(viewGroup: ViewGroup?, isScrollAbsListview: Boolean) {
    if (viewGroup == null) {
        return
    }
    val count = viewGroup.childCount
    for (i in 0 until count) {
        val view = viewGroup.getChildAt(i)
        if (view is ViewGroup) { //递归查询所有子view
            // 若是布局控件（LinearLayout或RelativeLayout）,继续查询子View
            hookStart(view, isScrollAbsListview)
        } else {
            view.hookView()
        }
    }
    viewGroup.hookView()
}

//自定义的代理事件监听器
class OnClickListenerProxy constructor(`object`: View.OnClickListener) :
    View.OnClickListener {
    private val `object`: View.OnClickListener?
    private val MIN_CLICK_DELAY_TIME = 500
    private var lastClickTime: Long = 0

    init {
        this.`object` = `object`
    }

    override fun onClick(v: View) {
        //点击时间控制
        val currentTime: Long = Calendar.getInstance().getTimeInMillis()
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime
            Log.e("OnClickListenerProxy", "OnClickListenerProxy")
            `object`?.onClick(v)
            val tag = v.tag
            if (tag != null && tag is BaseViewTagEvent) {
                kotlin.runCatching {
                    tag.trackMap?.let { map ->
                        LinkedHashMap<String, String?>().apply {
                            putAll(map.apply {
                                put("event_type", "click")
                            })
                            v.track(this@apply)
                        }
                    }
                }.onFailure {
                    it.printStackTrace()
                }
            }
        }
    }
}

fun View.hookView() {
    try {
        val viewClazz = Class.forName("android.view.View")
        //事件监听器都是这个实例保存的
        val listenerInfoMethod: Method = viewClazz.getDeclaredMethod("getListenerInfo")
        if (!listenerInfoMethod.isAccessible()) {
            listenerInfoMethod.setAccessible(true)
        }
        val listenerInfoObj: Any = listenerInfoMethod.invoke(this)
        val listenerInfoClazz = Class.forName("android.view.View\$ListenerInfo")
        val onClickListenerField: Field = listenerInfoClazz.getDeclaredField("mOnClickListener")
        if (!onClickListenerField.isAccessible()) {
            onClickListenerField.setAccessible(true)
        }
        onClickListenerField.get(listenerInfoObj)?.let {
            if (it is View.OnClickListener) {
                val mOnClickListener = it
                //自定义代理事件监听器
                val onClickListenerProxy: View.OnClickListener =
                    OnClickListenerProxy(mOnClickListener)
                //更换
                onClickListenerField.set(listenerInfoObj, onClickListenerProxy)
            }
        }

    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * type: TrackType = TrackType.SHOW,
event_id: String?,
page_type: String?,
page_id: String?,
page_inst_id: String?,
parent_type: String?,
parent_id: String?,
parent_inst_id: String?,
object_type: String?,
object_id: String?,
object_inst_id: String?,
object_order: String?,
 */
fun View.track(
    type: TrackType = TrackType.SHOW,
    page_type: String?,
    page_id: String?,
    parent_type: String?,
    parent_id: String?,
    parent_inst_id: String?,
    object_type: String?,
    object_id: String?,
    object_order: String? = "0",
) {
    val map = LinkedHashMap<String, String?>().apply {
        put("page_type", page_type)
        put("page_id", page_id)
        put("page_inst_id", if (get("page_type").isNullOrEmpty()) null else "")
        put("parent_type", parent_type)
        put("parent_id", parent_id)
        put("parent_inst_id", parent_inst_id)
        put("object_type", object_type)
        put("object_id", object_id)
        put("object_inst_id", if (get("object_type").isNullOrEmpty()) null else "")
        put("event_id", if (get("object_type").isNullOrEmpty()) null else "")
        put("object_order", object_order)
        put(
            "event_type", if (type == TrackType.CLICK) {
                "click"
            } else if (type == TrackType.SHOW) {
                "show"
            } else if (type == TrackType.DISAPPEAR) {
                "disappear"
            } else {
                "unKnown"
            }
        )
    }
    if (tag == null) {
        tag = BaseViewTagEvent(trackMap = LinkedHashMap<String, String?>().apply {
            putAll(map)
        })
    } else if (tag is BaseViewTagEvent) {
        (tag as BaseViewTagEvent).trackMap = LinkedHashMap<String, String?>().apply {
            putAll(map)
        }
    }
}

fun View.track(
    map: LinkedHashMap<String, String?>
) {
}

fun View.trackView(parentMap : HashMap<String,String?>, object_type : String,object_id: String?=""){
    track(TrackType.SHOW, parentMap["page_type"],parentMap["page_id"], parentMap["page_type"],parentMap["page_id"],parentMap["page_inst_id"],object_type,object_id)
}
fun View.trackViewDispear(tag : Any?){
    if(tag != null && tag is BaseViewTagEvent && tag.trackMap != null) {
        val hashMap = tag.trackMap
        hashMap?.let { map ->
            map["event_type"] = "disappear"
        }

    }
}