package ca.mohawk.doto.ui.home;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
//https://developer.android.com/training/volley
import ca.mohawk.doto.Adapters.TaskAdapter;
import ca.mohawk.doto.Helpers.Config;
import ca.mohawk.doto.Helpers.SharedPref;
import ca.mohawk.doto.Login;
import ca.mohawk.doto.Objects.Task;
import ca.mohawk.doto.R;
import ca.mohawk.doto.databinding.FragmentHomeBinding;
import com.google.gson.Gson;
//https://github.com/google/gson
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    CardView item_search_user;
    ArrayList<Task> list;
    RecyclerView recyclerView;

    Switch fav;
    EditText search;
    TaskAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        recyclerView=root.findViewById(R.id.RV);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        search=root.findViewById(R.id.search_editText);
        fav=root.findViewById(R.id.switch_favourite_only);

        fav.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked())
                {
                    String url = Config.url + "/get_task_from_fav.php";
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
                                            adapter=new TaskAdapter(getContext(),list);
                                            recyclerView.setAdapter(adapter);
                                        }
                                    } catch (Exception e) {
                                        list=new ArrayList<>();
                                        adapter=new TaskAdapter(getContext(),list);
                                        recyclerView.setAdapter(adapter);
                                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    list=new ArrayList<>();
                                    adapter=new TaskAdapter(getContext(),list);
                                    recyclerView.setAdapter(adapter);
                                }
                            }
                    ) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            // the POST parameters:
                            params.put("email", Login.user.email);

                            return params;
                        }
                    };

                    Volley.newRequestQueue(getContext()).add(postRequest);


                }else
                {
                    String url = Config.url + "/get_all_posts.php";
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
                                            adapter=new TaskAdapter(getContext(),list);
                                            recyclerView.setAdapter(adapter);
                                        }
                                    } catch (Exception e) {
                                        list=new ArrayList<>();
                                        adapter=new TaskAdapter(getContext(),list);
                                        recyclerView.setAdapter(adapter);
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    list=new ArrayList<>();
                                    adapter=new TaskAdapter(getContext(),list);
                                    recyclerView.setAdapter(adapter);
                                }
                            }
                    ) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            params.put("email", new SharedPref(getContext()).GetSharedPref("email"));
                            return params;
                        }
                    };
                    Volley.newRequestQueue(getContext()).add(postRequest);
                }
            }
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence);
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });

        String url = Config.url + "/get_all_posts.php";
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
                                adapter=new TaskAdapter(getContext(),list);
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
                // the POST parameter:
                params.put("email", new SharedPref(getContext()).GetSharedPref("email"));

                return params;
            }
        };
        Volley.newRequestQueue(getContext()).add(postRequest);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}