package ca.mohawk.doto;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
//https://developer.android.com/training/volley
import ca.mohawk.doto.Adapters.FavAdapter;
import ca.mohawk.doto.Helpers.Config;
import ca.mohawk.doto.Helpers.SharedPref;
import com.google.gson.Gson;
//https://github.com/google/gson
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Favourites extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<String> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);
        //Setting up recycler view management
        recyclerView=findViewById(R.id.RV);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    @Override
    protected void onResume() {
        super.onResume();
        String url = Config.url + "/get_favs.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if (response.startsWith("no")) {
                                Toast.makeText(getApplicationContext(), "No Data Found", Toast.LENGTH_SHORT).show();
                            } else {
                                list=new ArrayList<>();
                                Gson gson = new Gson();
                                JSONArray array = new JSONArray(response);
                                for (int i=0;i<array.length();i++)
                                {
                                    JSONObject obj=array.getJSONObject(i);
                                    String task = obj.getString("fav");
                                    list.add(task);
                                }
                                FavAdapter adapter=new FavAdapter(getApplicationContext(),list);
                                recyclerView.setAdapter(adapter);
                            }
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // the POST parameters:
                params.put("email", new SharedPref(getApplicationContext()).GetSharedPref("email"));
                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(postRequest);
    }
}