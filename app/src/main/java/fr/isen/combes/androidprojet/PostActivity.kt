package fr.isen.combes.androidprojet

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import fr.isen.combes.androidprojet.ui.theme.AndroidProjetTheme
import java.util.Locale

class PostActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidProjetTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PostPage()
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PostPage() {
    val context = LocalContext.current
    val description = remember { mutableStateOf("") }
    val lieu = remember { mutableStateOf("") }
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
                    text = "Publier une photo".toUpperCase(Locale.ROOT),
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
                value = lieu.value,
                onValueChange = { lieu.value = it },
                label = { Text("lieu") },
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
                        text = AnnotatedString("Valider").toUpperCase(),
                        onClick = {
                            uploadPost(
                                lieu.value,
                                description.value,
                                imageUri,
                                context
                            )
                            val indent = Intent(context, MainActivity::class.java)
                            context.startActivity(indent)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        style = TextStyle(textAlign = TextAlign.Center, color = Color.White, fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

fun uploadProfilePicture(uri: Uri, context: Context, onComplete: (String) -> Unit) {
    val fileName = "postPicture/${Firebase.auth.currentUser?.uid}_${System.currentTimeMillis()}.jpg"
    val storageReference = Firebase.storage.reference.child(fileName)
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

fun uploadPost(
    lieu: String,
    description: String,
    imageUri: Uri?,
    current: Context
) {
    if(imageUri != null){
        uploadProfilePicture(imageUri, current) { image ->
            savePostData(lieu, description, image)
            showToast(current,"Image Upload !")
            (current as? Activity)?.finish()
        }
    }else{
        savePostData(lieu, description, "")
        showToast(current,"Aucune Image Upload")
        (current as? Activity)?.finish()
    }
}

fun savePostData(lieu: String, description: String, image: String) {
    val database = Firebase.database
    val myRef = database.getReference("Post").push()

    val postMap = hashMapOf(
        "uid" to Firebase.auth.currentUser?.uid,
        "lieu" to lieu,
        "description" to description,
        "image" to image,
        "date" to android.icu.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(java.util.Date()),
        "like" to 0
    )

    myRef.setValue(postMap).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            Log.d("FirebaseDatabase", "Post data saved successfully")
        } else {
            Log.w("FirebaseDatabase", "Error saving user data", task.exception)
        }
    }
}