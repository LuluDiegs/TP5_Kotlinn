package br.unisanta.tp5_kotlin.approom.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import br.unisanta.approom.R
import br.unisanta.approom.databinding.ActivityUserBinding
import br.unisanta.tp5_kotlin.approom.dao.UserDao
import br.unisanta.tp5_kotlin.approom.db.AppDatabase
import br.unisanta.tp5_kotlin.approom.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserBinding
    private lateinit var userDao: UserDao
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "db-user"
        ).fallbackToDestructiveMigration()
            .build()

        userDao = db.userDao()

        val uid = intent.getLongExtra("uid", -1L)
        if (uid == -1L) {
            Toast.makeText(this, "Usuário inválido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        lifecycleScope.launch {
            user = withContext(Dispatchers.IO) {
                userDao.getByUid(uid)
            }

            if (user == null) {
                Toast.makeText(this@UserActivity, "Usuário não encontrado", Toast.LENGTH_SHORT).show()
                finish()
                return@launch
            }

            binding.edtProfileName.setText(user!!.name)
            binding.edtProfileEmail.setText(user!!.email)
            binding.edtProfileSenha.setText(user!!.password)
        }

        binding.btnUpdate.setOnClickListener {
            lifecycleScope.launch {
                user?.let {
                    val updatedUser = it.copy(
                        name = binding.edtProfileName.text.toString(),
                        email = binding.edtProfileEmail.text.toString(),
                        password = binding.edtProfileSenha.text.toString()
                    )

                    withContext(Dispatchers.IO) {
                        userDao.update(updatedUser)

                    }
                    Toast.makeText(this@UserActivity, "Usuário atualizado", Toast.LENGTH_SHORT).show()
                }
                    ?:
                    Toast.makeText(this@UserActivity, "Usuário inválido", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnDelete.setOnClickListener {
            lifecycleScope.launch {
                user?.let {
                    withContext(Dispatchers.IO) {
                        userDao.delete(it)
                    }

                    Toast.makeText(this@UserActivity, "Usuário excluído", Toast.LENGTH_SHORT).show()
                    finish()
                }
                    ?:
                    Toast.makeText(this@UserActivity, "Usuário inválido", Toast.LENGTH_SHORT).show()
            }
        }
    }
}