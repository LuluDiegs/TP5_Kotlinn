package br.unisanta.tp5_kotlin.approom.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import br.unisanta.tp5_kotlin.approom.controller.UserController
import br.unisanta.tp5_kotlin.approom.dao.UserDao
import br.unisanta.tp5_kotlin.approom.db.AppDatabase
import br.unisanta.tp5_kotlin.approom.model.User
import br.unisanta.usuario_sqlroom.databinding.ActivityUserBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserBinding
    private lateinit var userDao: UserDao
    private lateinit var userController: UserController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AppDatabase.getDatabase(applicationContext)
        userDao = db.userDao()
        userController = UserController(userDao)

        binding.btnRegister.setOnClickListener {
            registerUser()
        }
    }
    private fun registerUser() {
        val email = binding.edtRegisterEmail.text.toString().trim()
        val senha = binding.edtRegisterSenha.text.toString().trim()
        val nome = binding.edtRegisterNome.text.toString().trim()
        val idadeStr = binding.edtRegisterIdade.text.toString().trim()
        val telefoneStr = binding.edtRegisterTelefone.text.toString().trim()
        val curso = binding.edtRegisterCurso.text.toString().trim()

        if (email.isBlank() || senha.isBlank() || nome.isBlank() || idadeStr.isBlank() || telefoneStr.isBlank() || curso.isBlank()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        if (!email.matches(emailRegex)) {
            Toast.makeText(this, "Informe um email válido", Toast.LENGTH_SHORT).show()
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

        val newUser = User(
            uid = 0,
            email = email,
            password = senha,
            name = nome,
            idade = idade,
            telefone = telefone,
            curso = curso
        )

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                userController.registerUser(newUser)
            }
            Toast.makeText(this@UserActivity, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}