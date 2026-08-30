package com.example.tvdashboard

import android.app.Activity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar

class MainActivity : Activity() {

    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar

    /*
     * ═══════════════════════════════════════════════════════════════
     *  修改这里：改成你的 Dashboard 远程地址
     * ═══════════════════════════════════════════════════════════════
     */
    private val DASHBOARD_URL = "https://ops.amekai.top/store-dashboard.html"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        progressBar = findViewById(R.id.progressBar)

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            allowFileAccess = true
            allowContentAccess = true
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            cacheMode = WebSettings.LOAD_DEFAULT
            useWideViewPort = true
            loadWithOverviewMode = true
            setMediaPlaybackRequiresUserGesture(false)
        }

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                return false
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressBar.visibility = View.GONE
                injectDPadSupport()
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                progressBar.progress = newProgress
            }
        }

        webView.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
                if (webView.canGoBack()) {
                    webView.goBack()
                    return@setOnKeyListener true
                }
            }
            false
        }

        webView.loadUrl(DASHBOARD_URL)
    }

    private fun injectDPadSupport() {
        val js = """
        (function(){
            if(window.__tvNavInjected)return;window.__tvNavInjected=true;
            var focusables='.nav-item,.period-card,.fs-btn,.sync-btn,.upload-btn,.setting-switch,.pager .pdot,.weekly-bar,.pw-img,.moment-tile,.img-modal-close,.seasonal-audio-toggle';
            var current=null;
            function getAll(){return Array.prototype.slice.call(document.querySelectorAll(focusables)).filter(function(el){return el.offsetParent!==null;});}
            function focusEl(el){if(!el)return;current=el;el.scrollIntoView({behavior:'smooth',block:'center',inline:'center'});el.style.outline='3px solid #22c55e';el.style.outlineOffset='2px';el.style.transition='outline .15s';}
            function blurEl(el){if(el)el.style.outline='';}
            document.addEventListener('keydown',function(e){
                var all=getAll();if(!all.length)return;
                if(!current||!all.includes(current)){current=all[0];focusEl(current);return;}
                var idx=all.indexOf(current);
                var rect=current.getBoundingClientRect();
                var cx=rect.left+rect.width/2,cy=rect.top+rect.height/2;
                function dist(a,b){var dx=a.x-b.x,dy=a.y-b.y;return dx*dx+dy*dy;}
                var next=null,best=Infinity;
                if(e.key==='ArrowRight'||e.key==='Right'){
                    for(var i=0;i<all.length;i++){if(i===idx)continue;var r=all[i].getBoundingClientRect();var x=r.left+r.width/2,y=r.top+r.height/2;if(x>cx){var d=dist({x:x,y:y},{x:cx,y:cy});if(d<best){best=d;next=all[i];}}}
                }else if(e.key==='ArrowLeft'||e.key==='Left'){
                    for(var i=0;i<all.length;i++){if(i===idx)continue;var r=all[i].getBoundingClientRect();var x=r.left+r.width/2,y=r.top+r.height/2;if(x<cx){var d=dist({x:x,y:y},{x:cx,y:cy});if(d<best){best=d;next=all[i];}}}
                }else if(e.key==='ArrowDown'||e.key==='Down'){
                    for(var i=0;i<all.length;i++){if(i===idx)continue;var r=all[i].getBoundingClientRect();var x=r.left+r.width/2,y=r.top+r.height/2;if(y>cy){var d=dist({x:x,y:y},{x:cx,y:cy});if(d<best){best=d;next=all[i];}}}
                }else if(e.key==='ArrowUp'||e.key==='Up'){
                    for(var i=0;i<all.length;i++){if(i===idx)continue;var r=all[i].getBoundingClientRect();var x=r.left+r.width/2,y=r.top+r.height/2;if(y<cy){var d=dist({x:x,y:y},{x:cx,y:cy});if(d<best){best=d;next=all[i];}}}
                }else if(e.key==='Enter'||e.key==='OK'||e.keyCode===13||e.keyCode===23){
                    current.click();e.preventDefault();return;
                }
                if(next){blurEl(current);focusEl(next);e.preventDefault();}
            });
            var first=getAll()[0];if(first)focusEl(first);
        })();
        """.trimIndent()
        webView.evaluateJavascript(js, null)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode in listOf(
                KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_DPAD_DOWN,
                KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_DPAD_RIGHT,
                KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER,
                KeyEvent.KEYCODE_BACK
            )) {
            webView.dispatchKeyEvent(event)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onResume() {
        super.onResume()
        webView.onResume()
    }

    override fun onPause() {
        super.onPause()
        webView.onPause()
    }

    override fun onDestroy() {
        webView.destroy()
        super.onDestroy()
    }
}
