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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavController

@Composable
fun HomeScreen(
    selectedCountry: String,
    countryList: List<String>,
    onCountrySelected: (String) -> Unit,
    onStartClicked: () -> Unit,
    onBookmarksClicked: () -> Unit
) {

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
                Text(
                    text = buildAnnotatedString {
                        append("Welcome to\n")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("AsymmetricLabs")
                        }
                        append("\nTest App")
                    },
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Please select your country",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White // White text
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    CountrySelector(
                        selectedCountry = selectedCountry,
                        countryList = countryList,
                        onCountrySelected = onCountrySelected
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onStartClicked,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White // Button background color
                    ),
                    shape = RoundedCornerShape(4.dp) // Rounded corners
                ) {
                    Text("Start Test", color = Color.Black) // Set text color separately
                }

                Button(
                    onClick = onBookmarksClicked,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White // Button background color
                    ),
                    shape = RoundedCornerShape(4.dp) // Rounded corners
                ) {
                    Text(
                        text = "Bookmarked Questions",
                        color = Color.Black // Button text color
                    )
                }
            }
        }
    }
}

@Composable
fun CountrySelector(
    selectedCountry: String,
    countryList: List<String>,
    onCountrySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }

        Column(
        ) {
            OutlinedTextField(
                value = selectedCountry,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        textFieldSize = coordinates.size.toSize()
                    },
                label = { Text("Select Country", color = Color.White) },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown",
                        tint = Color.White,
                        modifier = Modifier.clickable { expanded = true }
                            .size(30.dp)
                    )
                },
                colors = TextFieldDefaults.colors(

                )
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .width(with(LocalDensity.current) { textFieldSize.width.toDp() })
                    .background(Color.White)
                    .align(Alignment.Start)
            ) {
                countryList.forEach { country ->
                    DropdownMenuItem(
                        text = { Text(country, color = Color.Black) },
                        onClick = {
                            onCountrySelected(country)
                            expanded = false
                        }
                    )
                }
            }
        }

}

