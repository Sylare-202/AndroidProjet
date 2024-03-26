package fr.isen.combes.androidprojet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.isen.combes.androidprojet.ui.theme.AndroidProjetTheme

import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase

class ProfileEditActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidProjetTheme {
                ProfileEditScreen()
            }
        }
    }
}

@Composable
fun ProfileEditScreen() {
    var name by remember { mutableStateOf("John Doe") }
    var userName by remember { mutableStateOf("@JohnDoe") }
    var description by remember { mutableStateOf("Developer at XYZ Corp") }
    var email by remember { mutableStateOf("John.doe@johndoe.john") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            ProfileEditHeader()
            Spacer(modifier = Modifier.height(16.dp))
            ProfilePicture()
            Spacer(modifier = Modifier.height(16.dp))
            EditableNameField(initialName = name) { name = it }
            Spacer(modifier = Modifier.height(8.dp))
            EditableUserNameField(initialUserName = userName) { userName = it }
            Spacer(modifier = Modifier.height(16.dp))
            EditableDesciptionField(initialDescription = description) { description = it }
            Spacer(modifier = Modifier.height(16.dp))
            EditableEmailField(initialEmail = email) { email = it }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    println("Name: $name")
                    println("Username: $userName")
                    println("Description: $description")
                    println("Email: $email")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Save Changes")
            }
        }
    }
}


@Composable
fun ProfilePicture() {
    Text(
        text = "Profile Picture",
        style = MaterialTheme.typography.bodySmall
    )
    ProfileHeader(size = 200)
}

@Composable
fun EditableNameField(initialName: String, onNameChange: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Name",
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(modifier = Modifier.height(4.dp))
        TextField(
            value = initialName,
            onValueChange = { onNameChange(it) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = {
                Text(
                    text = "Enter your name",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        )
    }
}


@Composable
fun EditableUserNameField(initialUserName: String, onUserNameChange: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Username",
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(modifier = Modifier.height(4.dp))
        TextField(
            value = initialUserName,
            onValueChange = { onUserNameChange(it) },
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.bodyMedium,
            singleLine = true,
            placeholder = {
                Text(
                    text = "Enter your username",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        )
    }
}


@Composable
fun EditableDesciptionField(initialDescription: String, onDescriptionChange: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Description",
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(modifier = Modifier.height(4.dp))
        TextField(
            value = initialDescription,
            onValueChange = { onDescriptionChange(it) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = {
                Text(
                    text = initialDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        )
    }
}


@Composable
fun EditableEmailField(initialEmail: String, onEmailChange: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Email",
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(modifier = Modifier.height(4.dp))
        TextField(
            value = initialEmail,
            onValueChange = { onEmailChange(it) },
            modifier = Modifier.fillMaxWidth(),
            isError = !initialEmail.contains("@"),
            singleLine = true,
            placeholder = {
                Text(
                    text = "Enter your email",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        )
        if (!initialEmail.contains("@")) {
            Text(
                text = "Email isnot valid.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}


@Composable
fun ProfileEditHeader() {
    Text(
        text = "Edit Profile",
        style = MaterialTheme.typography.headlineMedium
    )
}
