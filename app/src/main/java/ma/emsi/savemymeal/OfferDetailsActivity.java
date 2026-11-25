/*
 * Projet : SaveMyMeal
 * Fichier : OfferDetailsActivity.java
 * Description : Affichage des détails d'une offre
 * Responsables :
 * - Abdelali Raqioui
 * - Salah Eddine Toubi
 */

package ma.emsi.savemymeal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class OfferDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_details);

        TextView txtOfferDetails = findViewById(R.id.txtOfferDetails);
        Button btnOrder = findViewById(R.id.btnOrder);
        Button btnBack = findViewById(R.id.btnBack);

        String offer = getIntent().getStringExtra("offre_selectionnee");
        String clientEmail = getIntent().getStringExtra("client_email");

        if (offer == null) {
            offer = "Aucune offre sélectionnée.";
        }

        txtOfferDetails.setText(offer);

        final String selectedOffer = offer;

        btnOrder.setOnClickListener(view -> {
            Intent intent = new Intent(
                    OfferDetailsActivity.this,
                    OrderActivity.class
            );

            intent.putExtra("offre_selectionnee", selectedOffer);
            intent.putExtra("client_email", clientEmail);

            startActivity(intent);
        });

        btnBack.setOnClickListener(view -> finish());
    }
}