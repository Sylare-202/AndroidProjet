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
import androidx.compose.runtime.LaunchedEffect
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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import fr.isen.combes.androidprojet.ui.theme.AndroidProjetTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Post(
    var id: String = "",
    var description: String = "",
    var imageUrl: String = "",
    var publicationDate: String = "",
    var likesCount: Int = 0,
    var likedBy: MutableSet<String> = mutableSetOf(),
    var uid: String = "",
    val comments: MutableList<Comment> = mutableListOf(),
    var commentCount: Int = 0
)

data class Comment(
    var profileImageId: String? = null,
    var username: String = "",
    val timestamp: Long = 0,
    val commentText: String = "",
    val userId: String = "",
    val postId: String = ""
)


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userId = Firebase.auth.currentUser?.uid
        if (userId == null) {
            // Rediriger vers LoginActivity si aucun utilisateur n'est connecté.
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Récupération des données utilisateur de Firebase.
        fetchUserDataFromFirebase(userId) { user ->
            if (user != null) {
                // Configuration de l'UI avec les données utilisateur.
                setContent {
                    AndroidProjetTheme {
                        MyApp(user = user)
                    }
                }
            } else {
                Log.e("MainActivity", "Erreur lors de la récupération des données utilisateur.")
                // Gérer l'erreur.
            }
        }
    }
}

fun fetchPostsFromFirebase(onPostsFetched: (List<Post>) -> Unit) {
    val postsRef = Firebase.database.reference.child("Post")
    postsRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val posts = mutableListOf<Post>()
            for (postSnapshot in snapshot.children) {
                val post = postSnapshot.getValue(Post::class.java)?.apply {
                    // Mise à jour pour correspondre à la structure de votre Post
                    this.commentCount = postSnapshot.child("commentCount").getValue(Int::class.java) ?: 0
                    this.id = postSnapshot.key ?: ""
                    this.uid = postSnapshot.child("uid").getValue(String::class.java) ?: ""
                    this.imageUrl = postSnapshot.child("image").getValue(String::class.java) ?: ""
                    this.description = postSnapshot.child("description").getValue(String::class.java) ?: ""
                    this.publicationDate = postSnapshot.child("date").getValue(String::class.java) ?: ""
                    this.likesCount = postSnapshot.child("like").getValue(Int::class.java) ?: 0
                }
                post?.let { posts.add(it) }
            }
            onPostsFetched(posts)
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("fetchPostsFromFirebase", "Erreur lors de la récupération des posts: ${error.message}")
        }
    })
}


fun fetchAllUsers(onUsersFetched: (Map<String, User>) -> Unit) {
    val usersRef = Firebase.database.reference.child("Users")
    usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            // Filtrer les entrées null et convertir le résultat en une Map non-nulle.
            val usersMap = snapshot.children.mapNotNull { dataSnapshot ->
                dataSnapshot.key?.let { key ->
                    dataSnapshot.getValue(User::class.java)?.let { user ->
                        key to user
                    }
                }
            }.toMap()
            onUsersFetched(usersMap)
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("fetchAllUsers", "Error fetching users: ${error.message}")
            onUsersFetched(emptyMap())
        }
    })
}

fun addCommentToFirebase(text: String, postId: String, context: Context, onCommentAdded: (Comment) -> Unit, onCommentCountUpdated: (Int) -> Unit) {
    val uid = Firebase.auth.currentUser?.uid
    if (uid != null && postId.isNotEmpty()) {
        val databaseReference = Firebase.database.reference.child("Comments").push()

        val comment = Comment(
            userId = uid,
            commentText = text,
            timestamp = System.currentTimeMillis(),
            postId = postId
        )

        databaseReference.setValue(comment).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                showToast(context, "Commentaire ajouté avec succès")

                // Mise à jour du nombre de commentaires dans le post
                val postRef = Firebase.database.reference.child("Post").child(postId)
                postRef.child("commentCount").runTransaction(object : Transaction.Handler {
                    override fun doTransaction(mutableData: MutableData): Transaction.Result {
                        var count = mutableData.getValue(Int::class.java) ?: 0
                        count++
                        mutableData.value = count
                        return Transaction.success(mutableData)
                    }

                    override fun onComplete(
                        databaseError: DatabaseError?,
                        committed: Boolean,
                        dataSnapshot: DataSnapshot?
                    ) {
                        if (committed) {
                            val newCount = dataSnapshot?.getValue(Int::class.java) ?: 0
                            onCommentCountUpdated(newCount)
                            onCommentAdded(comment) // Appeler le callback avec le nouveau commentaire
                        }
                    }
                })
            } else {
                showToast(context, "Erreur lors de l'ajout du commentaire")
            }
        }
    } else {
        showToast(context, "Utilisateur non identifié")
    }
}

fun fetchCommentsForPost(postId: String, onCommentsFetched: (List<Comment>) -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        val commentsRef = Firebase.database.reference.child("Comments")
        val snapshot = commentsRef.orderByChild("postId").equalTo(postId).get().await()

        val comments = mutableListOf<Comment>()
        snapshot.children.forEach { child ->
            val comment = child.getValue<Comment>()
            comment?.let { comments.add(it) }
        }

        Log.d("fetchComments", "Fetched ${comments.size} comments for post $postId")

        withContext(Dispatchers.Main) {
            onCommentsFetched(comments)
        }
    }
}

fun toggleLikeForPost(postId: String, userId: String, context: Context) {
    val postRef = Firebase.database.reference.child("Post").child(postId)

    postRef.runTransaction(object : Transaction.Handler {
        override fun doTransaction(mutableData: MutableData): Transaction.Result {
            val post = mutableData.getValue(Post::class.java) ?: return Transaction.success(mutableData)

            if (post.likedBy.contains(userId)) {
                // Si l'utilisateur avait déjà aimé, on retire son like
                post.likesCount -= 1
                post.likedBy.remove(userId)
            } else {
                // Sinon, on ajoute son like
                post.likesCount += 1
                post.likedBy.add(userId)
            }

            // Mettre à jour la base de données
            mutableData.value = post
            return Transaction.success(mutableData)
        }

        override fun onComplete(databaseError: DatabaseError?, committed: Boolean, dataSnapshot: DataSnapshot?) {
            if (committed) {
                showToast(context, if (dataSnapshot?.getValue(Post::class.java)?.likedBy?.contains(userId) == true) "Aimé" else "Like retiré")
            } else {
                showToast(context, "Erreur lors de la mise à jour du like")
            }
        }
    })
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MyApp(user: User) {
    val coroutineScope = rememberCoroutineScope()
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberBottomSheetState(initialValue = BottomSheetValue.Collapsed)
    )
    val postsState = remember { mutableStateOf<List<Post>>(emptyList()) }
    val usersState = remember { mutableStateOf<Map<String, User>>(emptyMap()) }

    val selectedPost = remember { mutableStateOf<Post?>(null) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        fetchAllUsers { users ->
            usersState.value = users
            fetchPostsFromFirebase { posts ->
                postsState.value = posts
            }
        }
    }

    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetContent = {
            // Utilisez le post sélectionné pour afficher les commentaires
            selectedPost.value?.let { post ->
                // Passez users à SheetContent
                SheetContent(
                    post = selectedPost.value!!,
                    user = user,
                    context = context,
                    users = usersState.value,
                    onAddComment = { commentText, postId, onCommentAdded ->
                        addCommentToFirebase(commentText, postId, context, onCommentAdded, onCommentCountUpdated = { newCount ->
                            // Mise à jour du nombre de commentaires localement
                            val updatedPosts = postsState.value.map { post ->
                                if (post.id == postId) post.copy(commentCount = newCount) else post
                            }
                            postsState.value = updatedPosts
                        })
                    }
                )
            } ?: Text("Pas de post sélectionné") // Fallback si aucun post n'est sélectionné
        },
        sheetPeekHeight = 0.dp
    ) {
        MainScreen(onCommentClick = { post, postId ->
            selectedPost.value = post
            coroutineScope.launch { bottomSheetScaffoldState.bottomSheetState.expand() }
        }, profilePictureUrl = user.profilePicture, posts = postsState.value, users = usersState.value)
    }
}

@Composable
fun MainScreen(onCommentClick: (Post, String) -> Unit, profilePictureUrl: String?, posts: List<Post>, users: Map<String, User>) {
    val context = LocalContext.current // Récupérer le contexte local

    Scaffold(
        topBar = { MyAppTopBar() },
        bottomBar = { MyBottomAppBar(profilePictureUrl, context) }
    ) { innerPadding ->
        PostsList(posts = posts, users = users, onCommentClick = onCommentClick, modifier = Modifier.padding(innerPadding))
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

@Composable
fun PostsList(posts: List<Post>, users: Map<String, User>, onCommentClick: (Post, String) -> Unit, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(posts) { post ->
            PostCard(post = post, users = users, onCommentClick = { selectedPost, postId ->
                onCommentClick(selectedPost, postId)
            })
        }
    }
}

@Composable
fun PostCard(post: Post, users: Map<String, User>, onCommentClick: (Post, String) -> Unit) {
    val likesText = remember { mutableStateOf("${post.likesCount} J'aime") }
    val isLiked = remember { mutableStateOf(false) }

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val postDate = dateFormat.format(Date()) // Utilisez la bonne date du post

    val user = users[post.uid] // Trouver l'utilisateur associé au post

    // Fallbacks pour le cas où l'utilisateur n'est pas trouvé
    val username = user?.username ?: "Utilisateur inconnu"
    val userProfilePictureUrl = user?.profilePicture ?: "" // Remplacer par une URL d'image de profil par défaut si souhaité

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
                    painter = rememberImagePainter(userProfilePictureUrl, builder = {
                        crossfade(true)
                    }),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = username, style = MaterialTheme.typography.bodyMedium) // Remplacez "Username" par le nom d'utilisateur réel
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Image du post prenant toute la largeur de la Card
            Image(
                painter = rememberImagePainter(post.imageUrl),
                contentDescription = "Post Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Icônes sous l'image
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Icône de cœur vide
                IconButton(onClick = {
                    // Ici, mettez à jour votre base de données Firebase pour refléter le like et mettez à jour le compteur de likes localement.
                    isLiked.value = !isLiked.value
                    if (isLiked.value) {
                        // Simuler l'ajout d'un like. Dans la pratique, mettez à jour la base de données et rafraîchissez la valeur de likesCount.
                        likesText.value = "${post.likesCount + 1} J'aime"
                    } else {
                        // Simuler le retrait d'un like. Dans la pratique, mettez à jour la base de données et rafraîchissez la valeur de likesCount.
                        likesText.value = "${post.likesCount} J'aime"
                    }
                }) {
                    Icon(
                        imageVector = if (isLiked.value) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (isLiked.value) Color.Magenta else Color.Black
                    )
                }
                // Icône de bulle de texte
                IconButton(onClick = { onCommentClick(post, post.id) }) {
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
                text = "Voir les ${post.commentCount} commentaires",
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.clickable { onCommentClick(post, post.id) }
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Publié le $postDate",
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun CommentCard(comment: Comment, users: Map<String, User>) {
    val user = users[comment.userId]

    // Fallbacks pour le cas où l'utilisateur n'est pas trouvé
    val username = user?.username ?: "Utilisateur inconnu"
    val userProfilePictureUrl = user?.profilePicture ?: "" // Remplacer par une URL d'image de profil par défaut si souhaité

    Row(
        modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // Assurez-vous que l'image de profil est correctement chargée
        // Note : Adapter le chargement de l'image en fonction de votre cas d'usage, par exemple en utilisant coil
        Image(
            painter = rememberImagePainter(userProfilePictureUrl, builder = {
                crossfade(true)
            }),
            contentDescription = "Profile picture",
            modifier = Modifier.size(40.dp).clip(CircleShape).border(0.5.dp, Color.Gray, CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = username ?: "Anonyme",
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

@Composable
fun SheetContent(post: Post, user: User, context: Context, onAddComment: (String, String, (Comment) -> Unit) -> Unit, users: Map<String, User>, ) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val sheetHeight = screenHeight * 0.5f

    val commentsState = remember { mutableStateOf<List<Comment>>(emptyList()) }

    // Récupération des commentaires lorsque le post est sélectionné
    LaunchedEffect(post.id) {
        fetchCommentsForPost(post.id) { comments ->
            commentsState.value = comments
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = sheetHeight)
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

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(commentsState.value) { comment ->
                CommentCard(comment = comment, users = users)
            }
        }

        NewCommentSection(user, post.id) { commentText, postId ->
            onAddComment(commentText, postId, { newComment ->
                // Mettre à jour la liste des commentaires localement
                commentsState.value = commentsState.value + newComment
            })
        }
    }
}

@Composable
fun NewCommentSection(user: User, postId: String, onAddComment: (String, String) -> Unit) {
    val (commentText, setCommentText) = remember { mutableStateOf("") }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = rememberImagePainter(data = user.profilePicture),
            contentDescription = "Votre photo de profil",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(8.dp))

        TextField(
            value = commentText,
            onValueChange = setCommentText,
            placeholder = { Text("Ajoutez un commentaire...") },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            textStyle = TextStyle(color = Color.Black),
            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent)
        )

        Button(onClick = {
            if (commentText.isNotBlank()) {
                onAddComment(commentText, postId)
                setCommentText("")
            }
        }) {
            Text("Envoyer")
        }
    }
}

