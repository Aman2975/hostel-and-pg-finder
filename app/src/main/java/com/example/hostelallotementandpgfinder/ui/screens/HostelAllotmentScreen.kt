package com.example.hostelallotementandpgfinder.ui.screens

import androidx.compose.foundation.layout.*
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
import com.example.hostelallotementandpgfinder.network.ApiClient
import com.example.hostelallotementandpgfinder.data.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HostelAllotmentScreen(
    onBackClick: () -> Unit,
    onSubmitRequest: (String, String, String, String, String, String) -> Unit
) {
    // Form fields
    var selectedHostel by remember { mutableStateOf<Hostel?>(null) }
    var studentName by remember { mutableStateOf("") }
    var studentId by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var course by remember { mutableStateOf("") }
    var academicYear by remember { mutableStateOf("") }
    var preferredRoomType by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var specialRequirements by remember { mutableStateOf("") }
    var emergencyContact by remember { mutableStateOf("") }
    var emergencyRelation by remember { mutableStateOf("") }
    
    // UI State
    var showSuccess by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var hostels by remember { mutableStateOf<List<Hostel>>(emptyList()) }
    var showHostelSelection by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Room type options
    val roomTypes = listOf("Single", "Double", "Triple", "Quad", "Dormitory")
    val durations = listOf("1 Month", "3 Months", "6 Months", "1 Year", "Full Academic Year")
    val academicYears = listOf("1st Year", "2nd Year", "3rd Year", "4th Year", "Post Graduate")

    // Load hostels from API
    LaunchedEffect(Unit) {
        try {
            isLoading = true
            val response = ApiClient.apiService.getHostels()
            if (response.isSuccessful && response.body()?.success == true) {
                hostels = response.body()?.data ?: emptyList()
            } else {
                errorMessage = "Failed to load hostels: ${response.body()?.message ?: "Unknown error"}"
            }
        } catch (e: Exception) {
            errorMessage = "Failed to load hostels: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    // Success dialog
    if (showSuccess) {
        AlertDialog(
            onDismissRequest = { showSuccess = false },
            title = { Text("Request Submitted Successfully!") },
            text = { Text("Your hostel allotment request has been submitted. You will receive a confirmation email shortly.") },
            confirmButton = {
                TextButton(onClick = { 
                    showSuccess = false
                    onBackClick()
                }) {
                    Text("OK")
                }
            }
        )
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
                text = "Hostel Allotment Request",
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

            // Hostel Selection
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "1. Select Hostel",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    if (selectedHostel != null) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = selectedHostel!!.name,
                                    fontWeight = FontWeight.Bold
                                )
                                Text("Location: ${selectedHostel!!.location}")
                                Text("Area: ${selectedHostel!!.area}")
                                Text("Price: ₹${selectedHostel!!.price_per_month}/month")
                                Text("Available Rooms: ${selectedHostel!!.available_rooms}")
                                Text("Status: ${if (selectedHostel!!.is_active == 1) "Active" else "Inactive"}")
                                if (!selectedHostel!!.amenities.isNullOrEmpty()) {
                                    Text("Amenities: ${selectedHostel!!.amenities}")
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = { showHostelSelection = true }) {
                            Text("Change Hostel")
                        }
                    } else {
                        TextButton(
                            onClick = { showHostelSelection = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Select a Hostel")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Personal Information
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "2. Personal Information",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = studentName,
                        onValueChange = { studentName = it },
                        label = { Text("Full Name *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = studentId,
                        onValueChange = { studentId = it },
                        label = { Text("Student ID *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = contact,
                        onValueChange = { contact = it },
                        label = { Text("Phone Number *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = course,
                        onValueChange = { course = it },
                        label = { Text("Course/Program *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Academic Year Dropdown
                    var expandedYear by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expandedYear,
                        onExpandedChange = { expandedYear = !expandedYear }
                    ) {
                        OutlinedTextField(
                            value = academicYear,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("Academic Year *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedYear) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedYear,
                            onDismissRequest = { expandedYear = false }
                        ) {
                            academicYears.forEach { year ->
                                DropdownMenuItem(
                                    text = { Text(year) },
                                    onClick = {
                                        academicYear = year
                                        expandedYear = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Hostel Preferences
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "3. Hostel Preferences",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Room Type Selection
                    Text("Preferred Room Type *", fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Column(modifier = Modifier.selectableGroup()) {
                        roomTypes.forEach { roomType ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = (preferredRoomType == roomType),
                                        onClick = { preferredRoomType = roomType }
                                    )
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = (preferredRoomType == roomType),
                                    onClick = { preferredRoomType = roomType }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(roomType)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Duration Selection
                    Text("Duration of Stay *", fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Column(modifier = Modifier.selectableGroup()) {
                        durations.forEach { durationOption ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = (duration == durationOption),
                                        onClick = { duration = durationOption }
                                    )
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = (duration == durationOption),
                                    onClick = { duration = durationOption }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(durationOption)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Additional Information
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "4. Additional Information",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = specialRequirements,
                        onValueChange = { specialRequirements = it },
                        label = { Text("Special Requirements/Medical Conditions") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = emergencyContact,
                        onValueChange = { emergencyContact = it },
                        label = { Text("Emergency Contact Number") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = emergencyRelation,
                        onValueChange = { emergencyRelation = it },
                        label = { Text("Relationship with Emergency Contact") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Submit Button
            Button(
                onClick = {
                    // Validate form
                    if (selectedHostel == null) {
                        errorMessage = "Please select a hostel"
                        return@Button
                    }
                    if (studentName.isBlank() || studentId.isBlank() || email.isBlank() || 
                        contact.isBlank() || course.isBlank() || academicYear.isBlank() ||
                        preferredRoomType.isBlank() || duration.isBlank()) {
                        errorMessage = "Please fill in all required fields"
                        return@Button
                    }

                    // Submit request
                    coroutineScope.launch {
                        try {
                            isLoading = true
                            errorMessage = ""
                            
                            val bookingRequest = BookingRequest(
                                student_id = studentId,
                                property_id = selectedHostel!!.id,
                                property_type = "hostel",
                                special_requests = buildString {
                                    append("Student Name: $studentName\n")
                                    append("Room Type: $preferredRoomType\n")
                                    append("Duration: $duration\n")
                                    append("Course: $course\n")
                                    append("Academic Year: $academicYear\n")
                                    append("Contact: $contact\n")
                                    append("Email: $email\n")
                                    if (specialRequirements.isNotBlank()) {
                                        append("Special Requirements: $specialRequirements\n")
                                    }
                                    if (emergencyContact.isNotBlank()) {
                                        append("Emergency Contact: $emergencyContact")
                                        if (emergencyRelation.isNotBlank()) {
                                            append(" ($emergencyRelation)")
                                        }
                                    }
                                }
                            )
                            
                            val response = ApiClient.apiService.createHostelBooking(bookingRequest)
                            
                            if (response.isSuccessful && response.body()?.success == true) {
                                showSuccess = true
                                onSubmitRequest(
                                    selectedHostel!!.name,
                                    studentName,
                                    studentId,
                                    contact,
                                    preferredRoomType,
                                    duration
                                )
                            } else {
                                errorMessage = response.body()?.message ?: "Failed to submit request"
                            }
                        } catch (e: Exception) {
                            errorMessage = "Network error: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Submit Hostel Allotment Request")
            }
        }
    }

    // Hostel Selection Dialog
    if (showHostelSelection) {
        AlertDialog(
            onDismissRequest = { showHostelSelection = false },
            title = { Text("Select Hostel") },
            text = {
                Column {
                    hostels.forEach { hostel ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            onClick = {
                                selectedHostel = hostel
                                showHostelSelection = false
                            }
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = hostel.name,
                                    fontWeight = FontWeight.Bold
                                )
                                Text("Location: ${hostel.location}")
                                Text("Area: ${hostel.area}")
                                Text("Price: ₹${hostel.price_per_month}/month")
                                Text("Available: ${hostel.available_rooms} rooms")
                                Text("Status: ${if (hostel.is_active == 1) "Active" else "Inactive"}")
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showHostelSelection = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}