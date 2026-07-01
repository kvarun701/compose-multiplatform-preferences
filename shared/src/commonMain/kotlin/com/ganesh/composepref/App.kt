package com.ganesh.composepref

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class PreferenceType {
    STRING, INT, LONG, FLOAT, BOOLEAN
}

@Composable
fun App(storage: KeyValueStorage = remember { InMemoryKeyValueStorage() }) {
    MaterialTheme {
        var selectedNamespace by remember { mutableStateOf("default") }
        
        val activeStorage = remember(storage, selectedNamespace) {
            NamespacedKeyValueStorage(storage, selectedNamespace)
        }
        
        var storedKeys by remember(activeStorage) {
            mutableStateOf(
                activeStorage.getString("__keys__")
                    ?.split(",")
                    ?.filter { it.isNotEmpty() && it != "__keys__" }
                    ?.toSet()
                    ?: emptySet()
            )
        }
        
        var keyInput by remember { mutableStateOf("") }
        var valueInput by remember { mutableStateOf("") }
        var selectedType by remember { mutableStateOf(PreferenceType.STRING) }
        var booleanInput by remember { mutableStateOf(true) }
        
        var feedbackMessage by remember { mutableStateOf("") }
        
        fun updateKeysMetadata(newKeys: Set<String>) {
            storedKeys = newKeys
            activeStorage.putString("__keys__", newKeys.joinToString(","))
        }
        
        fun savePreference() {
            val key = keyInput.trim()
            if (key.isEmpty() || key == "__keys__") {
                feedbackMessage = "Invalid Key!"
                return
            }
            
            try {
                when (selectedType) {
                    PreferenceType.STRING -> {
                        activeStorage.putString(key, valueInput)
                    }
                    PreferenceType.INT -> {
                        val v = valueInput.toIntOrNull()
                        if (v == null) { feedbackMessage = "Value is not a valid Int!"; return }
                        activeStorage.putInt(key, v)
                    }
                    PreferenceType.LONG -> {
                        val v = valueInput.toLongOrNull()
                        if (v == null) { feedbackMessage = "Value is not a valid Long!"; return }
                        activeStorage.putLong(key, v)
                    }
                    PreferenceType.FLOAT -> {
                        val v = valueInput.toFloatOrNull()
                        if (v == null) { feedbackMessage = "Value is not a valid Float!"; return }
                        activeStorage.putFloat(key, v)
                    }
                    PreferenceType.BOOLEAN -> {
                        activeStorage.putBoolean(key, booleanInput)
                    }
                }
                
                updateKeysMetadata(storedKeys + key)
                feedbackMessage = "Saved key: $key"
                keyInput = ""
                valueInput = ""
            } catch (e: Exception) {
                feedbackMessage = "Error: ${e.message}"
            }
        }
        
        fun deletePreference(key: String) {
            activeStorage.remove(key)
            updateKeysMetadata(storedKeys - key)
            feedbackMessage = "Deleted key: $key"
        }
        
        fun clearAll() {
            storedKeys.forEach { activeStorage.remove(it) }
            updateKeysMetadata(emptySet())
            feedbackMessage = "Cleared namespace '$selectedNamespace'"
        }
        
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🚀 ComposePref",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                
                Text(
                    text = "Lightweight Multiplatform Preference Storage",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp),
                    textAlign = TextAlign.Center
                )
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Text(
                        text = "Platform: ${getPlatform().name}",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
                
                if (feedbackMessage.isNotEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = feedbackMessage,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                fontSize = 13.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "✕",
                                modifier = Modifier
                                    .clickable { feedbackMessage = "" }
                                    .padding(horizontal = 8.dp),
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Preferences Editor",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Active Namespace:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf("default", "settings", "user_profile").forEach { ns ->
                                        val selected = selectedNamespace == ns
                                        Button(
                                            onClick = { selectedNamespace = ns },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                                contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                            ),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(ns, fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                        
                        OutlinedTextField(
                            value = keyInput,
                            onValueChange = { keyInput = it },
                            label = { Text("Key") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        
                        Text(
                            text = "Data Type",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            PreferenceType.values().forEach { type ->
                                val selected = selectedType == type
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedType = type },
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                                    ),
                                    border = if (selected) BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = type.name.lowercase().capitalize(),
                                            fontSize = 11.sp,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                        
                        if (selectedType == PreferenceType.BOOLEAN) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Boolean Value: ${booleanInput.toString().uppercase()}", fontSize = 14.sp)
                                Switch(
                                    checked = booleanInput,
                                    onCheckedChange = { booleanInput = it }
                                )
                            }
                        } else {
                            OutlinedTextField(
                                value = valueInput,
                                onValueChange = { valueInput = it },
                                label = { Text("Value") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { savePreference() },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Save Key")
                            }
                            
                            OutlinedButton(
                                onClick = { clearAll() },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.error
                                ),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                            ) {
                                Text("Clear All")
                            }
                        }
                    }
                    
                    Column(
                        modifier = Modifier
                            .weight(1.2f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Stored Values ($selectedNamespace)",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        if (storedKeys.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .background(
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No keys stored in this namespace.",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(storedKeys.toList().sorted()) { key ->
                                    val isBool = activeStorage.getBoolean(key, false) != activeStorage.getBoolean(key, true)
                                    val stringVal = activeStorage.getString(key)
                                    
                                    val displayValue: String
                                    val displayType: String
                                    
                                    if (isBool) {
                                        displayValue = activeStorage.getBoolean(key).toString().uppercase()
                                        displayType = "Boolean"
                                    } else {
                                        val intVal = activeStorage.getInt(key, -999999)
                                        val longVal = activeStorage.getLong(key, -999999L)
                                        val floatVal = activeStorage.getFloat(key, -999999f)
                                        
                                        if (intVal != -999999 && activeStorage.getInt(key, 999999) == intVal) {
                                            displayValue = intVal.toString()
                                            displayType = "Int"
                                        } else if (longVal != -999999L && activeStorage.getLong(key, 999999L) == longVal) {
                                            displayValue = longVal.toString()
                                            displayType = "Long"
                                        } else if (floatVal != -999999f && activeStorage.getFloat(key, 999999f) == floatVal) {
                                            displayValue = floatVal.toString()
                                            displayType = "Float"
                                        } else {
                                            displayValue = stringVal ?: "null"
                                            displayType = "String"
                                        }
                                    }
                                    
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(
                                                        text = key,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 15.sp,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Card(
                                                        colors = CardDefaults.cardColors(
                                                            containerColor = MaterialTheme.colorScheme.primaryContainer
                                                        ),
                                                        shape = RoundedCornerShape(4.dp)
                                                    ) {
                                                        Text(
                                                            text = displayType,
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Medium,
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                                        )
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = displayValue,
                                                    fontSize = 14.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                            IconButton(
                                                onClick = { deletePreference(key) }
                                            ) {
                                                Text("🗑️")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

class NamespacedKeyValueStorage(
    private val parent: KeyValueStorage,
    private val namespace: String
) : KeyValueStorage {
    
    private fun prefixKey(key: String): String = "ns.$namespace.$key"
    
    override fun putString(key: String, value: String) = parent.putString(prefixKey(key), value)
    override fun getString(key: String, defaultValue: String?): String? = parent.getString(prefixKey(key), defaultValue)
    
    override fun putInt(key: String, value: Int) = parent.putInt(prefixKey(key), value)
    override fun getInt(key: String, defaultValue: Int): Int = parent.getInt(prefixKey(key), defaultValue)
    
    override fun putLong(key: String, value: Long) = parent.putLong(prefixKey(key), value)
    override fun getLong(key: String, defaultValue: Long): Long = parent.getLong(prefixKey(key), defaultValue)
    
    override fun putFloat(key: String, value: Float) = parent.putFloat(prefixKey(key), value)
    override fun getFloat(key: String, defaultValue: Float): Float = parent.getFloat(prefixKey(key), defaultValue)
    
    override fun putBoolean(key: String, value: Boolean) = parent.putBoolean(prefixKey(key), value)
    override fun getBoolean(key: String, defaultValue: Boolean): Boolean = parent.getBoolean(prefixKey(key), defaultValue)
    
    override fun remove(key: String) = parent.remove(prefixKey(key))
    override fun clear() = parent.clear()
    override fun contains(key: String): Boolean = parent.contains(prefixKey(key))
}

private fun String.capitalize(): String {
    return if (isNotEmpty()) this[0].uppercaseChar() + substring(1) else this
}