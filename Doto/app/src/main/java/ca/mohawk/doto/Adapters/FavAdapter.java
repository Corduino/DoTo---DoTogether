package ca.mohawk.doto.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import ca.mohawk.doto.Helpers.Config;
import ca.mohawk.doto.Objects.User;
import ca.mohawk.doto.R;
import ca.mohawk.doto.ViewProfile;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class FavAdapter extends RecyclerView.Adapter<FavAdapter.ViewHolder> {

    //All methods in this adapter are required for a bare minimum recyclerview adapter
    ArrayList<String> favourites ;
    Context context;

    // Constructor of the class
    public FavAdapter(Context context, ArrayList<String> favourites) {
        this.context = context;
        this.favourites = favourites;
    }

    // get the size of the list
    @Override
    public int getItemCount() {
        return favourites.size();
    }


    // specify the row layout file and click for each row
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_messages, parent, false);
        ViewHolder myViewHolder = new ViewHolder(view);
        return myViewHolder;
    }

    // load data in each row element
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
       try {


           String url = Config.url + "/login.php";
           StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                   new Response.Listener<String>() {
                       @Override
                       public void onResponse(String response) {
                           try {
                               Gson gson = new Gson();
                               JSONArray array = new JSONArray(response);
                               User user= gson.fromJson(array.getString(0), User.class);
                               holder.name.setText(user.name);
                               holder.gender.setText(user.gender);

                               Glide
                                       .with(context)
                                       .load(Config.url+"images/"+user.pic)
                                       .centerCrop()
                                       .into(holder.pic);
                               try {
                                   Date date = new SimpleDateFormat("dd/MM/yyyy").parse(user.birthday);
                                   Calendar c = Calendar.getInstance();
                                   c.setTime(date);
                                   int year = c.get(Calendar.YEAR);
                                   int month = c.get(Calendar.MONTH) + 1;
                                   int day = c.get(Calendar.DATE);
                                   holder.gender.append(" ,  " + getAge(year, month, day));

                               } catch (ParseException e) {
                                   e.printStackTrace();
                               }


                               holder.cardView.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View v) {
                                       ViewProfile.email=favourites.get(position);
                                       Intent intent=new Intent(context, ViewProfile.class);
                                       intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                       context.startActivity(intent);
                                   }
                               });

                           } catch (Exception e) {

                               //  e.printStackTrace();
                           }
                       }
                   },
                   new Response.ErrorListener() {
                       @Override
                       public void onErrorResponse(VolleyError error) {

                           // error.printStackTrace();
                           // Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();

                       }
                   }
           ) {
               @Override
               protected Map<String, String> getParams() {
                   Map<String, String> params = new HashMap<>();
                   // the POST parameters:
                   params.put("email", favourites.get(position));
                   params.put("password", "");


                   return params;
               }
           };

           Volley.newRequestQueue(context).add(postRequest);






       }catch (Exception e)
       {

       }
    }


    // Static inner class to initialize the views of rows
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, gender;
        CircleImageView pic;
        ConstraintLayout cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            name =itemView.findViewById(R.id.username_textView);
            gender =itemView.findViewById(R.id.last_message_textView);
            pic =itemView.findViewById(R.id.photo_imageView);
            cardView=itemView.findViewById(R.id.item_message);

        }
    }
    private String getAge(int year, int month, int day) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        return ageS;
    }
}
