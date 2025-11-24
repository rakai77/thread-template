package com.example.thread.thread.presentation.screen.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.thread.thread.domain.model.CryptoTicker
import com.example.thread.thread.utils.formatPercent
import com.example.thread.thread.utils.formatPrice
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
) {
    val viewModel: HomeViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crypto Market") },
                actions = {
                    IconButton(onClick = { viewModel.onRefresh() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                state.error != null && state.cryptoList.isEmpty() -> {
                    ErrorMessage(
                        message = state.error ?: "Unknown error",
                        onRetry = { viewModel.onRefresh() },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                state.cryptoList.isEmpty() -> {
                    Text(
                        text = "No data available",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    CryptoList(cryptoList = state.cryptoList)
                }
            }
        }
    }

}

@Composable
fun CryptoList(cryptoList: List<CryptoTicker>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = cryptoList,
            key = { it.symbol }
        ) {
            CryptoCard(crypto = it)
        }
    }
}

@Composable
fun CryptoCard(crypto: CryptoTicker) {
    val isPositive = crypto.priceChangePercent >= 0

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(visible = visible) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Symbol & Name
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = crypto.symbol.replace("USDT", ""),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = crypto.symbol,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                // Center: Price
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "$${crypto.currentPrice.formatPrice()}", // ✅ Extension function
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$${crypto.priceChange.formatPrice()}", // ✅ Extension function
                        fontSize = 12.sp,
                        color = if (isPositive) Color(0xFF4CAF50) else Color(0xFFF44336)
                    )
                }

                // Right: Percentage & Indicator
                Spacer(modifier = Modifier.width(12.dp))
                PriceChangeIndicator(
                    priceChangePercent = crypto.priceChangePercent,
                    isPositive = isPositive
                )
            }
        }
    }
}

@Composable
fun PriceChangeIndicator(
    priceChangePercent: Double,
    isPositive: Boolean
) {
    val backgroundColor = if (isPositive) Color(0xFF4CAF50) else Color(0xFFF44336)
    val icon = if (isPositive) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown

    val rotation by animateFloatAsState(
        targetValue = if (isPositive) 0f else 180f,
        animationSpec = tween(durationMillis = 300),
        label = "rotation"
    )

    Box(
        modifier = Modifier
            .size(70.dp, 40.dp)
            .background(
                color = backgroundColor.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = if (isPositive) "Up" else "Down",
                tint = backgroundColor,
                modifier = Modifier
                    .size(24.dp)
                    .rotate(rotation)
            )
            Text(
                text = "${priceChangePercent.formatPercent()}%",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = backgroundColor
            )
        }
    }
}

@Composable
fun ErrorMessage(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}