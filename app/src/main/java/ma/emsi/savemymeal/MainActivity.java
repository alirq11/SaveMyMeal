/*
 * Projet : SaveMyMeal
 * Fichier : MainActivity.java
 * Description : Affichage des offres et accès aux commandes du client
 * Responsables :
 * - Abdelali Raqioui
 * - Salah Eddine Toubi
 */

package ma.emsi.savemymeal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private ListView listOffers;
    private ArrayList<String> offers;
    private String clientEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (view, insets) -> {
                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    view.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );

        clientEmail = getIntent().getStringExtra("client_email");

        databaseHelper = new DatabaseHelper(this);
        listOffers = findViewById(R.id.listOffers);

        Button btnRefresh = findViewById(R.id.btnRefresh);
        Button btnMyOrders = findViewById(R.id.btnMyOrders);

        afficherOffres();

        listOffers.setOnItemClickListener((parent, view, position, id) -> {
            String selectedOffer = offers.get(position);

            Intent intent = new Intent(
                    MainActivity.this,
                    OfferDetailsActivity.class
            );
            intent.putExtra("offre_selectionnee", selectedOffer);
            intent.putExtra("client_email", clientEmail);
            startActivity(intent);
        });

        btnRefresh.setOnClickListener(view -> {
            afficherOffres();
            Toast.makeText(
                    MainActivity.this,
                    "Offres rechargées depuis la base de données",
                    Toast.LENGTH_SHORT
            ).show();
        });

        btnMyOrders.setOnClickListener(view -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    OrdersActivity.class
            );
            intent.putExtra("client_email", clientEmail);
            startActivity(intent);
        });
    }

    private void afficherOffres() {
        offers = databaseHelper.getAllOffers();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                offers
        );

        listOffers.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}