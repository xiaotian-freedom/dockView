package com.storn.dockview

import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.storn.dockview.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val defGifUrl = "https://tenfei04.cfp.cn/creative/vcg/800/new/VCG211273401000.gif"
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvDefUrl.text = defGifUrl

        binding.btLoadInput.setOnClickListener {
            val originUrl = binding.edtOriginUrl.text.toString().trim()
            val dockUrl = binding.edtDockUrl.text.toString().trim()
            checkUrl(originUrl, dockUrl)
        }
        binding.btLoadDef.setOnClickListener {
            checkUrl(defGifUrl, defGifUrl)
        }
        binding.btHide.setOnClickListener {
            binding.ivTick.hideToSide()
        }
        binding.btDock.setOnClickListener {
            binding.ivTick.dockToSide()
        }
        binding.btSwimming.setOnClickListener {
            binding.ivTick.resetSwimming()
        }
        binding.ivTick.setOnClickListener {
            Toast.makeText(this, "我被电击了，you can do something in here", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 检测输入的图片地址
     */
    private fun checkUrl(originUrl: String?, dockUrl: String?) {
        originUrl?.let {
            loadOrigin(it, dockUrl)
        } ?: run {
            Toast.makeText(this@MainActivity, "图片地址不可为空", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 加载四处游动的图片
     */
    private fun loadOrigin(originUrl: String, dockUrl: String?) {
        GlideHelper.loadGif(this, originUrl, binding.ivTick, object : OnImageLoadListener {
            override fun onSuccess(resource: Drawable) {
                dockUrl?.let {
                    loadDock(resource, it)
                } ?: run {
                    showWithDockDrawable(resource, resource)
                }
            }

            override fun onFailed() {
                Toast.makeText(this@MainActivity, "图片加载失败，请检查地址是否可用", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     * 加载停靠边栏的图片
     */
    private fun loadDock(origin: Drawable, dockUrl: String?) {
        GlideHelper.downloadImage(this, dockUrl, object : OnImageLoadListener {
            override fun onSuccess(resource: Drawable) {
                showWithDockDrawable(origin, resource)
            }

            override fun onFailed() {
                showWithDockDrawable(origin, origin)
            }
        })
    }

    /**
     * 展示图片并开始游动
     */
    private fun showWithDockDrawable(
        origin: Drawable,
        dock: Drawable
    ) {
        binding.ivTick.post {
            binding.ivTick.Builder()
                .setX(binding.ivTick.x)
                .setY(binding.ivTick.y)
                .setWidth(SizeUtils.dp2px(100f))
                .setHeight(SizeUtils.dp2px(100f))
                .setMaxWidth(binding.rootLayout.width)
                .setMaxHeight(binding.rootLayout.height)
                .setDozeDuration(3)
                .setShowDockDrawable(true)
                .setDockDrawable(dock)
                .setOriginDrawable(origin)
                .move()
        }
    }
}