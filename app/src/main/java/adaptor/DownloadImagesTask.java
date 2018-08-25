package adaptor;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.root.skor.R;

import java.io.InputStream;
import java.net.URL;

import activity.rewardz.RewardzDetailActivity;


public class DownloadImagesTask extends AsyncTask<String, String, Bitmap> {
    Bitmap bitmap;
    ProgressDialog pDialog;
    Context mContext;
    AlertDialog alertDialog1;

    public DownloadImagesTask(RewardzDetailActivity rewardzDetailActivity) {
        this.mContext = rewardzDetailActivity;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(mContext);
        pDialog.setMessage("Loading Image ....");
        pDialog.show();

    }

    protected Bitmap doInBackground(String... args) {
        try {
            bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    protected void onPostExecute(Bitmap image) {

        if (image != null) {
            pDialog.dismiss();
            setImage(image);
            getMimeType(image.toString());

        } else {

            pDialog.dismiss();
            Toast.makeText(mContext, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();

        }

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
       public void setImage(Bitmap image) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View view = layoutInflater.inflate(R.layout.downloaded_imageview_rewardz, null);
        Button submit = (Button) view.findViewById(R.id.submit);
        ImageView imageView = (ImageView) view.findViewById(R.id.crossimage1);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog1.dismiss();
            }
        });
        ImageView downloadedImage = (ImageView) view.findViewById(R.id.electricity_image);
        builder.setView(view);
        downloadedImage.setImageBitmap(image);
        alertDialog1 = builder.create();
        alertDialog1.show();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog1.dismiss();
            }
        });
    }
}