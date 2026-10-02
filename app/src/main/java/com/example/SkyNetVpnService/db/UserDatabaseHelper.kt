package com.example.SkyNetVpnService.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class UserAccount(
    val id: Int,
    val username: String,
    val email: String,
    val password: String,
    val role: String,
    val isApproved: Boolean,
    val isSubscribed: Boolean,
    val createdAt: String,
    val secQ1: String = "What was your first pet's name?",
    val secA1: String = "",
    val secQ2: String = "What city were you born in?",
    val secA2: String = ""
)

class UserDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    companion object {
        private const val DATABASE_NAME = "skynet_vpn.db"
        private const val DATABASE_VERSION = 2

        private const val TABLE_USERS = "users"
        private const val COLUMN_ID = "id"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_ROLE = "role"
        private const val COLUMN_IS_APPROVED = "is_approved"
        private const val COLUMN_IS_SUBSCRIBED = "is_subscribed"
        private const val COLUMN_CREATED_AT = "created_at"
        private const val COLUMN_SEC_Q1 = "sec_q1"
        private const val COLUMN_SEC_A1 = "sec_a1"
        private const val COLUMN_SEC_Q2 = "sec_q2"
        private const val COLUMN_SEC_A2 = "sec_a2"

        const val DEFAULT_Q1 = "What was your first pet's name?"
        const val DEFAULT_Q2 = "What city were you born in?"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME TEXT UNIQUE NOT NULL,
                $COLUMN_EMAIL TEXT NOT NULL,
                $COLUMN_PASSWORD TEXT NOT NULL,
                $COLUMN_ROLE TEXT NOT NULL,
                $COLUMN_IS_APPROVED INTEGER NOT NULL,
                $COLUMN_IS_SUBSCRIBED INTEGER NOT NULL,
                $COLUMN_CREATED_AT TEXT NOT NULL,
                $COLUMN_SEC_Q1 TEXT NOT NULL,
                $COLUMN_SEC_A1 TEXT NOT NULL,
                $COLUMN_SEC_Q2 TEXT NOT NULL,
                $COLUMN_SEC_A2 TEXT NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTableQuery)

        // Seed default Administrator account
        val currentDate = getCurrentTimestamp()
        val adminValues = ContentValues().apply {
            put(COLUMN_USERNAME, "Admin")
            put(COLUMN_EMAIL, "admin@skynetvpn.com")
            put(COLUMN_PASSWORD, "Admin123!")
            put(COLUMN_ROLE, "Admin")
            put(COLUMN_IS_APPROVED, 1)
            put(COLUMN_IS_SUBSCRIBED, 1)
            put(COLUMN_CREATED_AT, currentDate)
            put(COLUMN_SEC_Q1, DEFAULT_Q1)
            put(COLUMN_SEC_A1, "dog")
            put(COLUMN_SEC_Q2, DEFAULT_Q2)
            put(COLUMN_SEC_A2, "new york")
        }
        db.insert(TABLE_USERS, null, adminValues)

        // Seed sample regular users for testing
        val user1Values = ContentValues().apply {
            put(COLUMN_USERNAME, "Ndumiso")
            put(COLUMN_EMAIL, "ndumiso@skynetvpn.com")
            put(COLUMN_PASSWORD, "Ndumiso1")
            put(COLUMN_ROLE, "User")
            put(COLUMN_IS_APPROVED, 1)
            put(COLUMN_IS_SUBSCRIBED, 1)
            put(COLUMN_CREATED_AT, currentDate)
            put(COLUMN_SEC_Q1, DEFAULT_Q1)
            put(COLUMN_SEC_A1, "fluffy")
            put(COLUMN_SEC_Q2, DEFAULT_Q2)
            put(COLUMN_SEC_A2, "durban")
        }
        db.insert(TABLE_USERS, null, user1Values)

        val user2Values = ContentValues().apply {
            put(COLUMN_USERNAME, "Mmesiya")
            put(COLUMN_EMAIL, "mmesiya@skynetvpn.com")
            put(COLUMN_PASSWORD, "Mmesiya2")
            put(COLUMN_ROLE, "User")
            put(COLUMN_IS_APPROVED, 1) // Auto-approved
            put(COLUMN_IS_SUBSCRIBED, 0)
            put(COLUMN_CREATED_AT, currentDate)
            put(COLUMN_SEC_Q1, DEFAULT_Q1)
            put(COLUMN_SEC_A1, "buddy")
            put(COLUMN_SEC_Q2, DEFAULT_Q2)
            put(COLUMN_SEC_A2, "johannesburg")
        }
        db.insert(TABLE_USERS, null, user2Values)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    fun registerUser(
        username: String,
        email: String,
        password: String,
        secA1: String = "dog",
        secA2: String = "city",
        secQ1: String = DEFAULT_Q1,
        secQ2: String = DEFAULT_Q2,
        role: String = "User"
    ): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_EMAIL, email)
            put(COLUMN_PASSWORD, password)
            put(COLUMN_ROLE, role)
            put(COLUMN_IS_APPROVED, 1) // AUTO-APPROVED automatically!
            put(COLUMN_IS_SUBSCRIBED, 0)
            put(COLUMN_CREATED_AT, getCurrentTimestamp())
            put(COLUMN_SEC_Q1, secQ1)
            put(COLUMN_SEC_A1, secA1.trim().lowercase(Locale.getDefault()))
            put(COLUMN_SEC_Q2, secQ2)
            put(COLUMN_SEC_A2, secA2.trim().lowercase(Locale.getDefault()))
        }
        return try {
            db.insertOrThrow(TABLE_USERS, null, values)
        } catch (e: Exception) {
            -1L
        }
    }

    fun loginUser(username: String, password: String): UserAccount? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            null,
            "$COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?",
            arrayOf(username, password),
            null,
            null,
            null
        )

        return cursor.use {
            if (it.moveToFirst()) {
                parseUserFromCursor(it)
            } else {
                null
            }
        }
    }

    fun getUserSecurityQuestions(username: String): Pair<String, String>? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_SEC_Q1, COLUMN_SEC_Q2),
            "$COLUMN_USERNAME = ?",
            arrayOf(username),
            null,
            null,
            null
        )

        return cursor.use {
            if (it.moveToFirst()) {
                val q1 = it.getString(it.getColumnIndexOrThrow(COLUMN_SEC_Q1))
                val q2 = it.getString(it.getColumnIndexOrThrow(COLUMN_SEC_Q2))
                Pair(q1, q2)
            } else {
                null
            }
        }
    }

    fun verifySecurityAnswersAndResetPassword(
        username: String,
        ans1: String,
        ans2: String,
        newPassword: String
    ): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_SEC_A1, COLUMN_SEC_A2),
            "$COLUMN_USERNAME = ?",
            arrayOf(username),
            null,
            null,
            null
        )

        var isCorrect = false
        cursor.use {
            if (it.moveToFirst()) {
                val savedAns1 = it.getString(it.getColumnIndexOrThrow(COLUMN_SEC_A1))
                val savedAns2 = it.getString(it.getColumnIndexOrThrow(COLUMN_SEC_A2))

                val inputAns1 = ans1.trim().lowercase(Locale.getDefault())
                val inputAns2 = ans2.trim().lowercase(Locale.getDefault())

                if (savedAns1 == inputAns1 && savedAns2 == inputAns2) {
                    isCorrect = true
                }
            }
        }

        if (isCorrect) {
            val wDb = writableDatabase
            val values = ContentValues().apply {
                put(COLUMN_PASSWORD, newPassword)
            }
            val rows = wDb.update(TABLE_USERS, values, "$COLUMN_USERNAME = ?", arrayOf(username))
            return rows > 0
        }
        return false
    }

    fun getUserById(userId: Int): UserAccount? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            null,
            "$COLUMN_ID = ?",
            arrayOf(userId.toString()),
            null,
            null,
            null
        )

        return cursor.use {
            if (it.moveToFirst()) {
                parseUserFromCursor(it)
            } else {
                null
            }
        }
    }

    fun getAllUsers(): List<UserAccount> {
        val userList = mutableListOf<UserAccount>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            null,
            null,
            null,
            null,
            null,
            "$COLUMN_ID DESC"
        )

        cursor.use {
            while (it.moveToNext()) {
                userList.add(parseUserFromCursor(it))
            }
        }
        return userList
    }

    fun approveUser(userId: Int): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_IS_APPROVED, 1)
        }
        val rowsAffected = db.update(TABLE_USERS, values, "$COLUMN_ID = ?", arrayOf(userId.toString()))
        return rowsAffected > 0
    }

    fun revokeApproval(userId: Int): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_IS_APPROVED, 0)
        }
        val rowsAffected = db.update(TABLE_USERS, values, "$COLUMN_ID = ?", arrayOf(userId.toString()))
        return rowsAffected > 0
    }

    fun cancelSubscription(userId: Int): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_IS_SUBSCRIBED, 0)
        }
        val rowsAffected = db.update(TABLE_USERS, values, "$COLUMN_ID = ?", arrayOf(userId.toString()))
        return rowsAffected > 0
    }

    fun updateSubscriptionStatus(userId: Int, isSubscribed: Boolean): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_IS_SUBSCRIBED, if (isSubscribed) 1 else 0)
        }
        val rowsAffected = db.update(TABLE_USERS, values, "$COLUMN_ID = ?", arrayOf(userId.toString()))
        return rowsAffected > 0
    }

    fun deleteUser(userId: Int): Boolean {
        val db = writableDatabase
        val rowsAffected = db.delete(TABLE_USERS, "$COLUMN_ID = ?", arrayOf(userId.toString()))
        return rowsAffected > 0
    }

    private fun parseUserFromCursor(cursor: android.database.Cursor): UserAccount {
        val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
        val username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME))
        val email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
        val password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD))
        val role = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE))
        val isApproved = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_APPROVED)) == 1
        val isSubscribed = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_SUBSCRIBED)) == 1
        val createdAt = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT))
        val secQ1 = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SEC_Q1))
        val secA1 = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SEC_A1))
        val secQ2 = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SEC_Q2))
        val secA2 = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SEC_A2))

        return UserAccount(
            id = id,
            username = username,
            email = email,
            password = password,
            role = role,
            isApproved = isApproved,
            isSubscribed = isSubscribed,
            createdAt = createdAt,
            secQ1 = secQ1,
            secA1 = secA1,
            secQ2 = secQ2,
            secA2 = secA2
        )
    }

    private fun getCurrentTimestamp(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return sdf.format(Date())
    }
}
