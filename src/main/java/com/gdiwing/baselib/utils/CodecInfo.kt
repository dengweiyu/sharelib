package com.gdiwing.baselib.utils

import android.media.MediaCodecInfo
import android.media.MediaCodecList
import android.os.Build
import android.util.Log
import java.util.*


object CodecInfo {
    private const val TAG = "CodecInfo"
    private val codecList = MediaCodecList(MediaCodecList.REGULAR_CODECS)

    fun getSoftDecoderByMime(mime: String): MediaCodecInfo? {
        try {
            val list = codecList.codecInfos
            return list.filter { !it.isEncoder && isSoftwareOnly(it) && it.supportedTypes.contains(mime) }
                .firstOrNull()
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "Cannot retrieve decoder codec info", e)
        }
        return null
    }
    fun getEncoderByMime(mime: String): MediaCodecInfo? {
        try {
            val list = codecList.codecInfos
            var targets = list.filter { it.isEncoder && (isHardwareAccelerated(it)) && it.supportedTypes.contains(mime) }
            if (targets.isNotEmpty()) {
                return targets.first()
            }
            targets = list.filter { it.isEncoder && it.supportedTypes.contains(mime) }
            return targets.first()
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "Cannot retrieve decoder codec info", e)
        }
        return null
    }

    fun getCodecByName(name: String): MediaCodecInfo? {
        try {
            val list = codecList.codecInfos
            return list.filter { it.name.equals(name) }
                .firstOrNull()
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "Cannot retrieve decoder codec info", e)
        }
        return null
    }

    fun printMediaCodecInfo() {
        var CodecCount = 0
        CodecCount = try {
            MediaCodecList.getCodecCount()
        } catch (e: Exception) {
            Log.e(TAG, "##### Failed to get codec count!")
            e.printStackTrace()
            return
        }
        for (i in 0 until CodecCount) {
            var info: MediaCodecInfo? = null
            try {
                info = MediaCodecList.getCodecInfoAt(i)
            } catch (e: IllegalArgumentException) {
                Log.e(TAG, "Cannot retrieve decoder codec info", e)
            }
            if (info == null) {
                continue
            }
            var codecInfo = "MediaCodec, name=" + info.name + ", ["
            for (mimeType in info.supportedTypes) {
                codecInfo += "$mimeType,"
                var capabilities: MediaCodecInfo.CodecCapabilities
                capabilities = try {
                    info.getCapabilitiesForType(mimeType)
                } catch (e: IllegalArgumentException) {
                    Log.e(TAG, "Cannot retrieve decoder capabilities", e)
                    continue
                }
                codecInfo += " max inst:" + capabilities.maxSupportedInstances + ","
                var strColorFormatList = ""
                for (colorFormat in capabilities.colorFormats) {
                    strColorFormatList += " 0x" + Integer.toHexString(colorFormat)
                }
                codecInfo += "$strColorFormatList] ["
            }
            Log.w(TAG, codecInfo)
        }

//        boolean bSupportHwVP8 = MediaCodecVideoEncoder.isVp8HwSupported();
//        boolean bSupportHwVP9 = MediaCodecVideoEncoder.isVp9HwSupported();
//        boolean bSupportHwH264 = MediaCodecVideoEncoder.isH264HwSupported();

//        String webrtcCodecInfo = "WebRTC codec support: HwVP8=" + bSupportHwVP8 + ", HwVP9=" + bSupportHwVP9
//                + ", Hw264=" + bSupportHwH264;
//
//        if(bSupportHwH264) {
//            webrtcCodecInfo += ", Hw264HighProfile=" + MediaCodecVideoEncoder.isH264HighProfileHwSupported();
//        }

//        Log.w(TAG, webrtcCodecInfo);
    }

    fun isHardwareAccelerated(codecInfo: MediaCodecInfo): Boolean {
        return if (Build.VERSION.SDK_INT >= 29) {
            codecInfo.isHardwareAccelerated
        } else !isSoftwareOnly(codecInfo)
        // codecInfo.isHardwareAccelerated() != codecInfo.isSoftwareOnly() is not necessarily true.
        // However, we assume this to be true as an approximation.
    }

    fun isSoftwareOnly(codecInfo: MediaCodecInfo): Boolean {
        if (Build.VERSION.SDK_INT >= 29) {
            return codecInfo.isSoftwareOnly
        }
        val codecName: String = codecInfo.name.toLowerCase(Locale.ENGLISH)
        return if (codecName.startsWith("arc.")) { // App Runtime for Chrome (ARC) codecs
            false
        } else codecName.startsWith("omx.google.")
                || codecName.startsWith("omx.ffmpeg.")
                || (codecName.startsWith("omx.sec.") && codecName.contains(".sw."))
                || codecName.equals("omx.qcom.video.decoder.hevcswvdec")
                || codecName.startsWith("c2.android.")
                || codecName.startsWith("c2.google.")
                || (!codecName.startsWith("omx.") && !codecName.startsWith("c2."))
    }


    /**
     * feature-intra-refresh 帧内刷新技术
     * value = 1 是否要把I帧平摊到多个P帧的intra 宏块中传输。 意思就是除了第一个IDR，后面都没有IDR<然后每个p帧中有几列是帧内参考的，一个GOP内所有的帧内参考组合起来就是一个完整的帧。
     * 帧内刷新技术能降低网络延时、降低网络压力，降低丢包率（传输出差率），平滑码流，但是会降低编码器的效率（即是编码复杂度提升），H264/265均支持该技术。
     *
     * intra_refresh编码模式，可以有如下优点：

    １、码率稳定。所有的P帧都有一条区域使用帧内预测模式，其他区域运行率失真优化选择最优模式，因此每个Ｐ帧的大小波动不会太大。
    ２、降低时延。避免出现超大Ｉ帧。该模式把I帧数据平分在GOP中的每个P帧上，起始I帧质量压力没有之前那么大，可以不必分配过多码率，保证后面P帧的质量。
    ３、错误恢复，每帧中的区块都是参考其前一帧的相应位置的块。假设第一帧丢失，那么第二帧有第二条区域可以正常解码，第三帧的第二条参考第二帧的第二条，那么第三帧的第二条和第三条可以正常解码依次类推，一个GOP期内可以恢复回来。

    但是实际应用时，需要注意错误恢复这块，实现这个功能的前提是需要把残帧送给解码器，解码器在一个GOP自动恢复。

    1、webrtc上在调度侧会对帧的完整性做一次判断，发现是残帧，直接丢弃一整帧。所以正常情况下，错误恢复这个功能在webrtc上是不起作用的。

    2、通常情况下，人宁可看到卡顿的视频，也不喜欢看到局部花屏的视频。这种错误恢复的机制，不见得能提升用户的直观感受。
     */

    /**
     *feature-qp-bounds  允许你为视频的不同区域指定不同的 QP 范围，以实现对视频中不同区域的精细控制
     * 视频编码器（通常是 H.264 或 H.265）的一个特性，用于指定编码器在进行区域内的质量调整（QP 范围）时的相关参数。
     * QP（Quantization Parameter）是视频编码中用于控制量化级别的参数。量化级别越低，图像质量越好，但比特率越高。
     */

    /**
     *MediaFormat.KEY_INTRA_REFRESH_PERIOD  Long-Term Reference (LTR)  长参考帧编码技术 表示 LTR 的间隔，以帧数为单位
     *该技术可用于提高编码效率，提高视频质量及节省网络带宽，该技术可在应用层进行控制。思科长期将LTR作为其视频会议的纠错算法。
     * Long-Term Reference (LTR) 是视频编码中的一个概念，用于改进视频质量和网络适应性。
     * LTR 允许编码器在一段时间内参考以前的帧，而不仅仅是前一个关键帧（I-frame）。LTR 可以用于减少错误传播和提高视频质量。
     */
}