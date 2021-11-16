package ca.mohawk.doto;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
//https://developer.android.com/training/volley
import ca.mohawk.doto.Helpers.Config;
import ca.mohawk.doto.Helpers.EditTextValueCheck;
import ca.mohawk.doto.Helpers.FilePath;
import ca.mohawk.doto.Helpers.Upload;
import ca.mohawk.doto.Objects.User;
import com.bumptech.glide.Glide;
//https://github.com/bumptech/glide
import com.jaredrummler.materialspinner.MaterialSpinner;
//https://github.com/jaredrummler/MaterialSpinner

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
//https://github.com/hdodenhof/CircleImageView


//Bascilly the same as Signup but with a different API calls
public class EditProfile extends AppCompatActivity {

    EditText dob_editText, username_editText, email_editText, pass_editText, loc_editTExt, bio_editText;

    CircleImageView profilepic;
    ImageView location_image;
    String gender="",pic="";


    public String path;
    public String fname;
    Button update;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        TextView activity_title = findViewById(R.id.subtitle_textView);
        activity_title.setText("Edit Profile");

        dob_editText = findViewById(R.id.dob_editText);
        username_editText = findViewById(R.id.username_editText);
        email_editText = findViewById(R.id.email_reset_editText);
        pass_editText = findViewById(R.id.password_editText);
        loc_editTExt = findViewById(R.id.location_editText);
        bio_editText = findViewById(R.id.bio_editText);
        update = findViewById(R.id.signup_button);
        update.setText("Update");
        pic=Login.user.pic;

        profilepic=findViewById(R.id.photo_imageView);
        location_image = findViewById(R.id.location_button);

        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryintent = new Intent(Intent.ACTION_GET_CONTENT, null);
                galleryintent.setType("image/*");

                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                Intent chooser = new Intent(Intent.ACTION_CHOOSER);
                chooser.putExtra(Intent.EXTRA_INTENT, galleryintent);
                chooser.putExtra(Intent.EXTRA_TITLE, "Select from:");

                Intent[] intentArray = { cameraIntent };
                chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                startActivityForResult(chooser, 1);
            }
        });

        Calendar calendar = Calendar.getInstance();
        final  int year = calendar.get(Calendar.YEAR);
        final  int month = calendar.get(Calendar.MONTH);
        final  int day = calendar.get(Calendar.DAY_OF_MONTH);

        dob_editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        EditProfile.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                        month = month+1;
                        String date = day+"/"+month+"/"+year;
                        dob_editText.setText(date);
                    }
                },year,month,day);

                datePickerDialog.show();
            }
        });
        //Same as signup, gender spinner
        MaterialSpinner gender_spinner = (MaterialSpinner)findViewById(R.id.gender_spinner);
        gender_spinner.setItems("Male","Female","Other");
        gender_spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                gender=item;
            }
        });


        if (Login.user.gender.equals("Male"))
            gender_spinner.setSelectedIndex(0);
        else if (Login.user.gender.equals("Female"))
            gender_spinner.setSelectedIndex(1);
        else
            gender_spinner.setSelectedIndex(2);

        username_editText.setText(Login.user.name);
        email_editText.setText(Login.user.email);
        pass_editText.setText(Login.user.password);
        dob_editText.setText(Login.user.birthday);
        loc_editTExt.setText(Login.user.location);
        bio_editText.setText(Login.user.bio);
        //Glide processes Image
        Glide
                .with(EditProfile.this)
                .load(Config.url+"images/"+Login.user.pic)
                .centerCrop()
                .into(profilepic);
        location_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Location loc = get_cords();
                if(loc != null) {
                    Log.e("tag:", "location_gps");
                    //loc.getLongitude();
                    //loc.getLatitude();
                    //https://dev.virtualearth.net/REST/v1/Locations/43,-79.5?o=xml&key=Ag-Xw-WfC1Mj2SKq5_yg0wEMqxACPN9nFGiqHz0tmt1ItkWNHchd3SDq-nrlGgaC  Wa
                    //String bingAPI = "https://dev.virtualearth.net/REST/v1/Locations/" + (43) + "," + (-73.5) + "?o=xml&key=Ag-Xw-WfC1Mj2SKq5_yg0wEMqxACPN9nFGiqHz0tmt1ItkWNHchd3SDq-nrlGgaC";
                    Geocoder geo = new Geocoder(EditProfile.this);
                    List<Address> ad = null;
                    try {
                        ad = geo.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                        Toast.makeText(EditProfile.this, ad.get(0).getLocality(), Toast.LENGTH_SHORT).show();
                        loc_editTExt.setText(ad.get(0).getLocality());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.e("trying geo", "" + ad.get(0).getLocality());
                }
                else{
                    Toast.makeText(EditProfile.this, "Location Not Available", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //GPS call button, when clicking the GPS button will take GEO data convert to city loaction and place in location text view
    private Location get_cords() {

        Location currentBestLocation = null;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return currentBestLocation;
        }
        Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        long GPSLocationTime = 0;
        if (null != locationGPS) { GPSLocationTime = locationGPS.getTime(); }

        long NetLocationTime = 0;

        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }

        if ( 0 < GPSLocationTime - NetLocationTime ) {
            return locationGPS;
        }
        else {
            return locationNet;
        }
    }

    public void on_submit(View view) {
        EditTextValueCheck check = new EditTextValueCheck();

        if (check.hasValue(email_editText) && check.hasValue(pass_editText)) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();

            String url = Config.url+ "/update_profile.php";
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                Toast.makeText(EditProfile.this, ""+response, Toast.LENGTH_SHORT).show();
                                Login.user=new User();
                                Intent intent = new Intent(EditProfile.this, MainMenu.class);
                                startActivity(intent);
                                finish();
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
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
                    params.put("name", username_editText.getText().toString());
                    params.put("email", email_editText.getText().toString());
                    params.put("password", pass_editText.getText().toString());
                    params.put("bd", dob_editText.getText().toString());
                    params.put("gender",gender);
                    params.put("location", loc_editTExt.getText().toString());
                    params.put("bio", bio_editText.getText().toString());
                    params.put("pic",pic);
                    params.put("latitude", "");
                    params.put("longitude","");

                    return params;
                }
            };
            Volley.newRequestQueue(getApplicationContext()).add(postRequest);
        }else
        {
            Toast.makeText(EditProfile.this, "Provide all information", Toast.LENGTH_SHORT).show();
        }
    }

    public void on_backPress(View view) {
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap cBitmap = null;
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            try {
                cBitmap = (Bitmap) extras.get("data");
            }
            catch(Exception e){
                Log.e("file check", "no data - this should be a file");
            }
            if ((data != null) && (data.getData() != null)) {
                Log.e("file","picking file");
                try {
                    Uri imageFileUri = data.getData();
                    Glide
                            .with(EditProfile.this)
                            .load(imageFileUri)
                            .centerCrop()
                            .into(profilepic);
                    Uri file = imageFileUri;

                    path = FilePath.getPath(EditProfile.this, file);
                    fname = path.substring(path.lastIndexOf("/") + 1);

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(EditProfile.this.getContentResolver(), imageFileUri);
                    File f = createImageFile();
                    // File file = new File("path");
                    OutputStream os = new BufferedOutputStream(new FileOutputStream(f));
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                    os.close();
                    fname = f.getName();
                    path = f.getAbsolutePath();
                    profilepic.setImageBitmap(bitmap);
                    uploadimage();

                } catch (Exception e) {
                    Toast.makeText(EditProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }else if (cBitmap != null){
                Log.e("Camera","" +cBitmap);
                try {

                    File f = createImageFile();
                    OutputStream os = new BufferedOutputStream(new FileOutputStream(f));
                    cBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                    os.close();
                    fname = f.getName();
                    path = f.getAbsolutePath();
                    profilepic.setImageBitmap(cBitmap);
                    uploadimage();
                }catch (Exception e) {
                    Toast.makeText(EditProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    private void uploadimage() {
        class UploadVideo extends AsyncTask<Void, Void, String> {
            ProgressDialog uploading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                uploading = ProgressDialog.show(EditProfile.this, "Uploading", "Please wait...", false, false);
            }

            @Override
            protected void onPostExecute(String picture_string) {
                super.onPostExecute(picture_string);
                uploading.dismiss();
                pic=picture_string;
            }

            @Override
            protected String doInBackground(Void... params) {
                Upload u = new Upload(EditProfile.this);
                String msg = "";
                msg = u.uploadVideo(path, fname);
                return msg;
            }
        }
        UploadVideo uv = new UploadVideo();
        uv.execute();
    }

    private File createImageFile() {
        File image = null;
        try {
            String timeStamp =
                    new SimpleDateFormat("yyyyMMdd_HHmmss",
                            Locale.getDefault()).format(new Date());
            String imageFileName = "IMG_" + timeStamp + "_";
            //filename=imageFileName;
            File storageDir =
                    EditProfile.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            image = File.createTempFile(
                    imageFileName, //file name
                    ".jpg",  //file type
                    storageDir  //Where its going
            );

            path = image.getAbsolutePath();
            return image;
        } catch (Exception e) {
            Toast.makeText(EditProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return image;
        }
    }
}