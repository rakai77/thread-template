package com.example.thread.thread.presentation.screen.home

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.thread.thread.domain.model.coindesk.CoinMarketCap
import com.example.thread.thread.presentation.component.EmptyState
import com.example.thread.thread.presentation.component.ShimmerCoinCard
import com.example.thread.thread.presentation.components.CryptoCoinCard
import com.example.thread.thread.presentation.theme.CryptoColors
import com.example.thread.thread.utils.getCurrentTimeMillis
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
) {

    val viewModel: HomeViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            CryptoTopBar(
                onRefresh = { viewModel.refresh() },
                isRefreshing = state.isRefreshing,
                lastUpdateTime = state.lastUpdateTime
            )
        },
        containerColor = CryptoColors.AppBackground
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Main content based on UI state
            AnimatedContent(
                targetState = uiState,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith
                            fadeOut(animationSpec = tween(300))
                }
            ) { currentUiState ->
                when (currentUiState) {
                    is CryptoUiState.Loading -> {
                        LoadingContent()
                    }

                    is CryptoUiState.Success -> {
                        if (currentUiState.coins.isEmpty()) {
                            EmptyContent(
                                onRetry = { viewModel.fetchTopMarketCap() }
                            )
                        } else {
                            CryptoListContent(
                                coins = currentUiState.coins,
                                listState = listState,
                                isRefreshing = state.isRefreshing,
                                onCoinClick = { coin ->
                                    println("Clicked: ${coin.name}")
                                    // Navigate to detail
                                }
                            )
                        }
                    }

                    is CryptoUiState.Error -> {
                        ErrorContent(
                            message = currentUiState.message,
                            onRetry = { viewModel.fetchTopMarketCap() }
                        )
                    }
                    else -> Unit
                }
            }

            state.error?.let { errorMessage ->
                LaunchedEffect(errorMessage) {
                    delay(3000)
                    viewModel.clearError()
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CryptoTopBar(
    onRefresh: () -> Unit,
    isRefreshing: Boolean,
    lastUpdateTime: Long,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "Crypto Market",
                    fontWeight = FontWeight.Bold
                )
                if (lastUpdateTime > 0) {
                    val timeAgo = remember(lastUpdateTime) {
                        val diff = getCurrentTimeMillis() - lastUpdateTime
                        when {
                            diff < 60_000 -> "Just now"
                            diff < 3600_000 -> "${diff / 60_000}m ago"
                            else -> "${diff / 3600_000}h ago"
                        }
                    }
                    Text(
                        text = "Updated $timeAgo",
                        style = MaterialTheme.typography.bodySmall,
                        color = CryptoColors.TextSecondary
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = CryptoColors.AppBackground,
            titleContentColor = CryptoColors.TextPrimary
        ),
        actions = {
            IconButton(
                onClick = onRefresh,
                enabled = !isRefreshing
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    tint = if (isRefreshing)
                        CryptoColors.TextSecondary
                    else
                        CryptoColors.AccentGold
                )
            }
        },
        modifier = modifier
    )
}

@Composable
private fun LoadingContent(
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(10) {
            ShimmerCoinCard()
        }
    }
}

@Composable
private fun CryptoListContent(
    coins: List<CoinMarketCap>,
    listState: androidx.compose.foundation.lazy.LazyListState,
    isRefreshing: Boolean,
    onCoinClick: (CoinMarketCap) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Refreshing indicator di top
        if (isRefreshing) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = CryptoColors.AccentGold,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Updating...",
                            style = MaterialTheme.typography.bodySmall,
                            color = CryptoColors.TextSecondary
                        )
                    }
                }
            }
        }

        // List of coins dengan animated items
        itemsIndexed(
            items = coins,
            key = { _, coin -> coin.id }
        ) { index, coin ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                CryptoCoinCard(
                    coin = coin,
                    rank = index + 1,
                    onClick = { onCoinClick(coin) }
                )
            }
        }

        // Footer spacer
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun EmptyContent(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    EmptyState(
        emoji = "📊",
        title = "No Cryptocurrencies",
        message = "No cryptocurrency data available at the moment",
        actionText = "Refresh",
        onAction = onRetry,
        modifier = modifier
    )
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    EmptyState(
        emoji = "❌",
        title = "Oops!",
        message = message,
        actionText = "Try Again",
        onAction = onRetry,
        modifier = modifier
    )
}