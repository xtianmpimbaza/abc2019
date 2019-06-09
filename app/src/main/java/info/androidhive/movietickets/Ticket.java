package info.androidhive.movietickets;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.crowdfire.cfalertdialog.CFAlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.androidhive.movietickets.Globals.CONFIG;

public class Ticket extends AppCompatActivity {
    private TextView txtName, txtCountry, txtCompany;
    ProgressDialog progressDialog;
    @BindView(R.id.layout_ticket)
    ConstraintLayout ticketLayout;
    @BindView(R.id.txt_error)
    ConstraintLayout txtError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Ticket");
        ButterKnife.bind(this);

        txtName = findViewById(R.id.delegate_name);
        txtCompany = findViewById(R.id.delegate_company);
        txtCountry = findViewById(R.id.delegate_country);

        String barcode = getIntent().getStringExtra("code");

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading .....");

        // close the activity in case of empty barcode
        if (TextUtils.isEmpty(barcode)) {
            Toast.makeText(getApplicationContext(), "Barcode is empty!", Toast.LENGTH_LONG).show();
            finish();
        }

        // search the barcode
//        searchBarcode("dunkirk");
        searchBarcode(barcode);
    }

    private void searchBarcode(String barcode) {

        RequestQueue queue = Volley.newRequestQueue(this);
        progressDialog.show();
        final JSONObject params = new JSONObject();

        try {
            params.put("address", barcode);
        } catch (JSONException e) {
        }

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, CONFIG.TICKET_CHECK_URL, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String stat = response.toString();
                        Log.e("response", stat);
                        try {
                            String name = response.getString("name");
                            String company = response.getString("company");
                            String country = response.getString("country");
                            onNetworkSuccessful(name, company, country);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            showNoTicket();
                        }

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                showNoTicket();
            }
        });
        queue.add(req);
    }
    private void showNoTicket() {
        txtError.setVisibility(View.VISIBLE);
        ticketLayout.setVisibility(View.GONE);
        progressDialog.dismiss();
        Log.e("Ticket","showNoTicket");
    }

    void onNetworkSuccessful(String name, String company, String country) {
        txtName.setText(name);
        txtCompany.setText(company);
        txtCountry.setText(country);
        txtError.setVisibility(View.GONE);
        ticketLayout.setVisibility(View.VISIBLE);
        progressDialog.dismiss();
        Log.e("Ticket","onNetworkSuccessful");
    }

    void onNetworkFailed() {
        final CFAlertDialog.Builder builder = new CFAlertDialog.Builder(this)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.NOTIFICATION)
                .setTitle("Error")
                .setIcon(getResources().getDrawable(R.drawable.ic_failed))
                .setMessage("Failed, check your internet connection and try again");
        builder.show();
        progressDialog.dismiss();
        Log.e("Ticket","onNetworkFailed");
    }


}
