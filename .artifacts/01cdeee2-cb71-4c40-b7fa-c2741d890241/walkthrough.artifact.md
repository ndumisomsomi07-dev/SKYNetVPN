# Walkthrough - Fixing Conflicting Import in MainActivity.kt

I fixed the "Conflicting import: imported name 'Bundle' is ambiguous" error in `MainActivity.kt`.

## Problem
The `MainActivity.kt` file contained two sets of Kotlin code concatenated together. Each set had its own `package` declaration, `import` statements (including `android.os.Bundle`), and `class MainActivity` definition. This duplication caused the Kotlin compiler to report ambiguous imports and duplicate class definitions.

## Changes

### [MainActivity.kt](file:///C:/Users/Dell/AndroidStudioProjects/SKYNetVPN/app/src/main/java/com/example/SkyNetVpnService/MainActivity.kt)

I removed the redundant second part of the file that belonged to a different package (`com.example.securevpn`).

```diff
-package com.example.securevpn
-
-import android.os.Bundle
-import android.widget.Button
-import android.widget.ImageView
-import android.widget.TextView
-import androidx.appcompat.app.AppCompatActivity
-
-class MainActivity : AppCompatActivity() {
-
-    private var isConnected = false
-
-    override fun onCreate(savedInstanceState: Bundle?) {
-        super.onCreate(savedInstanceState)
-        setContentView(R.layout.activity_main)
-
-        val connectButton = findViewById<Button>(R.id.connectButton)
-        val statusText = findViewById<TextView>(R.id.statusText)
-        val statusIcon = findViewById<ImageView>(R.id.statusIcon)
-
-        connectButton.setOnClickListener {
-            isConnected = !isConnected
-
-            if (isConnected) {
-                statusText.text = "Connected"
-                connectButton.text = "DISCONNECT"
-                // TODO: start your VPN connection logic here
-            } else {
-                statusText.text = "Disconnected"
-                connectButton.text = "CONNECT"
-                // TODO: stop your VPN connection here
-            }
-        }
-    }
-}
```

## Verification Results

### Automated Tests
I ran the Gradle task to compile the Kotlin code for the app module:
- Command: `./gradlew :app:compileDebugKotlin`
- Result: **Build finished successfully.**

The "Conflicting import" error is no longer present, and the project builds correctly.