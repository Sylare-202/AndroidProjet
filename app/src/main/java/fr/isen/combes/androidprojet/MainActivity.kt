@file:OptIn(ExperimentalMaterialApi::class)

package fr.isen.combes.androidprojet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import fr.isen.combes.androidprojet.ui.theme.AndroidProjetTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Post(
    val title: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val publicationDate: Long = 0L,
    var likesCount: Int = 0, // Ajouté
    var isLiked: Boolean = false // Ajouté
)

data class Comment(
    val profileImageId: Int,
    val username: String,
    val timestamp: Long,
    val commentText: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val profilePictureUrl = intent.getStringExtra("profilePictureUrl") ?: ""
        Log.v("MainActivity", "Profile picture URL: $profilePictureUrl")
        setContent {
            AndroidProjetTheme {
                MyApp(profilePictureUrl = profilePictureUrl)
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MyApp(profilePictureUrl: String) {
    val coroutineScope = rememberCoroutineScope()
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberBottomSheetState(initialValue = BottomSheetValue.Collapsed)
    )
    val comments = remember { mutableStateListOf<Comment>(
        Comment(
            profileImageId = R.drawable.ic_launcher_background,
            username = "Utilisateur1",
            timestamp = System.currentTimeMillis() - 3600000,
            commentText = "Super photo !"
        ),
        Comment(
            profileImageId = R.drawable.ic_launcher_background,
            username = "Utilisateur2",
            timestamp = System.currentTimeMillis() - 7200000,
            commentText = "J'adore cet endroit."
        )
    )}

    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetContent = {
            SheetContent(onDismiss = { coroutineScope.launch { bottomSheetScaffoldState.bottomSheetState.collapse() } }, comments = comments, onAddComment = { commentText ->
                val newComment = Comment(
                    profileImageId = R.drawable.ic_launcher_background, // Utiliser un id de ressource approprié
                    username = "Moi",
                    timestamp = System.currentTimeMillis(),
                    commentText = commentText
                )
                comments.add(newComment)
            })
        },
        sheetPeekHeight = 0.dp
    ) {
        MainScreen(comments = comments, onCommentClick = { coroutineScope.launch { bottomSheetScaffoldState.bottomSheetState.expand() } }, profilePictureUrl = profilePictureUrl)
    }
}

@Composable
fun MainScreen(comments: List<Comment>, onCommentClick: () -> Unit, profilePictureUrl: String?) {
    val context = LocalContext.current // Récupérer le contexte local

    Scaffold(
        topBar = { MyAppTopBar() },
        bottomBar = { MyBottomAppBar(profilePictureUrl, context) }
    ) { innerPadding ->
        PostsList(posts = samplePosts(), comments = comments, onCommentClick = onCommentClick, modifier = Modifier.padding(innerPadding))
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
fun MyBottomAppBar(profilePictureUrl: String?, context: Context) {
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
            IconButton(onClick = {
                // Rediriger vers ProfileViewActivity
                val intent = Intent(context, ProfileViewActivity::class.java)
                context.startActivity(intent)
            }) {
                val imagePainter = if (!profilePictureUrl.isNullOrEmpty()) {
                    rememberImagePainter(profilePictureUrl)
                } else {
                    painterResource(id = R.drawable.ic_launcher_background) // Image de profil par défaut
                }
                Image(
                    painter = imagePainter,
                    contentDescription = "Profile",
                    modifier = Modifier
                        .size(40.dp) // Set the size of the icon
                        .clip(CircleShape) // Make the image circular
                        .fillMaxSize(), // Ensure the image fills the space
                    contentScale = ContentScale.Crop // Crop the image if necessary to fit
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
fun PostsList(posts: List<Post>, comments: List<Comment>, onCommentClick: () -> Unit, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(posts) { post ->
            // Ici, on passe la liste complète des commentaires à chaque PostCard
            // Dans une application réelle, vous voudriez filtrer les commentaires spécifiques à ce post
            PostCard(post = post, comments = comments, onCommentClick = onCommentClick)
        }
    }
}

@Composable
fun PostCard(post: Post, comments: List<Comment>, onCommentClick: () -> Unit) {
    val initialLikes = 120
    val likesText = remember { mutableStateOf("120 J'aime") }
    val isLiked = remember { mutableStateOf(false) }

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
                IconButton(onClick = {
                    isLiked.value = !isLiked.value
                    if (isLiked.value) {
                        // Incrémente le nombre de J'aime et met à jour le texte
                        likesText.value = "${initialLikes + 1} J'aime"
                    } else {
                        // Décrémente le nombre de J'aime et met à jour le texte
                        likesText.value = "$initialLikes J'aime"
                    }
                }) {
                    Icon(
                        imageVector = if (isLiked.value) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (isLiked.value) Color.Magenta else Color.Black
                    )
                }
                // Icône de bulle de texte
                IconButton(onClick = onCommentClick) {
                    Icon(
                        Icons.Filled.MailOutline,
                        contentDescription = "Comment",
                        modifier = Modifier.size(24.dp),
                        tint = Color.Black
                    )
                }
            }

            Text(
                text = likesText.value,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Description et date de publication en bas
            Text(text = post.description, style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Voir les ${comments.size} commentaires",
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.clickable { onCommentClick() }
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

@Composable
fun SheetContent(onDismiss: () -> Unit, comments: List<Comment>, onAddComment: (String) -> Unit) {
    // Hauteur maximale de la BottomSheet à 50% de la hauteur de l'écran
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val sheetHeight = screenHeight * 0.5f // Limite à 50% de la hauteur de l'écran

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = sheetHeight) // Limite la hauteur de la Column
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(40.dp)
                .height(4.dp)
                .background(color = Color.LightGray, shape = RoundedCornerShape(50))
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Commentaires",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        // Seuls les commentaires sont scrollables
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(comments) { comment ->
                CommentItem(comment = comment)
                Divider()
            }
        }

        NewCommentSection(onAddComment = onAddComment)
    }
}

@Composable
fun NewCommentSection(onAddComment: (String) -> Unit) {
    val (commentText, setCommentText) = remember { mutableStateOf("") }

    Row(verticalAlignment = Alignment.CenterVertically) {
        // Votre photo de profil
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background), // Utilisez l'ID de votre propre ressource
            contentDescription = "Votre photo de profil",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        // Champ de texte pour ajouter un commentaire
        TextField(
            value = commentText,
            onValueChange = setCommentText,
            placeholder = { Text("Ajoutez un commentaire...") },
            modifier = Modifier.fillMaxWidth().weight(1f),
            textStyle = TextStyle(color = Color.Black),
            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent)
        )
        // Bouton pour envoyer le commentaire
        Button(onClick = {
            if(commentText.isNotBlank()) {
                onAddComment(commentText)
                setCommentText("") // Réinitialiser le champ de texte après l'envoi
            }
        }) {
            Text("Envoyer")
        }
    }
}
@Composable
fun CommentItem(comment: Comment) {
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Image(
            painter = painterResource(id = comment.profileImageId),
            contentDescription = "Profile picture",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(0.5.dp, Color.Gray, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = comment.username,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(comment.timestamp)),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = comment.commentText,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
