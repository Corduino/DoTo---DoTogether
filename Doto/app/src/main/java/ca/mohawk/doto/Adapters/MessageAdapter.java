package ca.mohawk.doto.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import ca.mohawk.doto.Login;
import ca.mohawk.doto.Objects.Message;
import ca.mohawk.doto.R;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Date;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    //All methods in this adapter are required for a bare minimum recyclerview adapter
    ArrayList<Message> messages;
    Context context;
    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }
    // Constructor of the class
    public MessageAdapter(Context context, ArrayList<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    // get the size of the list
    @Override
    public int getItemCount() {
        return messages.size();
    }


    // specify the row layout file and click for each row
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.e("Messege Adapter Is attached: ","true");
        View view = LayoutInflater.from(context).inflate(R.layout.item_message_send_receive, parent, false);
        ViewHolder myViewHolder = new ViewHolder(view);
        return myViewHolder;
    }

    // load data in each row element
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
       try {

           long time = Long.parseLong(messages.get(position).time);
           PrettyTime p = new PrettyTime();
           int incoming = Integer.parseInt(messages.get(position).sender);
           Log.e("prior compare", "" + (incoming == Login.user.id));
           if (incoming == Login.user.id)
           {
               holder.msgsend.setText(messages.get(position).msg);
               Date datetime = new Date(time);
               Log.e("Date outcome: ", "" + datetime);
               holder.timesend.setText(p.format(datetime));
               holder.send.setVisibility(View.VISIBLE);
               holder.receive.setVisibility(View.INVISIBLE);

           }else
           {
               Date datetime = new Date(time);
               holder.msdgrece.setText(messages.get(position).msg);
               holder.timereceive.setText(p.format(datetime));
               holder.send.setVisibility(View.INVISIBLE);
               holder.receive.setVisibility(View.VISIBLE);

           }


       }catch (Exception e)
       {
           Log.e("Instant error: ", "" +e);
       }
    }


    // Static class to make the views of rows
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView msgsend, msdgrece,timesend,timereceive;
        CardView send,receive;

        public ViewHolder(View itemView) {
            super(itemView);
            msgsend =itemView.findViewById(R.id.msg_send);
            msdgrece =itemView.findViewById(R.id.msg_receive);
            timesend =itemView.findViewById(R.id.tv_send_time);
            timereceive =itemView.findViewById(R.id.tv_receive_time);
            send =itemView.findViewById(R.id.sendLay);
            receive=itemView.findViewById(R.id.receiveLay);

        }
    }

}
