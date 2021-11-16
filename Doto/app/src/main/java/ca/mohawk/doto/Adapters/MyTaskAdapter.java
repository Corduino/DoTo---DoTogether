package ca.mohawk.doto.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import ca.mohawk.doto.Helpers.Config;
import ca.mohawk.doto.Login;
import ca.mohawk.doto.Objects.Task;
import ca.mohawk.doto.R;
import ca.mohawk.doto.ui.todo.EditTodo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MyTaskAdapter extends RecyclerView.Adapter<MyTaskAdapter.ViewHolder> implements Filterable {
    ArrayList<Task> tasks,all ;
    Context context;
    boolean bookmark;

    // Constructor of the class
    public MyTaskAdapter(Context context, ArrayList<Task> tasks,boolean bookmark) {
        this.context = context;
        this.tasks = tasks;
        this.bookmark=bookmark;
        all=new ArrayList<>();
        all.addAll(tasks);
    }

    // get the size of the list
    @Override
    public int getItemCount() {
        return tasks.size();
    }


    // specify the row layout file and click for each row
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_todo_list_your, parent, false);
        ViewHolder myViewHolder = new ViewHolder(view);
        return myViewHolder;
    }

    // load data in each row element
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
       try {
           
           holder.title.setText(tasks.get(position).title);
           holder.time.setText(tasks.get(position).time);
           holder.location.setText(tasks.get(position).location);
           holder.other.setText(tasks.get(position).other);

           if (bookmark)
           {
               holder.edit.setVisibility(View.INVISIBLE);
               holder.delete.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {

                       String url = Config.url + "/remove_bookmark.php";
                       StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                               new Response.Listener<String>() {
                                   @Override
                                   public void onResponse(String response) {

                                       tasks.remove(position);
                                       notifyDataSetChanged();
                                       Toast.makeText(context, response, Toast.LENGTH_SHORT).show();

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
                           protected Map<String, String> getParams() {
                               Map<String, String> params = new HashMap<>();
                               // the POST parameters:
                               params.put("id", tasks.get(position).id+"");
                               params.put("email", Login.user.email);


                               return params;
                           }
                       };
                       Volley.newRequestQueue(context).add(postRequest);
                   }
               });
           }else
           {
               holder.edit.setVisibility(View.VISIBLE);
               holder.edit.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {

                       EditTodo.task=tasks.get(position);
                       Intent intent=new Intent(context, EditTodo.class);
                       context.startActivity(intent);
                   }
               });

               holder.delete.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {

                       String url = Config.url + "/delete_task.php";
                       StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                               new Response.Listener<String>() {
                                   @Override
                                   public void onResponse(String response) {
                                       tasks.remove(position);
                                       notifyDataSetChanged();
                                       Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
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
                           protected Map<String, String> getParams() {
                               Map<String, String> params = new HashMap<>();
                               // the POST parameters:
                               params.put("id", tasks.get(position).id+"");
                               return params;
                           }
                       };
                       Volley.newRequestQueue(context).add(postRequest);
                   }
               });
           }
       }catch (Exception e)
       {

       }
    }

    // Static inner class to initialize the views of rows
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, time,location,other;
        ImageButton edit,delete;
        ConstraintLayout cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            title =itemView.findViewById(R.id.tv_task_title);
            time =itemView.findViewById(R.id.tv_time_details);
            location =itemView.findViewById(R.id.tv_location_details);
            other =itemView.findViewById(R.id.tv_other_details);
            edit =itemView.findViewById(R.id.btn_bookmark);
            delete =itemView.findViewById(R.id.btn_delete);
            cardView=itemView.findViewById(R.id.item);

        }
    }

    @Override
    public Filter getFilter() {
        return filter;
    }
    private Filter filter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Task> filterdcontacts=new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filterdcontacts.addAll(all);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Task item : all) {
                    if (item.title.toLowerCase().contains(filterPattern)) {
                        filterdcontacts.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filterdcontacts;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            tasks.clear();
            tasks.addAll((ArrayList<Task>) results.values);
            notifyDataSetChanged();
        }
    };
}
