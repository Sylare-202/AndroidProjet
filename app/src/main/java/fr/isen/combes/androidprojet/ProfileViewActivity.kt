package fr.isen.combes.androidprojet

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import fr.isen.combes.androidprojet.ui.theme.AndroidProjetTheme

import androidx.compose.foundation.clickable
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import coil.compose.rememberImagePainter
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

data class User(
    var uid: String = "",
    val description: String = "",
    val email: String = "",
    val firstname: String = "",
    val lastname: String = "",
    val password: String = "",
    val profilePicture: String = "",
    val username: String = ""
) {
    constructor() : this("", "", "", "", "", "", "")
}

data class Post(
    val image: String = "",
    val date: String = "",
    val description: String = "",
    val location: String = "",
    val like: Int = 0
) {
    constructor() : this("", "", "", "", 0)
}

class ProfileViewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val userId = intent.getStringExtra("userId")
        super.onCreate(savedInstanceState)
        if (userId != null) {
            fetchUserData(userId)
        }
    }

    override fun onResume() {
        val userId = intent.getStringExtra("userId")
        super.onResume()
        if (userId != null) {
            fetchUserData(userId)
        }
    }

    private fun fetchUserData(userId: String) {
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("Users/$userId")
        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                user?.let {
                    fetchUserPosts(user)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Error fetching user data: ${databaseError.message}")
            }
        })
    }

    private fun fetchUserPosts(user: User) {
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("Users")
        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.children.forEach { userSnapshot ->
                    val userData = userSnapshot.getValue(User::class.java)
                    userData?.let {
                        if (userData == user) {
                            val userId = userSnapshot.key // Retrieve the UID
                            userId?.let { uid ->
                                // Use the UID to fetch posts
                                val postsRef =
                                    database.getReference("Post").orderByChild("uid").equalTo(uid)
                                postsRef.addListenerForSingleValueEvent(object :
                                    ValueEventListener {
                                    override fun onDataChange(postsSnapshot: DataSnapshot) {
                                        val posts = mutableListOf<Post>()
                                        postsSnapshot.children.forEach { postSnapshot ->
                                            val post = postSnapshot.getValue(Post::class.java)
                                            post?.let {
                                                posts.add(post)
                                            }
                                        }
                                        setContent {
                                            AndroidProjetTheme {
                                                user.uid = uid
                                                println("User data: $user")
                                                Log.e(
                                                    "User data",
                                                    "NEW imageURI: ${user.profilePicture}"
                                                )
                                                ProfileScreen(this@ProfileViewActivity, user, posts)
                                            }
                                        }
                                    }

                                    override fun onCancelled(databaseError: DatabaseError) {
                                        println("Error fetching user posts: ${databaseError.message}")
                                    }
                                })
                            }
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Error fetching users data: ${databaseError.message}")
            }
        })
    }
}


@Composable
fun ProfileScreen(activity: ComponentActivity, user: User, posts: List<Post>) {
    var followerCount by remember { mutableIntStateOf(0) }
    var followingCount by remember { mutableIntStateOf(0) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 0.dp),
                verticalArrangement = Arrangement.Top
            ) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (user.uid == Firebase.auth.currentUser?.uid) {
                            Text(
                                text = "Disconnect",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Red,
                                modifier = Modifier.clickable {
                                    Firebase.auth.signOut()
                                    val intent = Intent(activity, LoginActivity::class.java)
                                    activity.startActivity(intent)
                                }
                            )
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ProfileHeader(size = 120, user = user)
                        Spacer(modifier = Modifier.width(16.dp))
                        val numberOfPosts = posts.size

                        countFollowers(user.uid,
                            onCountReceived = { count ->
                                followerCount = count
                            },
                            onError = { error ->
                                throw Exception(error)
                            }
                        )

                        countFollowing(user.uid,
                            onCountReceived = { count ->
                                followingCount = count
                            },
                            onError = { error ->
                                throw Exception(error)
                            }
                        )

                        ProfileInfo(
                            numberOfPosts,
                            followingCount,
                            followerCount,
                            user.firstname,
                            user.lastname,
                            user.username,
                            user.description
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Log.e("UserInfo", "User: ${user.uid}")
                    Log.e("UserInfo", "Current user: ${Firebase.auth.currentUser?.uid}")
                    if (user.uid != Firebase.auth.currentUser?.uid) {
                        FollowProfileButton(user)
                    } else {
                        ProfileButton(activity, user, "edit profile")
                    }


                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        color = Color.Gray,
                        thickness = 1.dp
                    )
                }
                item {
                    PostGrid(posts = posts, activity = activity)
                }
            }
            MyBottomAppBarProfil(user = user, context = activity)
        }
    }
}


@Composable
fun MyBottomAppBarProfil(user: User, context: Context) {
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
            IconButton(onClick = {
                (context as ComponentActivity).finish()
            }) {
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
                    .border(
                        2.5.dp,
                        Color.Black,
                        MaterialTheme.shapes.medium
                    ) // Bordure (couleur et épaisseur
                    .size(32.dp) // Taille du cadre carré
            ) {
                IconButton(onClick = {
                    val intent = Intent(context, PostActivity::class.java)
                    context.startActivity(intent)
                }) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "Add",
                        modifier = Modifier.size(32.dp), // Ajuste la taille de l'icône Add à l'intérieur du
                        tint = Color.Black
                    )
                }
            }
            // Icône de profil
            if (com.google.firebase.ktx.Firebase.auth.currentUser?.uid == user.uid) {
                IconButton(onClick = {
                    Toast.makeText(context, "Already on the profile", Toast.LENGTH_SHORT).show()
                }) {
                    val imagePainter = if (!user.profilePicture.isNullOrEmpty()) {
                        rememberImagePainter(user.profilePicture)
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
            } else {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            val intent = Intent(context, ProfileViewActivity::class.java)
                            intent.putExtra("userId", user.uid)
                            context.startActivity(intent)
                        }
                        .background(Color.Transparent) // Set the background color to transparent
                ) {
                    // No content inside the Box
                }
            }
        }
    }
}


@Composable
fun ProfileInfo(
    publication: Int,
    following: Int,
    follower: Int,
    firstName: String,
    lastName: String,
    username: String,
    description: String
) {
    Column {
        Row {
            Text(
                text = "$firstName $lastName",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
        Text(
            text = "@$username",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF00C974)
        )
        Spacer(modifier = Modifier.height(8.dp))
        PublicationInformation(publication, following, follower)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "$description",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun PublicationInformation(publication: Int, following: Int, follower: Int) {
    Row {
        Column(verticalArrangement = Arrangement.Center) {
            Text(
                text = "$publication",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(text = "Publication", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.width(5.dp))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(verticalArrangement = Arrangement.Center) {
            Text(
                text = "$following",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(text = "Following", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.width(5.dp))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(verticalArrangement = Arrangement.Center) {
            Text(
                text = "$follower",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(text = "Follower", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.width(5.dp))
        }
    }
}


@Composable
fun ProfileHeader(size: Int, user: User) {
    Image(
        painter = rememberImagePainter(user.profilePicture.toString()),
        contentDescription = "Profile Image",
        modifier = Modifier
            .size(size.dp)
            .padding(16.dp)
            .clip(MaterialTheme.shapes.extraLarge),
        contentScale = ContentScale.Crop,
    )
}

@Composable
fun ProfileButton(activity: ComponentActivity, userData: User, text: String) {
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
                val intent = Intent(activity, ProfileEditActivity::class.java)
                // Pass user data as extra to the intent
                intent.putExtra("userFirstName", userData.firstname)
                intent.putExtra("userLastName", userData.lastname)
                intent.putExtra("userUsername", userData.username)
                intent.putExtra("userDescription", userData.description)
                intent.putExtra("userEmail", userData.email)
                intent.putExtra("imageUri", userData.profilePicture)

                activity.startActivity(intent)
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

fun countFollowers(userId: String, onCountReceived: (Int) -> Unit, onError: (String) -> Unit) {
    val followsRef = Firebase.database.reference.child("follows").child(userId)

    val followerCountListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val count = snapshot.childrenCount.toInt()
            Log.e("FollowerCount", "Count (follow): $count")
            onCountReceived(count)
        }

        override fun onCancelled(error: DatabaseError) {
            onError(error.message)
        }
    }

    followsRef.addValueEventListener(followerCountListener)
    Runtime.getRuntime().addShutdownHook(Thread {
        followsRef.removeEventListener(followerCountListener)
    })
}

fun countFollowing(userId: String, onCountReceived: (Int) -> Unit, onError: (String) -> Unit) {
    val followsRef = Firebase.database.reference.child("follows")

    val followingCountListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            var count = 0
            for (userSnapshot in snapshot.children) {
                if (userSnapshot.hasChild(userId)) {
                    count++
                }
            }
            Log.e("FollowerCount", "Count (following): $count")
            onCountReceived(count)
        }

        override fun onCancelled(error: DatabaseError) {
            onError(error.message)
        }
    }

    followsRef.addValueEventListener(followingCountListener)

    Runtime.getRuntime().addShutdownHook(Thread {
        followsRef.removeEventListener(followingCountListener)
    })
}

@Composable
fun FollowProfileButton(userData: User) {
    val context = LocalContext.current
    val currentUser = FirebaseAuth.getInstance().currentUser
    var isCurrentUserFollowing by remember { mutableStateOf(false) }
    val buttonText = if (isCurrentUserFollowing) "Following" else "Follow"

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            val followsRef = Firebase.database.reference.child("follows")
            followsRef.child(currentUser.uid).child(userData.uid)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val isFollowing = snapshot.exists()
                        isCurrentUserFollowing = isFollowing
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("FollowProfileButton", "Error: ${error.message}")
                    }
                })
        }
    }

    Box(
        modifier = Modifier
            .padding(bottom = 10.dp)
            .background(
                color = if (isCurrentUserFollowing) Color(0xFF00C974) else Color.Gray,
                shape = MaterialTheme.shapes.extraLarge
            )
    ) {
        ClickableText(
            text = AnnotatedString(buttonText).toUpperCase(),
            onClick = {
                currentUser?.uid?.let { uid ->
                    if (isCurrentUserFollowing) {
                        unfollowUser(uid, userData.uid, userData.username, context)
                    } else {
                        followUser(uid, userData.uid, userData.username, context)
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

private fun followUser(
    currentUserId: String,
    targetUserId: String,
    targetUserUsername: String,
    context: Context
) {
    val followsRef = Firebase.database.reference.child("follows")
    followsRef.child(currentUserId).child(targetUserId).setValue(true)
        .addOnSuccessListener {
            Toast.makeText(
                context,
                "You are now following ${targetUserUsername}",
                Toast.LENGTH_SHORT
            ).show()
        }
        .addOnFailureListener { e ->
            Toast.makeText(
                context,
                "Failed to follow ${targetUserId}: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
}

private fun unfollowUser(
    currentUserId: String,
    targetUserId: String,
    targetUserUsername: String,
    context: Context
) {
    val followsRef = Firebase.database.reference.child("follows")
    followsRef.child(currentUserId).child(targetUserId).removeValue()
        .addOnSuccessListener {
            Toast.makeText(
                context,
                "You have unfollowed ${targetUserId}",
                Toast.LENGTH_SHORT
            ).show()
        }
        .addOnFailureListener { e ->
            Toast.makeText(
                context,
                "Failed to unfollow ${targetUserId}: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
}


@Composable
fun PostGrid(posts: List<Post>, activity: ComponentActivity) {
    val placeholdersNeeded = if (posts.size % 3 == 0) 0 else 3 - (posts.size % 3)

    val updatedPosts = posts + List(placeholdersNeeded) { Post() }

    val postsPerRow = 3
    val rowCount = (updatedPosts.size + postsPerRow - 1) / postsPerRow

    Column {
        repeat(rowCount) { rowIndex ->
            println("Row Index: $rowIndex")
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(postsPerRow) { columnIndex ->
                    val postIndex = rowIndex * postsPerRow + columnIndex
                    PostImage(updatedPosts.getOrNull(postIndex), activity)
                }
            }
            Spacer(modifier = Modifier.height(1.dp))
        }
    }
}

@Composable
fun PostImage(post: Post?, activity: ComponentActivity) {
    if (post != null) {
        Image(
            painter = rememberImagePainter(post.image),
            contentDescription = "Post Image",
            modifier = Modifier
                .size(125.dp)
                .padding(1.dp)
                .clickable {
                    activity.finish()
                }
                .clip(MaterialTheme.shapes.small),
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = Modifier
                .size(125.dp)
                .padding(1.dp)
                .background(Color.Transparent)
        )
    }
}