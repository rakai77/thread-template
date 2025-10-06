package com.example.thread.thread

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {
    MaterialTheme {
        var idx by remember { mutableStateOf(0) }
        val steps = listOf("fab", "promo", "profile")

        var rectFab by remember { mutableStateOf<Rect?>(null) }
        var rectPromo by remember { mutableStateOf<Rect?>(null) }
        var rectProfile by remember { mutableStateOf<Rect?>(null) }

        var overlayOffset by remember { mutableStateOf(Offset.Zero) } // posisi overlay di window
        var show by remember { mutableStateOf(true) }

        Box(Modifier.fillMaxSize()) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Beranda") },
                        actions = {
                            IconButton(
                                onClick = {},
                                modifier = Modifier.captureRectInWindow { rectProfile = it }
                            ) {
                                Icon(Icons.Default.AccountCircle, contentDescription = "Profile")
                            }
                        }
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {},
                        modifier = Modifier.captureRectInWindow { rectFab = it }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                ) {
                    Card(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                            .height(120.dp)
                            .captureRectInWindow { rectPromo = it }
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Card Promo")
                        }
                    }
                    Spacer(Modifier.height(800.dp))
                }
            }

            // --- tangkap offset overlay terhadap window
            Box(
                Modifier
                    .matchParentSize()
                    .onGloballyPositioned { overlayOffset = it.localToWindow(Offset.Zero) }
            )

            // --- pilih rect target
            val target = when (steps.getOrNull(idx)) {
                "fab" -> rectFab
                "promo" -> rectPromo
                "profile" -> rectProfile
                else -> null
            }

            WalkthroughOverlayFixed(
                visible = show,
                targetRectInWindow = target,
                overlayWindowOffset = overlayOffset,
                stepTitle = when (steps.getOrNull(idx)) {
                    "fab" -> "Tambah data"
                    "promo" -> "Promo spesial"
                    else -> "Profil kamu"
                },
                stepDesc = "Ikuti petunjuk ini.",
                stepIndex = idx,
                stepsCount = steps.size,
                onNext = { if (idx < steps.lastIndex) idx++ else show = false },
                onBack = { if (idx > 0) idx-- },
                onSkip = { show = false }
            )
        }
    }
}

@Composable
fun WalkthroughOverlayFixed(
    visible: Boolean,
    targetRectInWindow: Rect?,
    overlayWindowOffset: Offset,
    stepTitle: String,
    stepDesc: String,
    stepIndex: Int,
    stepsCount: Int,
    onNext: () -> Unit,
    onBack: (() -> Unit)? = null,
    onSkip: () -> Unit
) {
    if (!visible || targetRectInWindow == null) return

    val density = LocalDensity.current
    val corner = 14.dp
    val pad = 12.dp

    // Konversi rect ke koordinat lokal overlay
    val r = targetRectInWindow.translate(-overlayWindowOffset.x, -overlayWindowOffset.y)

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val screenW = with(density) { maxWidth.toPx() }
        val screenH = with(density) { maxHeight.toPx() }
        val tooltipW = with(density) { 280.dp.toPx() }
        val tooltipH = with(density) { 148.dp.toPx() }
        val margin = with(density) { 16.dp.toPx() }

        val spaceAbove = r.top
        val spaceBelow = screenH - r.bottom
        val placeBelow = spaceBelow >= spaceAbove

        val tx = (r.center.x - tooltipW / 2f).coerceIn(margin, screenW - margin - tooltipW)
        val ty = if (placeBelow) r.bottom + with(density) { pad.toPx() }
        else (r.top - with(density) { pad.toPx() } - tooltipH).coerceAtLeast(margin)

        // SCRIM + CUTOUT (pakai Clear di offscreen layer biar konsisten lintas platform)
        Box(
            Modifier
                .fillMaxSize()
                .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
        ) {
            Canvas(Modifier.fillMaxSize()) {
                // scrim
                drawRect(Color(0xCC000000))
                // lubang
                drawRoundRect(
                    color = Color.Transparent,
                    topLeft = Offset(r.left, r.top),
                    size = Size(r.width, r.height),
                    cornerRadius = CornerRadius(with(density) { corner.toPx() }),
                    blendMode = BlendMode.Clear
                )
                // stroke highlight
                drawRoundRect(
                    color = Color.White.copy(alpha = 0.9f),
                    topLeft = Offset(r.left, r.top),
                    size = Size(r.width, r.height),
                    cornerRadius = CornerRadius(with(density) { corner.toPx() }),
                    style = Stroke(width = with(density) { 2.dp.toPx() })
                )
            }

            // Tooltip
            Box(
                modifier =  Modifier
                    .absoluteOffset(x = with(density) { tx.toDp() }, y = with(density) { ty.toDp() })
                    .width(280.dp)
                    .navigationBarsPadding() // biar gak ketutup gesture bar
            ) {
                TooltipCard(
                    title = stepTitle,
                    description = stepDesc,
                    currentIndex = stepIndex,
                    totalSteps = stepsCount,
                    onNext = onNext,
                    onBack = onBack,
                    onSkip = onSkip
                )
            }
        }
    }
}

@Composable
private fun TooltipCard(
    title: String,
    description: String,
    currentIndex: Int,
    totalSteps: Int,
    onNext: () -> Unit,
    onBack: (() -> Unit)?,
    onSkip: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E1E)
        )
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(text = title, color = Color.White, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Text(text = description, color = Color.White.copy(alpha = 0.9f))
            Spacer(Modifier.height(12.dp))
            Row {
                Text(
                    "${currentIndex + 1} / $totalSteps",
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.weight(1f)
                )
                if (onBack != null && currentIndex > 0) TextButton(onClick = onBack) { Text("Back") }
                TextButton(onClick = onSkip) { Text("Skip") }
                Button(onClick = onNext) { Text(if (currentIndex == totalSteps - 1) "Finish" else "Next") }
            }
        }
    }
}


fun Modifier.captureRectInWindow(onRect: (Rect) -> Unit) = this.then(
    Modifier.onGloballyPositioned { onRect(it.boundsInWindow()) }
)
