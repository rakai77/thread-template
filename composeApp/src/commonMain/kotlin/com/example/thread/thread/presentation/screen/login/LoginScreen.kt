package com.example.thread.thread.presentation.screen.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.thread.thread.presentation.component.PasswordTextField
import com.example.thread.thread.presentation.component.TrailingTextField
import com.example.thread.thread.presentation.navigation.AppRoute
import com.example.thread.thread.presentation.theme.Black
import com.example.thread.thread.presentation.theme.Primary
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun LoginScreen(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isButtonEnable by remember { mutableStateOf(false) }
    val registerText = "Belum punya akun? Daftar"
    val notHaveAccount = "Belum punya akun? Daftar"
    val registerString = buildAnnotatedString {
        withStyle(style = SpanStyle(color = Black)) {
            pushStringAnnotation(tag = notHaveAccount, annotation = notHaveAccount)
            append(notHaveAccount)
        }
        withStyle(style = SpanStyle(color = Primary, fontWeight = FontWeight.SemiBold)) {
            pushStringAnnotation(tag = registerText, annotation = registerText)
            append(registerText)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.systemBars.asPaddingValues())
            .verticalScroll(rememberScrollState())
    ) {
//        TextHeader(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(top = 44.dp)
//                .padding(horizontal = 24.dp),
//            headerText = stringResource(R.string.login_title),
//            supportText = stringResource(R.string.login_description)
//        )
        TrailingTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 36.dp)
                .padding(horizontal = 24.dp),
            value = email,
            label = "Email",
            placeholder = "Masukkan email anda",
            trailingIcon = {
                Icon(imageVector = Icons.Default.Email, contentDescription = "icon email")
            },
            keyboardOption = KeyboardOptions(keyboardType = KeyboardType.Email),
            onValueChange = {
                email = it
            }
        )
        PasswordTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
                .padding(horizontal = 24.dp),
            value = password,
            label = "Password",
            placeholder = "Masukkan password anda",
            onValueChange = {
                password = it
            }
        )
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 60.dp),
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.White,
                containerColor = Primary
            ),
            contentPadding = PaddingValues(vertical = 16.dp),
            enabled = isButtonEnable,
            onClick = {
//                viewModel.login(email, password)
//                keyboardController?.hide()
            }
        ) {
            Text(
                text = "Login",
                style = MaterialTheme.typography.labelMedium
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        ClickableText(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 30.dp, top = 70.dp)
            ,
            style = MaterialTheme.typography.bodyMedium.copy(
                textAlign = TextAlign.Center
            ),
            text = registerString,
            onClick = { offset ->
                registerString.getStringAnnotations(offset, offset)
                    .firstOrNull()?.let { span ->
                        if (span.item == registerText) {
                            navController.navigate(AppRoute.HOME_SCREEN)
                        }
                    }
            }
        )
    }
}