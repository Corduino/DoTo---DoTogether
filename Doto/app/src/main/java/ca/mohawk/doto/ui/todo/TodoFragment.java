package ca.mohawk.doto.ui.todo;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import androidx.annotation.NonNull;
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
import ca.mohawk.doto.Adapters.MyTaskAdapter;
import ca.mohawk.doto.Helpers.Config;
import ca.mohawk.doto.Login;
import ca.mohawk.doto.Objects.Task;
import ca.mohawk.doto.R;
import ca.mohawk.doto.databinding.FragmentTodoBinding;
import com.google.gson.Gson;
//https://github.com/google/gson
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TodoFragment extends Fragment {

    private TodoViewModel todoViewModel;
    private FragmentTodoBinding binding;
    Button add_new_button;
    ArrayList<Task> list;
    RecyclerView recyclerView;
    EditText search;
    MyTaskAdapter adapter;
    Switch bookmarked;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        todoViewModel = new ViewModelProvider(this).get(TodoViewModel.class);
        binding = FragmentTodoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        bookmarked=root.findViewById(R.id.switch_bookmarked);

        list=new ArrayList<>();
        //setting up recycler managment
        recyclerView=root.findViewById(R.id.RV);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        add_new_button = root.findViewById(R.id.add_new_task_button);

        add_new_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddNewTodo.class);
                startActivity(intent);
            }
        });

        search=root.findViewById(R.id.search_editText);
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

        bookmarked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked())
                {
                    String url = Config.url + "/get_bookmarked.php";
                    StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        list = new ArrayList<>();
                                        if (response.startsWith("error")) {
                                            Toast.makeText(getContext(), "No Data Found", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Gson gson = new Gson();
                                            JSONArray array = new JSONArray(response);
                                            for (int i = 0; i < array.length(); i++) {
                                                JSONObject obj = array.getJSONObject(i);
                                                Task task = gson.fromJson(obj.toString(), Task.class);
                                                list.add(task);
                                            }
                                            adapter = new MyTaskAdapter(getContext(), list,true);
                                            recyclerView.setAdapter(adapter);
                                        }
                                    } catch (Exception e) {
                                        list = new ArrayList<>();
                                        adapter = new MyTaskAdapter(getContext(), list,false);
                                        recyclerView.setAdapter(adapter);
                                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    list = new ArrayList<>();
                                    adapter = new MyTaskAdapter(getContext(), list,false);
                                    recyclerView.setAdapter(adapter);
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
                }else {
                    String url = Config.url + "/get_user_posts.php";
                    StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        list = new ArrayList<>();

                                        if (response.startsWith("error")) {
                                            Toast.makeText(getContext(), "No Data Found", Toast.LENGTH_SHORT).show();
                                        } else {

                                            Gson gson = new Gson();
                                            JSONArray array = new JSONArray(response);
                                            for (int i = 0; i < array.length(); i++) {
                                                JSONObject obj = array.getJSONObject(i);
                                                Task task = gson.fromJson(obj.toString(), Task.class);
                                                list.add(task);
                                            }
                                            adapter = new MyTaskAdapter(getContext(), list,false);
                                            recyclerView.setAdapter(adapter);
                                        }
                                    } catch (Exception e) {
                                        list = new ArrayList<>();
                                        adapter = new MyTaskAdapter(getContext(), list,false);
                                        recyclerView.setAdapter(adapter);
                                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    list = new ArrayList<>();
                                    adapter = new MyTaskAdapter(getContext(), list,false);
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
                }
            }
        });
        return root;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    @Override
    public void onResume() {
        super.onResume();
        String url = Config.url + "/get_user_posts.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            list = new ArrayList<>();

                            if (response.startsWith("error")) {
                                Toast.makeText(getContext(), "No Data Found", Toast.LENGTH_SHORT).show();
                            } else {
                                Gson gson = new Gson();
                                JSONArray array = new JSONArray(response);
                                for (int i=0;i<array.length();i++)
                                {
                                    JSONObject obj=array.getJSONObject(i);
                                    Task task = gson.fromJson(obj.toString(), Task.class);
                                    list.add(task);
                                }
                                adapter=new MyTaskAdapter(getContext(),list,false);
                                recyclerView.setAdapter(adapter);
                            }
                        } catch (Exception e){
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
                // the POST parameters:
                params.put("email", Login.user.email);
                return params;
            }
        };
        Volley.newRequestQueue(getContext()).add(postRequest);
    }
}