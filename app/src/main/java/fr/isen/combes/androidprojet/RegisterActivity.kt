package fr.isen.combes.androidprojet

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import at.favre.lib.crypto.bcrypt.BCrypt
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import fr.isen.combes.androidprojet.ui.theme.AndroidProjetTheme

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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RegisterPage() {
    val context = LocalContext.current
    val firstname = remember { mutableStateOf("") }
    val lastname = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val username = remember { mutableStateOf("") }
    //val pp = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {

        TextField(
            value = firstname.value,
            onValueChange = { firstname.value = it },
            label = { Text("First Name") }
        )
        TextField(
            value = lastname.value,
            onValueChange = { lastname.value = it },
            label = { Text("Last Name") }
        )
        TextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text("Email") }
        )
        TextField(
            value = username.value,
            onValueChange = { username.value = it },
            label = { Text("Username") }
        )
        TextField(
            value = description.value,
            onValueChange = { description.value = it },
            label = { Text("Description") }
        )
        TextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text("Password") }
        )

        Button(onClick = {
            registerUser(
                firstname.value,
                lastname.value,
                email.value,
                username.value,
                description.value,
                password.value,
                context
            )
        }) {
            Text("Register")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            (context as? Activity)?.finish()
        }) {
            Text("Login")
        }
    }
}

fun registerUser(
    firstname: String,
    lastname: String,
    email: String,
    username: String,
    description: String,
    password: String,
    current: Context
) {
    if (firstname.isEmpty() || lastname.isEmpty() || email.isEmpty() || username.isEmpty() || description.isEmpty() || password.isEmpty()) {
        Log.w("RegisterActivity", "One or more fields are empty")
        showToast(current,"Please complete all")
        return
    }

    val auth = Firebase.auth

    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            // Registration successful
            val user = auth.currentUser
            user?.let {
                val userId = it.uid
                //Hash & Salt the password
                val hashsaltpassword = hashSaltPassword(password)

                saveUserData(userId, firstname, lastname, email, username, description, hashsaltpassword)
                showToast(current,"Registration successful")
            }
        } else {
            // Registration failed
            when (task.exception) {
                is FirebaseAuthWeakPasswordException -> showToast(current, "Password too weak. Please use a stronger password.")
                is FirebaseAuthUserCollisionException -> showToast( current, "Email already in use. Please use a different email.")
                else -> showToast(current, "Registration failed: ${task.exception?.localizedMessage}")
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

fun saveUserData(userId: String, firstname: String, lastname: String, email: String, username: String, description: String, hashsaltpassword: String) {
    val database = Firebase.database
    val myRef = database.getReference("Users").child(userId)

    val userMap = hashMapOf(
        "firstname" to firstname,
        "lastname" to lastname,
        "email" to email,
        "username" to username,
        "description" to description,
        "password" to hashsaltpassword
    )

    myRef.setValue(userMap).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            Log.d("FirebaseDatabase", "User data saved successfully")
            // TODO: Inform the user of success
        } else {
            Log.w("FirebaseDatabase", "Error saving user data", task.exception)
            // TODO: Inform the user of failure
        }
    }
}

