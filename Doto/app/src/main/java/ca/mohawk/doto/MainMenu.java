package ca.mohawk.doto;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import ca.mohawk.doto.Helpers.Config;
import ca.mohawk.doto.Helpers.SharedPref;
import ca.mohawk.doto.Objects.User;
import ca.mohawk.doto.databinding.ActivityMainMenuBinding;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

//https://developer.android.com/training/volley
//https://github.com/google/gson

public class MainMenu extends AppCompatActivity {

    private ActivityMainMenuBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu id as a set because each should be considered its own activity.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_messages, R.id.navigation_todo,R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main_navigation);
        NavigationUI.setupWithNavController(binding.navView, navController);

        if (Login.user.email.isEmpty())
        {
            String url = Config.url + "/login.php";
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                Gson gson = new Gson();
                                JSONArray array = new JSONArray(response);
                                Login.user = gson.fromJson(array.getString(0), User.class);
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                             Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    // the POST parameters:
                    params.put("email", new SharedPref(getApplicationContext()).GetSharedPref("email"));
                    params.put("password", "");
                    return params;
                }
            };
            Volley.newRequestQueue(getApplicationContext()).add(postRequest);
        }
    }
}