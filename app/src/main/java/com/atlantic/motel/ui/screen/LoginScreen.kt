package com.atlantic.motel.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.atlantic.motel.data.model.UserGender
import com.atlantic.motel.ui.theme.*
import com.atlantic.motel.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    loginViewModel: LoginViewModel = viewModel()
) {
    val state by loginViewModel.state.collectAsState()
    val registerState by loginViewModel.registerState.collectAsState()
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showRegisterDialog by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(state.success) {
        if (state.success) {
            onLoginSuccess()
        }
    }

    LaunchedEffect(registerState.success) {
        if (registerState.success) {
            loginViewModel.clearRegisterState()
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack)
            .systemBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Motel Manager",
                fontFamily = CormorantGaramondFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 34.sp,
                color = TextPrimary,
                letterSpacing = 1.sp
            )

            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(2.dp)
                    .background(Champagne.copy(alpha = 0.6f))
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it
                    loginViewModel.clearError()
                },
                label = { Text("Usuário", color = TextSecondary) },
                leadingIcon = {
                    Icon(Icons.Default.Person, null, tint = TextSecondary)
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = DeepCrimson,
                    unfocusedBorderColor = BorderDark,
                    focusedContainerColor = SurfaceBlack,
                    unfocusedContainerColor = SurfaceBlack,
                    cursorColor = DeepCrimson
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    loginViewModel.clearError()
                },
                label = { Text("Senha", color = TextSecondary) },
                leadingIcon = {
                    Icon(Icons.Default.Lock, null, tint = TextSecondary)
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Default.VisibilityOff
                            else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = TextSecondary
                        )
                    }
                },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        loginViewModel.login(username, password)
                    }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = DeepCrimson,
                    unfocusedBorderColor = BorderDark,
                    focusedContainerColor = SurfaceBlack,
                    unfocusedContainerColor = SurfaceBlack,
                    cursorColor = DeepCrimson
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            )

            if (state.error != null) {
                Text(
                    text = state.error!!,
                    color = MetallicRed,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = {
                    focusManager.clearFocus()
                    loginViewModel.login(username, password)
                },
                enabled = !state.isLoading && username.isNotBlank() && password.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DeepCrimson,
                    contentColor = Color.White,
                    disabledContainerColor = DeepBurgundy.copy(alpha = 0.5f),
                    disabledContentColor = TextDisabled
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("ENTRAR", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 2.sp)
                }
            }

            OutlinedButton(
                onClick = { showRegisterDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Champagne),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(Champagne.copy(alpha = 0.4f))
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("CADASTRAR", fontSize = 13.sp, fontWeight = FontWeight.Medium, letterSpacing = 1.sp)
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    if (showRegisterDialog) {
        RegisterDialog(
            onDismiss = {
                showRegisterDialog = false
                loginViewModel.clearRegisterState()
            },
            onRegister = { displayName, username, password, gender ->
                loginViewModel.register(displayName, username, password, gender)
            },
            registerState = registerState
        )
    }
}

@Composable
fun RegisterDialog(
    onDismiss: () -> Unit,
    onRegister: (String, String, String, UserGender) -> Unit,
    registerState: com.atlantic.motel.viewmodel.RegisterState
) {
    var displayName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var selectedGender by remember { mutableStateOf(UserGender.MASCULINO) }

    CloseableDialog(
        title = "Novo Usuário",
        onDismiss = onDismiss
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DarkTextField(
                value = displayName,
                onValueChange = { displayName = it },
                label = "Nome de exibição"
            )
            DarkTextField(
                value = username,
                onValueChange = { username = it },
                label = "Usuário"
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Senha", color = TextSecondary) },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Default.VisibilityOff
                            else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = TextSecondary
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = DeepCrimson,
                    unfocusedBorderColor = BorderDark,
                    focusedContainerColor = SurfaceBlack,
                    unfocusedContainerColor = SurfaceBlack,
                    cursorColor = DeepCrimson
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedGender == UserGender.MASCULINO,
                    onClick = { selectedGender = UserGender.MASCULINO },
                    label = { Text("Masculino", fontSize = 13.sp) },
                    leadingIcon = if (selectedGender == UserGender.MASCULINO) {
                        { Icon(Icons.Default.Person, null, modifier = Modifier.size(16.dp)) }
                    } else null,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = DeepCrimson.copy(alpha = 0.2f),
                        selectedLabelColor = TextPrimary,
                        selectedLeadingIconColor = DeepCrimson,
                        containerColor = SurfaceBlack,
                        labelColor = TextSecondary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = selectedGender == UserGender.FEMININO,
                    onClick = { selectedGender = UserGender.FEMININO },
                    label = { Text("Feminino", fontSize = 13.sp) },
                    leadingIcon = if (selectedGender == UserGender.FEMININO) {
                        { Icon(Icons.Default.Person, null, modifier = Modifier.size(16.dp)) }
                    } else null,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = DeepCrimson.copy(alpha = 0.2f),
                        selectedLabelColor = TextPrimary,
                        selectedLeadingIconColor = DeepCrimson,
                        containerColor = SurfaceBlack,
                        labelColor = TextSecondary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                )
            }

            if (registerState.error != null) {
                Text(
                    text = registerState.error!!,
                    color = MetallicRed,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            DarkButton(
                onClick = {
                    onRegister(displayName, username, password, selectedGender)
                },
                text = if (registerState.isLoading) "Cadastrando..." else "Cadastrar",
                enabled = !registerState.isLoading && displayName.isNotBlank() && username.isNotBlank() && password.isNotBlank()
            )
        }
    }
}
