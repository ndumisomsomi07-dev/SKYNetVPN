package com.example.SkyNetVpnService

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.VpnService
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.widget.ViewFlipper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.SkyNetVpnService.db.UserDatabaseHelper
import java.util.Locale
import java.util.Random

class MainActivity : AppCompatActivity() {

    private val VPN_REQUEST_CODE = 1001
    private var isConnected = false

    private val mockServers = listOf(
        VpnServer("104.28.14.88", "New York, USA"),
        VpnServer("185.220.101.5", "Frankfurt, Germany"),
        VpnServer("139.99.8.12", "Tokyo, Japan"),
        VpnServer("45.33.32.156", "London, UK"),
        VpnServer("192.168.1.100", "Paris, France"),
        VpnServer("172.16.0.1", "Toronto, Canada"),
        VpnServer("10.0.0.1", "Sydney, Australia"),
        VpnServer("8.8.8.8", "Mountain View, USA"),
        VpnServer("1.1.1.1", "Singapore"),
        VpnServer("4.4.4.4", "Amsterdam, Netherlands"),
        VpnServer("95.217.1.1", "Helsinki, Finland"),
        VpnServer("2.2.2.2", "Seoul, South Korea")
    )

    private lateinit var dbHelper: UserDatabaseHelper
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var viewFlipper: ViewFlipper

    // Login & Register components
    private lateinit var txtLoginHeader: TextView
    private lateinit var etEmail: EditText
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var containerRegisterSecurity: View
    private lateinit var etRegAns1: EditText
    private lateinit var etRegAns2: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnToggleRegister: Button
    private lateinit var btnForgotPassword: Button
    private var isRegisterMode = false

    // Forgot Password components
    private lateinit var etForgotUsername: EditText
    private lateinit var btnFindAccount: Button
    private lateinit var containerSecurityQuestions: View
    private lateinit var txtSecQ1: TextView
    private lateinit var etSecAns1: EditText
    private lateinit var txtSecQ2: TextView
    private lateinit var etSecAns2: EditText
    private lateinit var etNewPassword: EditText
    private lateinit var btnResetPassword: Button
    private lateinit var btnBackToLogin: Button

    // Subscription screen components
    private lateinit var btnFreeTrial: Button
    private lateinit var btnPayment: Button

    // Payment Selection components
    private lateinit var btnMasterCard: Button
    private lateinit var btnPayPal: Button

    // Payment Forms
    private lateinit var btnPayMasterCard: Button
    private lateinit var btnLoginPaypal: Button

    // Server Selection screen components
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnConnect: Button
    private lateinit var btnUserLogout: Button
    private var selectedServer: VpnServer? = null

    // Admin Dashboard components
    private lateinit var txtTotalUsers: TextView
    private lateinit var txtPendingUsers: TextView
    private lateinit var txtActiveSubscribers: TextView
    private lateinit var btnAdminLogout: Button
    private lateinit var recyclerViewAdminUsers: RecyclerView
    private lateinit var adminUserAdapter: AdminUserAdapter

    // Stats screen components
    private lateinit var txtStatsLocation: TextView
    private lateinit var txtStatsIp: TextView
    private lateinit var txtDataIn: TextView
    private lateinit var txtDataOut: TextView
    private lateinit var btnDisconnectStats: Button
    private lateinit var statsStatusLight: View

    // Traffic simulation
    private val handler = Handler(Looper.getMainLooper())
    private var dataIn = 0.0
    private var dataOut = 0.0
    private val statsRunnable = object : Runnable {
        override fun run() {
            if (isConnected) {
                dataIn += Random().nextDouble() * 50
                dataOut += Random().nextDouble() * 20
                txtDataIn.text = String.format(Locale.US, "%.1f KB", dataIn)
                txtDataOut.text = String.format(Locale.US, "%.1f KB", dataOut)
                handler.postDelayed(this, 1000)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activitymain)

        dbHelper = UserDatabaseHelper(this)
        sharedPrefs = getSharedPreferences("SkyNetPrefs", Context.MODE_PRIVATE)
        viewFlipper = findViewById(R.id.viewFlipper)

        initLoginScreen()
        initForgotPasswordScreen()
        initSubscriptionScreen()
        initPaymentScreens()
        initServerSelectionScreen()
        initStatsScreen()
        initAdminDashboard()

        checkNavigationState()
    }

    private fun checkNavigationState() {
        val isLoggedIn = sharedPrefs.getBoolean("isLoggedIn", false)
        val userRole = sharedPrefs.getString("userRole", "User") ?: "User"
        val userId = sharedPrefs.getInt("userId", -1)

        if (!isLoggedIn) {
            viewFlipper.displayedChild = 0 // Login
        } else if (userRole.equals("Admin", ignoreCase = true)) {
            refreshAdminDashboard()
            viewFlipper.displayedChild = 8 // Admin Dashboard
        } else {
            // Verify current user state from SQLite DB
            val currentUser = if (userId != -1) dbHelper.getUserById(userId) else null

            if (currentUser == null || !currentUser.isApproved) {
                Toast.makeText(
                    this,
                    "Your account has been revoked or blocked by Administrator.",
                    Toast.LENGTH_LONG
                ).show()
                logoutUser()
            } else if (!currentUser.isSubscribed) {
                viewFlipper.displayedChild = 1 // Subscription
            } else {
                viewFlipper.displayedChild = 2 // Server Selection
            }
        }
    }

    private fun initLoginScreen() {
        txtLoginHeader = findViewById(R.id.txtLoginHeader)
        etEmail = findViewById(R.id.etEmail)
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        containerRegisterSecurity = findViewById(R.id.containerRegisterSecurity)
        etRegAns1 = findViewById(R.id.etRegAns1)
        etRegAns2 = findViewById(R.id.etRegAns2)
        btnLogin = findViewById(R.id.btnLogin)
        btnToggleRegister = findViewById(R.id.btnToggleRegister)
        btnForgotPassword = findViewById(R.id.btnForgotPassword)

        btnToggleRegister.setOnClickListener {
            isRegisterMode = !isRegisterMode
            if (isRegisterMode) {
                txtLoginHeader.text = "Create SkyNet Account"
                etEmail.visibility = View.VISIBLE
                containerRegisterSecurity.visibility = View.VISIBLE
                btnForgotPassword.visibility = View.GONE
                btnLogin.text = "Register Account"
                btnToggleRegister.text = "Already have an account? Login"
            } else {
                txtLoginHeader.text = "Welcome to SKY Net"
                etEmail.visibility = View.GONE
                containerRegisterSecurity.visibility = View.GONE
                btnForgotPassword.visibility = View.VISIBLE
                btnLogin.text = "Login"
                btnToggleRegister.text = "Need an account? Register"
            }
        }

        btnForgotPassword.setOnClickListener {
            viewFlipper.displayedChild = 9 // Go to Forgot Password Screen
        }

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val email = etEmail.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isRegisterMode) {
                val ans1 = etRegAns1.text.toString().trim()
                val ans2 = etRegAns2.text.toString().trim()

                if (email.isEmpty() || ans1.isEmpty() || ans2.isEmpty()) {
                    Toast.makeText(this, "Please complete email and security answers", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val newUserId = dbHelper.registerUser(
                    username = username,
                    email = email,
                    password = password,
                    secA1 = ans1,
                    secA2 = ans2
                )

                if (newUserId != -1L) {
                    Toast.makeText(
                        this,
                        "Account Created Successfully! Auto-approved.",
                        Toast.LENGTH_LONG
                    ).show()
                    // Switch back to Login mode
                    btnToggleRegister.performClick()
                    etUsername.setText(username)
                    etPassword.setText("")
                } else {
                    Toast.makeText(
                        this,
                        "Registration failed. Username might already exist.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                val user = dbHelper.loginUser(username, password)
                if (user != null) {
                    sharedPrefs.edit {
                        putBoolean("isLoggedIn", true)
                        putInt("userId", user.id)
                        putString("username", user.username)
                        putString("userRole", user.role)
                        putBoolean("isSubscribed", user.isSubscribed)
                    }

                    if (user.role.equals("Admin", ignoreCase = true)) {
                        Toast.makeText(this, "Welcome, Administrator!", Toast.LENGTH_SHORT).show()
                        refreshAdminDashboard()
                        viewFlipper.displayedChild = 8 // Admin Dashboard
                    } else if (!user.isApproved) {
                        Toast.makeText(
                            this,
                            "Account is blocked by Administrator.",
                            Toast.LENGTH_LONG
                        ).show()
                        logoutUser()
                    } else {
                        Toast.makeText(this, "Welcome back, ${user.username}!", Toast.LENGTH_SHORT).show()
                        checkNavigationState()
                    }
                } else {
                    Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initForgotPasswordScreen() {
        etForgotUsername = findViewById(R.id.etForgotUsername)
        btnFindAccount = findViewById(R.id.btnFindAccount)
        containerSecurityQuestions = findViewById(R.id.containerSecurityQuestions)
        txtSecQ1 = findViewById(R.id.txtSecQ1)
        etSecAns1 = findViewById(R.id.etSecAns1)
        txtSecQ2 = findViewById(R.id.txtSecQ2)
        etSecAns2 = findViewById(R.id.etSecAns2)
        etNewPassword = findViewById(R.id.etNewPassword)
        btnResetPassword = findViewById(R.id.btnResetPassword)
        btnBackToLogin = findViewById(R.id.btnBackToLogin)

        btnFindAccount.setOnClickListener {
            val username = etForgotUsername.text.toString().trim()
            if (username.isEmpty()) {
                Toast.makeText(this, "Please enter your username", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val questions = dbHelper.getUserSecurityQuestions(username)
            if (questions != null) {
                txtSecQ1.text = "Q1: ${questions.first}"
                txtSecQ2.text = "Q2: ${questions.second}"
                containerSecurityQuestions.visibility = View.VISIBLE
                Toast.makeText(this, "Account found! Please answer security questions.", Toast.LENGTH_SHORT).show()
            } else {
                containerSecurityQuestions.visibility = View.GONE
                Toast.makeText(this, "Username not found.", Toast.LENGTH_SHORT).show()
            }
        }

        btnResetPassword.setOnClickListener {
            val username = etForgotUsername.text.toString().trim()
            val ans1 = etSecAns1.text.toString().trim()
            val ans2 = etSecAns2.text.toString().trim()
            val newPass = etNewPassword.text.toString().trim()

            if (ans1.isEmpty() || ans2.isEmpty() || newPass.isEmpty()) {
                Toast.makeText(this, "Please fill in all answers and new password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val resetSuccess = dbHelper.verifySecurityAnswersAndResetPassword(username, ans1, ans2, newPass)
            if (resetSuccess) {
                Toast.makeText(this, "Password Reset Successful! Please Login.", Toast.LENGTH_LONG).show()
                containerSecurityQuestions.visibility = View.GONE
                etForgotUsername.setText("")
                etSecAns1.setText("")
                etSecAns2.setText("")
                etNewPassword.setText("")
                viewFlipper.displayedChild = 0 // Return to Login
            } else {
                Toast.makeText(this, "Incorrect Security Answers! Check spelling.", Toast.LENGTH_LONG).show()
            }
        }

        btnBackToLogin.setOnClickListener {
            viewFlipper.displayedChild = 0 // Return to Login
        }
    }

    private fun initSubscriptionScreen() {
        btnFreeTrial = findViewById(R.id.btnFreeTrial)
        btnPayment = findViewById(R.id.btnPayment)

        btnFreeTrial.setOnClickListener {
            val userId = sharedPrefs.getInt("userId", -1)
            if (userId != -1) {
                dbHelper.updateSubscriptionStatus(userId, true)
            }
            sharedPrefs.edit { putBoolean("isSubscribed", true) }
            viewFlipper.displayedChild = 2 // Go to Server Selection
            Toast.makeText(this, "7-Day Trial Activated!", Toast.LENGTH_SHORT).show()
        }

        btnPayment.setOnClickListener {
            viewFlipper.displayedChild = 4 // Go to Payment Selection
        }
    }

    private fun initPaymentScreens() {
        btnMasterCard = findViewById(R.id.btnMasterCard)
        btnPayPal = findViewById(R.id.btnPayPal)
        btnPayMasterCard = findViewById(R.id.btnPayMasterCard)
        btnLoginPaypal = findViewById(R.id.btnLoginPaypal)

        btnMasterCard.setOnClickListener { viewFlipper.displayedChild = 5 }
        btnPayPal.setOnClickListener { viewFlipper.displayedChild = 6 }

        val processPayment = View.OnClickListener {
            viewFlipper.displayedChild = 7 // Show Processing screen
            handler.postDelayed({
                val userId = sharedPrefs.getInt("userId", -1)
                if (userId != -1) {
                    dbHelper.updateSubscriptionStatus(userId, true)
                }
                sharedPrefs.edit { putBoolean("isSubscribed", true) }
                viewFlipper.displayedChild = 2 // Done, go to Server Selection
                Toast.makeText(this, "Payment Successful!", Toast.LENGTH_SHORT).show()
            }, 3000) // 3 second simulation
        }

        btnPayMasterCard.setOnClickListener(processPayment)
        btnLoginPaypal.setOnClickListener(processPayment)
    }

    private fun initServerSelectionScreen() {
        recyclerView = findViewById(R.id.recyclerViewServers)
        btnConnect = findViewById(R.id.btnConnect)
        btnUserLogout = findViewById(R.id.btnUserLogout)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ServerAdapter(mockServers) { server ->
            selectedServer = server
        }

        btnConnect.setOnClickListener {
            if (selectedServer == null) {
                Toast.makeText(this, "Please select a server first", Toast.LENGTH_SHORT).show()
            } else {
                prepareAndStartVpn()
            }
        }

        btnUserLogout.setOnClickListener {
            logoutUser()
        }
    }

    private fun initAdminDashboard() {
        txtTotalUsers = findViewById(R.id.txtTotalUsers)
        txtPendingUsers = findViewById(R.id.txtPendingUsers)
        txtActiveSubscribers = findViewById(R.id.txtActiveSubscribers)
        btnAdminLogout = findViewById(R.id.btnAdminLogout)
        recyclerViewAdminUsers = findViewById(R.id.recyclerViewAdminUsers)

        recyclerViewAdminUsers.layoutManager = LinearLayoutManager(this)

        adminUserAdapter = AdminUserAdapter(
            users = emptyList(),
            onToggleApprove = { user ->
                if (user.isApproved) {
                    dbHelper.revokeApproval(user.id)
                    Toast.makeText(this, "Revoked approval / Blocked user: ${user.username}", Toast.LENGTH_SHORT).show()
                } else {
                    dbHelper.approveUser(user.id)
                    Toast.makeText(this, "Approved user: ${user.username}", Toast.LENGTH_SHORT).show()
                }
                refreshAdminDashboard()
            },
            onCancelSubscription = { user ->
                dbHelper.cancelSubscription(user.id)
                Toast.makeText(this, "Canceled subscription for: ${user.username}", Toast.LENGTH_SHORT).show()
                refreshAdminDashboard()
            },
            onDelete = { user ->
                dbHelper.deleteUser(user.id)
                Toast.makeText(this, "Deleted user: ${user.username}", Toast.LENGTH_SHORT).show()
                refreshAdminDashboard()
            }
        )
        recyclerViewAdminUsers.adapter = adminUserAdapter

        btnAdminLogout.setOnClickListener {
            logoutUser()
        }
    }

    private fun refreshAdminDashboard() {
        val users = dbHelper.getAllUsers()
        adminUserAdapter.updateUsers(users)

        val total = users.size
        val pendingOrBlocked = users.count { !it.isApproved && !it.role.equals("Admin", ignoreCase = true) }
        val subscribed = users.count { it.isSubscribed }

        txtTotalUsers.text = total.toString()
        txtPendingUsers.text = pendingOrBlocked.toString()
        txtActiveSubscribers.text = subscribed.toString()
    }

    private fun logoutUser() {
        if (isConnected) {
            disconnectVpn()
        }
        sharedPrefs.edit { clear() }
        viewFlipper.displayedChild = 0 // Go to Login screen
    }

    private fun initStatsScreen() {
        txtStatsLocation = findViewById(R.id.txtStatsLocation)
        txtStatsIp = findViewById(R.id.txtStatsIp)
        txtDataIn = findViewById(R.id.txtDataIn)
        txtDataOut = findViewById(R.id.txtDataOut)
        btnDisconnectStats = findViewById(R.id.btnDisconnectStats)
        statsStatusLight = findViewById(R.id.statsStatusLight)

        btnDisconnectStats.setOnClickListener {
            disconnectVpn()
        }
    }

    private fun prepareAndStartVpn() {
        val intent = VpnService.prepare(this)
        if (intent != null) {
            startActivityForResult(intent, VPN_REQUEST_CODE)
        } else {
            startVpnService()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == VPN_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            startVpnService()
        }
    }

    private fun startVpnService() {
        val server = selectedServer ?: return
        val intent = Intent(this, SkyNetVpnService::class.java).apply {
            putExtra("EXTRA_LOCATION", server.location)
        }
        startService(intent)

        isConnected = true
        txtStatsLocation.text = "Location: ${server.location}"
        txtStatsIp.text = "IP: ${server.ip}"
        statsStatusLight.background.setTint(android.graphics.Color.GREEN)

        // Reset stats
        dataIn = 0.0
        dataOut = 0.0

        viewFlipper.displayedChild = 3 // Go to Stats
        handler.post(statsRunnable)
    }

    private fun disconnectVpn() {
        val intent = Intent(this, SkyNetVpnService::class.java).apply {
            action = "STOP"
        }
        startService(intent)

        isConnected = false
        handler.removeCallbacks(statsRunnable)
        viewFlipper.displayedChild = 2 // Go back to Server Selection
    }

    override fun onBackPressed() {
        when (viewFlipper.displayedChild) {
            3 -> { /* Connected - do nothing */ }
            4, 5, 6 -> viewFlipper.displayedChild = 1 // Go back to Plan choice
            7 -> { /* Processing - do nothing */ }
            8 -> logoutUser()
            9 -> viewFlipper.displayedChild = 0 // Return from Forgot Password
            else -> {
                if (viewFlipper.displayedChild == 0) super.onBackPressed()
                else viewFlipper.displayedChild = 0
            }
        }
    }
}
