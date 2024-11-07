package com.example.battlelog.view

// Additional imports needed for keyboard actions
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SearchBar(
    modifier: Modifier,
    placeholder: String,
    value: String,
    onChange: (String) -> Unit,
    onEnterPress: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Row(
        modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            modifier = modifier
                .padding(horizontal = 10.dp)
                .fillMaxWidth(if (value.isEmpty()) 1f else 0.75f),
            shape = CircleShape,
            value = value,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null
                )
            },
            onValueChange = onChange,
            maxLines = 1,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    onEnterPress()
                }
            ),
            placeholder = { Text(text = placeholder) },
        )
        if (value.isNotEmpty()) {
            Text(
                text = "Cancel",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier
                    .clickable { onChange("") }
                    .padding(top = 8.dp, end = 20.dp)
            )
        }
    }
}