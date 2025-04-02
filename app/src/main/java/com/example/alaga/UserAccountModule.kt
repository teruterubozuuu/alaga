package com.example.alaga

import UserAdapter
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yourpackage.models.User

class UserAccountModule : AppCompatActivity(), UserAdapter.UserClickListener {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var userAdapter: UserAdapter
    private lateinit var spinnerRole: Spinner
    private lateinit var recyclerViewUsers: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_account_module)

        dbHelper = DatabaseHelper(this)
        spinnerRole = findViewById(R.id.spinnerRole)
        recyclerViewUsers = findViewById(R.id.recyclerViewUsers)
        recyclerViewUsers.layoutManager = LinearLayoutManager(this)

        spinnerRole.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                loadUsers(spinnerRole.selectedItem.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        loadUsers("All")

        findViewById<Button>(R.id.backButton).setOnClickListener {
            val intent = Intent(this, Homepage::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun loadUsers(role: String) {
        val users = dbHelper.getAllUsers(role)
        userAdapter = UserAdapter(users, this)
        recyclerViewUsers.adapter = userAdapter
    }

    private fun showEditDialog(user: User) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_user, null)
        val editName = dialogView.findViewById<EditText>(R.id.editName)
        val editEmail = dialogView.findViewById<EditText>(R.id.editEmail)
        val editPassword = dialogView.findViewById<EditText>(R.id.editPassword)
        val spinnerRole = dialogView.findViewById<Spinner>(R.id.spinnerRoleEdit)

        // Pre-fill fields with user data
        editName.setText(user.name)
        editEmail.setText(user.email)
        editPassword.setText(user.password)

        // Set spinner selection
        val roles = arrayOf("Admin", "Doctor", "Nurse", "Patient")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRole.adapter = adapter
        spinnerRole.setSelection(roles.indexOf(user.role))

        // Show dialog
        AlertDialog.Builder(this)
            .setTitle("Edit User")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val newName = editName.text.toString()
                val newEmail = editEmail.text.toString()
                val newPassword = editPassword.text.toString()
                val newRole = spinnerRole.selectedItem.toString()

                // Update user in the database
                dbHelper.updateUser(user.id, newName, newEmail, newPassword, newRole)
                loadUsers(spinnerRole.selectedItem.toString()) // Refresh list
            }
            .setNegativeButton("Cancel", null)
            .show()
    }



    override fun onEdit(user: User) { showEditDialog(user) }
    override fun onDelete(userId: Int) { dbHelper.deleteUser(userId); loadUsers(spinnerRole.selectedItem.toString()) }
}