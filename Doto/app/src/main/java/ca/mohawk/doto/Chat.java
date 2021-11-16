package ca.mohawk.doto;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
//https://developer.android.com/training/volley
import ca.mohawk.doto.Adapters.MessageAdapter;
import ca.mohawk.doto.Helpers.Config;
import ca.mohawk.doto.Objects.Message;
import ca.mohawk.doto.Objects.User;
import com.bumptech.glide.Glide;
//https://github.com/bumptech/glide
import com.google.gson.Gson;
//https://github.com/google/gson
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
//https://github.com/hdodenhof/CircleImageView



//This is called once you open a chat with another user
public class Chat extends AppCompatActivity {
    public static String email;
    public static User user;
    public static int id;

    EditText msgbox;
    RecyclerView recyclerView;
    ArrayList<Message> messages;
    CircleImageView img;
    Button refesh;
    TextView name;
    MessageAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        msgbox=findViewById(R.id.edt_message);
        recyclerView=findViewById(R.id.RV);
        img=findViewById(R.id.img_profile);
        name=findViewById(R.id.tv_user_name);
        refesh=findViewById(R.id.refresh_button);

        name.setText(user.name);
        id = user.id;
        Glide
                .with(Chat.this)
                .load(Config.url+"images/"+user.pic)
                .into(img);


        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                ViewProfile.email=user.email;
                Intent intent=new Intent(getApplicationContext(),ViewProfile.class);
                startActivity(intent);
            }
        });
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                ViewProfile.email=user.email;
                Intent intent=new Intent(getApplicationContext(),ViewProfile.class);
                startActivity(intent);
            }
        });
        refesh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMessages();
            }
        });

        getMessages();
    }
    public void getMessages() {
        Log.e("messesges",id + " " + user.id + " " + Login.user.id);
        String chat_id=user.id +":"+Login.user.id;
        recyclerView=findViewById(R.id.RV);
        recyclerView.setLayoutManager(new LinearLayoutManager(Chat.this,LinearLayoutManager.VERTICAL,false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        Log.e("messesges","Before url");
        String url = Config.url + "get_messages.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Received response",response);
                        try {
                            if (response.startsWith("error")) {
                                // Toast.makeText(Chat.this, "No Data Found", Toast.LENGTH_SHORT).show();
                            } else {
                                messages=new ArrayList<>();

                                Gson gson = new Gson();
                                JSONArray array = new JSONArray(response);

                                for (int i=0;i<array.length();i++)
                                {
                                    JSONObject obj=array.getJSONObject(i);
                                    Message message = gson.fromJson(obj.toString(), Message.class);
                                    messages.add(message);
                                }

                                Collections.sort(messages, new Comparator<Message>() {
                                    @Override
                                    public int compare(Message o1, Message o2) {

                                        try {
                                            int a= (int) (Long.parseLong(o1.time)-Long.parseLong(o2.time));
                                            return a;
                                        }catch (Exception e)
                                        {
                                            return -1;
                                        }
                                    }
                                });


                                if (adapter==null)
                                {
                                    adapter=new MessageAdapter(Chat.this,messages);
                                    recyclerView.setAdapter(adapter);
                                }else
                                {
                                    adapter.setMessages(messages);
                                    adapter.notifyDataSetChanged();
                                }

                                if (messages.size()>0)
                                {
                                    recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount() - 1);

                                }
                            }
                        } catch (Exception e) {
                            Toast.makeText(Chat.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<>();
                Log.e("chats id",chat_id + " " + id + " " + Login.user.id);
                // the POST parameters:
                params.put("chat_id1", id +":"+Login.user.id);
                params.put("chat_id2", Login.user.id + ":"+id);
                return params;
            }
        };

        Volley.newRequestQueue(Chat.this).add(postRequest);

    }

    public void on_backPress(View view) {
        finish();
    }
    public void on_refesh(View view) {
        getMessages();
    }

    public void sendMessage(View view) {

        if (!msgbox.getText().toString().isEmpty())
        {
            String msg=msgbox.getText().toString();
            msgbox.setText("");
            String url = Config.url+ "/send_message.php";
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(Chat.this, error.getMessage(), Toast.LENGTH_LONG).show();

                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams()
                {
                    Map<String, String> params = new HashMap<>();

                    // the POST parameters:
                    params.put("sender","" + Login.user.id);
                    params.put("receiver","" +id);
                    params.put("msg",msg);
                    params.put("time",System.currentTimeMillis()+"");
                    params.put("chat_id",(id +":"+Login.user.id));
                    Log.e("chat_param: ",params.get("chat_id"));
                    return params;
                }
            };

            Volley.newRequestQueue(Chat.this).add(postRequest);


        }
        on_refesh(view);
    }
}