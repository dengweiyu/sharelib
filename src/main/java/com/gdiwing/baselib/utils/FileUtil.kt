package com.gdiwing.baselib.utils

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.io.OutputStream

class FileUtil {
    companion object {

        /**
         * 移动文件
         * @param srcPath    源文件完整路径
         * @param destPath    目的目录完整路径
         * @return 文件移动成功返回true，否则返回false
         */
        fun moveFile(context: Context, srcPath: String?, destPath: String?): Boolean {
            if (srcPath == null || destPath == null) return false
            var srcFile: File? = null
            try {
                srcFile = File(srcPath)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (srcFile == null ||
                !srcFile.exists() ||
                !srcFile.isFile()
            ) return false
            var destFile: File? = null
            try {
                destFile = File(destPath)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (destFile == null) return false
            if (!destFile.getParentFile().exists()) destFile.getParentFile().mkdirs()
            var videoUri = srcPath
            var flag = false
            kotlin.runCatching {
                val outputStream: Pair<Uri?, OutputStream?> = if (Build.VERSION.SDK_INT < 29) {
                    val contentValues = ContentValues().also {
                        it.put(MediaStore.Video.Media.DISPLAY_NAME, destFile.name)
                        it.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
                        it.put(MediaStore.Video.Media.DATA, destFile.absolutePath)
                        it.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis())
//                    it.put(MediaStore.Video.Media.DATE_TAKEN,System.currentTimeMillis())
                    }
                    val uri = context.contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)
                    Log.e("recorder", "uri ${uri?.toString()}")
                    uri to uri?.let { context.contentResolver.openOutputStream(it) }
                } else {
                    val contentValues = ContentValues().also {
                        it.put(MediaStore.Video.Media.DISPLAY_NAME, destFile.name)
                        it.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
                        it.put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES + "/" + context.packageName.substring(context.packageName.lastIndexOf(".") + 1))
                        it.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis())
                        it.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis())
                    }
                    val uri = context.contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)
                    uri to uri?.let { context.contentResolver.openOutputStream(it) }
                }
                outputStream.second?.use { opt ->
                    File(srcPath).inputStream().apply {
                        copyTo(opt)
                        kotlin.runCatching { close() }
                    }
                }
                if (outputStream.second!=null){
                    videoUri = outputStream.first.toString()

                }
            }.onFailure {
                it.printStackTrace()
            }
            if(videoUri?.startsWith("content") == true){
                srcFile.parentFile?.let {
                    it.listFiles()?.forEach { it.delete() }
                    it.delete()
                }
                flag = true
            }
            return flag
        }

        fun formatFileSize(sizeInBytes: Long): String {
            val kiloBytes = sizeInBytes / 1024.0
            val megaBytes = kiloBytes / 1024.0
            val gigaBytes = megaBytes / 1024.0

            return when {
                gigaBytes >= 1.0 -> String.format("%.2f GB", gigaBytes)
                megaBytes >= 1.0 -> String.format("%.2f MB", megaBytes)
                kiloBytes >= 1.0 -> String.format("%.2f KB", kiloBytes)
                else -> String.format("%d B", sizeInBytes)
            }
        }

        fun getPathUri(mContext: Activity, path: String): Uri? {
            var fileUri: Uri? = null
            val baseUri = MediaStore.Files.getContentUri("external")
            val cursor = mContext.managedQuery(baseUri, null, null, null, null)
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                val data = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA))
                if (path == data) {
                    val id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID))
                    fileUri = Uri.withAppendedPath(baseUri, id.toString() + "")
                    break
                }
                cursor.moveToNext()
            }
            return fileUri
        }

        fun shareFile(context : Activity,path :String){
            val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(context, context.packageName + ".fileprovider", File(path))
            }else{
                getPathUri(context, path)
            }
            val shareIntent = Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            shareIntent.setDataAndType(uri, "text/plain");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.setType("text/plain");//此处可发送多种文件
            context.grantUriPermission("com.tencent.mm", uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);  //没有这一句Android10会提示“获取资源失败”
            context.startActivity(Intent.createChooser(shareIntent, "分享到："));
        }
    }
}