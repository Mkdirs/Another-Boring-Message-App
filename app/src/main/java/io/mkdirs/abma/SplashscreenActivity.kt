package io.mkdirs.abma

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import io.mkdirs.abma.model.User
import io.mkdirs.abma.utils.DB
import io.mkdirs.abma.utils.PASSWORD_PREFS_KEY
import io.mkdirs.abma.utils.PREFS_NAME
import kotlinx.coroutines.*


class SplashscreenActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

        auth = FirebaseAuth.getInstance()
        DB.goOnline()
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)


        //Connexion automatique avec les identifiants enregistrés
        if(auth.currentUser != null){
            val mail = auth.currentUser?.email
            val password = prefs.getString(PASSWORD_PREFS_KEY, "null")
            auth.signInWithEmailAndPassword(mail!!, password!!).addOnCompleteListener {
                when(it.isSuccessful){
                    //Connexion réussie
                    true -> {
                        //L'utilisateur a confirmé son email
                        if(auth.currentUser!!.isEmailVerified) {
                            GlobalScope.launch {
                                User.currentUser = async { User.fromDB(auth.currentUser!!.uid) }.await()
                                startActivity(Intent(applicationContext, MainActivity::class.java))
                                finish()
                            }



                            //On attend la confirmation de l'email
                        } else {
                            startActivity(
                                Intent(
                                    applicationContext,
                                    EmailVerificationActivity::class.java
                                )
                            )
                            finish()
                        }


                    }
                    //Impossible de se connecter
                    else ->{
                        prefs.edit().clear().apply()
                        startActivity(Intent(applicationContext, SignUpActivity::class.java))
                        finish()
                    }
                }
            }
        }else{
            startActivity(Intent(applicationContext, SignUpActivity::class.java))
            finish()
        }

    }
}