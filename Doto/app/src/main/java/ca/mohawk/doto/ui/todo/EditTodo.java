package ca.mohawk.doto.ui.todo;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
//https://developer.android.com/training/volley
import ca.mohawk.doto.Helpers.Config;
import ca.mohawk.doto.Helpers.EditTextValueCheck;
import ca.mohawk.doto.Login;
import ca.mohawk.doto.Objects.Task;
import ca.mohawk.doto.R;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;
//https://github.com/Kunzisoft/Android-SwitchDateTimePicker
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class EditTodo extends AppCompatActivity {
    public static Task task;
EditText title,time,location,other,edt_time_details;
    private SwitchDateTimeDialogFragment dateTimeFragment;
    private static final String TAG_DATETIME_FRAGMENT = "TAG_DATETIME_FRAGMENT";
    Button update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_todo);

        title=findViewById(R.id.task_title_editText);
        time=findViewById(R.id.time_editText);
        location=findViewById(R.id.loc_editText);
        other=findViewById(R.id.extra_details_editText);
        edt_time_details=findViewById(R.id.time_editText);
        update=findViewById(R.id.add_task_button);
        update.setText("Update");
        location.setText(Login.user.location);
        title.setText(task.title);
        time.setText(task.time);
        location.setText(task.location);
        other.setText(task.other);

        dateTimeFragment = (SwitchDateTimeDialogFragment) getSupportFragmentManager().findFragmentByTag(TAG_DATETIME_FRAGMENT);
        if (dateTimeFragment == null) {
            dateTimeFragment = SwitchDateTimeDialogFragment.newInstance(
                    getString(R.string.label_datetime_dialog),
                    getString(android.R.string.ok),
                    getString(android.R.string.cancel)
            );
        }
        dateTimeFragment.setTimeZone(TimeZone.getDefault());
        final SimpleDateFormat myDateFormat = new SimpleDateFormat("d MMM yyyy HH:mm", Locale.getDefault());
        dateTimeFragment.set24HoursMode(false);
        dateTimeFragment.setHighlightAMPMSelection(false);
        dateTimeFragment.setMinimumDateTime(new GregorianCalendar(2015, Calendar.JANUARY, 1).getTime());
        dateTimeFragment.setMaximumDateTime(new GregorianCalendar(2025, Calendar.DECEMBER, 31).getTime());
        try {
            dateTimeFragment.setSimpleDateMonthAndDayFormat(new SimpleDateFormat("MMMM dd", Locale.getDefault()));
        } catch (SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException e) {
            Log.e(TAG, e.getMessage());
        }
        dateTimeFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonWithNeutralClickListener() {
            @Override
            public void onPositiveButtonClick(Date date) {
                edt_time_details.setText(myDateFormat.format(date));
            }
            @Override
            public void onNegativeButtonClick(Date date) {}
            @Override
            public void onNeutralButtonClick(Date date) {
                edt_time_details.setText("");
            }
        });

        edt_time_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateTimeFragment.startAtCalendarView();
                dateTimeFragment.setDefaultDateTime(new GregorianCalendar(2017, Calendar.MARCH, 4, 15, 20).getTime());
                dateTimeFragment.show(getSupportFragmentManager(), TAG_DATETIME_FRAGMENT);
            }
        });
    }

    public void on_backPress(View view) {
        finish();
    }

    public void on_submit(View view) {
        EditTextValueCheck check = new EditTextValueCheck();

        if (check.hasValue(title) && check.hasValue(location)) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();

            String url = Config.url+ "/update_task.php";
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response){
                            try {
                                Toast.makeText(EditTodo.this, ""+response, Toast.LENGTH_SHORT).show();
                                finish();
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
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
                    params.put("title",title.getText().toString());
                    params.put("time",time.getText().toString());
                    params.put("location",location.getText().toString());
                    params.put("other",other.getText().toString());
                    params.put("id",task.id+"");
                    return params;
                }
            };
            Volley.newRequestQueue(getApplicationContext()).add(postRequest);
        }else
        {
            Toast.makeText(EditTodo.this, "Provide all information", Toast.LENGTH_SHORT).show();
        }
    }
}