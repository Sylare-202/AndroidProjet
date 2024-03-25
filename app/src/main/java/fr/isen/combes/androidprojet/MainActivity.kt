package fr.isen.combes.androidprojet

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.isen.combes.androidprojet.ui.theme.AndroidProjetTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Post(
    val title: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val publicationDate: Long = 0L
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle("DroidRestaurant")
        setContent {
            AndroidProjetTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(onCommentClick = { showToast("Commentaires") })
                }
            }
        }
    }

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun MainScreen(onCommentClick: () -> Unit) {
    Scaffold(
        topBar = { MyAppTopBar() },
        bottomBar = { MyBottomAppBar() }
    ) { innerPadding ->
        PostsList(posts = samplePosts(), onCommentClick = onCommentClick, modifier = Modifier.padding(innerPadding))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAppTopBar() {
    TopAppBar(
        title = {
            val customFont = FontFamily(Font(R.font.weed, FontWeight.Normal))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Remplacez `drawable.logo` par votre ressource de logo réelle
                Image(
                    painter = painterResource(id = R.drawable.marijuana),
                    contentDescription = "Logo",
                    modifier = Modifier.size(40.dp) // Taille du logo
                )
                Spacer(modifier = Modifier.width(8.dp)) // Espacement entre le logo et le titre
                Text(
                    text = "Cass'tongram",
                    style = MaterialTheme.typography.titleLarge.copy(fontFamily = customFont)
                )
            }
        },
    )
}

@Composable
fun MyBottomAppBar() {
    val backgroundColor = Color(0xFFF7F7F7)

    BottomAppBar(
        containerColor = backgroundColor,
        modifier = Modifier.height(56.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp), // Ajuste le padding horizontal pour centrer les icônes si nécessaire
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icône Home
            IconButton(onClick = { /* Handle home icon click */ }) {
                Icon(
                    Icons.Filled.Home,
                    contentDescription = "Home",
                    modifier = Modifier.size(32.dp) // Augmente la taille de l'icône Home
                )
            }
            // Icône Add
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .background(backgroundColor)
                    .border(2.5.dp, Color.Black, MaterialTheme.shapes.medium) // Bordure (couleur et épaisseur
                    .size(32.dp) // Taille du cadre carré
            ) {
                IconButton(onClick = { /* Handle add icon click */ }) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "Add",
                        modifier = Modifier.size(32.dp), // Ajuste la taille de l'icône Add à l'intérieur du
                        tint = Color.Black
                    )
                }
            }
            // Icône de profil
            IconButton(onClick = { /* Handle profile icon click */ }) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_background), // Utilisez votre image de profil ici
                    contentDescription = "Profile",
                    modifier = Modifier
                        .size(40.dp) // Augmente la taille de l'icône de profil
                        .clip(CircleShape)
                )
            }
        }
    }
}

// Données d'exemple pour les posts
fun samplePosts() = listOf(
    Post(
        title = "Premier post",
        description = "Ceci est le premier post de notre flux d'actualités.",
        imageUrl = "",
        publicationDate = System.currentTimeMillis() - 100000
    ),
    Post(
        title = "Deuxième post",
        description = "Voici un autre exemple de post avec une description plus longue pour voir comment il s'affiche.",
        imageUrl = "",
        publicationDate = System.currentTimeMillis() - 50000
    )
)

@Composable
fun PostsList(posts: List<Post>, onCommentClick: () -> Unit, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(posts) { post ->
            PostCard(post = post, onCommentClick = onCommentClick)
        }
    }
}


@Composable
fun PostCard(post: Post, onCommentClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header de la Card avec l'image de profil et le nom d'utilisateur
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_background), // Remplacez par l'image de profil réelle
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape) // Pour rendre l'image circulaire
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Username", style = MaterialTheme.typography.bodyMedium) // Remplacez "Username" par le nom d'utilisateur réel
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Image du post prenant toute la largeur de la Card
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground), // Remplacez par l'image du post réelle
                contentDescription = "Post Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp) // Hauteur fixe pour l'image, ajustez selon vos besoins
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Icônes sous l'image
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Icône de cœur vide
                IconButton(onClick = { /* Action pour le cœur */ }) {
                    Icon(
                        Icons.Filled.FavoriteBorder,
                        contentDescription = "Like",
                        modifier = Modifier.size(24.dp),
                        tint = Color.Black // Bordure noire pour l'icône
                    )
                }
                // Icône de bulle de texte
                IconButton(onClick = { /* Action pour les commentaires */ }) {
                    Icon(
                        Icons.Filled.MailOutline,
                        contentDescription = "Comment",
                        modifier = Modifier.size(24.dp),
                        tint = Color.Black // Bordure noire pour l'icône
                    )
                }
            }

            Text(
                text = "120 J'aime",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Description et date de publication en bas
            Text(text = post.description, style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Voir les 335 commentaires",
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.clickable { onCommentClick() } // Rendre le texte cliquable
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Published: ${
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(post.publicationDate))
                }",
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AndroidProjetTheme {
        Scaffold(
            bottomBar = { MyBottomAppBar() }
        ) {
            PostsList(posts = samplePosts(), onCommentClick = {}, modifier = Modifier.padding(it))
        }
    }
}

@Composable
fun SheetContent(onDismiss: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Commentaires", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))
        // Ici, vous pouvez ajouter le contenu de vos commentaires, par exemple, une liste de commentaires.
        // Pour cet exemple, nous ajoutons simplement un texte et un bouton pour fermer la feuille.
        Button(onClick = onDismiss) {
            Text("Fermer")
        }
    }
}

