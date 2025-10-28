package br.unisanta.tp5_kotlin.approom.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import br.unisanta.approom.R
import br.unisanta.approom.databinding.ActivityMainBinding
import br.unisanta.tp5_kotlin.approom.dao.UserDao
import br.unisanta.tp5_kotlin.approom.db.AppDatabase
import br.unisanta.tp5_kotlin.approom.model.User
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var db: AppDatabase
    private lateinit var dao: UserDao

    private fun showLoginView() {
        binding.txvTitle.text = "Login"
        binding.edtName.visibility = View.GONE
        binding.btnLogin.visibility = View.VISIBLE
        binding.btnGoToRegister.visibility = View.VISIBLE
        binding.btnRegister.visibility = View.GONE
        binding.btnGoToLogin.visibility = View.GONE
    }

    private fun showRegisterView() {
        binding.txvTitle.text = "Cadastro"
        binding.edtName.visibility = View.VISIBLE
        binding.btnLogin.visibility = View.GONE
        binding.btnGoToRegister.visibility = View.GONE
        binding.btnRegister.visibility = View.VISIBLE
        binding.btnGoToLogin.visibility = View.VISIBLE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "db-user"
        ).fallbackToDestructiveMigration()
            .build()

        dao = db.userDao()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        showLoginView()

        binding.btnGoToRegister.setOnClickListener {
            showRegisterView()
        }

        binding.btnGoToLogin.setOnClickListener {
            showLoginView()
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()

            lifecycleScope.launch {
                val user = dao.login(email, password)

                if (user != null) {
                    Toast.makeText(
                        this@MainActivity,
                        "Login realizado com sucesso",
                        Toast.LENGTH_SHORT
                    ).show()

                    startActivity(
                        Intent(this@MainActivity, UserActivity::class.java)
                            .putExtra("uid", user.uid)
                            .putExtra("email", email)
                            .putExtra("password", password)
                    )
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Email ou senha estão incorretos",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.btnRegister.setOnClickListener {
            val name = binding.edtName.text.toString()
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()

            lifecycleScope.launch {
                val uid = dao.insert(User(0, name, email, password))

                if (uid != -1L) {
                    Toast.makeText(
                        this@MainActivity,
                        "Cadastro realizado com sucesso",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Este email já está em uso",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}