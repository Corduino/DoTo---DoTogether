package ca.mohawk.doto;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import ca.mohawk.doto.Helpers.Config;
import ca.mohawk.doto.Objects.User;

public class Reset extends AppCompatActivity {
    EditText passEdit;
    String password,username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        Intent i = getIntent();
        username = i.getStringExtra("name");


        Button resetButton = findViewById(R.id.requestButton2);
        resetButton.setOnClickListener(this::on_resetPass);
    }

    public void on_resetPass(View v) {
        EditText passEdit = findViewById(R.id.password_reset_editText);
        Intent i = getIntent();
        password = passEdit.getText().toString();

        if (!password.isEmpty()) {
            final ProgressDialog progressDialog = new ProgressDialog(Reset.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
            Log.e("Data sent:",username+"+"+password);
            String url = Config.url + "set_param.php";
            StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressDialog.dismiss();
                    try {
                        if (response.startsWith("success")) {
                            Toast.makeText(getApplicationContext(), "Password Reset", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Reset.this,Login.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            Log.e("Data sent:",username+"+"+password + " --- " + response.toString());
                            Toast.makeText(getApplicationContext(), "Invalid info", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) { progressDialog.dismiss(); }
                    }
            ){
                @Override
                protected Map<String, String> getParams()
                {
                    Map<String, String> params = new HashMap<>();
                    // the POST parameters:
                    params.put("username",username.trim());
                    params.put("param_type","password".trim());
                    params.put("new_param",password.trim());

                    return params;
                }
            };
            Volley.newRequestQueue(getApplicationContext()).add(postRequest);
        }
        else{
            Toast.makeText(this, "Please fill out all feilds", Toast.LENGTH_LONG).show();
        }
    }
    //On backbutton
    public void on_backPress(View view) {
        finish();
    }
}