package com.example.thread.thread.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thread.thread.domain.model.CryptoTicker

enum class PriceDirection {
    UP,
    DOWN,
    NEUTRAL
}

/**
 * Extension to determine price direction
 */
fun CryptoTicker.getPriceDirection(): PriceDirection {
    return when {
        (priceChangePercentage24h ?: 0.0) > 0 -> PriceDirection.UP
        (priceChangePercentage24h ?: 0.0) < 0 -> PriceDirection.DOWN
        else -> PriceDirection.NEUTRAL
    }
}

/**
 * Card component for displaying crypto ticker
 * Shows: symbol, price, change percentage with color coding
 */


@Composable
fun CryptoCard(
    crypto: CryptoTicker,
    modifier: Modifier = Modifier
) {
    val priceDirection = crypto.getPriceDirection()
    val backgroundColor = when (priceDirection) {
        PriceDirection.UP -> Color(0xFF4CAF50).copy(alpha = 0.1f) // Light green
        PriceDirection.DOWN -> Color(0xFFF44336).copy(alpha = 0.1f) // Light red
        PriceDirection.NEUTRAL -> MaterialTheme.colorScheme.surface
    }

    val changeColor = when (priceDirection) {
        PriceDirection.UP -> Color(0xFF4CAF50) // Green
        PriceDirection.DOWN -> Color(0xFFF44336) // Red
        PriceDirection.NEUTRAL -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side: Logo and ticker info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Symbol logo (circle with initials)
                CryptoSymbolLogo(symbol = crypto.base)

                // Ticker info
                Column {
                    Text(
                        text = "${crypto.base}/${crypto.quote}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = crypto.market,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Right side: Price and change
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = crypto.formattedPrice,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                crypto.formattedChange?.let { change ->
                    Text(
                        text = change,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = changeColor
                    )
                }
            }
        }
    }
}

/**
 * Symbol logo component - circle with crypto symbol
 */
@Composable
private fun CryptoSymbolLogo(
    symbol: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = symbol.take(3).uppercase(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontSize = 14.sp
        )
    }
}
