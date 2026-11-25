/*
 * Projet : SaveMyMeal
 * Fichier : OrdersActivity.java
 * Description : Affichage des commandes du client connecté
 * Responsables :
 * - Abdelali Raqioui
 * - Salah Eddine Toubi
 */

package ma.emsi.savemymeal;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class OrdersActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        ListView listOrders = findViewById(R.id.listClientOrders);
        TextView txtNoOrders = findViewById(R.id.txtNoOrders);
        Button btnBack = findViewById(R.id.btnOrdersBack);

        String email = getIntent().getStringExtra("client_email");
        databaseHelper = new DatabaseHelper(this);

        ArrayList<String> orders = databaseHelper.getOrdersForClient(email);

        if (orders.isEmpty()) {
            listOrders.setVisibility(View.GONE);
            txtNoOrders.setVisibility(View.VISIBLE);
        } else {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_1,
                    orders
            );
            listOrders.setAdapter(adapter);
        }

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