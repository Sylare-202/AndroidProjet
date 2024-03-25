package fr.isen.combes.androidprojet

import android.annotation.SuppressLint
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.isen.combes.androidprojet.ui.theme.AndroidProjetTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.SideEffect

import androidx.compose.ui.platform.LocalContext

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidProjetTheme {
                ProfileScreen()
            }
        }
    }
}

@Composable
fun ProfileScreen() {
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

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                ProfileHeader()
                Spacer(modifier = Modifier.width(16.dp))
                ProfileInfo()
                Spacer(modifier = Modifier.height(16.dp))

            }
            Spacer(modifier = Modifier.height(16.dp))
            EditProfileButton()
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
fun ProfileInfo() {
        Column {
            Row {
                Text(text = "John Doe", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(4.dp))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "@johndoe", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            PublicationInformation()
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Developer at XYZ Corp", style = MaterialTheme.typography.bodyLarge)
        }
}

@Composable
fun PublicationInformation() {
    Row {
        Column(verticalArrangement = Arrangement.Center) {
            Text(
                text = "12",
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
                text = "12",
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
                text = "12",
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
fun ProfileHeader() {
    Image(
        painter = painterResource(id = R.drawable.avatar),
        contentDescription = "Profile Image",
        modifier = Modifier
            .size(120.dp)
            .padding(16.dp)
            .clip(MaterialTheme.shapes.large),
        contentScale = ContentScale.Crop,
    )
}
@Composable
fun EditProfileButton() {
    Button(
        onClick = { /* Handle Edit Profile button click */ },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "Edit Profile")
    }
}


@Composable
fun PostGrid() {
    val postCount = 9 // Total number of posts to display
    val postsPerRow = 3 // Number of posts per row

    // Calculate number of rows needed
    val rowCount = (postCount + postsPerRow - 1) / postsPerRow

    Column {
        repeat(rowCount) { rowIndex ->
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(postsPerRow) { columnIndex ->
                    val postIndex = rowIndex * postsPerRow + columnIndex + 1
                    if (postIndex <= postCount) {
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
            .padding(1.dp),
        contentScale = ContentScale.Crop
    )
}


fun getResourceId(name: String, type: String, context: Context): Int {
    return context.resources.getIdentifier(name, type, context.packageName)
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AndroidProjetTheme {
        ProfileScreen()
    }
}
