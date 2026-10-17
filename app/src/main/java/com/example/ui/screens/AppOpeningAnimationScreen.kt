package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

/**
 * Cinematic App Opening Animation Screen for SHIVAAR 2.0.
 * Features glowing cosmic radial gradients, animated pulsing emblem,
 * particle orbits, staggered feature typography, and seamless auto/interactive transition.
 */
@Composable
fun AppOpeningAnimationScreen(
    onAnimationComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var animationTriggered by remember { mutableStateOf(false) }

    // Multi-phase transition timings
    val progress = remember { Animatable(0f) }
    val emblemScale = remember { Animatable(0.2f) }
    val emblemRotation = remember { Animatable(-30f) }
    val textAlpha = remember { Animatable(0f) }
    val badgesAlpha = remember { Animatable(0f) }
    val footerAlpha = remember { Animatable(0f) }

    // Continuous ambient animations
    val infiniteTransition = rememberInfiniteTransition(label = "ambient_glow")
    val ambientPulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )
    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ring_rotation"
    )

    LaunchedEffect(Unit) {
        animationTriggered = true

        // Phase 1: Emblem explosive reveal with overshoot bounce
        emblemScale.animateTo(
            targetValue = 1.0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        emblemRotation.animateTo(
            targetValue = 0f,
            animationSpec = tween(600, easing = FastOutSlowInEasing)
        )

        // Phase 2: Title and branding fade in
        textAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(500, easing = LinearOutSlowInEasing)
        )

        // Phase 3: Pillars and feature badges fade in
        badgesAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(500, easing = LinearOutSlowInEasing)
        )

        // Phase 4: Launch loader progress bar fills
        footerAlpha.animateTo(1f, animationSpec = tween(300))
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(1200, easing = FastOutSlowInEasing)
        )

        // Graceful hold before entering app
        delay(300)
        onAnimationComplete()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("app_opening_animation_screen")
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF030712), // Deep Void Black
                        Color(0xFF0F172A), // Slate Navy
                        Color(0xFF1E1B4B), // Indigo Deep
                        Color(0xFF0F172A)
                    )
                )
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                // Allows user to tap anywhere to enter immediately
                onAnimationComplete()
            },
        contentAlignment = Alignment.Center
    ) {
        // --- Layer 1: Ambient Glowing Background Particles & Rings ---
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height * 0.40f)
            val baseRadius = size.width * 0.38f

            // Outer Radial Aura
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF4F46E5).copy(alpha = 0.35f * ambientPulse),
                        Color(0xFF7C3AED).copy(alpha = 0.20f * ambientPulse),
                        Color.Transparent
                    ),
                    center = center,
                    radius = baseRadius * 1.6f
                ),
                radius = baseRadius * 1.6f,
                center = center
            )

            // Tech Grid Concentric Rings
            drawCircle(
                color = Color(0xFF6366F1).copy(alpha = 0.25f),
                radius = baseRadius * 1.15f * ambientPulse,
                center = center,
                style = Stroke(width = 1.5f)
            )
            drawCircle(
                color = Color(0xFFA855F7).copy(alpha = 0.18f),
                radius = baseRadius * 1.4f,
                center = center,
                style = Stroke(width = 1f)
            )

            // Orbiting Stardust Nodes
            val particleCount = 8
            for (i in 0 until particleCount) {
                val angle = Math.toRadians((ringRotation + (i * 360f / particleCount)).toDouble())
                val orbitRadius = baseRadius * 1.15f * ambientPulse
                val px = (center.x + orbitRadius * cos(angle)).toFloat()
                val py = (center.y + orbitRadius * sin(angle)).toFloat()

                drawCircle(
                    color = if (i % 2 == 0) Color(0xFF38BDF8) else Color(0xFFFBBF24),
                    radius = 3.5f,
                    center = Offset(px, py)
                )
            }
        }

        // --- Layer 2: Main Animated Content Stack ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top System Status Pill
            Surface(
                shape = CircleShape,
                color = Color(0xFF1E293B).copy(alpha = 0.8f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF6366F1).copy(alpha = 0.4f)),
                modifier = Modifier
                    .alpha(footerAlpha.value)
                    .padding(top = 16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981))
                    )
                    Text(
                        text = "SYSTEM ENGINE • v2.0 READY",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        ),
                        color = Color(0xFFE2E8F0)
                    )
                }
            }

            // Center: Emblem + Logo + Feature Pillars
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                // Animated Emblem Shield with Cap & Code Brackets
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .scale(emblemScale.value)
                        .rotate(emblemRotation.value),
                    contentAlignment = Alignment.Center
                ) {
                    // Glowing Backdrop Glow
                    Surface(
                        modifier = Modifier.size(130.dp),
                        shape = RoundedCornerShape(32.dp),
                        color = Color(0xFF312E81).copy(alpha = 0.6f),
                        border = androidx.compose.foundation.BorderStroke(
                            2.dp,
                            Brush.linearGradient(
                                listOf(Color(0xFF818CF8), Color(0xFFC084FC), Color(0xFF38BDF8))
                            )
                        ),
                        shadowElevation = 16.dp
                    ) {}

                    // Custom Vector Mascot Drawing inside Box
                    Canvas(modifier = Modifier.size(90.dp)) {
                        val w = size.width
                        val h = size.height

                        // Academic Cap Apex (Diamond)
                        val capPath = Path().apply {
                            moveTo(w * 0.5f, h * 0.22f)
                            lineTo(w * 0.85f, h * 0.38f)
                            lineTo(w * 0.5f, h * 0.54f)
                            lineTo(w * 0.15f, h * 0.38f)
                            close()
                        }
                        drawPath(
                            path = capPath,
                            brush = Brush.linearGradient(
                                listOf(Color.White, Color(0xFFE0E7FF))
                            )
                        )

                        // Academic Cap Base
                        val basePath = Path().apply {
                            moveTo(w * 0.30f, h * 0.46f)
                            lineTo(w * 0.30f, h * 0.68f)
                            quadraticBezierTo(w * 0.5f, h * 0.80f, w * 0.70f, h * 0.68f)
                            lineTo(w * 0.70f, h * 0.46f)
                            lineTo(w * 0.5f, h * 0.56f)
                            close()
                        }
                        drawPath(
                            path = basePath,
                            brush = Brush.linearGradient(
                                listOf(Color(0xFFA5B4FC), Color(0xFF6366F1))
                            )
                        )

                        // Code Symbol: Left Bracket <
                        val leftBracket = Path().apply {
                            moveTo(w * 0.38f, h * 0.78f)
                            lineTo(w * 0.28f, h * 0.84f)
                            lineTo(w * 0.38f, h * 0.90f)
                        }
                        drawPath(
                            path = leftBracket,
                            color = Color(0xFF38BDF8),
                            style = Stroke(width = 4f)
                        )

                        // Code Symbol: Right Bracket >
                        val rightBracket = Path().apply {
                            moveTo(w * 0.62f, h * 0.78f)
                            lineTo(w * 0.72f, h * 0.84f)
                            lineTo(w * 0.62f, h * 0.90f)
                        }
                        drawPath(
                            path = rightBracket,
                            color = Color(0xFF38BDF8),
                            style = Stroke(width = 4f)
                        )

                        // Golden North Star at Apex
                        drawCircle(
                            color = Color(0xFFFBBF24),
                            radius = 6f,
                            center = Offset(w * 0.85f, h * 0.30f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Brand Name "SHIVAAR" with Gradient & Neon "2.0"
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.alpha(textAlpha.value)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "SHIVAAR",
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 3.sp
                            ),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF4F46E5),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF818CF8))
                        ) {
                            Text(
                                text = "2.0",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color(0xFFFBBF24),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "THE ULTIMATE ENGINEERING OPERATING SYSTEM",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color(0xFFA5B4FC)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "48-Month Strategic B.Tech CSE & Placement Acceleration Platform",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Feature Highlights Pill Badges (Staggered Animation)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(badgesAlpha.value),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FeaturePill(
                        icon = Icons.Default.School,
                        title = "AKTU University Curriculum Sync",
                        subtitle = "75% Attendance Guard • Unit Checklists • SGPA Target",
                        accentColor = Color(0xFF38BDF8)
                    )
                    FeaturePill(
                        icon = Icons.Default.Speed,
                        title = "Daily Execution & Focus Engine",
                        subtitle = "Timed Task Sessions • Adaptive Recovery • Milestone Spawning",
                        accentColor = Color(0xFF818CF8)
                    )
                    FeaturePill(
                        icon = Icons.Default.Code,
                        title = "DSA Placement & Capstone Studio",
                        subtitle = "Striver A2Z Tracking • Recruiter Value • Career Pipeline",
                        accentColor = Color(0xFFF43F5E)
                    )
                }
            }

            // Bottom Section: Progress Bar & Direct Enter Button
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(footerAlpha.value),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Progress Bar with Shimmer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (progress.value < 1f) "INITIALIZING WORKSPACE..." else "ENVIRONMENT READY",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = Color(0xFF94A3B8)
                    )
                    Text(
                        text = "${(progress.value * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color(0xFF38BDF8)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { progress.value },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = Color(0xFF6366F1),
                    trackColor = Color(0xFF1E293B)
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Enter App Fast-Forward Button
                Button(
                    onClick = onAnimationComplete,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4F46E5),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_enter_app")
                ) {
                    Text(
                        text = "Enter Shivaar 2.0",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Enter",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun FeaturePill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    accentColor: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF1E293B).copy(alpha = 0.7f),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            accentColor.copy(alpha = 0.25f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = accentColor.copy(alpha = 0.18f),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = Color(0xFF94A3B8)
                )
            }
        }
    }
}
