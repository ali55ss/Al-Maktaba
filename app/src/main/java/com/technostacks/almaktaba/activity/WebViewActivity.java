package com.technostacks.almaktaba.activity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PdfPrint;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.technostacks.almaktaba.R;
import com.technostacks.almaktaba.util.Const;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WebViewActivity extends AppCompatActivity implements WebView.PictureListener {

    public static final String TAG = WebViewActivity.class.getSimpleName();
    @BindView(R.id.pdfview)
    PDFView webView;
    String url;
    Bitmap bmp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        ButterKnife.bind(this);

        if (getIntent().getExtras()!=null)
            url = getIntent().getExtras().getString(Const.DOC_URL);

        Log.e(TAG,"webview url = "+url);
        /*webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                *//*Log.e(TAG, url);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, "time up");
                        createWebPrintJob(webView);
                    }
                },10000);*//*

            }
        });
      //  webView.loadUrl("https://drive.google.com/file/d/0Bw8blm2s4VSmaEdBTTRtRVZjS3c/view?usp=sharing");
        webView.setPictureListener(this);
//        WebView webView1 = new WebView(this);
//        webView1.loadUrl(url);
        webView.loadUrl(url);*/
        webView.fromUri(Uri.parse(url))
                .enableSwipe(true)
                .enableDoubletap(true)
                .enableAnnotationRendering(false)
                .load();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onNewPicture(WebView view, Picture picture) {

        if (picture != null) {
            try {

                bmp = pictureDrawable2Bitmap(new PictureDrawable(
                         picture));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    private static Bitmap pictureDrawable2Bitmap(PictureDrawable pictureDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(
                pictureDrawable.getIntrinsicWidth(),
                pictureDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawPicture(pictureDrawable.getPicture());
        return bitmap;
    }

    public class myWebviewClient extends WebViewClient{
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
            Log.i("OnPageLoadFinished", url);

//            createWebPrintJob(webView);
        }
    }

    /*public void SimplePDFTable() throws Exception {

        File direct = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/AamirPDF");
        if (!direct.exists()) {
            if (direct.mkdir()) {
                Toast.makeText(this,
                        "Folder Is created in sd card", Toast.LENGTH_SHORT)
                        .show();
            }
        }
        String test = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/AamirPDF";
        Document document = new Document();

        PdfWriter.getInstance(document, new FileOutputStream(test
                + "/mypdf.pdf"));

        document.open();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        Image image = Image.getInstance(byteArray);


        image.scaleToFit(PageSize.A4.getHeight(), PageSize.A4.getWidth());
        document.add(image);
        document.close();

    }*/

    private void createWebPrintJob(WebView webView) {
        String jobName = getString(R.string.app_name) + " Document";
        PrintAttributes attributes = new PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                .setResolution(new PrintAttributes.Resolution("pdf", "pdf", 600, 600))
                .setMinMargins(PrintAttributes.Margins.NO_MARGINS).build();
        File path = new File(Const.TEMP_FOLDER_PATH+"/"+Const.DIRECTORY_NAME);
        PdfPrint pdfPrint = new PdfPrint(attributes);
        pdfPrint.print(webView.createPrintDocumentAdapter(jobName), path, "output_" + System.currentTimeMillis() + ".pdf");
    }


    /*PrintDocumentAdapter.WriteResultCallback callback = new  PrintDocumentAdapter.WriteResultCallback() {
        @Override
        public void onWriteFinished(PageRange[] pages) {
            super.onWriteFinished(pages);
        }

        @Override
        public void onWriteFailed(CharSequence error) {
            super.onWriteFailed(error);
        }

        @Override
        public void onWriteCancelled() {
            super.onWriteCancelled();
        }
    }*/
}
