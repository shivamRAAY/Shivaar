package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessage
import com.example.data.model.MessageSender
import com.example.ui.ShivaarViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AiMentorScreen(
    viewModel: ShivaarViewModel,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.chatMessages.collectAsState()
    val isThinking by viewModel.isMentorThinking.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val suggestedPrompts = listOf(
        "Explain C pointers in simple Hinglish",
        "AKTU Python viva questions",
        "What should I study right now?",
        "Engineering Mathematics Cayley-Hamilton tip"
    )

    LaunchedEffect(messages.size, isThinking) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("ai_mentor_screen")
    ) {
        // --- Header ---
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(IndigoPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Mentor",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Shivaar Intelligence",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Personal B.Tech CSE Academic & Code Mentor",
                        style = MaterialTheme.typography.bodySmall,
                        color = CharcoalTextSecondary
                    )
                }
            }
        }

        // --- Chat Messages List ---
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages, key = { it.id }) { message ->
                ChatMessageBubble(message = message)
            }

            if (isThinking) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = IndigoContainer,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = IndigoPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Analyzing curriculum & synthesizing answer...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OnIndigoContainer
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- Suggested Prompt Chips ---
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(suggestedPrompts) { prompt ->
                SuggestionChip(
                    onClick = {
                        viewModel.sendMentorMessage(prompt)
                    },
                    label = {
                        Text(
                            text = prompt,
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = SurfaceCardSubtle,
                        labelColor = IndigoPrimary
                    )
                )
            }
        }

        // --- Bottom Input Bar ---
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Ask anything (viva prep, C pointers, code debug)...") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("mentor_input_field"),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = IndigoPrimary,
                        unfocusedBorderColor = SurfaceCardBorder
                    ),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            val textToSend = inputText
                            inputText = ""
                            viewModel.sendMentorMessage(textToSend)
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (inputText.isNotBlank()) IndigoPrimary else SurfaceCardSubtle)
                        .testTag("send_mentor_msg_btn"),
                    enabled = inputText.isNotBlank()
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send Message",
                        tint = if (inputText.isNotBlank()) Color.White else CharcoalTextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatMessageBubble(message: ChatMessage) {
    val isUser = message.sender == MessageSender.USER
    val context = LocalContext.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            color = if (isUser) IndigoPrimary else MaterialTheme.colorScheme.surface,
            border = if (isUser) null else androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder),
            shadowElevation = if (isUser) 0.dp else 1.dp,
            modifier = Modifier
                .widthIn(max = if (isUser) 320.dp else 360.dp)
                .testTag("chat_bubble_${message.id}")
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                if (!isUser) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = IndigoPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Shivaar Intelligence",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = IndigoPrimary
                            )
                        }

                        // Grounding badge
                        if (message.isGrounded && message.sources.isNotEmpty()) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFECFDF5),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA7F3D0))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Search Grounded",
                                        tint = Color(0xFF059669),
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "Search Grounded",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 10.sp,
                                        color = Color(0xFF047857),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }

                    // Unavailable search notice
                    if (message.isServiceUnavailable && message.errorMessage != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFFFBEB),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = Color(0xFFD97706),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "Grounding Restricted (HTTP 429) • Local Curriculum Baseline Active",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    color = Color(0xFF92400E)
                                )
                            }
                        }
                    }
                }

                // Render lines with code block detection
                val formattedText = message.text
                if (formattedText.contains("```")) {
                    RenderFormattedContent(formattedText)
                } else {
                    Text(
                        text = formattedText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }

                // --- Grounding Citations Section ---
                if (!isUser && message.citations.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = SurfaceCardBorder, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Verified Citations & Claims",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = IndigoPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        message.citations.distinctBy { it.url }.forEachIndexed { idx, citation ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = SurfaceCardSubtle,
                                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    if (citation.segmentText.isNotBlank()) {
                                        Text(
                                            text = "“${citation.segmentText.trim()}”",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = CharcoalTextSecondary,
                                            fontSize = 11.sp
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "[${idx + 1}] ${citation.title}",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = IndigoPrimary,
                                            maxLines = 1,
                                            modifier = Modifier.weight(1f)
                                        )

                                        TextButton(
                                            onClick = {
                                                try {
                                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(citation.url))
                                                    context.startActivity(intent)
                                                } catch (e: Exception) {}
                                            },
                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                                contentDescription = "Open Source",
                                                modifier = Modifier.size(12.dp),
                                                tint = IndigoPrimary
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = "Open Source",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontSize = 10.sp,
                                                color = IndigoPrimary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // --- Grounding Sources Overview ---
                if (!isUser && message.sources.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Official Sources (${message.sources.size})",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = CharcoalTextMuted
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(message.sources) { src ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (src.isOfficial) IndigoContainer else SurfaceCardSubtle,
                                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder),
                                modifier = Modifier.clickable {
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(src.url))
                                        context.startActivity(intent)
                                    } catch (e: Exception) {}
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                        contentDescription = null,
                                        tint = if (src.isOfficial) IndigoPrimary else CharcoalTextSecondary,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = src.domain,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (src.isOfficial) IndigoPrimary else CharcoalTextSecondary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RenderFormattedContent(rawText: String) {
    val segments = rawText.split("```")
    segments.forEachIndexed { index, segment ->
        if (index % 2 == 1) {
            // Code Block
            val cleanCode = segment.lines().filterIndexed { i, _ -> i > 0 || !segment.startsWith("c") }.joinToString("\n")
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF1E293B),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Text(
                    text = cleanCode.trim(),
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                    color = Color(0xFFE2E8F0),
                    modifier = Modifier.padding(10.dp)
                )
            }
        } else {
            // Standard Text Block
            if (segment.isNotBlank()) {
                Text(
                    text = segment.trim(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}
