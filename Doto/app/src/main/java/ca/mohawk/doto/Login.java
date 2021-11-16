package ca.mohawk.doto;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
//https://developer.android.com/training/volley
import ca.mohawk.doto.Helpers.Config;
import ca.mohawk.doto.Helpers.EditTextValueCheck;
import ca.mohawk.doto.Helpers.SharedPref;
import ca.mohawk.doto.Objects.User;
import com.google.gson.Gson;
//https://github.com/google/gson
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
//https://github.com/nabinbhandari/Android-Permissions

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {
    EditText email_editText, pass_editText;

    public static User user;
    String Password,Email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email_editText =findViewById(R.id.email_reset_editText);
        pass_editText =findViewById(R.id.password_editText);
        user=new User();
        get_permissions();
    }
    //ON login_button to see if the info is correct, and if it is start calls and send to main nav
    public void on_login_button(View view) {

        EditTextValueCheck check = new EditTextValueCheck();
        if (check.hasValue(email_editText) && check.hasValue(pass_editText)) {
            final ProgressDialog progressDialog = new ProgressDialog(Login.this);
            //User Readability to make sure they dont click twice
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();

            Password = pass_editText.getText().toString().trim();
            Email = email_editText.getText().toString().trim();

            String url = Config.url + "/login.php";
            StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.dismiss();
                            try {
                                if (response.startsWith("invalid")) {
                                    Toast.makeText(getApplicationContext(), "Invalid Email", Toast.LENGTH_SHORT).show();
                                } else {

                                    Gson gson = new Gson();
                                    JSONArray array = new JSONArray(response);
                                    user = gson.fromJson(array.getString(0), User.class);
                                    // Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                                    // JSONArray json_stores1=json_stores.getJSONArray(0);

                                    if (Password.equals(user.password))
                                    {
                                        SharedPref sh = new SharedPref(Login.this);
                                        sh.SaveSharedPref("email", user.email);
                                        Intent intent = new Intent(Login.this, MainMenu.class);
                                        startActivity(intent);
                                        finish();
                                    }else {
                                        pass_editText.setError("Wrong Password");
                                    }
                                }
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    // the POST parameters:
                    params.put("email", Email);
                    params.put("password", Password);
                    return params;
                }
            };
            Volley.newRequestQueue(getApplicationContext()).add(postRequest);
        }


    }
    public void on_forgot_button(View view){
        Intent intent = new Intent(Login.this, Forgot.class);
        startActivity(intent);
    }

    public void on_backPress(View view) {
        finish();
    }

    public void on_signUp_button(View view) {
        Intent intent = new Intent(Login.this,Signup.class);
        startActivity(intent);
    }
    private void get_permissions() {

        final String[] permissions = { Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION};
        Permissions.check(this/*context*/, permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
            @Override
            public void onGranted() {

                if (!new SharedPref(Login.this).GetSharedPref("email").isEmpty())
                {
                    Intent intent = new Intent(Login.this, MainMenu.class);
                    startActivity(intent);
                    finish();
                }
            }
            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                get_permissions();
            }
        });
    }
}