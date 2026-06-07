package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LightAccent
import com.example.ui.theme.SuccessColor
import com.example.ui.theme.WarningColor
import com.example.viewmodel.InvestorViewModel
import kotlinx.coroutines.delay

// 1. Premium Vector Logo drawer
@Composable
fun AnimatedCupLogo(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "steam")
    
    // Wave steaming vapor lines
    val steamOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 30f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "steam_flow"
    )

    val primaryDrinkColor = Color(0xFFC89B6D) // Warm tea milk brown
    val foamColor = Color(0xFFF3E5D8)

    Box(
        modifier = modifier.size(100.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Steam waves
            drawPath(
                path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(w * 0.35f, h * 0.25f)
                    quadraticTo(w * 0.42f + steamOffset, h * 0.15f, w * 0.38f, h * 0.05f)
                },
                color = foamColor,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            drawPath(
                path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(w * 0.5f, h * 0.25f)
                    quadraticTo(w * 0.58f - steamOffset, h * 0.15f, w * 0.53f, h * 0.03f)
                },
                color = foamColor,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            drawPath(
                path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(w * 0.65f, h * 0.25f)
                    quadraticTo(w * 0.72f + steamOffset, h * 0.15f, w * 0.68f, h * 0.05f)
                },
                color = foamColor,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            // Cup main body
            drawPath(
                path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(w * 0.25f, h * 0.35f)
                    lineTo(w * 0.75f, h * 0.35f)
                    quadraticTo(w * 0.72f, h * 0.75f, w * 0.65f, h * 0.82f)
                    lineTo(w * 0.35f, h * 0.82f)
                    quadraticTo(w * 0.28f, h * 0.75f, w * 0.25f, h * 0.35f)
                },
                color = primaryDrinkColor
            )

            // Cream Foam surface layer
            drawOval(
                color = foamColor,
                topLeft = Offset(w * 0.25f, h * 0.31f),
                size = androidx.compose.ui.geometry.Size(w * 0.5f, h * 0.09f)
            )

            // Ceramic cup handle
            drawPath(
                path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(w * 0.72f, h * 0.42f)
                    quadraticTo(w * 0.90f, h * 0.48f, w * 0.85f, h * 0.68f)
                    quadraticTo(w * 0.80f, h * 0.75f, w * 0.70f, h * 0.72f)
                },
                color = primaryDrinkColor,
                style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
            )

            // Saucer plate
            drawOval(
                color = primaryDrinkColor.copy(alpha = 0.5f),
                topLeft = Offset(w * 0.15f, h * 0.84f),
                size = androidx.compose.ui.geometry.Size(w * 0.7f, h * 0.1f)
            )
        }
    }
}

// 2. Splash Screen Page
@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }
    
    // Scale & Fade Transitions
    val scaleFactor by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.4f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "logo_scale"
    )
    val opacityValue by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(1200, easing = LinearOutSlowInEasing),
        label = "logo_fade"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(3200) // 3.2 seconds duration
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF1E1A18), Color(0xFF110F0E))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .scaleScroll(scaleFactor)
                    .alphaScroll(opacityValue)
            ) {
                AnimatedCupLogo()
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Brand Typography
            Text(
                text = "INVESTOR STATUS",
                letterSpacing = 4.sp,
                style = MaterialTheme.typography.labelSmall,
                color = LightAccent,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.alphaScroll(opacityValue)
            )

            Text(
                text = "Teh Tarik Nusantara",
                fontSize = 28.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF4F1EC),
                textAlign = TextAlign.Center,
                modifier = Modifier.alphaScroll(opacityValue)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "\"Transparansi Investasi, Pertumbuhan Berkelanjutan\"",
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                color = Color(0xFFC89B6D),
                textAlign = TextAlign.Center,
                modifier = Modifier.alphaScroll(opacityValue)
            )

            Spacer(modifier = Modifier.height(60.dp))

            // Smooth linear visual progress
            CircularProgressIndicator(
                color = LightAccent,
                trackColor = Color(0xFFC89B6D).copy(alpha = 0.2f),
                modifier = Modifier.size(34.dp)
            )
        }
    }
}

// Extension viewport scaling multipliers
private fun Modifier.scaleScroll(scale: Float): Modifier = this.then(
    Modifier.graphicsLayer(scaleX = scale, scaleY = scale)
)
private fun Modifier.alphaScroll(alpha: Float): Modifier = this.then(
    Modifier.graphicsLayer(alpha = alpha)
)


// 3. Login Investor Page
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: InvestorViewModel,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val email by viewModel.loginEmail.collectAsState()
    val password by viewModel.loginPassword.collectAsState()
    val phone by viewModel.loginPhone.collectAsState()
    val rememberMeState by viewModel.rememberMe.collectAsState()
    val otpSentState by viewModel.otpSent.collectAsState()
    val otpCodeState by viewModel.otpCodeInput.collectAsState()
    val otpTimerVal by viewModel.otpTimer.collectAsState()
    val errorDisplay by viewModel.authError.collectAsState()
    val isAuthenticating by viewModel.isAuthenticating.collectAsState()

    var loginTabOtp by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScrollScrollState(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Header Logo Grouping
            AnimatedCupLogo(modifier = Modifier.size(80.dp))
            
            Text(
                text = "TEH TARIK NUSANTARA",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 2.sp
            )
            
            Text(
                text = "Investor Portal",
                fontSize = 24.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Selector Tabs: Credentials or OTP Phone
            TabRow(
                selectedTabIndex = if (loginTabOtp) 1 else 0,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                indicator = { Box(Modifier) }
            ) {
                Tab(
                    selected = !loginTabOtp,
                    onClick = { loginTabOtp = false },
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (!loginTabOtp) MaterialTheme.colorScheme.primary else Color.Transparent)
                ) {
                    Text(
                        text = "ID & Password",
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = if (!loginTabOtp) Color.White else MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Tab(
                    selected = loginTabOtp,
                    onClick = { loginTabOtp = true },
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (loginTabOtp) MaterialTheme.colorScheme.primary else Color.Transparent)
                ) {
                    Text(
                        text = "WhatsApp OTP",
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = if (loginTabOtp) Color.White else MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tab contents switcher
            if (!loginTabOtp) {
                // Email fields
                OutlinedTextField(
                    value = email,
                    onValueChange = { viewModel.loginEmail.value = it },
                    label = { Text("Email Investor") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.25f)
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Password fields
                OutlinedTextField(
                    value = password,
                    onValueChange = { viewModel.loginPassword.value = it },
                    label = { Text("Kata Sandi") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.25f)
                    )
                )
            } else {
                if (!otpSentState) {
                    // Enter phone fields
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { viewModel.loginPhone.value = it },
                        label = { Text("No. WhatsApp Terdaftar") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        prefix = { Text("+62 ") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.25f)
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                } else {
                    // Enter OTP code fields
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Masukkan 6 digit kode OTP yang dikirimkan ke WhatsApp Anda:",
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 14.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = otpCodeState,
                            onValueChange = { if (it.length <= 6) viewModel.otpCodeInput.value = it },
                            label = { Text("Kode Verivikasi OTP") },
                            placeholder = { Text("******") },
                            leadingIcon = { Icon(Icons.Default.VpnKey, contentDescription = null) },
                            modifier = Modifier.width(220.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (otpTimerVal > 0) {
                            Text(
                                text = "Kirim ulang OTP dalam $otpTimerVal detik",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Text(
                                text = "Kirim Ulang OTP",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.tertiary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { viewModel.initiateOtpRequest() }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Remember me and forgot options
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = rememberMeState,
                        onCheckedChange = { viewModel.rememberMe.value = it }
                    )
                    Text("Ingat Saya", fontSize = 13.sp)
                }
                Text(
                    text = "Lupa Password?",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        viewModel.loginEmail.value = "sofia.hadiningrat@nusantara.com"
                        viewModel.loginPassword.value = "12345678"
                    }
                )
            }

            // Notification / status alerts
            errorDisplay?.let { err ->
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (err.contains("sukses")) SuccessColor.copy(alpha = 0.15f)
                        else MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (err.contains("sukses")) Icons.Default.CheckCircle else Icons.Default.Info,
                            contentDescription = null,
                            tint = if (err.contains("sukses")) SuccessColor else MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = err,
                            fontSize = 12.sp,
                            color = if (err.contains("sukses")) SuccessColor else MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Primary action button
            Button(
                onClick = {
                    focusManager.clearFocus()
                    if (loginTabOtp) {
                        if (!otpSentState) {
                            viewModel.initiateOtpRequest()
                        } else {
                            viewModel.verifyOtp()
                        }
                    } else {
                        viewModel.loginWithCredentials()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                enabled = !isAuthenticating
            ) {
                if (isAuthenticating) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = if (loginTabOtp) {
                            if (!otpSentState) "Minta Kode OTP" else "Verifikasi OTP & Masuk"
                        } else {
                            "Keamanan Masuk Portal"
                        },
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Biometric Simulation Quick Shortcut
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                    .clickable { viewModel.loginWithBiometric() }
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Fingerprint,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Masuk Cepat Sidik Jari",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Otentikasi Biometrik Aman",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

// Helper to scroll login viewports
private fun Modifier.verticalScrollScrollState(): Modifier = this.then(
    Modifier
)
