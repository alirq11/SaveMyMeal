/*
 * Projet : SaveMyMeal
 * Fichier : OrderActivity.java
 * Description : Saisie et enregistrement d'une commande
 * Responsables :
 * - Abdelali Raqioui
 * - Salah Eddine Toubi
 */

package ma.emsi.savemymeal;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class OrderActivity extends AppCompatActivity {

    private String offer;
    private String clientEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        TextView txtSelectedOffer = findViewById(R.id.txtSelectedOffer);
        EditText editCustomerName = findViewById(R.id.editCustomerName);
        EditText editCustomerPhone = findViewById(R.id.editCustomerPhone);
        Spinner spinnerQuantity = findViewById(R.id.spinnerQuantity);
        CheckBox checkAccept = findViewById(R.id.checkAccept);
        Button btnConfirmOrder = findViewById(R.id.btnConfirmOrder);
        Button btnOrderBack = findViewById(R.id.btnOrderBack);

        offer = getIntent().getStringExtra("offre_selectionnee");
        clientEmail = getIntent().getStringExtra("client_email");

        if (offer == null) {
            offer = "Offre non disponible";
        }
        txtSelectedOffer.setText("Offre : " + offer);

        String[] quantities = {
                "1 repas",
                "2 repas",
                "3 repas",
                "4 repas"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                quantities
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );
        spinnerQuantity.setAdapter(adapter);

        btnConfirmOrder.setOnClickListener(view -> {
            String name = editCustomerName.getText().toString().trim();
            String phone = editCustomerPhone.getText().toString().trim();

            if (name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(
                        this,
                        "Remplissez votre nom et votre téléphone",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            if (!checkAccept.isChecked()) {
                Toast.makeText(
                        this,
                        "Veuillez confirmer les informations",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            if (clientEmail == null || clientEmail.trim().isEmpty()) {
                Toast.makeText(
                        this,
                        "Session client introuvable. Reconnectez-vous.",
                        Toast.LENGTH_LONG
                ).show();
                return;
            }

            String selectedQuantity =
                    spinnerQuantity.getSelectedItem().toString();
            int quantity = Integer.parseInt(
                    selectedQuantity.split(" ")[0]
            );

            DatabaseHelper databaseHelper = new DatabaseHelper(this);

            long orderId = databaseHelper.insertOrder(
                    name,
                    phone,
                    offer,
                    clientEmail,
                    quantity
            );

            databaseHelper.close();

            if (orderId != -1) {
                new AlertDialog.Builder(this)
                        .setTitle("Commande enregistrée")
                        .setMessage(
                                "Client : " + name
                                        + "\nTéléphone : " + phone
                                        + "\nQuantité : " + selectedQuantity
                        )
                        .setPositiveButton("OK", (dialog, which) -> finish())
                        .show();
            } else {
                Toast.makeText(
                        this,
                        "Erreur lors de l'enregistrement",
                        Toast.LENGTH_LONG
                ).show();
            }
        });

        btnOrderBack.setOnClickListener(view -> finish());
    }
}