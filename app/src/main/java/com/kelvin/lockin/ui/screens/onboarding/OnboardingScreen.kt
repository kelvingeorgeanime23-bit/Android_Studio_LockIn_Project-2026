package com.kelvin.lockin.ui.screens.onboarding

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kelvin.lockin.R
import com.kelvin.lockin.ui.navigation.ROUTES
import com.kelvin.lockin.ui.theme.*
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

data class OnboardingPage(
    @DrawableRes val image: Int,
    val title: String,
    val description: String
)

@Composable
fun OnboardingScreen(navController: NavController) {

    val pages = listOf(
        OnboardingPage(
            R.drawable.pexels_1st_onboarding_screen,
            "Lock In.\nNo Excuses.",
            "LockIn creates a distraction-free environment so you can study, sleep or work without your phone getting in the way."
        ),
        OnboardingPage(
            R.drawable.pexels_2nd_onboarding_screen,
            "You Control\nYour Focus.",
            "Choose which apps stay open during your session. Block everything else. Stay locked in until the time is up."
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {

        // BACKGROUND IMAGE WITH CROSSFADE
        AnimatedContent(
            targetState = pagerState.currentPage,
            transitionSpec = {
                fadeIn(animationSpec = tween(600)) togetherWith
                        fadeOut(animationSpec = tween(600))
            },
            label = "bgTransition"
        ) { currentPage ->
            Image(
                painter = painterResource(id = pages[currentPage].image),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { alpha = 0.60f }
            )
        }

        // DARK GRADIENT OVERLAY
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            BackgroundDark.copy(alpha = 0.6f),
                            BackgroundDark.copy(alpha = 0.85f),
                            BackgroundDark
                        )
                    )
                )
        )

        // PURPLE GLOW TOP LEFT
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-80).dp, y = (-60).dp)
                .blur(120.dp)
                .background(PurplePrimary.copy(alpha = 0.25f), CircleShape)
        )

        // PURPLE GLOW BOTTOM RIGHT
        Box(
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 60.dp, y = 60.dp)
                .blur(120.dp)
                .background(PurpleLight.copy(alpha = 0.2f), CircleShape)
        )

        // MAIN CONTENT
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // SKIP BUTTON (always reserves space)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = {
                        navController.navigate(ROUTES.LOGIN) {
                            popUpTo(ROUTES.ONBOARDING) { inclusive = true }
                        }
                    },
                    enabled = pagerState.currentPage == 0
                ) {
                    Text(
                        text = "Skip",
                        color = if (pagerState.currentPage == 0) TextGrey else Color.Transparent,
                        fontFamily = InterFont,
                        fontSize = 14.sp
                    )
                }
            }

            // PAGER
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                val pageOffset = ((pagerState.currentPage - page) +
                        pagerState.currentPageOffsetFraction)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.height(32.dp))

                    // GLASSY CARD
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(32.dp))
                            .background(Color.White.copy(alpha = 0.07f))
                            .graphicsLayer {
                                translationX = pageOffset * size.width * 0.3f
                                alpha = 1f - pageOffset.absoluteValue.coerceIn(0f, 1f)
                                scaleX = 0.9f + (1f - pageOffset.absoluteValue.coerceIn(0f, 1f)) * 0.1f
                                scaleY = 0.9f + (1f - pageOffset.absoluteValue.coerceIn(0f, 1f)) * 0.1f
                            }
                            .padding(28.dp)
                    ) {
                        Column {
                            // PAGE NUMBER
                            Text(
                                text = (page + 1).toString().padStart(2, '0'),
                                fontFamily = OrbitronFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = PurpleLight,
                                letterSpacing = 4.sp
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // TITLE
                            Text(
                                text = pages[page].title,
                                fontFamily = OrbitronFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp,
                                color = TextWhite,
                                lineHeight = 36.sp
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // PURPLE DIVIDER
                            Box(
                                modifier = Modifier
                                    .width(48.dp)
                                    .height(3.dp)
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(PurplePrimary, PurpleLight)
                                        ),
                                        RoundedCornerShape(2.dp)
                                    )
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // DESCRIPTION
                            Text(
                                text = pages[page].description,
                                fontFamily = InterFont,
                                fontWeight = FontWeight.Normal,
                                fontSize = 15.sp,
                                color = TextGrey,
                                lineHeight = 24.sp
                            )
                        }
                    }
                }
            }

            // DOT INDICATORS
            Row(
                modifier = Modifier
                    .height(40.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(pages.size) { iteration ->
                    val isSelected = pagerState.currentPage == iteration
                    val color = if (isSelected) PurpleLight else TextMuted
                    val width by animateDpAsState(
                        targetValue = if (isSelected) 24.dp else 8.dp,
                        label = "dotWidth"
                    )
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(height = 8.dp, width = width)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // NEXT / GET STARTED BUTTON
            AnimatedContent(
                targetState = pagerState.currentPage,
                transitionSpec = {
                    fadeIn(animationSpec = tween(400)) togetherWith
                            fadeOut(animationSpec = tween(400))
                },
                label = "buttonTransition"
            ) { currentPage ->
                Button(
                    onClick = {
                        if (currentPage < pages.size - 1) {
                            scope.launch { pagerState.animateScrollToPage(currentPage + 1) }
                        } else {
                            navController.navigate(ROUTES.LOGIN) {
                                popUpTo(ROUTES.ONBOARDING) { inclusive = true }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PurplePrimary
                    ),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    Text(
                        text = if (currentPage < pages.size - 1) "Next" else "Get Started",
                        fontFamily = InterFont,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = TextWhite
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}