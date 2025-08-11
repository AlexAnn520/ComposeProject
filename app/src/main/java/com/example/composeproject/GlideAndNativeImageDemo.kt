package com.example.composeproject

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

/**
 * 方案2: 使用 Glide 库
 * Glide是Android最流行的图片加载库之一，功能强大且成熟
 * 
 * 注意：需要添加依赖
 * implementation("com.github.bumptech.glide:compose:1.0.0-beta01")
 */
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun GlideImageExample() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            "方案2: Glide库实现",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Glide Compose集成", fontWeight = FontWeight.Medium)
                Text(
                    "需要添加: implementation(\"com.github.bumptech.glide:compose:1.0.0-beta01\")",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                // 使用GlideImage (需要添加glide-compose依赖)
                // GlideImage(
                //     model = "https://picsum.photos/400/300",
                //     contentDescription = "Glide加载的图片",
                //     modifier = Modifier
                //         .fillMaxWidth()
                //         .height(200.dp)
                //         .clip(RoundedCornerShape(8.dp)),
                //     loading = placeholder(R.drawable.mm),
                //     failure = placeholder(android.R.drawable.ic_dialog_alert)
                // )
                
                // 或使用AndroidView包装传统Glide
                GlideWithAndroidView(
                    imageUrl = "https://picsum.photos/400/300",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }
        }
    }
}

/**
 * 使用AndroidView包装传统Glide实现
 * 这种方式不需要额外的compose依赖，但性能不如原生Compose组件
 */
@Composable
fun GlideWithAndroidView(
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { context ->
            android.widget.ImageView(context).apply {
                scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
            }
        },
        modifier = modifier,
        update = { imageView ->
            Glide.with(imageView.context)
                .load(imageUrl)
                .placeholder(R.drawable.mm)
                .error(android.R.drawable.ic_dialog_alert)
                .centerCrop()
                .into(imageView)
        }
    )
}

/**
 * 方案3: 原生Kotlin实现（仅用于学习，不推荐生产环境）
 * 展示底层原理，了解图片加载的基本流程
 */
@Composable
fun NativeKotlinImageExample() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            "方案3: 原生Kotlin实现",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("原生实现（学习用）", fontWeight = FontWeight.Medium)
                Text(
                    "⚠️ 仅用于学习，缺少缓存、错误处理等重要功能",
                    fontSize = 12.sp,
                    color = Color.Red,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                SimpleNetworkImage(
                    url = "https://picsum.photos/400/300",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }
        }
    }
}

/**
 * 简单的原生网络图片加载实现
 * 展示基本原理：网络请求 -> Bitmap转换 -> 显示
 */
@Composable
fun SimpleNetworkImage(
    url: String,
    modifier: Modifier = Modifier
) {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }
    
    // 使用LaunchedEffect加载图片
    LaunchedEffect(url) {
        isLoading = true
        hasError = false
        
        try {
            bitmap = loadImageFromUrl(url)
            hasError = bitmap == null
        } catch (e: Exception) {
            hasError = true
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }
    
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> {
                // 加载中
                CircularProgressIndicator(
                    modifier = Modifier.size(40.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            hasError -> {
                // 错误状态
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Red.copy(alpha = 0.1f))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("加载失败", color = Color.Red)
                    Text(url, fontSize = 10.sp, color = Color.Gray)
                }
            }
            bitmap != null -> {
                // 显示图片
                Image(
                    painter = BitmapPainter(bitmap!!.asImageBitmap()),
                    contentDescription = "网络图片",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

/**
 * 从URL加载图片的原生实现
 * 注意：这是简化版本，实际应用需要更多错误处理和优化
 */
suspend fun loadImageFromUrl(urlString: String): Bitmap? = withContext(Dispatchers.IO) {
    try {
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        connection.doInput = true
        connection.connect()
        
        val inputStream = connection.inputStream
        val bitmap = BitmapFactory.decodeStream(inputStream)
        
        inputStream.close()
        connection.disconnect()
        
        bitmap
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

/**
 * 图片缓存管理器示例（简化版）
 * 实际应用应使用LruCache等更完善的缓存策略
 */
object SimpleImageCache {
    private val memoryCache = mutableMapOf<String, Bitmap>()
    
    fun get(key: String): Bitmap? = memoryCache[key]
    
    fun put(key: String, bitmap: Bitmap) {
        // 简单的内存缓存，实际应考虑内存限制
        if (memoryCache.size < 20) {
            memoryCache[key] = bitmap
        }
    }
    
    fun clear() {
        memoryCache.clear()
    }
}

/**
 * 带缓存的原生图片加载组件
 */
@Composable
fun CachedNetworkImage(
    url: String,
    modifier: Modifier = Modifier
) {
    var bitmap by remember { mutableStateOf<Bitmap?>(SimpleImageCache.get(url)) }
    var isLoading by remember { mutableStateOf(bitmap == null) }
    var hasError by remember { mutableStateOf(false) }
    
    LaunchedEffect(url) {
        // 检查缓存
        val cached = SimpleImageCache.get(url)
        if (cached != null) {
            bitmap = cached
            isLoading = false
            return@LaunchedEffect
        }
        
        // 从网络加载
        isLoading = true
        hasError = false
        
        try {
            val loaded = loadImageFromUrl(url)
            if (loaded != null) {
                SimpleImageCache.put(url, loaded)
                bitmap = loaded
            } else {
                hasError = true
            }
        } catch (e: Exception) {
            hasError = true
        } finally {
            isLoading = false
        }
    }
    
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(40.dp)
                )
            }
            hasError -> {
                Text("加载失败", color = Color.Red)
            }
            bitmap != null -> {
                Image(
                    painter = BitmapPainter(bitmap!!.asImageBitmap()),
                    contentDescription = "缓存的网络图片",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}