package ca.mohawk.doto;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
//https://developer.android.com/training/volley
import ca.mohawk.doto.Adapters.TaskAdapter;
import ca.mohawk.doto.Helpers.Config;
import ca.mohawk.doto.Objects.Task;
import ca.mohawk.doto.Objects.User;
import com.bumptech.glide.Glide;
//https://github.com/bumptech/glide
import com.google.gson.Gson;
import ca.mohawk.doto.R;
//https://github.com/google/gson
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
//https://github.com/hdodenhof/CircleImageView
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import nl.bryanderidder.themedtogglebuttongroup.ThemedButton;
import nl.bryanderidder.themedtogglebuttongroup.ThemedToggleButtonGroup;
//https://github.com/Bryanx/themed-toggle-button-group

public class ViewProfile extends AppCompatActivity {
    CircleImageView profilepic;
    TextView gender,name,location,bio;
    ArrayList<Task> list;

    ThemedButton favorite_button;
    ThemedToggleButtonGroup group;
    User user;
    public static String email;
    RecyclerView recyclerView;

    //On create function once the activity is loaded up
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        profilepic=findViewById(R.id.photo_imageView);
        gender=findViewById(R.id.gender_textView);
        name=findViewById(R.id.username_textView);
        location=findViewById(R.id.loc_textView);
        bio=findViewById(R.id.bio_textView);
        favorite_button =findViewById(R.id.btnMic);
        group=findViewById(R.id.toggleGroupadd_to_favourite);

        recyclerView=findViewById(R.id.RV);

        //Setting up the function prototype
        on_favorite_button();
        //Creating recycler view Managment
        recyclerView.setLayoutManager(new LinearLayoutManager(ViewProfile.this,LinearLayoutManager.VERTICAL,false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        //gets the post from a user under their basic profile info
        String url = Config.url + "/get_user_posts.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if (response.startsWith("error")) {
                                Toast.makeText(ViewProfile.this, "No Data Found", Toast.LENGTH_SHORT).show();
                            } else {
                                list=new ArrayList<>();
                                Gson gson = new Gson();
                                JSONArray array = new JSONArray(response);
                                for (int i=0;i<array.length();i++)
                                {
                                    JSONObject obj=array.getJSONObject(i);
                                    Task task = gson.fromJson(obj.toString(), Task.class);
                                    list.add(task);
                                }
                                TaskAdapter adapter=new TaskAdapter(ViewProfile.this,list);
                                recyclerView.setAdapter(adapter);
                            }
                        } catch (Exception e) {
                            Toast.makeText(ViewProfile.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
                params.put("email", email);
                return params;
            }
        };
        Volley.newRequestQueue(ViewProfile.this).add(postRequest);


        //get Profile data and starts displaying everything
        String url1 = Config.url + "/login.php";
        StringRequest postRequest1 = new StringRequest(Request.Method.POST, url1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if (response.startsWith("invalid")) {
                                Intent intent = new Intent(ViewProfile.this, Signup.class);
                                startActivity(intent);
                            } else {
                                Gson gson = new Gson();
                                JSONArray array = new JSONArray(response);
                                user = gson.fromJson(array.getString(0), User.class);

                                Glide
                                        .with(ViewProfile.this)
                                        .load(Config.url+"images/"+user.pic)
                                        .into(profilepic);
                                gender.setText(user.gender);
                                name.setText(user.name);
                                location.setText(user.location);
                                bio.setText(user.bio);
                            }
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
                params.put("email", email);
                params.put("password", "");
                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(postRequest1);
    }

    //Checks to see if favorited, and if not adds favorite
    private void on_favorite_button() {
        String url = Config.url+ "/get_fav.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("no"))
                        {
                        }else
                        {
                            group.selectButton(R.id.btnMic);
                        }
                        group.setOnSelectListener(new Function1<ThemedButton, Unit>() {
                            @Override
                            public Unit invoke(ThemedButton themedButton) {
                                if (!themedButton.isSelected())
                                {
                                    String url = Config.url+ "/remove_fav.php";
                                    StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                                                }
                                            },
                                            new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    //Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            }
                                    ) {
                                        @Override
                                        protected Map<String, String> getParams()
                                        {
                                            Map<String, String> params = new HashMap<>();
                                            // the POST parameters:
                                            params.put("fav",email);
                                            params.put("email",Login.user.email);
                                            return params;
                                        }
                                    };
                                    Volley.newRequestQueue(getApplicationContext()).add(postRequest);
                                }else
                                {
                                    String url = Config.url+ "/add_fav.php";
                                    StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                                                }
                                            },
                                            new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    // Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            }
                                    ) {
                                        @Override
                                        protected Map<String, String> getParams()
                                        {
                                            Map<String, String> params = new HashMap<>();
                                            // the POST parameters:
                                            params.put("fav",email);
                                            params.put("email",Login.user.email);

                                            return params;
                                        }
                                    };
                                    Volley.newRequestQueue(getApplicationContext()).add(postRequest);
                                }
                                return null;
                            }
                        });
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<>();
                // the POST parameters:
                params.put("fav",email);
                params.put("email",Login.user.email);
                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(postRequest);
    }
    //Closes the current Activity
    public void on_backPress(View view) {
        finish();
    }
    //Opens up the messeges with this user
    public void on_messege_button(View view) {
        Chat.email=email;
        Chat.user=user;
        Intent intent = new Intent(ViewProfile.this,Chat.class);
        startActivity(intent);
    }
}