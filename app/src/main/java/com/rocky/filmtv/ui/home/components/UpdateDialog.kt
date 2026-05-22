package com.rocky.filmtv.ui.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.*
import com.rocky.filmtv.BuildConfig
import com.rocky.filmtv.core.theme.PrimaryRed
import com.rocky.filmtv.data.repository.UpdateInfo
import com.rocky.filmtv.ui.home.UpdateUiState
import kotlinx.coroutines.delay

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun UpdateDialog(
    state: UpdateUiState,
    onUpdateClick: (UpdateInfo) -> Unit,
    onDismissClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (state is UpdateUiState.NoUpdate) return

    val focusRequester = remember { FocusRequester() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .width(550.dp)
                .background(Color(0xFF141414), shape = RoundedCornerShape(12.dp))
                .border(1.dp, Color.White.copy(alpha = 0.15f), shape = RoundedCornerShape(12.dp))
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Branded Tag Header
            Box(
                modifier = Modifier
                    .background(PrimaryRed, shape = RoundedCornerShape(4.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "BẢN CẬP NHẬT MỚI",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (state) {
                is UpdateUiState.UpdateAvailable -> {
                    val info = state.info
                    Text(
                        text = "Phát hiện phiên bản v${info.newVersion}",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Phiên bản hiện tại: v${BuildConfig.VERSION_NAME}",
                        color = Color.Gray,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Changelog Section
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .background(Color(0xFF1C1C1E), shape = RoundedCornerShape(8.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.05f), shape = RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Có gì mới:",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = info.changelog,
                                color = Color.LightGray,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Interactive TV Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = { onUpdateClick(info) },
                            colors = ButtonDefaults.colors(
                                containerColor = PrimaryRed,
                                contentColor = Color.White,
                                focusedContainerColor = Color.White,
                                focusedContentColor = Color.Black
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .focusRequester(focusRequester),
                            shape = ButtonDefaults.shape(RoundedCornerShape(6.dp))
                        ) {
                            Text(
                                text = "Cập Nhật Ngay",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Button(
                            onClick = onDismissClick,
                            colors = ButtonDefaults.colors(
                                containerColor = Color.White.copy(alpha = 0.1f),
                                contentColor = Color.White,
                                focusedContainerColor = Color.White,
                                focusedContentColor = Color.Black
                            ),
                            modifier = Modifier.weight(1f),
                            shape = ButtonDefaults.shape(RoundedCornerShape(6.dp))
                        ) {
                            Text(
                                text = "Để Sau",
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        }
                    }

                    // Auto-focus Update button on show
                    LaunchedEffect(state) {
                        delay(150)
                        try {
                            focusRequester.requestFocus()
                        } catch (e: Exception) {
                            // ignore
                        }
                    }
                }

                is UpdateUiState.Downloading -> {
                    Text(
                        text = "Đang tải bản cập nhật...",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    LinearProgressIndicator(
                        progress = state.progress,
                        color = PrimaryRed,
                        trackColor = Color.White.copy(alpha = 0.1f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .background(Color.Transparent, shape = RoundedCornerShape(3.dp))
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val sizeText = if (state.totalMB > 0) {
                        "${state.downloadedMB}MB / ${state.totalMB}MB"
                    } else {
                        "${state.downloadedMB}MB"
                    }
                    Text(
                        text = "$sizeText (${(state.progress * 100).toInt()}%)",
                        color = Color.Gray,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                is UpdateUiState.Error -> {
                    Text(
                        text = "Tải xuống thất bại",
                        color = PrimaryRed,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = state.message,
                        color = Color.LightGray,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onDismissClick,
                        colors = ButtonDefaults.colors(
                            containerColor = Color.White.copy(alpha = 0.15f),
                            contentColor = Color.White,
                            focusedContainerColor = Color.White,
                            focusedContentColor = Color.Black
                        ),
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .focusRequester(focusRequester),
                        shape = ButtonDefaults.shape(RoundedCornerShape(6.dp))
                    ) {
                        Text(
                            text = "Đóng",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    LaunchedEffect(state) {
                        delay(150)
                        try {
                            focusRequester.requestFocus()
                        } catch (e: Exception) {
                            // ignore
                        }
                    }
                }
                else -> {}
            }
        }
    }
}
