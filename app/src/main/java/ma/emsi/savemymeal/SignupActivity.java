/*
 * Projet : SaveMyMeal
 * Fichier : SignupActivity.java
 * Description : Création d'un compte client dans SQLite
 * Responsables :
 * - Abdelali Raqioui
 * - Salah Eddine Toubi
 */

package ma.emsi.savemymeal;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        databaseHelper = new DatabaseHelper(this);

        EditText editName = findViewById(R.id.editSignupName);
        EditText editEmail = findViewById(R.id.editSignupEmail);
        EditText editPassword = findViewById(R.id.editSignupPassword);
        EditText editConfirmPassword =
                findViewById(R.id.editSignupConfirmPassword);

        Button btnCreateAccount = findViewById(R.id.btnCreateAccount);
        Button btnBack = findViewById(R.id.btnSignupBack);

        btnCreateAccount.setOnClickListener(view -> {
            String name = editName.getText().toString().trim();
            String email = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString();
            String confirmPassword =
                    editConfirmPassword.getText().toString();

            if (name.isEmpty() || email.isEmpty()
                    || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(
                        this,
                        "Remplissez tous les champs",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                editEmail.setError("Adresse e-mail invalide");
                return;
            }

            if (password.length() < 6) {
                editPassword.setError(
                        "Le mot de passe doit contenir au moins 6 caractères"
                );
                return;
            }

            if (!password.equals(confirmPassword)) {
                editConfirmPassword.setError(
                        "Les mots de passe ne correspondent pas"
                );
                return;
            }

            long result = databaseHelper.creerCompteClient(
                    name,
                    email,
                    password
            );

            if (result != -1) {
                Toast.makeText(
                        this,
                        "Compte créé. Connectez-vous maintenant.",
                        Toast.LENGTH_LONG
                ).show();
                finish();
            } else {
                editEmail.setError("Cette adresse e-mail est déjà utilisée");
            }
        });

        btnBack.setOnClickListener(view -> finish());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}