/*
 * Projet : SaveMyMeal
 * Fichier : LoginActivity.java
 * Description : Connexion du client avec vérification SQLite
 * Responsables :
 * - Abdelali Raqioui
 * - Salah Eddine Toubi
 */

package ma.emsi.savemymeal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        databaseHelper = new DatabaseHelper(this);

        EditText editEmail = findViewById(R.id.editLoginEmail);
        EditText editPassword = findViewById(R.id.editLoginPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnGoSignup = findViewById(R.id.btnGoSignup);

        btnLogin.setOnClickListener(view -> {
            String email = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(
                        LoginActivity.this,
                        "Saisissez l'e-mail et le mot de passe",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            boolean connected = databaseHelper.authentifierClient(
                    email,
                    password
            );

            if (connected) {
                Intent intent = new Intent(
                        LoginActivity.this,
                        MainActivity.class
                );

                // Envoyer l'identité du client à MainActivity
                intent.putExtra("client_email", email);

                startActivity(intent);
                finish();
            } else {
                Toast.makeText(
                        LoginActivity.this,
                        "E-mail ou mot de passe incorrect",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        btnGoSignup.setOnClickListener(view -> {
            Intent intent = new Intent(
                    LoginActivity.this,
                    SignupActivity.class
            );
            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}