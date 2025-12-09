package com.example.thread.thread.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thread.thread.presentation.theme.CryptoColors

@Composable
fun CoinAvatar(
    coinName: String,
    coinSymbol: String,
    size: Dp = 48.dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(
                brush = Brush.linearGradient(
                    colors = getGradientForCoin(coinSymbol)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = coinSymbol.take(2).uppercase(),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = when {
                size >= 56.dp -> 22.sp
                size >= 40.dp -> 18.sp
                else -> 14.sp
            }
        )
    }
}

private fun getGradientForCoin(symbol: String): List<Color> {
    return when (symbol.uppercase().hashCode() % 6) {
        0 -> CryptoColors.GradientGold
        1 -> CryptoColors.GradientGreen
        2 -> CryptoColors.GradientBlue
        3 -> CryptoColors.GradientRed
        4 -> CryptoColors.GradientPurple
        else -> CryptoColors.GradientOrange
    }
}
