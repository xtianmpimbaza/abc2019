package info.androidhive.movietickets;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import info.androidhive.movietickets.Globals.CONFIG;

public class TicketResultActivity extends AppCompatActivity {
    private static final String TAG = TicketResultActivity.class.getSimpleName();

    // url to search barcode
    private static final String URL = CONFIG.TICKET_CHECK_URL;
//    private static final String URL = "https://api.androidhive.info/barcodes/search.php?code=";

    private TextView txtName, txtCountry, txtCompany, txtGenre, txtError;
//    private TextView txtName, txtCountry, txtCompany, txtGenre, txtRating, txtPrice, txtError;
    private ImageView imgPoster;
//    private Button btnBuy;
    private ProgressBar progressBar;
    private TicketView ticketView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_result);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtName = findViewById(R.id.name);
        txtCompany = findViewById(R.id.director);
        txtCountry = findViewById(R.id.duration);

//        imgPoster = findViewById(R.id.poster);
//        txtGenre = findViewById(R.id.genre);
//        imgPoster = findViewById(R.id.poster);
        txtError = findViewById(R.id.txt_error);
        ticketView = findViewById(R.id.layout_ticket);
        progressBar = findViewById(R.id.progressBar);

        String barcode = getIntent().getStringExtra("code");

        // close the activity in case of empty barcode
        if (TextUtils.isEmpty(barcode)) {
            Toast.makeText(getApplicationContext(), "Barcode is empty!", Toast.LENGTH_LONG).show();
            finish();
        }

        // search the barcode
//        searchBarcode("dunkirk");
        searchBarcode(barcode);
    }

    /**
     * Searches the barcode by making http call
     * Request was made using Volley network library but the library is
     * not suggested in production, consider using Retrofit
     */
    private void searchBarcode(String barcode) {

        // making volley's json request
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                URL + barcode, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, "Ticket response: " + response.toString());

                        // check for success status
//                        if (!response.has("error")) {
                            // received movie response
                            renderMovie(response);
//                        } else {
//                             no movie found
//                            showNoTicket();
//                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                showNoTicket();
            }
        });

        MyApplication.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void showNoTicket() {
        txtError.setVisibility(View.VISIBLE);
        ticketView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    /**
     * Rendering movie details on the ticket
     */
    private void renderMovie(JSONObject response) {
        try {

            // converting json to movie object
            Movie movie = new Gson().fromJson(response.toString(), Movie.class);

            if (movie != null) {
                txtName.setText(movie.getName());
                txtCompany.setText(movie.getCompany());
                txtCountry.setText(movie.getCountry());
//                Glide.with(this).load(movie.getPoster()).into(imgPoster);
                
                ticketView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            } else {
                // movie not found
                showNoTicket();
            }
        } catch (JsonSyntaxException e) {
            Log.e(TAG, "JSON Exception: " + e.getMessage());
            showNoTicket();
            Toast.makeText(getApplicationContext(), "Error occurred. Check your LogCat for full report", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            // exception
            showNoTicket();
            Toast.makeText(getApplicationContext(), "Error occurred. Check your LogCat for full report", Toast.LENGTH_SHORT).show();
        }
    }
    
//    private void renderMovie(JSONObject response) {
//        try {
//
//            // converting json to movie object
////            Movie movie = new Gson().fromJson(response.toString(), Movie.class);
//
//                txtName.setText("African Blockchain Conference");
//                txtCompany.setText("Ticket");
//                txtCountry.setText("VIP");
//                txtGenre.setText("Serena");
////                Glide.with(this).load("https://www.africanblockchain.org/wp-content/uploads/2019/03/Conference-Logo2019-01-1.png").into(imgPoster);
//
//                ticketView.setVisibility(View.VISIBLE);
//                progressBar.setVisibility(View.GONE);
//
//        } catch (JsonSyntaxException e) {
//            Log.e(TAG, "JSON Exception: " + e.getMessage());
//            showNoTicket();
//            Toast.makeText(getApplicationContext(), "Error occurred.", Toast.LENGTH_SHORT).show();
//        } catch (Exception e) {
//            // exception
//            showNoTicket();
//            Toast.makeText(getApplicationContext(), "Error occurred.", Toast.LENGTH_SHORT).show();
//        }
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private class Movie {
        String name;
        String company;
        String country;
        String address;
        String id;

        @SerializedName("released")
        
        public String getName() {
            return name;
        }

        public String getCompany() {
            return company;
        }

        public String getCountry() {
            return country;
        }

        public String getAddress() {
            return address;
        }

        public String getId() {
            return id;
        }


    }
}
