package com.example.thread.thread.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.InternalResourceApi
import thread.composeapp.generated.resources.*

data class ThreadTextStyles(
    val headlineExtraLarge: TextStyle,
    val headlineLarge: TextStyle,
    val headlineMediumBold: TextStyle,
    val headlineMedium: TextStyle,
    val headlineSmall: TextStyle,
    val titleLargeBold: TextStyle,
    val titleLarge: TextStyle,
    val titleMediumBold: TextStyle,
    val titleMedium: TextStyle,
    val titleSmallBold: TextStyle,
    val titleSmall: TextStyle,
    val bodyLargeBold: TextStyle,
    val bodyLarge: TextStyle,
    val bodyMediumBold: TextStyle,
    val bodyMedium: TextStyle,
    val bodySmallBold: TextStyle,
    val bodySmall: TextStyle,
    val labelLarge: TextStyle,
    val labelLargeBold: TextStyle,
    val labelMedium: TextStyle,
    val labelSmall: TextStyle,
    val labelTextField: TextStyle,
    val placeholderTextField: TextStyle,
    val placeholderTextFieldTransactionEmas: TextStyle,
    val valueTextField: TextStyle,
    val errorTextField: TextStyle,
    val buttonText: TextStyle,
    val label12NormalNoLineHeight: TextStyle,
    val label12SemiBoldNoLineHeight: TextStyle,
    val label12SemiBold: TextStyle,
    val label24Bold: TextStyle,
    val label20Bold: TextStyle
)

@OptIn(InternalResourceApi::class)
@Composable
fun findeFontFamily()  = FontFamily(
    Font(Res.font.nunito_light, FontWeight.Light),
    Font(Res.font.nunito_extra_light, FontWeight.ExtraLight),
    Font(Res.font.nunito_regular, FontWeight.Normal),
    Font(Res.font.nunito_medium, FontWeight.Medium),
    Font(Res.font.nunito_bold, FontWeight.Bold),
    Font(Res.font.nunito_semi_bold, FontWeight.SemiBold),
    Font(Res.font.nunito_extra_bold, FontWeight.ExtraBold),
)

// 2. Modify findeFontSize to return an instance of FindeTextStyles
@Composable
fun findeFontSize(): ThreadTextStyles {
    val nunito = findeFontFamily()
    return ThreadTextStyles(
        headlineExtraLarge = TextStyle(
            fontFamily = nunito,
            fontWeight = FontWeight.Bold,
            fontSize = 56.sp,
            lineHeight = 78.sp
        ),
        headlineLarge = TextStyle(
            fontFamily = nunito,
            fontWeight = FontWeight.Bold,
            fontSize = 40.sp,
            lineHeight = 44.sp
        ),
        headlineMediumBold = TextStyle(
            fontFamily = nunito,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            lineHeight = 38.sp
        ),
        headlineMedium = TextStyle( // Note: This was identical to headlineLarge in your original code.
            fontFamily = nunito,    // You might want to differentiate it or remove if redundant.
            fontWeight = FontWeight.Bold,
            fontSize = 40.sp, // Original was 40.sp, same as headlineLarge
            lineHeight = 44.sp // Original was 44.sp, same as headlineLarge
        ),
        headlineSmall = TextStyle(
            fontFamily = nunito,
            fontWeight = FontWeight.Normal,
            fontSize = 26.sp,
            lineHeight = 32.sp
        ),
        titleLargeBold = TextStyle(
            fontFamily = nunito,
            fontWeight = FontWeight.Bold,
            fontSize = 23.sp,
            lineHeight = 36.sp
        ),
        titleLarge = TextStyle(
            fontFamily = nunito,
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp,
            lineHeight = 36.sp
        ),
        titleMediumBold = TextStyle(
            fontFamily = nunito,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            lineHeight = 26.sp
        ),
        titleMedium = TextStyle(
            fontFamily = nunito,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            lineHeight = 26.sp
        ),
        titleSmallBold = TextStyle(
            fontFamily = nunito,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            lineHeight = 24.sp
        ),
        titleSmall = TextStyle(
            fontFamily = nunito,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            lineHeight = 24.sp
        ),
        bodyLargeBold = TextStyle(
            fontFamily = nunito,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            lineHeight = 22.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = nunito,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            lineHeight = 22.sp
        ),
        bodyMediumBold = TextStyle(
            fontFamily = nunito,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            lineHeight = 20.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = nunito,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            lineHeight = 20.sp
        ),
        bodySmallBold = TextStyle(
            fontFamily = nunito,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            lineHeight = 18.sp
        ),
        bodySmall = TextStyle(
            fontFamily = nunito,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            lineHeight = 18.sp
        ),
        labelLarge = TextStyle(
            fontFamily = nunito,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            lineHeight = 18.sp
        ),
        labelLargeBold = TextStyle(
            fontFamily = nunito,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            lineHeight = 18.sp
        ),
        labelMedium = TextStyle(
            fontFamily = nunito,
            fontWeight = FontWeight.SemiBold,
            fontSize = 10.sp,
            lineHeight = 16.sp
        ),
        labelSmall = TextStyle(
            fontFamily = nunito,
            fontWeight = FontWeight.SemiBold,
            fontSize = 8.sp,
            lineHeight = 14.sp
        ),
        labelTextField = TextStyle(
            fontFamily = nunito,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            lineHeight = 18.sp
        ),
        placeholderTextField = TextStyle(
            fontFamily = nunito,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 24.sp
        ),
        placeholderTextFieldTransactionEmas = TextStyle(
            fontFamily = nunito,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            lineHeight = 26.sp
        ),
        valueTextField = TextStyle(
            color = FindeColor.FocusTextField,
            fontFamily = nunito,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 24.sp
        ),
        errorTextField = TextStyle(
            fontFamily = nunito,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 14.sp
        ),
        buttonText = TextStyle(
            fontFamily = nunito,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            lineHeight = 24.sp
        ),
        label12NormalNoLineHeight = TextStyle(
            fontFamily = nunito,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
        ),
        label12SemiBoldNoLineHeight = TextStyle(
            fontFamily = nunito,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
        ),
        label12SemiBold = TextStyle(
            fontFamily = nunito,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            lineHeight = 18.sp,
        ),
        label24Bold = TextStyle(
            fontFamily = nunito,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            lineHeight = 36.sp
        ),
        label20Bold = TextStyle(
            fontFamily = nunito,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            lineHeight = 36.sp
        )
    )
}
