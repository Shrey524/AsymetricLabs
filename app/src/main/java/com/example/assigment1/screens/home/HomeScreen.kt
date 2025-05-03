package com.example.assigment1.screens.home

import android.content.Context
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
import androidx.navigation.NavController
import com.example.assigment1.R


@Composable
fun HomeScreen(
    selectedCountry: String,
    countryList: List<String>,
    onCountrySelected: (String) -> Unit,
    onStartClicked: () -> Unit,
    onBookmarksClicked: () -> Unit,
) {

    //Strings
    val welcomeMessage = stringResource(R.string.welcome_message)
    val testApp = stringResource(id = R.string.test_app)
    val selectCountryMessage = stringResource(id = R.string.select_country_message)
    val startTestButton = stringResource(id = R.string.start_test_button)
    val bookmarkedQuestionsButton = stringResource(id = R.string.bookmarked_questions_button)

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

                // Country Selector DropDown with HHardcoded list couldn't find API with just a list of countries
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    CountrySelector(
                        selectedCountry = selectedCountry,
                        countryList = countryList,
                        onCountrySelected = onCountrySelected,
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Start Quiz Button
                Button(
                    onClick = onStartClicked,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(startTestButton, color = Color.Black)
                }

                // Bookmarked Question Button
                Button(
                    onClick = onBookmarksClicked,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = bookmarkedQuestionsButton,
                        color = Color.Black
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
    onCountrySelected: (String) -> Unit,
) {
    val selectCountryLabel = stringResource(id = R.string.select_country_label)
    val dropdownContentDescription = stringResource(id = R.string.dropdown_content_description)

    var expanded by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    Column {
        OutlinedTextField(
            value = selectedCountry,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates -> textFieldSize = coordinates.size.toSize() },
            label = { Text(selectCountryLabel, color = Color.White) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = dropdownContentDescription,
                    tint = Color.White,
                    modifier = Modifier.clickable { expanded = true }
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


