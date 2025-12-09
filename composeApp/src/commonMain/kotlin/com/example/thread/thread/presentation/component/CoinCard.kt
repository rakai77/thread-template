package com.example.thread.thread.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.thread.thread.domain.model.coindesk.CoinMarketCap
import com.example.thread.thread.presentation.component.CoinAvatar
import com.example.thread.thread.presentation.component.PriceChangeChip
import com.example.thread.thread.presentation.theme.CryptoColors
import kotlinx.coroutines.delay
import toRupiahSimple

@Composable
fun CryptoCoinCard(
    coin: CoinMarketCap,
    rank: Int,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val price = coin.marketData?.price ?: 0.0
    val changePct = coin.marketData?.changePct24h ?: 0.0
    val isPositive = changePct >= 0

    // Animated color untuk perubahan harga
    val changeColor by animateColorAsState(
        targetValue = when {
            changePct > 0 -> CryptoColors.PriceUp
            changePct < 0 -> CryptoColors.PriceDown
            else -> CryptoColors.PriceNeutral
        },
        animationSpec = tween(durationMillis = 300)
    )

    // Scale animation saat harga berubah
    var isAnimating by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isAnimating) 1.02f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        finishedListener = { isAnimating = false }
    )

    // Trigger animation ketika price berubah
    LaunchedEffect(price) {
        if (price > 0) {
            isAnimating = true
            delay(300)
            isAnimating = false
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale),
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = CryptoColors.CardBackground
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 6.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Rank + Avatar + Name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Rank
                Text(
                    text = "#$rank",
                    style = MaterialTheme.typography.labelLarge,
                    color = CryptoColors.TextTertiary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(40.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Coin Avatar
                CoinAvatar(
                    coinName = coin.name,
                    coinSymbol = coin.symbol,
                    size = 48.dp
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Coin Info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = coin.symbol,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = CryptoColors.TextPrimary
                    )
                    Text(
                        text = coin.fullName,
                        style = MaterialTheme.typography.bodySmall,
                        color = CryptoColors.TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Right: Price + Change
            Column(
                horizontalAlignment = Alignment.End
            ) {
                // Animated Price dengan fade transition
                AnimatedContent(
                    targetState = price,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) togetherWith
                                fadeOut(animationSpec = tween(300))
                    }
                ) { animatedPrice ->
                    Text(
                        text = animatedPrice.toRupiahSimple(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = CryptoColors.TextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Change Percentage chip
                PriceChangeChip(
                    changePct = changePct,
                    changeColor = changeColor
                )
            }
        }
    }
}
