package com.example.thread.thread.presentation.component

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import toPercentFormat

@Composable
fun PriceChangeChip(
    changePct: Double,
    changeColor: Color,
    modifier: Modifier = Modifier
) {
    val isPositive = changePct >= 0
    val isChanged = changePct != 0.0

    val scale by animateFloatAsState(
        targetValue = if (isChanged) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )

    Surface(
        modifier = modifier.scale(scale),
        shape = RoundedCornerShape(8.dp),
        color = changeColor.copy(alpha = 0.15f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Arrow icon
            Text(
                text = if (isPositive) "▲" else "▼",
                color = changeColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.width(4.dp))

            // Animated percentage
            AnimatedContent(
                targetState = changePct,
                transitionSpec = {
                    (fadeIn() + scaleIn()) togetherWith (fadeOut() + scaleOut())
                }
            ) { animatedChange ->
                Text(
                    text = animatedChange.toPercentFormat().removePrefix("+"),
                    color = changeColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
