package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Priority
import com.example.data.model.TaskCategory
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickAddTaskDialog(
    onDismiss: () -> Unit,
    onAddTask: (title: String, category: TaskCategory, duration: Int, priority: Priority, why: String, dod: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var durationText by remember { mutableStateOf("30") }
    var selectedCategory by remember { mutableStateOf(TaskCategory.C_PROGRAMMING) }
    var selectedPriority by remember { mutableStateOf(Priority.MEDIUM) }
    var whyItMatters by remember { mutableStateOf("") }
    var definitionOfDone by remember { mutableStateOf("") }
    var categoryDropdownOpen by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .testTag("quick_add_task_dialog")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Add Daily Engineering Task",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title") },
                    placeholder = { Text("E.g., Practice C string functions") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("task_title_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Category selector
                    ExposedDropdownMenuBox(
                        expanded = categoryDropdownOpen,
                        onExpandedChange = { categoryDropdownOpen = !categoryDropdownOpen },
                        modifier = Modifier.weight(1.3f)
                    ) {
                        OutlinedTextField(
                            value = selectedCategory.label,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownOpen) },
                            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        )
                        ExposedDropdownMenu(
                            expanded = categoryDropdownOpen,
                            onDismissRequest = { categoryDropdownOpen = false }
                        ) {
                            TaskCategory.entries.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat.label) },
                                    onClick = {
                                        selectedCategory = cat
                                        categoryDropdownOpen = false
                                    }
                                )
                            }
                        }
                    }

                    // Duration
                    OutlinedTextField(
                        value = durationText,
                        onValueChange = { durationText = it },
                        label = { Text("Mins") },
                        modifier = Modifier.weight(0.7f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = whyItMatters,
                    onValueChange = { whyItMatters = it },
                    label = { Text("Why it matters (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = definitionOfDone,
                    onValueChange = { definitionOfDone = it },
                    label = { Text("Definition of Done") },
                    placeholder = { Text("E.g., Pass all test cases without warnings") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                val duration = durationText.toIntOrNull() ?: 30
                                onAddTask(title, selectedCategory, duration, selectedPriority, whyItMatters, definitionOfDone)
                                onDismiss()
                            }
                        },
                        enabled = title.isNotBlank(),
                        modifier = Modifier.testTag("submit_new_task_btn")
                    ) {
                        Text("Add to Today")
                    }
                }
            }
        }
    }
}
