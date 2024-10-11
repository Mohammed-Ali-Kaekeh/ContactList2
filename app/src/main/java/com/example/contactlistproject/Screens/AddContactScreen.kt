package com.example.contactlistproject.Screens


import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.example.contactlistproject.Contact
import com.example.contactlistproject.R
import com.example.contactlistproject.addContact


@Composable
fun AddContact(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var phoneNO by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var currentId by remember { mutableStateOf(1) }

    val context = LocalContext.current

    val imageCropLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            imageUri = result.uriContent
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ImagePicker(
            imageUri = imageUri,
            onClickSelectImage = {
                val cropOption = CropImageContractOptions(null, CropImageOptions())
                imageCropLauncher.launch(cropOption)
            },
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color.Gray)
        )

        Spacer(modifier = Modifier.height(20.dp))

        NameInputField(name) { name = it }
        Spacer(modifier = Modifier.height(10.dp))
        PhoneNOInputField(phoneNO) { phoneNO = it }
        Spacer(modifier = Modifier.height(10.dp))
        AddressInputField(address) { address = it }
        Spacer(modifier = Modifier.height(10.dp))
        EmailInputField(email) { email = it }
        Spacer(modifier = Modifier.height(10.dp))

        IconButtonWithText(
            name = name,
            phoneNO = phoneNO,
            email = email,
            imageUri = imageUri,
            currentId = currentId,
            onAddContact = {
                val newContact = Contact(
                    id = currentId,
                    name = name,
                    phoneNO = phoneNO,
                    email = email,
                    address = address,
                    image = imageUri
                )
                addContact(newContact)
                currentId++
                name = ""
                phoneNO = ""
                email = ""
                imageUri = null

                Toast.makeText(context, "Added Successfully", Toast.LENGTH_SHORT).show()

                navController.popBackStack() // Return to MainPage
            }
        )
    }
}

@Composable
fun ImagePicker(
    imageUri: Uri?,
    onClickSelectImage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    if (imageUri != null) {
        bitmap = if (Build.VERSION.SDK_INT < 28) {
            MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
        } else {
            val source = ImageDecoder.createSource(context.contentResolver, imageUri)
            ImageDecoder.decodeBitmap(source)
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap!!.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(120.dp)
                    .border(2.dp, Color.Blue, CircleShape)
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.baseline_person_24),
                contentDescription = null,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(120.dp)
                    .background(Color.Gray)
            )
        }

        IconButton(
            onClick = { onClickSelectImage() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(40.dp)
                .background(Color.Gray, CircleShape)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_photo_camera_24),
                contentDescription = "Select Image",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameInputField(name: String, onNameChange: (String) -> Unit) {
    OutlinedTextField(
        value = name,
        onValueChange = onNameChange,
        label = { Text("Name") },
        singleLine = true,
        textStyle = TextStyle(Color.Black),
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneNOInputField(phoneNO: String, onPhoneNOChange: (String) -> Unit) {
    var errorMessage by remember { mutableStateOf<String?>(null) }

    OutlinedTextField(
        value = phoneNO,
        onValueChange = { input ->
            if(input.all { it.isDigit() } && input.length <= 10) {
                onPhoneNOChange(input)
                errorMessage = null
            } else {
                errorMessage = "Please Enter a valid Phone Number."
            }
        },
        label = { Text(text = "Phone Number") },
        textStyle = TextStyle(Color.Black),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Phone
        ),
        isError = errorMessage != null,
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    )

    if (errorMessage != null) {
        Text(
            text = errorMessage ?: "",
            color = Color.Red,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressInputField(address: String, onAddressChange: (String) -> Unit) {
    OutlinedTextField(
        value = address,
        onValueChange = onAddressChange,
        label = { Text(text = "Address") },
        placeholder = { Text(text = "Street, City, Country") },
        singleLine = true,
        textStyle = TextStyle(Color.Black),
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailInputField(email: String, onEmailChange: (String) -> Unit) {
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val emailPattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"

    OutlinedTextField(
        value = email,
        onValueChange = { input ->
            onEmailChange(input)
            errorMessage = if (input.matches(emailPattern.toRegex())) {
                null
            } else {
                "Please enter a valid email address."
            }
        },
        label = { Text("Enter Email") },
        textStyle = TextStyle(Color.Black),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Email
        ),
        isError = errorMessage != null,
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    )

    if (errorMessage != null) {
        Text(
            text = errorMessage ?: "",
            color = Color.Red,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun IconButtonWithText(
    name: String,
    phoneNO: String,
    email: String,
    imageUri: Uri?,
    currentId: Int,
    onAddContact: () -> Unit
) {
    Button(
        onClick = {
            onAddContact()
        },
        modifier = Modifier.padding(top = 16.dp)
    ) {
        Icon(Icons.Default.Add, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text("ADD")
    }
}
