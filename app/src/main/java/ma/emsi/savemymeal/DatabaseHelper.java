/*
 * Projet : SaveMyMeal
 * Fichier : DatabaseHelper.java
 * Description : Création et gestion de la base SQLite
 * Responsables :
 * - Abdelali Raqioui
 * - Salah Eddine Toubi
 */

package ma.emsi.savemymeal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "savemymeal.db";
    private static final int DATABASE_VERSION = 3;

    private static final String TABLE_OFFERS = "offers";
    private static final String TABLE_ORDERS = "orders";
    private static final String TABLE_CLIENTS = "clients";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE offers (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "restaurant TEXT NOT NULL, " +
                        "meal TEXT NOT NULL, " +
                        "old_price INTEGER NOT NULL, " +
                        "new_price INTEGER NOT NULL)"
        );

        db.execSQL(
                "CREATE TABLE orders (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "customer_name TEXT NOT NULL, " +
                        "phone TEXT NOT NULL, " +
                        "offer_text TEXT NOT NULL, " +
                        "client_email TEXT, " +
                        "quantity INTEGER NOT NULL, " +
                        "created_at TEXT DEFAULT CURRENT_TIMESTAMP)"
        );

        creerTableClients(db);

        ajouterOffre(db, "Restaurant Atlas", "Couscous", 35, 18);
        ajouterOffre(db, "Snack Medina", "Sandwich", 30, 15);
        ajouterOffre(db, "Pizzeria Amal", "Pizza", 50, 25);

        ajouterClientDemo(db);
    }

    private void creerTableClients(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS clients (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "full_name TEXT NOT NULL, " +
                        "email TEXT NOT NULL UNIQUE, " +
                        "password TEXT NOT NULL, " +
                        "role TEXT NOT NULL)"
        );
    }

    private void ajouterClientDemo(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put("full_name", "Client SaveMyMeal");
        values.put("email", "client@savemymeal.ma");
        values.put("password", "Client123!");
        values.put("role", "CLIENT");

        db.insertWithOnConflict(
                TABLE_CLIENTS,
                null,
                values,
                SQLiteDatabase.CONFLICT_IGNORE
        );
    }

    public long creerCompteClient(
            String fullName,
            String email,
            String password
    ) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("full_name", fullName);
        values.put("email", email);
        values.put("password", password);
        values.put("role", "CLIENT");

        try {
            return db.insertOrThrow(TABLE_CLIENTS, null, values);
        } catch (SQLiteConstraintException e) {
            return -1; // Email déjà utilisé
        }
    }

    public boolean authentifierClient(String email, String password) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id FROM " + TABLE_CLIENTS +
                        " WHERE email = ? AND password = ? AND role = ?",
                new String[]{email, password, "CLIENT"}
        );

        try {
            return cursor.moveToFirst();
        } finally {
            cursor.close();
        }
    }

    private void ajouterOffre(
            SQLiteDatabase db,
            String restaurant,
            String meal,
            int oldPrice,
            int newPrice
    ) {
        ContentValues values = new ContentValues();
        values.put("restaurant", restaurant);
        values.put("meal", meal);
        values.put("old_price", oldPrice);
        values.put("new_price", newPrice);

        db.insert(TABLE_OFFERS, null, values);
    }

    public ArrayList<String> getAllOffers() {
        ArrayList<String> offers = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_OFFERS,
                new String[]{
                        "restaurant",
                        "meal",
                        "old_price",
                        "new_price"
                },
                null,
                null,
                null,
                null,
                "id ASC"
        );

        try {
            while (cursor.moveToNext()) {
                String restaurant = cursor.getString(0);
                String meal = cursor.getString(1);
                int oldPrice = cursor.getInt(2);
                int newPrice = cursor.getInt(3);

                offers.add(
                        restaurant + " — " + meal + " — " +
                                oldPrice + " DH → " + newPrice + " DH"
                );
            }
        } finally {
            cursor.close();
        }

        return offers;
    }

    public long insertOrder(
            String customerName,
            String phone,
            String offerText,
            String clientEmail,
            int quantity
    ) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("customer_name", customerName);
        values.put("phone", phone);
        values.put("offer_text", offerText);
        values.put("client_email", clientEmail);
        values.put("quantity", quantity);

        return db.insert(TABLE_ORDERS, null, values);
    }

    public ArrayList<String> getOrdersForClient(String email) {
        ArrayList<String> orders = new ArrayList<>();

        if (email == null) {
            return orders;
        }

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id, offer_text, quantity, created_at " +
                        "FROM " + TABLE_ORDERS +
                        " WHERE client_email = ? ORDER BY id DESC",
                new String[]{email}
        );

        try {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String offer = cursor.getString(1);
                int quantity = cursor.getInt(2);
                String date = cursor.isNull(3)
                        ? "Date inconnue"
                        : cursor.getString(3);

                orders.add(
                        "Commande #" + id +
                                "\n" + offer +
                                "\nQuantité : " + quantity +
                                "\nDate : " + date
                );
            }
        } finally {
            cursor.close();
        }

        return orders;
    }

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion
    ) {
        if (oldVersion < 2) {
            creerTableClients(db);
            ajouterClientDemo(db);
        }

        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE orders ADD COLUMN client_email TEXT");
        }
    }
}
