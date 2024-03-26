package fr.isen.combes.androidprojet

import android.app.Activity
import android.content.Context
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import at.favre.lib.crypto.bcrypt.BCrypt
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import fr.isen.combes.androidprojet.ui.theme.AndroidProjetTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.sp
import java.util.Locale

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidProjetTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RegisterPage()
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RegisterPage() {
    val context = LocalContext.current
    val firstname = remember { mutableStateOf("") }
    val lastname = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val username = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    var imageUri: Uri? by remember { mutableStateOf(null) }
    var imageName: String by remember { mutableStateOf("Aucune photo sélectionnée") }

    val launcher = rememberLauncherForActivityResult(GetContent()) { uri: Uri? ->
        imageUri = uri
        imageName = uri?.lastPathSegment ?: "Image sélectionnée"
    }

    Box (
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg2),
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )
        Column(
            modifier = Modifier
                .width(300.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
                    .background(color = Color(color = 0xFFD9E0E6), shape = MaterialTheme.shapes.large)
                    .border(width = 2.dp, color = Color(0xFF00C974), shape = MaterialTheme.shapes.large),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "Photo de profil".toUpperCase(Locale.ROOT),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    modifier = Modifier.padding(10.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { launcher.launch("image/*") },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(text = "Ajouter")
                    }
                    Text(
                        text = imageName,
                        style = TextStyle(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    if (imageUri != null) {
                        IconButton(
                            onClick = {
                                imageUri = null
                                imageName = "Aucune photo sélectionnée"
                            },
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                        }
                    }
                }
            }
            TextField(
                value = firstname.value,
                onValueChange = { firstname.value = it },
                label = { Text("Prénom") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .border(
                        width = 1.dp,
                        color = androidx.compose.ui.graphics.Color(0xFF00C974),
                        shape = MaterialTheme.shapes.extraLarge
                    ),
                shape = MaterialTheme.shapes.extraLarge,
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                    unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                    disabledIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
                )
            )
            TextField(
                value = lastname.value,
                onValueChange = { lastname.value = it },
                label = { Text("Nom") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .border(
                        width = 1.dp,
                        color = androidx.compose.ui.graphics.Color(0xFF00C974),
                        shape = MaterialTheme.shapes.extraLarge
                    ),
                shape = MaterialTheme.shapes.extraLarge,
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                    unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                    disabledIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
                )
            )
            TextField(
                value = email.value,
                onValueChange = { email.value = it },
                label = { Text("Adresse Mail") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .border(
                        width = 1.dp,
                        color = androidx.compose.ui.graphics.Color(0xFF00C974),
                        shape = MaterialTheme.shapes.extraLarge
                    ),
                shape = MaterialTheme.shapes.extraLarge,
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                    unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                    disabledIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
                )
            )
            TextField(
                value = username.value,
                onValueChange = { newValue ->
                    if (!newValue.contains("@")) {
                        username.value = newValue
                    }
                },
                label = { Text("Pseudo") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .border(
                        width = 1.dp,
                        color = androidx.compose.ui.graphics.Color(0xFF00C974),
                        shape = MaterialTheme.shapes.extraLarge
                    ),
                shape = MaterialTheme.shapes.extraLarge,
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                    unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                    disabledIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
                )
            )
            TextField(
                value = description.value,
                onValueChange = { description.value = it },
                label = { Text("Description") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .border(
                        width = 1.dp,
                        color = androidx.compose.ui.graphics.Color(0xFF00C974),
                        shape = MaterialTheme.shapes.extraLarge
                    ),
                shape = MaterialTheme.shapes.extraLarge,
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                    unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                    disabledIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
                )
            )
            TextField(
                value = password.value,
                onValueChange = { password.value = it },
                label = { Text("Mot de passe") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        registerUser(
                            firstname.value,
                            lastname.value,
                            email.value,
                            username.value,
                            description.value,
                            password.value,
                            imageUri,
                            context
                        )
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .border(
                        width = 1.dp,
                        color = androidx.compose.ui.graphics.Color(0xFF00C974),
                        shape = MaterialTheme.shapes.extraLarge
                    ),
                shape = MaterialTheme.shapes.extraLarge,
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                    unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                    disabledIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
                )
            )

            Column(
                modifier = Modifier.padding(top = 30.dp),
            ){
                Box(
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .background(
                            color = Color(0xFF00C974),
                            shape = MaterialTheme.shapes.extraLarge
                        )
                ) {
                    ClickableText(
                        text = AnnotatedString("S'inscrire").toUpperCase(),
                        onClick = {
                            registerUser(
                                firstname.value,
                                lastname.value,
                                email.value,
                                username.value,
                                description.value,
                                password.value,
                                imageUri,
                                context
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        style = TextStyle(textAlign = TextAlign.Center, color = Color.White, fontWeight = FontWeight.Bold)
                    )
                }
                Box(
                    modifier = Modifier
                        .background(color = Color.White, shape = MaterialTheme.shapes.extraLarge)
                        .border(
                            width = 2.dp,
                            color = Color(0xFF00C974),
                            shape = MaterialTheme.shapes.extraLarge
                        )
                ) {
                    ClickableText(
                        text = AnnotatedString("Se Connecter"),
                        onClick = {
                            (context as? Activity)?.finish()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        style = TextStyle(textAlign = TextAlign.Center, color = Color(0xFF00C974), fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

fun uploadProfilePicture(uri: Uri, userId: String, context: Context, onComplete: (String) -> Unit) {
    val storageReference = Firebase.storage.reference.child("profilePictures/$userId.jpg")
    storageReference.putFile(uri)
        .addOnSuccessListener {
            storageReference.downloadUrl.addOnSuccessListener { uri ->
                onComplete(uri.toString())
            }
        }
        .addOnFailureListener {
            showToast(context, "Erreur lors du téléchargement de la photo de profil")
        }
}

fun registerUser(
    firstname: String,
    lastname: String,
    email: String,
    username: String,
    description: String,
    password: String,
    imageUri: Uri?,
    current: Context
) {
    if (firstname.isEmpty() || lastname.isEmpty() || email.isEmpty() || username.isEmpty() || description.isEmpty() || password.isEmpty()) {
        Log.w("RegisterActivity", "One or more fields are empty")
        showToast(current,"Merci de tout remplir !")
        return
    }

    val auth = Firebase.auth

    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val user = auth.currentUser
            user?.let {
                val userId = it.uid
                val hashsaltpassword = hashSaltPassword(password)

                if(imageUri != null){
                    uploadProfilePicture(imageUri, userId, current) { pp ->
                        saveUserData(userId, firstname, lastname, email, username, description, hashsaltpassword, pp)
                        showToast(current,"Vous êtes maintenant inscrit !")
                        (current as? Activity)?.finish()
                    }
                }else{
                    saveUserData(userId, firstname, lastname, email, username, description, hashsaltpassword, "")
                    showToast(current,"Vous êtes maintenant inscrit !")
                    (current as? Activity)?.finish()
                }
            }
        } else {
            when (task.exception) {
                is FirebaseAuthWeakPasswordException -> showToast(current, "Mot de passe trop faible. Veuillez utiliser un mot de passe plus fort.")
                is FirebaseAuthUserCollisionException -> showToast( current, "Adresse email déjà utilisée.")
                else -> showToast(current, "Erreur lors de l'inscription : ${task.exception?.localizedMessage}")
            }
        }
    }
}

fun showToast(context: Context, message: String) {
    Toast.makeText(
        context,
        message,
        Toast.LENGTH_SHORT
    ).show()
}

fun hashSaltPassword(password: String): String {
    return BCrypt.withDefaults().hashToString(12, password.toCharArray())
}

fun saveUserData(userId: String, firstname: String, lastname: String, email: String, username: String, description: String, hashsaltpassword: String, pp: String) {
    val database = Firebase.database
    val myRef = database.getReference("Users").child(userId)

    val userMap = hashMapOf(
        "firstname" to firstname,
        "lastname" to lastname,
        "email" to email,
        "username" to username,
        "description" to description,
        "password" to hashsaltpassword,
        "profilePicture" to pp
    )

    myRef.setValue(userMap).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            Log.d("FirebaseDatabase", "User data saved successfully")
        } else {
            Log.w("FirebaseDatabase", "Error saving user data", task.exception)
        }
    }
}

