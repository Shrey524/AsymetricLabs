package com.example.assigment1.screens.home

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Size // for Size (instead of IntSize)
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.example.assigment1.R


@Composable
fun HomeScreen(
    selectedCountry: String,
    countryList: List<String>,
    onCountrySelected: (String) -> Unit,
    onStartClicked: (Long) -> Unit,
    onBookmarksClicked: (Long) -> Unit
) {
    val welcomeMessage = stringResource(R.string.welcome_message)
    val testApp = stringResource(id = R.string.test_app)
    val selectCountryMessage = stringResource(id = R.string.select_country_message)
    val startTestButton = stringResource(id = R.string.start_test_button)
    val bookmarkedQuestionsButton = stringResource(id = R.string.bookmarked_questions_button)

    val timerOptions = listOf(30, 60, 90)
    var selectedTimer by remember { mutableStateOf(30) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // Header Text
                Text(
                    text = buildAnnotatedString {
                        append(welcomeMessage)
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(stringResource(R.string.app_name))
                        }
                        append("\n$testApp")
                    },
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Select Country Header
                Text(
                    text = selectCountryMessage,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Country Selector
                DropdownSelector(
                    label = stringResource(id = R.string.select_country_label),
                    selectedItem = selectedCountry,
                    itemList = countryList,
                    onItemSelected = onCountrySelected
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Timer Selector
                Text(
                    text = "Select Question Timer (seconds)",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(4.dp))

                DropdownSelector(
                    label = "Timer Duration",
                    selectedItem = selectedTimer,
                    itemList = timerOptions,
                    onItemSelected = { selectedTimer = it },
                    displayText = { "$it seconds" }
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Start Quiz Button
                Button(
                    onClick = { onStartClicked(selectedTimer * 1000L) }, // Timer value in milliseconds
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(startTestButton, color = Color.Black)
                }

                // Bookmarked Question Button
                Button(
                    onClick = { onBookmarksClicked(selectedTimer * 1000L) }, // Timer value in milliseconds
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(bookmarkedQuestionsButton, color = Color.Black)
                }
            }
        }
    }
}


@Composable
fun <T> DropdownSelector(
    label: String,
    selectedItem: T,
    itemList: List<T>,
    onItemSelected: (T) -> Unit,
    displayText: (T) -> String = { it.toString() }
) {
    var expanded by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    Column {
        OutlinedTextField(
            value = displayText(selectedItem),
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates -> textFieldSize = coordinates.size.toSize() },
            label = { Text(label, color = Color.White) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown",
                    tint = Color.White,
                    modifier = Modifier
                        .clickable { expanded = true }
                        .size(30.dp)
                )
            },
            colors = TextFieldDefaults.colors()
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current) { textFieldSize.width.toDp() })
                .background(Color.White)
        ) {
            itemList.forEach { item ->
                DropdownMenuItem(
                    text = { Text(displayText(item), color = Color.Black) },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}





