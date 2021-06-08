package com.omid.rasouli.app

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_home.*
import java.io.*

class HomeActivity : AppCompatActivity() {

    var url = ""

    private val permissionRequester =
        prepareCall(ActivityResultContracts.RequestPermission()) { permissionGranted: Boolean ->
            if (permissionGranted)
                readConfigFile()
            else
                Toast.makeText(this, "Permission has been denied", Toast.LENGTH_SHORT).show()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setContentView(R.layout.activity_home)

        // Show Video
        val path = "android.resource://$packageName//raw//" + R.raw.splash

        val uri = Uri.parse(path)
        videoView.setVideoURI(uri)
        videoView.setOnPreparedListener { mp ->
            mp.isLooping = true
            videoView.start()
        }

        // Finish Showing Video
        webView?.let {
            it.settings.javaScriptEnabled = true
            it.settings.domStorageEnabled = true
            it.settings.setAppCachePath("/data/data/$packageName/cache")
            it.settings.allowFileAccess = true
            it.settings.setAppCacheEnabled(true)
            it.settings.mediaPlaybackRequiresUserGesture = false
            it.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    webView!!.visibility = View.VISIBLE
                    videoView!!.visibility = View.GONE
                }
            }
        }


        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            readConfigFile()
        } else {
            permissionRequester.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

        }
    }

    private fun readConfigFile() {
        val sdcard = this.getExternalFilesDir(null)

        val file = File(sdcard, "config.txt")

        try {
            val br = BufferedReader(FileReader(file))
            url = br.readLine().toString()
            br.close()
        } catch (e: IOException) {
            val bw = BufferedWriter(FileWriter(file))
            url = "https://fateh-dentist-sig.ir/display-1/"
            bw.write(url)
            bw.close()
        } finally {
            webView!!.loadUrl(url)
        }
    }


}
