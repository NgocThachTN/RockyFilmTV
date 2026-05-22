package com.rocky.filmtv.ui.player.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.*
import com.rocky.filmtv.core.theme.PrimaryRed
import com.rocky.filmtv.data.remote.mapper.Episode

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun BoxScope.EpisodeSelector(
    showEpisodeList: Boolean,
    movieSlug: String,
    currentEpisodeIndex: Int,
    episodes: List<Episode>,
    episodeListFocusRequester: FocusRequester,
    onPlayEpisode: (String, Int) -> Unit,
    onCloseSelector: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = showEpisodeList,
        enter = slideInHorizontally(initialOffsetX = { it }),
        exit = slideOutHorizontally(targetOffsetX = { it }),
        modifier = modifier
            .align(Alignment.CenterEnd)
            .fillMaxHeight()
            .width(360.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0A192F).copy(alpha = 0.95f))
                .padding(vertical = 24.dp, horizontal = 20.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "Danh Sách Tập",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    itemsIndexed(episodes) { index, ep ->
                        val isCurrent = index == currentEpisodeIndex
                        Button(
                            onClick = {
                                onCloseSelector()
                                onPlayEpisode(movieSlug, index)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .let { 
                                    val shouldFocus = if (currentEpisodeIndex >= 0) isCurrent else index == 0
                                    if (shouldFocus) it.focusRequester(episodeListFocusRequester) else it
                                },
                            colors = ButtonDefaults.colors(
                                containerColor = if (isCurrent) Color(0xFF172A45) else Color(0x33172A45),
                                contentColor = if (isCurrent) PrimaryRed else Color.White,
                                focusedContainerColor = Color.White,
                                focusedContentColor = Color(0xFF0A192F)
                            )
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = ep.name,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                                if (isCurrent) {
                                    Text(
                                        text = "Đang phát",
                                        fontSize = 11.sp,
                                        color = PrimaryRed,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
