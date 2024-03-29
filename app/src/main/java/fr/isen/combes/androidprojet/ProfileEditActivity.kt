package fr.isen.combes.androidprojet


import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import coil.compose.rememberImagePainter
import com.google.firebase.storage.storage

class ProfileEditActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userFirstName = intent.getStringExtra("userFirstName")
        val userLastName = intent.getStringExtra("userLastName")
        val userUsername = intent.getStringExtra("userUsername")
        val userDescription = intent.getStringExtra("userDescription")
        val userEmail = intent.getStringExtra("userEmail")
        val imageUri = intent.getStringExtra("imageUri")
        Log.e("ProfileEditActivity", "OLD Image Uri undated: $imageUri")

        setContent {
            AndroidProjetTheme {
                ProfileEditScreen(
                    this@ProfileEditActivity,
                    userFirstName,
                    userLastName,
                    userUsername,
                    userDescription,
                    userEmail,
                    imageUri
                )
            }
        }
    }
}

@Composable
fun ProfileEditScreen(
    activity: ComponentActivity,
    userFirstName: String?,
    userLastName: String?,
    userUsername: String?,
    userDescription: String?,
    userEmail: String?,
    imageUrl: String?
) {
    var firstName by remember { mutableStateOf(userFirstName) }
    var lastName by remember { mutableStateOf(userLastName) }
    var userName by remember { mutableStateOf(userUsername) }
    var description by remember { mutableStateOf(userDescription) }
    var email by remember { mutableStateOf(userEmail) }

    var imageUri by remember { mutableStateOf(imageUrl) }

    val oldImageUri = imageUrl.toString()
    println("oldImageUri: $oldImageUri")

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                ProfileEditHeader()
                Spacer(modifier = Modifier.height(16.dp))
                ProfilePicture(
                    imageUri = imageUri
                ) { uri ->
                    imageUri = uri.toString()
                }

                Spacer(modifier = Modifier.height(16.dp))
                firstName?.let { EditableFirstNameField(initialName = it) { firstName = it } }
                Spacer(modifier = Modifier.height(8.dp))
                lastName?.let { EditableLastNameField(initialName = it) { lastName = it } }
                Spacer(modifier = Modifier.height(8.dp))
                userName?.let { EditableUserNameField(initialUserName = it) { userName = it } }
                Spacer(modifier = Modifier.height(16.dp))
                description?.let {
                    EditableDesciptionField(initialDescription = it) {
                        description = it
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                email?.let { EditableEmailField(initialEmail = it) { email = it } }
                Spacer(modifier = Modifier.height(16.dp))

                ProfileButton(
                    activity = activity,
                    text = "Save Changes",
                    firstName = firstName,
                    lastName = lastName,
                    userName = userName,
                    description = description,
                    email = email,
                    imageUri = imageUri,
                    oldImageUri = oldImageUri
                )
            }
        }
    }
}

@Composable
fun ProfileButton(
    activity: ComponentActivity,
    text: String,
    firstName: String?,
    lastName: String?,
    userName: String?,
    description: String?,
    email: String?,
    imageUri: String?,
    oldImageUri: String?
) {
    Box(
        modifier = Modifier
            .padding(bottom = 10.dp)
            .background(
                color = Color(0xFF00C974),
                shape = MaterialTheme.shapes.extraLarge
            )
    ) {
        ClickableText(
            text = AnnotatedString(text).toUpperCase(),
            onClick = {

                if (imageUri != oldImageUri) {
                    updateUserInFirebase(
                        userId = Firebase.auth.currentUser?.uid ?: "",
                        firstName = firstName ?: "",
                        lastName = lastName ?: "",
                        username = userName ?: "",
                        description = description ?: "",
                        email = email ?: "",
                        imageUri = imageUri?.let { Uri.parse(it) }
                    ) {
                        activity.finish()
                    }
                } else {
                    updateUserInFirebase(
                        userId = Firebase.auth.currentUser?.uid ?: "",
                        firstName = firstName ?: "",
                        lastName = lastName ?: "",
                        username = userName ?: "",
                        description = description ?: "",
                        email = email ?: "",
                        imageUri = null
                    ) {
                        activity.finish()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            style = TextStyle(
                textAlign = TextAlign.Center,
                color = Color.White
            )
        )
    }
}

@Composable
fun ProfilePicture(
    imageUri: String?,
    onImageSelected: (Uri) -> Unit
) {
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { onImageSelected(it) }
        }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Profile Picture",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF00C974)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(color = Color(0x4000C974), shape = MaterialTheme.shapes.extraLarge)
                .clickable {
                    launcher.launch("image/*")
                },
            contentAlignment = Alignment.Center
        ) {
            println("imageUri: $imageUri")
            if (imageUri != null) {
                Image(
                    painter = rememberImagePainter(imageUri),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(180.dp)
                        .clip(MaterialTheme.shapes.extraLarge),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Profile Picture",
                    modifier = Modifier.size(100.dp)
                )
            }
        }
    }
}


fun updateUserInFirebase(
    userId: String,
    firstName: String,
    lastName: String,
    username: String,
    description: String,
    email: String,
    imageUri: Uri?,
    completion: () -> Unit
) {
    val database: DatabaseReference = Firebase.database.reference
    val userRef = database.child("Users").child(userId)

    if (imageUri != null) {
        uploadProfilePicture(imageUri, userId) { profilePictureUrl ->
            val userData = mapOf(
                "profilePicture" to profilePictureUrl,
                "firstname" to firstName,
                "lastname" to lastName,
                "username" to username,
                "description" to description,
                "email" to email
            )

            userRef.updateChildren(userData)
                .addOnSuccessListener {
                    println("User data updated successfully")
                    completion()
                }
                .addOnFailureListener { e ->
                    println("Error updating user data: $e")
                }
            Log.e("ProfileEditActivity", "Image URI UPDATED: $profilePictureUrl")
        }
    } else {
        val userData = mapOf(
            "firstname" to firstName,
            "lastname" to lastName,
            "username" to username,
            "description" to description,
            "email" to email
        )

        userRef.updateChildren(userData)
            .addOnSuccessListener {
                println("User data updated successfully")
                completion()
            }
            .addOnFailureListener { e ->
                println("Error updating user data: $e")
            }
    }
}

fun uploadProfilePicture(uri: Uri, userId: String, onComplete: (String) -> Unit) {
    val storageReference = Firebase.storage.reference.child("profilePictures/$userId.jpg")
    storageReference.putFile(uri)
        .addOnSuccessListener {
            storageReference.downloadUrl.addOnSuccessListener { uri ->
                onComplete(uri.toString())
            }
        }
        .addOnFailureListener {
            println("Error uploading profile picture: $it")
        }
}


@Composable
fun EditableFirstNameField(initialName: String, onNameChange: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "First Name",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF00C974)
        )
        Spacer(modifier = Modifier.height(4.dp))
        TextField(
            value = initialName,
            onValueChange = { onNameChange(it) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = {
                Text(
                    text = "Enter your first name",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        )
    }
}

@Composable
fun EditableLastNameField(initialName: String, onNameChange: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Last Name",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF00C974)
        )
        Spacer(modifier = Modifier.height(4.dp))
        TextField(
            value = initialName,
            onValueChange = { onNameChange(it) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = {
                Text(
                    text = "Enter your last name",
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
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF00C974)
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
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF00C974)
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
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF00C974)
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
