package ca.mohawk.doto.ui.profile;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
//https://developer.android.com/training/volley
import ca.mohawk.doto.Adapters.MyTaskAdapter;
import ca.mohawk.doto.EditProfile;
import ca.mohawk.doto.Favourites;
import ca.mohawk.doto.Helpers.Config;
import ca.mohawk.doto.Helpers.SharedPref;
import ca.mohawk.doto.Login;
import ca.mohawk.doto.Objects.Task;
import ca.mohawk.doto.R;
import com.bumptech.glide.Glide;
//https://github.com/bumptech/glide
import com.google.gson.Gson;
//https://github.com/google/gson
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private ProfileViewModel mViewModel;
    CircleImageView profilepic;
    TextView gender,name,location,bio,logout,fav;
    ArrayList<Task> list;
    Button edit_button;

    RecyclerView recyclerView;
    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.profile_fragment, container, false);

        profilepic=view.findViewById(R.id.photo_imageView);
        gender=view.findViewById(R.id.gender_textView);
        name=view.findViewById(R.id.username_textView);
        location=view.findViewById(R.id.loc_textView);
        bio=view.findViewById(R.id.bio_textView);
        logout=view.findViewById(R.id.logout_button);
        edit_button =view.findViewById(R.id.edit_profile_button);
        fav=view.findViewById(R.id.favorites_view_button);
        recyclerView=view.findViewById(R.id.RV);

        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), EditProfile.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SharedPref(getContext()).SaveSharedPref("email","");
                Intent intent = new Intent(getContext(), Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), Favourites.class);
                startActivity(intent);
            }
        });
        //setting up recylcer managment
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //Gets all users post for recycler
        String url = Config.url + "/get_user_posts.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if (response.startsWith("error")) {
                                Toast.makeText(getContext(), "No Data Found", Toast.LENGTH_SHORT).show();
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
                                MyTaskAdapter adapter=new MyTaskAdapter(getContext(),list,false);
                                recyclerView.setAdapter(adapter);
                            }
                        } catch (Exception e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
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
                params.put("email", Login.user.email);
                return params;
            }
        };
        Volley.newRequestQueue(getContext()).add(postRequest);
        //Process image glide
        Glide
                .with(getContext())
                .load(Config.url+"images/"+Login.user.pic)
                .into(profilepic);

        gender.setText(Login.user.gender);
        name.setText(Login.user.name);
        location.setText(Login.user.location);
        bio.setText(Login.user.bio);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        // TODO: Use the ViewModel
    }

}