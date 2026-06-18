package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.ui.MissionTrackerScreen
import com.example.ui.TaskViewModel
import com.example.ui.TaskViewModelFactory

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    
    // Obtain the persistent TaskViewModel with its factory instance
    val viewModel = ViewModelProvider(
        this,
        TaskViewModelFactory(application)
    )[TaskViewModel::class.java]

    setContent {
      MissionTrackerScreen(viewModel = viewModel)
    }
  }
}


