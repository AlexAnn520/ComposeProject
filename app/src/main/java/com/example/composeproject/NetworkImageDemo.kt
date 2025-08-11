package com.example.composeproject

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder

// 测试用的图片URL
//private const val SAMPLE_IMAGE_URL = "https://picsum.photos/400/300"
private const val SAMPLE_IMAGE_URL = "https://www.kelagirls.com/upload/2021/02/05/1612512276609.jpg"
private const val ERROR_IMAGE_URL = "https://invalid-url-for-testing.com/image.jpg"

/**
 * 方案1: 使用 Coil 库 - 最推荐的方式
 * Coil是Kotlin优先的图片加载库，专为Android和Compose设计
 */
@Composable
fun CoilImageExample() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            "方案1: Coil库实现",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // 1.1 最简单的使用方式
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("基础用法 - AsyncImage", fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(8.dp))
                
                AsyncImage(
                    model = SAMPLE_IMAGE_URL,
                    contentDescription = "网络图片",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
        
        // 1.2 带加载状态的实现
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("带加载状态", fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(8.dp))
                
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(SAMPLE_IMAGE_URL + "?random=${System.currentTimeMillis()}")
                        .crossfade(true) // 添加淡入动画
                        .build(),
                    contentDescription = "带状态的图片",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                    loading = {
                        // 加载中显示
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Gray.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(40.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    error = {
                        // 错误时显示
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Red.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("加载失败", color = Color.Red)
                                Icon(
                                    painter = painterResource(android.R.drawable.ic_delete),
                                    contentDescription = "错误",
                                    tint = Color.Red,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                        }
                    }
                )
            }
        }
        
        // 1.3 使用rememberAsyncImagePainter获取加载状态
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("监听加载状态", fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(8.dp))
                
                val painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(SAMPLE_IMAGE_URL + "?random2=${System.currentTimeMillis()}")
                        .crossfade(600)
                        .build()
                )
                
                // 显示加载状态
                when (painter.state) {
                    is AsyncImagePainter.State.Loading -> {
                        Text("正在加载...", color = Color.Blue)
                    }
                    is AsyncImagePainter.State.Success -> {
                        Text("加载成功!", color = Color.Green)
                    }
                    is AsyncImagePainter.State.Error -> {
                        Text("加载失败!", color = Color.Red)
                    }
                    else -> {}
                }
                
                Image(
                    painter = painter,
                    contentDescription = "状态监听图片",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Fit
                )
            }
        }
        
        // 1.4 带占位图和错误图的完整实现
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("完整配置示例", fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(8.dp))
                
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(ERROR_IMAGE_URL) // 故意使用错误URL演示错误处理
                        .crossfade(true)
                        .placeholder(R.drawable.mm) // 占位图
                        .error(android.R.drawable.ic_dialog_alert) // 错误图
                        .build(),
                    contentDescription = "完整配置图片",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

/**
 * 最佳实践：创建可复用的网络图片组件
 * 封装常用配置，提供统一的使用接口
 */
@Composable
fun NetworkImage(
    url: String,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    placeholderRes: Int? = null,
    errorRes: Int? = null,
    crossfadeDuration: Int = 300,
    onLoading: @Composable (() -> Unit)? = null,
    onError: @Composable (() -> Unit)? = null,
    onSuccess: (() -> Unit)? = null
) {
    val context = LocalContext.current
    
    if (onLoading != null || onError != null) {
        // 使用SubcomposeAsyncImage支持自定义加载和错误UI
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(context)
                .data(url)
                .crossfade(crossfadeDuration)
                .apply {
                    placeholderRes?.let { placeholder(it) }
                    errorRes?.let { error(it) }
                }
                .build(),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale,
            loading = { onLoading?.invoke() },
            error = { onError?.invoke() },
            onSuccess = { onSuccess?.invoke() }
        )
    } else {
        // 使用简单的AsyncImage
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(url)
                .crossfade(crossfadeDuration)
                .apply {
                    placeholderRes?.let { placeholder(it) }
                    errorRes?.let { error(it) }
                }
                .build(),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale,
            onSuccess = { onSuccess?.invoke() }
        )
    }
}

/**
 * 使用示例
 */
@Composable
fun NetworkImageUsageExample() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            "网络图片加载最佳实践",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // 使用封装的组件
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("使用封装组件", fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(8.dp))
                
                NetworkImage(
                    url = SAMPLE_IMAGE_URL,
                    contentDescription = "示例图片",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    placeholderRes = R.drawable.mm,
                    errorRes = android.R.drawable.ic_dialog_alert,
                    onLoading = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Gray.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    },
                    onSuccess = {
                        // 可以在这里记录日志或执行其他操作
                    }
                )
            }
        }
        
        // Coil实现示例
        Divider(modifier = Modifier.padding(vertical = 16.dp))
        CoilImageExample()
    }
}