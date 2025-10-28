package br.unisanta.tp5_kotlin.approom.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import br.unisanta.tp5_kotlin.approom.controller.UserController
import br.unisanta.tp5_kotlin.approom.dao.UserDao
import br.unisanta.tp5_kotlin.approom.db.AppDatabase
import br.unisanta.tp5_kotlin.approom.model.User
import br.unisanta.usuario_sqlroom.databinding.ActivityProfileBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var userDao: UserDao
    private lateinit var userController: UserController
    private var currentUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AppDatabase.getDatabase(applicationContext)
        userDao = db.userDao()
        userController = UserController(userDao)

        val sharedPrefs = getSharedPreferences("user_session", MODE_PRIVATE)
        val currentUserId = sharedPrefs.getInt("logged_in_uid", -1)

        if (currentUserId == -1) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        loadUserProfile(currentUserId)

        binding.btnUpdate.setOnClickListener {
            updateUserProfile()
        }

        binding.btnLogout.setOnClickListener {
            logout()
        }

        binding.btnDeleteAccount.setOnClickListener {
            confirmDeleteAccount()
        }
    }

    private fun loadUserProfile(userId: Int) {
        lifecycleScope.launch {
            currentUser = withContext(Dispatchers.IO) {
                userController.getUserById(userId)
            }

            currentUser?.let {
                binding.edtProfileEmail.setText(it.email)
                binding.edtProfileNome.setText(it.name)
                binding.edtProfileIdade.setText(it.idade.toString())
                binding.edtProfileTelefone.setText(it.telefone.toString())
                binding.edtProfileCurso.setText(it.curso)
            } ?: run {
                Toast.makeText(this@ProfileActivity, "Erro ao carregar perfil.", Toast.LENGTH_SHORT).show()
                logout()
            }
        }
    }

    private fun updateUserProfile() {
        val nome = binding.edtProfileNome.text.toString().trim()
        val idadeStr = binding.edtProfileIdade.text.toString().trim()
        val telefoneStr = binding.edtProfileTelefone.text.toString().trim()
        val curso = binding.edtProfileCurso.text.toString().trim()

        if (nome.isBlank() || idadeStr.isBlank() || telefoneStr.isBlank() || curso.isBlank()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        val idade = idadeStr.toIntOrNull()
        if (idade == null) {
            Toast.makeText(this, "Idade inválida", Toast.LENGTH_SHORT).show()
            return
        }

        val telefoneRegex = Regex("^\\d{11}\$")
        if (!telefoneStr.matches(telefoneRegex)) {
            Toast.makeText(this, "Telefone deve ter 11 números", Toast.LENGTH_SHORT).show()
            return
        }
        val telefone = telefoneStr.toLong()

        lifecycleScope.launch {
            currentUser?.let {
                val updatedUser = it.copy(
                    name = nome,
                    idade = idade,
                    telefone = telefone,
                    curso = curso
                )
                withContext(Dispatchers.IO) {
                    userController.updateUser(updatedUser)
                }
                Toast.makeText(this@ProfileActivity, "Dados atualizados com sucesso!", Toast.LENGTH_SHORT).show()
            } ?: run {
                Toast.makeText(this@ProfileActivity, "Erro: Usuário não encontrado.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun confirmDeleteAccount() {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Deletar conta")
            .setMessage("Tem certeza que deseja deletar sua conta? Esta ação não pode ser desfeita.")
            .setPositiveButton("Deletar") { _, _ ->
                deleteAccount()
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()
    }

    private fun deleteAccount() {
        val user = currentUser ?: return

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                userController.deleteUser(user)
            }

            // Remove sessão
            val sharedPrefs = getSharedPreferences("user_session", MODE_PRIVATE)
            sharedPrefs.edit().clear().apply()

            Toast.makeText(this@ProfileActivity, "Conta deletada com sucesso!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@ProfileActivity, LoginActivity::class.java))
            finish()
        }
    }

    private fun logout() {
        val sharedPrefs = getSharedPreferences("user_session", MODE_PRIVATE)
        sharedPrefs.edit().clear().apply()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
