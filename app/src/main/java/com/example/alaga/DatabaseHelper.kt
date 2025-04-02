package com.example.alaga

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.yourpackage.models.User


class DatabaseHelper (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "medical_app.db"
        private const val DATABASE_VERSION = 4

        // User Table
        private const val TABLE_USERS = "users"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_ROLE = "role"

        //Medical history table
        private const val TABLE_MEDICAL_HISTORY = "medical_history"
        private const val COLUMN_MH_ID = "id"
        private const val COLUMN_USER_ID = "user_id"
        private const val COLUMN_PATIENT_NAME = "patient_name"
        private const val COLUMN_AGE = "age"
        private const val COLUMN_SEX = "sex"
        private const val COLUMN_BIRTHDATE = "birthdate"
        private const val COLUMN_PHONE = "phone"
        private const val COLUMN_EMERGENCY = "emergency"
        private const val COLUMN_OCCUPATION = "occupation"
        private const val COLUMN_ISSUES = "issues"
        private const val COLUMN_TREATMENT = "treatment"
        private const val COLUMN_DOSAGE = "dosage"
        private const val COLUMN_FREQUENCY = "frequency"
        private const val COLUMN_PURPOSE = "purpose"
        private const val COLUMN_NOTES = "notes"


        private const val TABLE_APPOINTMENTS = "appointments"
        private const val COLUMN_APPT_ID = "id"
        private const val COLUMN_PATIENT_ID = "patient_id"
        private const val COLUMN_DOCTOR_ID = "doctor_id"
        private const val COLUMN_APPT_DATE = "appointment_date"
        private const val COLUMN_STATUS = "status"


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
        // Create users table
        db.execSQL("""
            CREATE TABLE $TABLE_USERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT,
                $COLUMN_EMAIL TEXT,
                $COLUMN_PASSWORD TEXT UNIQUE,
                $COLUMN_ROLE TEXT
            )
        """.trimIndent())

        // Create medical_history table
        db.execSQL("""
            CREATE TABLE $TABLE_MEDICAL_HISTORY (
                $COLUMN_MH_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USER_ID INTEGER,
                $COLUMN_PATIENT_NAME TEXT,
                $COLUMN_AGE INTEGER,
                $COLUMN_SEX TEXT,
                $COLUMN_BIRTHDATE TEXT,
                $COLUMN_PHONE TEXT,
                $COLUMN_EMERGENCY TEXT,
                $COLUMN_OCCUPATION TEXT,
                $COLUMN_ISSUES TEXT,
                $COLUMN_TREATMENT TEXT,
                $COLUMN_DOSAGE TEXT,
                $COLUMN_FREQUENCY TEXT,
                $COLUMN_PURPOSE TEXT,
                $COLUMN_NOTES TEXT,
                FOREIGN KEY ($COLUMN_USER_ID) REFERENCES $TABLE_USERS($COLUMN_ID) ON DELETE CASCADE
            )
        """.trimIndent())


        db.execSQL("""
        CREATE TABLE $TABLE_APPOINTMENTS (
            $COLUMN_APPT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_PATIENT_ID INTEGER,
            $COLUMN_DOCTOR_ID INTEGER,
            $COLUMN_APPT_DATE TEXT,
            $COLUMN_STATUS TEXT DEFAULT 'Pending',
            $COLUMN_NOTES TEXT,
            FOREIGN KEY ($COLUMN_PATIENT_ID) REFERENCES $TABLE_USERS($COLUMN_ID),
            FOREIGN KEY ($COLUMN_DOCTOR_ID) REFERENCES $TABLE_USERS($COLUMN_ID)
        )
    """.trimIndent())

        // Insert test data
        db.execSQL("""
            INSERT INTO $TABLE_USERS 
            ($COLUMN_NAME, $COLUMN_EMAIL, $COLUMN_PASSWORD, $COLUMN_ROLE) 
            VALUES
            ('Joy', 'joy@gmail.com', 'joyjoy', 'Admin'),
            ('Jupi', 'jupi@gmail.com', 'jupi', 'Doctor'),
            ('Grace', 'grace@gmail.com', 'grace', 'Nurse'),
            ('George', 'george@gmail.com', 'george', 'Patient')
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_APPOINTMENTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MEDICAL_HISTORY")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)

        when (oldVersion) {
            1 -> {
                // Version 1 to 2: Create medical_history table
                db.execSQL(
                    """
                    CREATE TABLE $TABLE_MEDICAL_HISTORY (
                        $COLUMN_MH_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                        $COLUMN_USER_ID INTEGER,
                        $COLUMN_PATIENT_NAME TEXT,
                        $COLUMN_AGE INTEGER,
                        $COLUMN_SEX TEXT,
                        $COLUMN_BIRTHDATE TEXT,
                        $COLUMN_PHONE TEXT,
                        $COLUMN_EMERGENCY TEXT,
                        $COLUMN_OCCUPATION TEXT,
                        $COLUMN_ISSUES TEXT,
                        $COLUMN_TREATMENT TEXT,
                        $COLUMN_DOSAGE TEXT,
                        $COLUMN_FREQUENCY TEXT,
                        $COLUMN_PURPOSE TEXT,
                        $COLUMN_NOTES TEXT,
                        FOREIGN KEY ($COLUMN_USER_ID) REFERENCES $TABLE_USERS($COLUMN_ID) ON DELETE CASCADE
                    )
                """.trimIndent()
                )
            }

            2 -> {
                // Future upgrades would go here
                if (oldVersion < 3) {
                    db.execSQL("""
        CREATE TABLE IF NOT EXISTS appointments (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            patient_name TEXT,
            doctor_name TEXT,
            appointment_date TEXT,
            status TEXT,
            notes TEXT
        )
    """.trimIndent())
                }
            }
        }}



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

    // get all patients for patient account module

    fun getAllPatients(): List<MedicalHistory> {
        val patientList = mutableListOf<MedicalHistory>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_MEDICAL_HISTORY ORDER BY $COLUMN_PATIENT_NAME ASC"

        val cursor = db.rawQuery(query, null)
        while (cursor.moveToNext()) {
            val patient = MedicalHistory(
                username = "",  // Not needed in this module
                patientName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PATIENT_NAME)),
                age = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AGE)),
                sex = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SEX)),
                birthdate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BIRTHDATE)),
                phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)),
                emergency = "",
                occupation = "",
                issues = "",
                treatment = "",
                dosage = "",
                frequency = "",
                purpose = "",
                notes = ""
            )
            patientList.add(patient)
        }
        cursor.close()
        db.close()
        return patientList
    }


    // get all user for user account module
    fun getAllUsers(role: String? = null): List<User> {
        val userList = mutableListOf<User>()
        val db = readableDatabase
        val query = if (role == null || role == "All") {
            "SELECT * FROM $TABLE_USERS ORDER BY $COLUMN_ID DESC"
        } else {
            "SELECT * FROM $TABLE_USERS WHERE $COLUMN_ROLE = ? ORDER BY $COLUMN_ID DESC"
        }
        val cursor = if (role == null || role == "All") {
            db.rawQuery(query, null)
        } else {
            db.rawQuery(query, arrayOf(role))
        }

        while (cursor.moveToNext()) {
            val user = User(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4)
            )
            userList.add(user)
        }

        cursor.close()
        db.close()
        return userList
    }

    // update user for user account module

    fun updateUser(id: Int, name: String, email: String, password: String, role: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_EMAIL, email)
            put(COLUMN_PASSWORD, password)
            put(COLUMN_ROLE, role)
        }
        db.update(TABLE_USERS, values, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
    }

    // delete user for user account module

    fun deleteUser(userId: Int) {
        val db = writableDatabase
        db.delete(TABLE_USERS, "$COLUMN_ID = ?", arrayOf(userId.toString()))
        db.close()
    }


    fun authenticateUser(name: String, password: String): String? {
        val db = readableDatabase
        val query = """
        SELECT $COLUMN_ROLE FROM $TABLE_USERS 
        WHERE $COLUMN_NAME = ? AND $COLUMN_PASSWORD = ?
    """
        val cursor = db.rawQuery(query, arrayOf(name, password))

        return if (cursor.moveToFirst()) {
            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE)).also {
                cursor.close()
            }
        } else {
            cursor.close()
            null
        }
    }


    data class MedicalHistory(
        val username: String,
        val patientName: String,
        val age: Int,
        val sex: String,
        val birthdate: String,
        val phone: String,
        val emergency: String,
        val occupation: String,
        val issues: String,
        val treatment: String,
        val dosage: String,
        val frequency: String,
        val purpose: String,
        val notes: String
    )

    // In DatabaseHelper class
    fun insertMedicalHistoryByUsername(
        username: String,
        patientName: String,
        age: Int,
        sex: String,
        birthdate: String,
        phone: String,
        emergency: String,
        occupation: String,
        issues: String,
        treatment: String,
        dosage: String,
        frequency: String,
        purpose: String,
        notes: String
    ): Boolean {
        val userId = getUserIdByUsername(username)
        if (userId == -1) return false

        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USER_ID, userId)
            put(COLUMN_PATIENT_NAME, patientName)
            put(COLUMN_AGE, age)
            put(COLUMN_SEX, sex)
            put(COLUMN_BIRTHDATE, birthdate)
            put(COLUMN_PHONE, phone)
            put(COLUMN_EMERGENCY, emergency)
            put(COLUMN_OCCUPATION, occupation)
            put(COLUMN_ISSUES, issues)
            put(COLUMN_TREATMENT, treatment)
            put(COLUMN_DOSAGE, dosage)
            put(COLUMN_FREQUENCY, frequency)
            put(COLUMN_PURPOSE, purpose)
            put(COLUMN_NOTES, notes)
        }

        // First try to update existing record
        val rowsAffected = db.update(
            TABLE_MEDICAL_HISTORY,
            values,
            "$COLUMN_USER_ID = ?",
            arrayOf(userId.toString())
        )

        // If no existing record, insert new one
        val result = if (rowsAffected == 0) {
            db.insert(TABLE_MEDICAL_HISTORY, null, values)
        } else {
            1L // Success
        }

        db.close()
        return result != -1L
    }

    public fun getUserIdByUsername(username: String): Int {
        val db = readableDatabase
        db.query(
            TABLE_USERS,
            arrayOf(COLUMN_ID),
            "$COLUMN_NAME = ?",
            arrayOf(username),
            null, null, null
        ).use { cursor ->
            return if (cursor.moveToFirst()) {
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            } else {
                -1
            }
        }
    }

    fun getMedicalHistoryByUsername(username: String): MedicalHistory? {
        val db = readableDatabase
        val query = """
        SELECT mh.* FROM $TABLE_MEDICAL_HISTORY mh
        JOIN $TABLE_USERS u ON mh.$COLUMN_USER_ID = u.$COLUMN_ID
        WHERE u.$COLUMN_NAME = ?
        LIMIT 1
    """.trimIndent()

        db.rawQuery(query, arrayOf(username)).use { cursor ->
            return if (cursor.moveToFirst()) {
                MedicalHistory(
                    username = username,
                    patientName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PATIENT_NAME)),
                    age = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AGE)),
                    sex = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SEX)),
                    birthdate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BIRTHDATE)),
                    phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)),
                    emergency = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMERGENCY)),
                    occupation = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OCCUPATION)),
                    issues = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ISSUES)),
                    treatment = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TREATMENT)),
                    dosage = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DOSAGE)),
                    frequency = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FREQUENCY)),
                    purpose = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PURPOSE)),
                    notes = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTES))
                )
            } else {
                null
            }
        }
    }

    // Add this to your DatabaseHelper class
// Replace the existing bookAppointment method with this one
    fun bookAppointment(patientId: Int, doctorId: Int, date: String, notes: String = ""): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_PATIENT_ID, patientId)
            put(COLUMN_DOCTOR_ID, doctorId)
            put(COLUMN_APPT_DATE, date)
            put(COLUMN_NOTES, notes)
            // Status defaults to 'Pending' as defined in table creation
        }

        val result = db.insert(TABLE_APPOINTMENTS, null, values)
        db.close()
        return result != -1L
    }

    fun hasAppointment(patientId: Int, doctorId: Int, date: String): Boolean {
        val db = readableDatabase
        val query = """
        SELECT COUNT(*) FROM $TABLE_APPOINTMENTS 
        WHERE $COLUMN_PATIENT_ID = ? 
        AND $COLUMN_DOCTOR_ID = ? 
        AND $COLUMN_APPT_DATE = ?
    """.trimIndent()

        db.rawQuery(query, arrayOf(patientId.toString(), doctorId.toString(), date)).use { cursor ->
            return if (cursor.moveToFirst()) {
                cursor.getInt(0) > 0
            } else {
                false
            }
        }
    }

    fun doesTableExist(tableName: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery("""
        SELECT name FROM sqlite_master 
        WHERE type='table' AND name=?
    """.trimIndent(), arrayOf(tableName))
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }



}