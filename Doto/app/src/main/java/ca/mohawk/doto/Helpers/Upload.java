package ca.mohawk.doto.Helpers;



import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class Upload {

    public static final String UPLOAD_URL= Config.url+"/upload_image.php";

    private int serverResponseCode;

    Context context;

    public Upload(Context context) {
        this.context = context;
    }

    public String compressImage(String filePath) {
  try {
      //    String filePath = getRealPathFromURI(imageUri);
      Bitmap scaledBitmap = null;

      BitmapFactory.Options options = new BitmapFactory.Options();
      //makes it so it takes more than bounds
      options.inJustDecodeBounds = true;
      Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

      int actualHeight = options.outHeight;
      int actualWidth = options.outWidth;

//      max Height and width values

      float maxHeight = 816.0f;
      float maxWidth = 612.0f;
      float imgRatio = actualWidth / actualHeight;
      float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

      if (actualHeight > maxHeight || actualWidth > maxWidth) {
          if (imgRatio < maxRatio) {
              imgRatio = maxHeight / actualHeight;
              actualWidth = (int) (imgRatio * actualWidth);
              actualHeight = (int) maxHeight;
          } else if (imgRatio > maxRatio) {
              imgRatio = maxWidth / actualWidth;
              actualHeight = (int) (imgRatio * actualHeight);
              actualWidth = (int) maxWidth;
          } else {
              actualHeight = (int) maxHeight;
              actualWidth = (int) maxWidth;

          }
      }
      //Gets us a scaled down thumbnail version
      options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

      //if true, can load the orignal
      options.inJustDecodeBounds = false;

      //keep memeory higher or else app gets reaaaly slow
      options.inPurgeable = true;
      options.inInputShareable = true;
      options.inTempStorage = new byte[16 * 1024];

      try {
          //load the bitmap from its path
          bmp = BitmapFactory.decodeFile(filePath, options);
      } catch (OutOfMemoryError exception) {
          exception.printStackTrace();

      }
      try {
          scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
      } catch (OutOfMemoryError exception) {
          exception.printStackTrace();
      }

      float ratioX = actualWidth / (float) options.outWidth;
      float ratioY = actualHeight / (float) options.outHeight;
      float middleX = actualWidth / 2.0f;
      float middleY = actualHeight / 2.0f;

      Matrix scaleMatrix = new Matrix();
      scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

      Canvas canvas = new Canvas(scaledBitmap);
      canvas.setMatrix(scaleMatrix);
      canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

      //Check the rotation of the image, to see if its right side up
      ExifInterface exif;
      try {
          exif = new ExifInterface(filePath);

          int orientation = exif.getAttributeInt(
                  ExifInterface.TAG_ORIENTATION, 0);
          Log.d("EXIF", "Exif: " + orientation);
          Matrix matrix = new Matrix();
          if (orientation == 6) {
              matrix.postRotate(90);
          } else if (orientation == 3) {
              matrix.postRotate(180);
          } else if (orientation == 8) {
              matrix.postRotate(270);
          }
          scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                  scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                  true);
      } catch (IOException e) {
          e.printStackTrace();
      }

      FileOutputStream out = null;
      String filename = getFilename();
      try {
          out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
          scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

      } catch (FileNotFoundException e) {
          e.printStackTrace();
      }

      return filename;
  }catch (Exception e){
      String s=e.getMessage();
      return s;
  }
    }





    public String uploadVideo(String file,String fileName) {

       // String fileName = file;
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1024 * 1024 * 1024;
        file=compressImage(file);
        File sourceFile = new File(file);
        if (!sourceFile.isFile()) {
            Log.e("Huzza", "Source File Does not exist");
           return file;
         //   return null;
        }

        try {
            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            URL url = new URL(UPLOAD_URL);
            JSONObject postDataParams = new JSONObject();
            postDataParams.put("name", "AHSAN");
            postDataParams.put("pas", "ALVI");
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("myFile", fileName);
            dos = new DataOutputStream(conn.getOutputStream());


            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"myFile\";filename=\"" + fileName + "\"" + lineEnd);
            dos.writeBytes(lineEnd);



            bytesAvailable = fileInputStream.available();
            Log.i("Huzza", "Initial .available : " + bytesAvailable);

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            serverResponseCode = conn.getResponseCode();

            fileInputStream.close();
            dos.flush();
            dos.close();
        } catch (MalformedURLException ex) {
            return ex.getMessage();
        } catch (Exception e) {
            e.getMessage();
        }

        if (serverResponseCode == 200) {
            StringBuilder sb = new StringBuilder();
            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn
                        .getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
                rd.close();
            } catch (IOException ioex) {
          return  ioex.getMessage();
            }
            return sb.toString();
        }else {
          //  return "Could not upload"+serverResponseCode;
           return  ""+serverResponseCode;
        }
    }

    public String getFilename() {
        //File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
        // File file=new File();
        File file=new File(context.getExternalFilesDir("")+"MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/test"  + ".jpg");
        return uriSting;

    }
    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height/ (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;      }       final float totalPixels = width * height;       final float totalReqPixelsCap = reqWidth * reqHeight * 2;       while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }
}