package fr.isen.combes.androidprojet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import fr.isen.combes.androidprojet.ui.theme.AndroidProjetTheme

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidProjetTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    LoginPage()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPage() {
    val context = LocalContext.current
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Box(
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
            Image(
                painter = painterResource(id = R.drawable.icon_fond),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(170.dp)
                    .padding(bottom = 10.dp)
            )
            Text(
                text = "Cass'Tongram",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.weed))
                ),
                modifier = Modifier.padding(bottom = 20.dp),
                color = Color.White
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
                value = password.value,
                onValueChange = { password.value = it },
                label = { Text("Mot de passe") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        loginUser(email.value, password.value, context)
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 30.dp)
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
                ),
            )
            Column(
                modifier = Modifier,
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
                        text = AnnotatedString("Se Connecter").toUpperCase(),
                        onClick = {
                            loginUser(email.value, password.value, context)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        style = TextStyle(textAlign = TextAlign.Center, color = Color.White, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
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
                        text = AnnotatedString("Créer un compte"),
                        onClick = {
                            val intent = Intent(context, RegisterActivity::class.java)
                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        style = TextStyle(textAlign = TextAlign.Center, color = Color(0xFF00C974), fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    )
                }
            }
            Text(
                text = "Design by\nCombes / Sayer / Bonnefon / De Sauvage & Daoulas",
                modifier = Modifier.padding(top = 20.dp),
                style = MaterialTheme.typography.bodySmall.copy(
                    textAlign = TextAlign.Center,
                ),
                color = Color.White
            )
        }
    }
}

fun loginUser(email: String, password: String, context: Context) {
    if (email.isEmpty() || password.isEmpty()) {
        Toast.makeText(context, "Merci de tout remplir !", Toast.LENGTH_SHORT).show()
        return
    }

    val auth = Firebase.auth
    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            Toast.makeText(context, "Vous êtes maintenant connecté !", Toast.LENGTH_SHORT).show()
            // TODO: Redirect to HomeActivity
        } else {
            Toast.makeText(context, "Erreur lors de la connexion : ${task.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AndroidProjetTheme {
        LoginPage()
    }
}