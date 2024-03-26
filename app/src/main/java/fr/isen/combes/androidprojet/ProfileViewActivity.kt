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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import fr.isen.combes.androidprojet.ui.theme.AndroidProjetTheme

import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.clickable
import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import coil.compose.rememberImagePainter
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

data class User(
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

class ProfileViewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fetchUserData()
    }

    override fun onResume() {
        super.onResume()
        fetchUserData()
    }

    private fun fetchUserData() {
        val userId = Firebase.auth.currentUser?.uid
        if (userId != null) {
            val database = FirebaseDatabase.getInstance()
            val usersRef = database.getReference("Users/$userId")
            usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue(User::class.java)
                    user?.let {
                        setContent {
                            AndroidProjetTheme {
                                println("User data: $user")
                                ProfileScreen(this@ProfileViewActivity, user)
                            }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    println("Error fetching user data: ${databaseError.message}")
                }
            })
        }
    }
}

@Composable
fun ProfileScreen(activity: ComponentActivity, user: User) {
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
            IconButton(
                onClick = {
                    Firebase.auth.signOut()
                    val intent = Intent(activity, LoginActivity::class.java)
                    activity.startActivity(intent)
                },
                modifier = Modifier
                    .padding(top = 0.dp, end = 0.dp)
                    .align(Alignment.End)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Logout",
                    tint = Color.Gray
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                ProfileHeader(size = 120, user = user)
                Spacer(modifier = Modifier.width(16.dp))
                ProfileInfo(12, 12, 12, user.firstname, user.lastname, user.username, user.description)
                Spacer(modifier = Modifier.height(16.dp))

            }
            Spacer(modifier = Modifier.height(16.dp))
            ProfileButton(activity, user)
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                color = Color.Gray,
                thickness = 1.dp
            )
            PostGrid()
        }
    }
}

@Composable
fun ProfileInfo(publication: Int, following: Int, follower: Int, firstName: String, lastName: String, username: String, description: String) {
        Column {
            Row {
                Text(text = "$firstName $lastName", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(4.dp))
            }
            Text(text = "@$username", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            PublicationInformation(publication, following, follower)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "$description", style = MaterialTheme.typography.bodyLarge)
        }
}

@Composable
fun PublicationInformation(publication: Int, following: Int, follower: Int) {
    Row {
        Column(verticalArrangement = Arrangement.Center) {
            Text(
                text = "$publication",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
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
                modifier = Modifier.align(Alignment.CenterHorizontally)
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
                modifier = Modifier.align(Alignment.CenterHorizontally)
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
fun ProfileButton(activity: ComponentActivity, userData: User) {
    Button(
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
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "Edit Profile")
    }
}



@Composable
fun PostGrid(userNumberPost: Int = 9) {
    val postsPerRow = 3


    val rowCount = (userNumberPost + postsPerRow - 1) / postsPerRow

    Column {
        repeat(rowCount) { rowIndex ->
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(postsPerRow) { columnIndex ->
                    val postIndex = rowIndex * postsPerRow + columnIndex + 1
                    if (postIndex <= userNumberPost) {
                        PostImage(postIndex)
                    }
                }
            }
            Spacer(modifier = Modifier.height(1.dp))
        }
    }
}

@Composable
fun PostImage(postIndex: Int) {
    if (postIndex > 5) {
        Box (
            modifier = Modifier
                .size(125.dp)
                .padding(1.dp)
                .clip(MaterialTheme.shapes.medium)
        )
        return
    }
    val resourceName = "post$postIndex"
    println("Resource Name: $resourceName") // Add this line to print the resource name
    val imageResource = getResourceId(resourceName, "drawable", LocalContext.current)

    Image(
        painter = painterResource(id = imageResource),
        contentDescription = "Post Image $postIndex",
        modifier = Modifier
            .size(125.dp)
            .padding(1.dp)
            .clickable { /* TODO: Add the redirection to the specific post knowing the index (here in dev) */ },
        contentScale = ContentScale.Crop
    )
}


/*fun fetchUserDataFromFirebase(userId: String, onUserDataFetched: (User?) -> Unit) {
    val database = FirebaseDatabase.getInstance()
    val usersRef = database.getReference("Users/$userId")

    usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val userData = dataSnapshot.getValue(User::class.java)
            onUserDataFetched(userData)
        }

        override fun onCancelled(databaseError: DatabaseError) {
            // Handle errors
            onUserDataFetched(null)
            println("Error fetching user data: ${databaseError.message}")
        }
    })
}*/

fun getResourceId(name: String, type: String, context: Context): Int {
    return context.resources.getIdentifier(name, type, context.packageName)
}
