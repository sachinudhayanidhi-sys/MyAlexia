package com.alexia.companion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Green = Color(0xFF9BEA35)
private val Deep = Color(0xFF10130F)
private val Panel = Color(0xFF1B2119)
private val Muted = Color(0xFFAAB3A4)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AlexiaApp() }
    }
}

data class ChatLine(val text: String, val fromAlexia: Boolean)

@Composable
fun AlexiaApp() {
    var tab by remember { mutableStateOf(0) }
    var message by remember { mutableStateOf("") }
    var mood by remember { mutableStateOf("Calm") }
    var habitDone by remember { mutableStateOf(false) }
    var chat by remember {
        mutableStateOf(listOf(ChatLine("Hey! I'm Alexia. 💚 I'm here to listen, help you reflect, and make today a little clearer. What's on your mind?", true)))
    }

    MaterialTheme(colorScheme = darkColorScheme(
        primary = Green, background = Deep, surface = Panel,
        onBackground = Color.White, onSurface = Color.White
    )) {
        Scaffold(
            containerColor = Deep,
            bottomBar = {
                NavigationBar(containerColor = Panel, contentColor = Green) {
                    NavigationBarItem(selected = tab == 0, onClick = { tab = 0 },
                        icon = { Icon(Icons.Default.ChatBubbleOutline, null) }, label = { Text("Chat") })
                    NavigationBarItem(selected = tab == 1, onClick = { tab = 1 },
                        icon = { Icon(Icons.Default.Insights, null) }, label = { Text("Growth") })
                    NavigationBarItem(selected = tab == 2, onClick = { tab = 2 },
                        icon = { Icon(Icons.Default.Person, null) }, label = { Text("Profile") })
                }
            }
        ) { padding ->
            when (tab) {
                0 -> ChatScreen(chat, message, { message = it }, {
                    if (message.isNotBlank()) {
                        val userText = message.trim()
                        chat = chat + ChatLine(userText, false) +
                            ChatLine(replyFor(userText), true)
                        message = ""
                    }
                }, Modifier.padding(padding))
                1 -> GrowthScreen(mood, { mood = it }, habitDone, { habitDone = it }, Modifier.padding(padding))
                else -> ProfileScreen(Modifier.padding(padding))
            }
        }
    }
}

fun replyFor(input: String): String {
    val s = input.lowercase()
    return when {
        "sad" in s || "bad" in s || "stress" in s ->
            "That sounds heavy. You don't have to solve everything at once. What feels like the biggest part of it right now?"
        "happy" in s || "good" in s ->
            "I love that for you. What do you think helped make today feel good?"
        "habit" in s || "goal" in s ->
            "Let's make it small enough to repeat. What's one action you could comfortably do today?"
        else -> "Thanks for sharing that with me. What do you think is underneath that feeling? We can take it one step at a time."
    }
}

@Composable
fun Header() {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(18.dp)) {
        Image(painterResource(R.drawable.alexia_avatar), null,
            Modifier.size(54.dp).clip(CircleShape), contentScale = ContentScale.Crop)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text("Alexia", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(7.dp).clip(CircleShape).background(Green))
                Spacer(Modifier.width(6.dp))
                Text("Your mindful companion", color = Muted, fontSize = 12.sp)
            }
        }
        Icon(Icons.Default.Favorite, null, tint = Green)
    }
}

@Composable
fun ChatScreen(
    chat: List<ChatLine>, message: String, onMessage: (String) -> Unit,
    onSend: () -> Unit, modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxSize()) {
        Header()
        Text("A little space for you.", color = Muted, modifier = Modifier.padding(horizontal = 18.dp))
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(chat) { line ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement =
                    if (line.fromAlexia) Arrangement.Start else Arrangement.End) {
                    Surface(
                        color = if (line.fromAlexia) Panel else Color(0xFF31451F),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.widthIn(max = 310.dp)
                    ) {
                        Text(line.text, color = Color.White, modifier = Modifier.padding(14.dp), lineHeight = 21.sp)
                    }
                }
            }
        }
        Row(
            Modifier.fillMaxWidth().padding(12.dp).clip(RoundedCornerShape(28.dp))
                .background(Panel).padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(value = message, onValueChange = onMessage,
                modifier = Modifier.weight(1f), textStyle = LocalTextStyle.current.copy(color = Color.White),
                decorationBox = { inner ->
                    Box {
                        if (message.isEmpty()) Text("Message Alexia...", color = Muted)
                        inner()
                    }
                })
            IconButton(onClick = onSend) {
                Icon(Icons.Default.Send, "Send", tint = Green)
            }
        }
        Text("Alexia is an AI prototype. Replies are generated locally for this demo.",
            color = Muted, fontSize = 10.sp, modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 8.dp))
    }
}

@Composable
fun GrowthScreen(mood: String, onMood: (String) -> Unit, done: Boolean, onDone: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Header()
        Text("Your growth space", fontSize = 25.sp, fontWeight = FontWeight.Bold)
        Text("Small check-ins can help you notice patterns over time.", color = Muted)
        Card(colors = CardDefaults.cardColors(containerColor = Panel)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("How are you feeling today?", fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Calm", "Happy", "Tired", "Anxious").forEach {
                        FilterChip(selected = mood == it, onClick = { onMood(it) }, label = { Text(it) })
                    }
                }
                Text("Today's mood: $mood", color = Green)
            }
        }
        Card(colors = CardDefaults.cardColors(containerColor = Panel)) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Daily intention", fontWeight = FontWeight.SemiBold)
                    Text("Take 5 minutes for yourself", color = Muted, fontSize = 13.sp)
                }
                Checkbox(checked = done, onCheckedChange = onDone)
            }
        }
        Text("Reflection prompt", fontWeight = FontWeight.SemiBold)
        Text("What is one thing you can give yourself credit for today?", color = Muted)
    }
}

@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Header()
        Text("About Alexia", fontSize = 25.sp, fontWeight = FontWeight.Bold)
        Text("A warm, thoughtful AI companion for reflection, habits, and everyday self-awareness.")
        Card(colors = CardDefaults.cardColors(containerColor = Panel)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Your privacy matters", fontWeight = FontWeight.Bold, color = Green)
                Text("This starter app keeps its demo chat and check-ins in app memory only. It does not connect to a server or send your messages anywhere.")
            }
        }
        Text("Version 1.0 • Personal prototype", color = Muted)
    }
}
