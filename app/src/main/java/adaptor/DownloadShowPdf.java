package adaptor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.webkit.CookieManager;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class DownloadShowPdf extends AsyncTask<String, String, String> {


    File file;
    String fileUrl;
    Activity activity;
    ProgressDialog dialog = null;
    private final static String FILE_PREFIX = "DH_";
    @Override
    protected String doInBackground(String... strings) {
        fileUrl = strings[0];

        file=downloadFile(fileUrl);

        return null;
    }
    @Override
    protected void onPostExecute(String result) {
        // might want to change "executed" for the returned string passed
        // into onPostExecute() but that is upto you
        super.onPostExecute(result);
        dialog.dismiss();
        String mimeType = getMimeType(fileUrl);

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);

            // Thread-safe.
        } catch (ActivityNotFoundException e) {
            // happens when we start intent without something that can
            // handle it
            e.printStackTrace();

        }

            /*else if(fileName.endsWith(".txt"))
            {

            }*/
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        dialog= ProgressDialog.show(activity, "", "Downloading....");

    }

    public DownloadShowPdf(Activity context)
    {
        this.activity=context;
    }

    private File downloadFile(String url) {

        try {
            // get an instance of a cookie manager since it has access to our
            // auth cookie
            CookieManager cookieManager = CookieManager.getInstance();

            // get the cookie string for the site.
            String auth = null;
            if (cookieManager.getCookie(url) != null) {
                auth = cookieManager.getCookie(url).toString();
            }

            URL url2 = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) url2.openConnection();
            if (auth != null) {
                conn.setRequestProperty("Cookie", auth);
            }

            InputStream reader = conn.getInputStream();

            String extension = MimeTypeMap.getFileExtensionFromUrl(url);
            File f = File.createTempFile(FILE_PREFIX, "." + extension,
                    null);
            // make sure the receiving app can read this file
            f.setReadable(true, false);
            FileOutputStream outStream = new FileOutputStream(f);

            byte[] buffer = new byte[1024];
            int readBytes = reader.read(buffer);
            while (readBytes > 0) {
                outStream.write(buffer, 0, readBytes);
                readBytes = reader.read(buffer);
            }
            reader.close();
            outStream.close();
            return f;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getMimeType(String url) {
        String mimeType = null;

        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            mimeType = mime.getMimeTypeFromExtension(extension);
        }

        System.out.println("Mime Type: " + mimeType);

        return mimeType;
    }

}
