package fr.isen.combes.androidprojet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.isen.combes.androidprojet.ui.theme.AndroidProjetTheme

import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

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
            EditableNameField("John Doe")
            Spacer(modifier = Modifier.height(8.dp))
            EditableUserNameField("@JohnDoe")
            Spacer(modifier = Modifier.height(16.dp))
            EditableDesciptionField("Developer at XYZ Corp")
            Spacer(modifier = Modifier.height(16.dp))
            EditableEmailField("John.doejohndoe.john")
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
fun EditableNameField(initialName: String) {
    var name by remember { mutableStateOf(initialName) }

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
            value = name,
            onValueChange = { name = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        )
    }
}

@Composable
fun EditableUserNameField(initialUserName: String) {
    var userName by remember { mutableStateOf(initialUserName) }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Username",
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(modifier = Modifier.height(4.dp))
        TextField(
            value = userName,
            onValueChange = { userName = it },
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.bodyMedium,
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
fun EditableDesciptionField(initialDescription: String) {
    var description by remember { mutableStateOf(initialDescription) }


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
            value = description,
            onValueChange = { description = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        )
    }
}


@Composable
fun EditableEmailField(initialEmail: String) {
    var email by remember { mutableStateOf(initialEmail) }

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
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            isError = !email.contains("@"), // Check if email contains "@", set isError accordingly
            placeholder = {
                Text(
                    text = "Enter your email",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        )
        // Add error message if email does not contain "@"
        if (!email.contains("@")) {
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

@Preview(showBackground = true)
@Composable
fun ProfileEditPreview() {
    AndroidProjetTheme {
        ProfileEditScreen()
    }
}
