package com.example.thread.thread.presentation.screen.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromoScreen(navController: NavHostController) {
    var idx by remember { mutableStateOf(0) }
    val steps = listOf("fab", "promo", "profile")

    var rectFab by remember { mutableStateOf<Rect?>(null) }
    var rectPromo by remember { mutableStateOf<Rect?>(null) }
    var rectProfile by remember { mutableStateOf<Rect?>(null) }

    var overlayOffset by remember { mutableStateOf(Offset.Zero) }
    var show by remember { mutableStateOf(true) }

    Box(Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Beranda") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "back")
                        }
                    },
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
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Card Promo")
                    }
                }
                Spacer(Modifier.height(800.dp))
            }
        }

        // simpan offset overlay terhadap window
        Box(
            Modifier
                .matchParentSize()
                .onGloballyPositioned { overlayOffset = it.localToWindow(Offset.Zero) }
        )

        val stepsOrdered = remember(rectFab, rectPromo, rectProfile) {
            val measured = listOfNotNull(
                rectProfile?.let { "profile" to it },
                rectPromo?.let { "promo" to it },
                rectFab?.let { "fab" to it },
            )
            if (measured.size < 3) {
                // fallback sementara sebelum semua komponen terukur
                listOf("profile", "promo", "fab")
            } else {
                measured.sortedBy { it.second.top } // top paling kecil = paling atas
                    .map { it.first }
            }
        }

        val currentKey = stepsOrdered.getOrNull(idx)
        val target = when (currentKey) {
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
    val arrowHeight = 8.dp
    val arrowWidth = 16.dp

    // Rect target dalam koordinat overlay
    val r = targetRectInWindow.translate(-overlayWindowOffset.x, -overlayWindowOffset.y)

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val screenW = with(density) { maxWidth.toPx() }
        val screenH = with(density) { maxHeight.toPx() }
        val tooltipW = with(density) { 280.dp.toPx() }
        val tooltipH = with(density) { 148.dp.toPx() }
        val arrowH = with(density) { arrowHeight.toPx() }
        val arrowW = with(density) { arrowWidth.toPx() }
        val margin = with(density) { 16.dp.toPx() }
        val padPx = with(density) { pad.toPx() }
        val cornerPx = with(density) { corner.toPx() }

        val spaceAbove = r.top
        val spaceBelow = screenH - r.bottom
        val placeBelow = spaceBelow >= spaceAbove

        // X card, tetap di layar
        val tx = (r.center.x - tooltipW / 2f).coerceIn(margin, screenW - margin - tooltipW)

        // Y card, perhitungkan tinggi panah sesuai posisi
        val ty = if (placeBelow) {
            // tooltip di bawah target, panah di atas card
            (r.bottom + padPx).coerceAtMost(screenH - margin - (tooltipH + arrowH))
        } else {
            // tooltip di atas target, panah di bawah card
            (r.top - padPx - tooltipH - arrowH).coerceAtLeast(margin)
        }

        // Offset panah agar menunjuk pusat target dan tidak menabrak sudut bulat
        val edgeGuard = cornerPx + with(density) { 8.dp.toPx() }
        val targetCenterX = r.center.x
        val arrowOffsetXPx = (targetCenterX - tx - arrowW / 2f)
            .coerceIn(edgeGuard, tooltipW - edgeGuard - arrowW)
        val arrowOffsetX = with(density) { arrowOffsetXPx.toDp() }

        Box(
            Modifier
                .fillMaxSize()
                .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
        ) {
            // Spotlight overlay berlubang di area target
            Canvas(Modifier.fillMaxSize()) {
                drawRect(Color(0xCC000000))
                drawRoundRect(
                    color = Color.Transparent,
                    topLeft = Offset(r.left, r.top),
                    size = Size(r.width, r.height),
                    cornerRadius = CornerRadius(cornerPx),
                    blendMode = BlendMode.Clear
                )
                drawRoundRect(
                    color = Color.White.copy(alpha = 0.9f),
                    topLeft = Offset(r.left, r.top),
                    size = Size(r.width, r.height),
                    cornerRadius = CornerRadius(cornerPx),
                    style = Stroke(width = with(density) { 2.dp.toPx() })
                )
            }

            Box(
                modifier = Modifier
                    .absoluteOffset(x = with(density) { tx.toDp() }, y = with(density) { ty.toDp() })
                    .width(280.dp)
                    .navigationBarsPadding()
            ) {
                TooltipCard(
                    title = stepTitle,
                    description = stepDesc,
                    currentIndex = stepIndex,
                    totalSteps = stepsCount,
                    onNext = onNext,
                    onBack = onBack,
                    onSkip = onSkip,
                    // jika tooltip di bawah target -> panah di atas card (mengarah ke atas)
                    // jika tooltip di atas target -> panah di bawah card (mengarah ke bawah)
                    arrowOnTop = placeBelow,
                    arrowOffsetX = arrowOffsetX
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
    onSkip: () -> Unit,
    arrowOnTop: Boolean = true,
    arrowOffsetX: Dp = 0.dp
) {
    Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
        // Panah di atas card, default TriangleShape sudah mengarah ke atas (tanpa rotasi)
        if (arrowOnTop) {
            Box(
                modifier = Modifier
                    .offset(x = arrowOffsetX)
                    .width(16.dp)
                    .height(8.dp)
                    .background(color = Color(0xFF1E1E1E), shape = TriangleShape())
            )
        }

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(text = title, color = Color.White, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Text(text = description, color = Color.White.copy(alpha = 0.9f))
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "${currentIndex + 1} / $totalSteps",
                        color = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.weight(1f)
                    )
                    if (onBack != null && currentIndex > 0) {
                        TextButton(onClick = onBack) { Text("Back") }
                    }
                    TextButton(onClick = onSkip) { Text("Skip") }
                    Button(onClick = onNext) {
                        Text(if (currentIndex == totalSteps - 1) "Finish" else "Next")
                    }
                }
            }
        }

        // Panah di bawah card, rotasi 180° agar mengarah ke bawah
        if (!arrowOnTop) {
            Box(
                modifier = Modifier
                    .offset(x = arrowOffsetX)
                    .width(16.dp)
                    .height(8.dp)
                    .graphicsLayer(rotationZ = 180f)
                    .background(color = Color(0xFF1E1E1E), shape = TriangleShape())
            )
        }
    }
}

fun Modifier.captureRectInWindow(onRect: (Rect) -> Unit) = this.then(
    Modifier.onGloballyPositioned { onRect(it.boundsInWindow()) }
)

class TriangleShape : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val path = Path().apply {
            // apex di atas -> bentuk panah mengarah ke atas secara default
            moveTo(size.width / 2f, 0f)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        return Outline.Generic(path)
    }
}