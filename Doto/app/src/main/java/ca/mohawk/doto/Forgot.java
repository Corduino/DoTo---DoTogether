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
import ca.mohawk.doto.Helpers.EditTextValueCheck;

public class Forgot extends AppCompatActivity {
    EditText emailEdit,userEdit;
    String email,username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailEdit = findViewById(R.id.email_reset_editText);
        userEdit = findViewById(R.id.username_reset_editText);

        Button signupButton = findViewById(R.id.requestButton);
        signupButton.setOnClickListener(this::on_reset);
    }

    public void on_reset(View view) {

        EditTextValueCheck check = new EditTextValueCheck();
        if(check.hasValue(emailEdit) && check.hasValue(userEdit)){
            final ProgressDialog progressDialog = new ProgressDialog(Forgot.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();

            email = emailEdit.getText().toString().trim();
            username = userEdit.getText().toString().trim();

            String url = Config.url + "/validate_user.php";
            StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressDialog.dismiss();
                    try {
                        if (response.startsWith("success")) {
                            Intent intent = new Intent(Forgot.this,Reset.class);
                            intent.putExtra("name",username);
                            startActivity(intent);
                        } else {
                            Log.e("Data sent:",username+"+"+email + " --- " + response);
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
                    params.put("username",username);
                    params.put("email",email);

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