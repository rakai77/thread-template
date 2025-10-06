package com.example.thread.thread.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun PlainTextField(
    modifier: Modifier = Modifier,
    value: String = "",
    label: String = "",
    placeholder: String = "",
    keyboardOption: KeyboardOptions = KeyboardOptions(),
    onValueChange: (String) -> Unit
) {
    BaseTextFiled(
        modifier = modifier,
        value = value,
        label = label,
        placeholder = placeholder,
        keyboardOption = keyboardOption,
        onValueChange = onValueChange
    )
}

@Composable
fun TrailingTextField(
    modifier: Modifier = Modifier,
    value: String = "",
    label: String = "",
    placeholder: String = "",
    trailingIcon: (@Composable () -> Unit)? = null,
    keyboardOption: KeyboardOptions = KeyboardOptions(),
    onValueChange: (String) -> Unit
) {
    BaseTextFiled(
        modifier = modifier,
        value = value,
        label = label,
        placeholder = placeholder,
        trailingIcon = trailingIcon,
        keyboardOption = keyboardOption,
        onValueChange = onValueChange
    )
}

@Composable
fun PasswordTextField(
    modifier: Modifier = Modifier,
    value: String = "",
    label: String = "",
    placeholder: String = "",
    onValueChange: (String) -> Unit
) {
    var shouldShowPassword by remember {
        mutableStateOf(false)
    }

    BaseTextFiled(
        modifier = modifier,
        value = value,
        label = label,
        placeholder = placeholder,
        trailingIcon = {
            Icon(
                modifier = Modifier.clickable {
                    shouldShowPassword = !shouldShowPassword
                },
                imageVector = if (shouldShowPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                contentDescription = ""
            )
        },
        keyboardOption = KeyboardOptions(keyboardType = KeyboardType.Password) ,
        visualTransformation = if (shouldShowPassword) VisualTransformation.None else PasswordVisualTransformation(),
        onValueChange = onValueChange
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseTextFiled(
    modifier: Modifier = Modifier,
    value: String = "",
    trailingIcon: (@Composable () -> Unit)? = null,
    label: String = "",
    placeholder: String = "",
    keyboardOption: KeyboardOptions = KeyboardOptions(),
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onValueChange: (String) -> Unit
) {
    Column(modifier = modifier) {
        Text(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp),
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = Black
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            value = value,
            onValueChange = onValueChange,
            colors = TextFieldDefaults.colors(
//                textColor = Black,
//                unfocusedBorderColor = Border,
//                focusedBorderColor = Primary,
//                placeholderColor = Label,
//                unfocusedTrailingIconColor = Icon,
//                focusedTrailingIconColor = Primary
            ),
            shape = RoundedCornerShape(15.dp),
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            trailingIcon = {
                trailingIcon?.invoke()
            },
            keyboardOptions = keyboardOption,
            visualTransformation = visualTransformation,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPlainTextField() {
    PlainTextField(
        value = "",
        label = "Fullname",
        placeholder = "Enter fullname",
        onValueChange = {}
    )
}


@Preview(showBackground = true)
@Composable
fun PreviewTrailingTextField() {
    TrailingTextField(
        value = "",
        label = "Email",
        placeholder = "Enter email",
        trailingIcon = {
            Icon(imageVector = Icons.Default.Email, contentDescription = "")
        },
        onValueChange = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewPasswordTextField() {
    PasswordTextField(
        value = "",
        label = "Password",
        placeholder = "Enter password",
        onValueChange = {}
    )
}