package com.example.alaga

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "medical_app.db"
        private const val DATABASE_VERSION = 1

        // User Table
        private const val TABLE_USERS = "users"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_ROLE = "role"
    }

    fun insertUser(name: String, email: String, password: String, role: String): Boolean {
        val db = writableDatabase
        val values = ContentValues()
        values.put(COLUMN_NAME, name)
        values.put(COLUMN_EMAIL, email)
        values.put(COLUMN_PASSWORD, password)
        values.put(COLUMN_ROLE, role)


        val result = db.insert(TABLE_USERS, null, values)
        db.close()
        return result != -1L
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUsersTable = """CREATE TABLE $TABLE_USERS (
            $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_NAME TEXT,
            $COLUMN_EMAIL TEXT,
            $COLUMN_PASSWORD TEXT UNIQUE,
            $COLUMN_ROLE TEXT)"""
        db.execSQL(createUsersTable)
    }

    fun printAllUsers() {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM users", null)

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            val email = cursor.getString(cursor.getColumnIndexOrThrow("email"))
            val password = cursor.getString(cursor.getColumnIndexOrThrow("password"))
            val role = cursor.getString(cursor.getColumnIndexOrThrow("role"))

            Log.d("Database", "ID: $id, Name: $name, Email: $email, Password: $password, Role: $role")
        }
        cursor.close()
    }



    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }



    fun authenticateUser(name: String, password: String): String? {
        val db = readableDatabase
        val query = "SELECT $COLUMN_ROLE FROM $TABLE_USERS WHERE $COLUMN_NAME = ? AND $COLUMN_PASSWORD = ?"
        val cursor = db.rawQuery("SELECT * FROM users WHERE name = ? AND password = ?", arrayOf(name, password))

        return if (cursor.moveToFirst()) {
            val role = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE))
            cursor.close()
            db.close()
            role
        } else {
            cursor.close()
            db.close()
            null
        }
    }

}