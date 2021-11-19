package ca.mohawk.doto.ui.messages;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import ca.mohawk.doto.Adapters.ChatsAdapter;
import ca.mohawk.doto.Helpers.Config;
import ca.mohawk.doto.Login;
import ca.mohawk.doto.R;
import ca.mohawk.doto.databinding.FragmentMessagesBinding;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;

public class MessagesFragment extends Fragment {

    private MessagesViewModel messagesViewModel;
    private FragmentMessagesBinding binding;
    RecyclerView recyclerView;
    ArrayList<String> messages;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {messagesViewModel = new ViewModelProvider(this).get(MessagesViewModel.class);

        View v = inflater.inflate(R.layout.fragment_messages, container, false);
        //Setting up recycler view management
        recyclerView=v.findViewById(R.id.RV);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //This is to get all the available chat Id's that the user is apart of by taking the first part of Chat id and searching USer for them
        String url = Config.url + "/get_chats.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if (response.startsWith("error")) {
                                Toast.makeText(getContext(), "No Data Found", Toast.LENGTH_SHORT).show();
                            } else {
                                //Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();
                                Log.e("dataMessege Frag",response);
                                messages=new ArrayList<>();
                                JSONArray array = new JSONArray(response);
                                for (int i=0;i<array.length();i++)
                                {
                                    JSONObject obj=array.getJSONObject(i);
                                    String message = obj.getString("chat_id");
                                    Log.e("chat id =", message);
                                    Log.e("currnet user ID", "" + Login.user.id);
                                    Log.e("chat id =", "" + message.contains("" + Login.user.id));

                                    //split up chat_id by : demlimiter and compare sides
                                    String[] split_up = message.split(":");

                                    Log.e("chat user ID 1", "" + split_up[0]);
                                    Log.e("chat user ID 2", "" + split_up[1]);
                                    Log.e("chat user ID 1 compare", "" + (Integer.parseInt(split_up[0]) == Login.user.id));
                                    //if chat user ID 1 == login, add the other number, else, add the other number if its in the array
                                    if ((Integer.parseInt(split_up[0]) == Login.user.id))
                                    {
                                        System.out.println("found on first part");
                                        message= "" + split_up[1];
                                        messages.add(message);
                                    }else if (message.contains("" + Login.user.id))
                                    {
                                        System.out.println("replace");
                                        message= "" + split_up[0];
                                        messages.add(message);
                                    }
                                }
                                Log.e("mess Frag =", messages + "");
                                HashMap new_mess = new HashMap();
                                for(int i=0; i < messages.size();i++){
                                    new_mess.put(messages.get(i).toString().trim(),i);
                                }
                                Log.e("mess Frag2 =", new_mess + "");
                                String temp_str = new_mess.keySet().toString().substring(1,new_mess.keySet().toString().length()-1);
                                String[] temp_array = temp_str.split(",");
                                messages = new ArrayList<>();
                                for(int i = 0;i< temp_array.length; i++){
                                    messages.add(temp_array[i]);
                                }
                                Log.e("mess Frag3 =", new_mess.keySet().toString() + "");
                                ChatsAdapter adapter=new ChatsAdapter(getContext(),messages);
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
        );
        Volley.newRequestQueue(getContext()).add(postRequest);
        return v;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}