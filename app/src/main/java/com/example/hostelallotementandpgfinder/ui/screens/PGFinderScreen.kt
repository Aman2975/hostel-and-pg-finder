package com.example.hostelallotementandpgfinder.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hostelallotementandpgfinder.data.*
import com.example.hostelallotementandpgfinder.network.ApiClient
import kotlinx.coroutines.launch

@Composable
fun PGFinderScreen(
    onBackClick: () -> Unit,
    onAreaSelect: (String) -> Unit
) {
    var selectedArea by remember { mutableStateOf("") }
    var showPGs by remember { mutableStateOf(false) }
    var pgs by remember { mutableStateOf<List<PG>>(emptyList()) }
    var filteredPGs by remember { mutableStateOf<List<PG>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showBookingDialog by remember { mutableStateOf(false) }
    var selectedPG by remember { mutableStateOf<PG?>(null) }
    val coroutineScope = rememberCoroutineScope()

    val availableAreas = listOf("Bhadurgarh", "Phase 1", "Phase 2", "Near PUP", "Patiala City")

    // Load PGs from API when area is selected
    LaunchedEffect(selectedArea) {
        if (selectedArea.isNotEmpty()) {
            isLoading = true
            errorMessage = ""
            
            try {
                val response = ApiClient.apiService.getPGs()
                if (response.isSuccessful && response.body()?.success == true) {
                    pgs = response.body()?.data ?: emptyList()
                    filteredPGs = pgs.filter { it.area.equals(selectedArea, ignoreCase = true) }
                    showPGs = true
                } else {
                    errorMessage = response.body()?.message ?: "Failed to load PGs"
                }
            } catch (e: Exception) {
                errorMessage = "Network error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBackClick) {
                Text("← Back")
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "PG Finder",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Error message
            if (errorMessage.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Text(
                        text = errorMessage,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (!showPGs) {
                // Area Selection
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Select Area to Find PGs",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text("Choose an area to see available PGs:", fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Column(modifier = Modifier.selectableGroup()) {
                            availableAreas.forEach { area ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .selectable(
                                            selected = (selectedArea == area),
                                            onClick = { selectedArea = area }
                                        )
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = (selectedArea == area),
                                        onClick = { selectedArea = area }
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = area,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Show PGs
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "PGs in $selectedArea",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            TextButton(onClick = { 
                                showPGs = false
                                selectedArea = ""
                                pgs = emptyList()
                                filteredPGs = emptyList()
                            }) {
                                Text("Change Area")
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Found ${filteredPGs.size} PGs available")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (filteredPGs.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text(
                            text = "No PGs available in $selectedArea at the moment.",
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredPGs) { pg ->
                            PGItem(
                                pg = pg,
                                onBookClick = {
                                    selectedPG = pg
                                    showBookingDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Booking Dialog
    if (showBookingDialog && selectedPG != null) {
        PGBookingDialog(
            pg = selectedPG!!,
            onDismiss = { 
                showBookingDialog = false
                selectedPG = null
            },
            onBook = { studentName, studentId, contact, email, moveInDate, duration ->
                // Handle booking
                coroutineScope.launch {
                    try {
                        val bookingRequest = BookingRequest(
                            student_id = studentId,
                            property_id = selectedPG!!.id,
                            property_type = "pg",
                            special_requests = buildString {
                                append("Student Name: $studentName\n")
                                append("Contact: $contact\n")
                                append("Email: $email\n")
                                append("Move-in Date: $moveInDate\n")
                                append("Duration: $duration")
                            }
                        )
                        
                        val response = ApiClient.apiService.createPGBooking(bookingRequest)
                        
                        if (response.isSuccessful && response.body()?.success == true) {
                            showBookingDialog = false
                            selectedPG = null
                            // Show success message
                        }
                    } catch (e: Exception) {
                        // Handle error
                    }
                }
            }
        )
    }
}

@Composable
fun PGItem(pg: PG, onBookClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = pg.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "📍 ${pg.location}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "🏘️ ${pg.area}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "₹${pg.price_per_month}/month",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "👥 ${pg.gender_preference} • ${pg.available_spots} spots",
                    fontSize = 14.sp
                )
                if (pg.contact_number != null) {
                    Text(
                        text = "📞 ${pg.contact_number}",
                        fontSize = 14.sp
                    )
                }
            }

            if (!pg.amenities.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "🏠 Amenities: ${pg.amenities}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (!pg.description.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = pg.description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onBookClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Book This PG")
            }
        }
    }
}

@Composable
fun PGBookingDialog(
    pg: PG,
    onDismiss: () -> Unit,
    onBook: (String, String, String, String, String, String) -> Unit
) {
    var studentName by remember { mutableStateOf("") }
    var studentId by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var moveInDate by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Book ${pg.name}") },
        text = {
            Column {
                Text("Fill in your details to book this PG:")
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = studentName,
                    onValueChange = { studentName = it },
                    label = { Text("Full Name *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = studentId,
                    onValueChange = { studentId = it },
                    label = { Text("Student ID *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = contact,
                    onValueChange = { contact = it },
                    label = { Text("Phone Number *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = moveInDate,
                    onValueChange = { moveInDate = it },
                    label = { Text("Preferred Move-in Date *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("DD/MM/YYYY") }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it },
                    label = { Text("Duration of Stay *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("e.g., 6 months, 1 year") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (studentName.isNotBlank() && studentId.isNotBlank() && 
                        contact.isNotBlank() && email.isNotBlank() && 
                        moveInDate.isNotBlank() && duration.isNotBlank()) {
                        onBook(studentName, studentId, contact, email, moveInDate, duration)
                    }
                }
            ) {
                Text("Book Now")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}