package com.example.okan_mazmanoglu_hw1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;

public class NewsDetail extends AppCompatActivity {
    ImageView imgNewsDet;
    TextView txtNewsDetTitle;
    TextView txtNewsDetBody;
    LinearLayout container;
    ProgressDialog prpDialog2;
    String newsId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        NewsItem newsdetail=(NewsItem) getIntent().getSerializableExtra("newsdetail");

        imgNewsDet=findViewById(R.id.imgNewsDetail);
        txtNewsDetTitle=findViewById(R.id.txtNewsDetailTitle);
        txtNewsDetBody=findViewById(R.id.txtNewsDetailBody);
        newsId=String.valueOf(newsdetail.getId());
        NewsDetailGetById aa=new NewsDetailGetById();
        aa.execute("http://94.138.207.51:8080/NewsApp/service/news/getnewsbyid/",String.valueOf(newsdetail.getId()));
        ImageDownloadDet idd=new ImageDownloadDet();
        idd.execute(newsdetail.getImagePath());
        /*URL url= null;
        Bitmap bitmap;
        try {
            url = new URL(newsdetail.getImagePath());

            InputStream in=new BufferedInputStream(url.openStream());
            bitmap= BitmapFactory.decodeStream(in);
            imgNewsDet.setImageBitmap(bitmap);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/




        /*imgNewsDet.setImageResource(newsdetail.getImageId());
        txtNewsDetTitle.setText(newsdetail.getTitle().toString()+"\n"+newsdetail.getNewsDate().toString());
        txtNewsDetBody.setText(newsdetail.getText().toString());*/

    }

    class ImageDownloadDet extends AsyncTask<String,Void, Bitmap> {
        ImageView imgv;


        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bitmap=null;
            try {
                URL url=new URL(strings[0]);

                InputStream in=new BufferedInputStream(url.openStream());
                bitmap= BitmapFactory.decodeStream(in);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }catch (IOException ioe) {
                ioe.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imgNewsDet.setImageBitmap(bitmap);
        }


    }
    class NewsDetailGetById extends AsyncTask<String,Void,String>
    {
        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            prpDialog2=new ProgressDialog(NewsDetail.this);
            prpDialog2.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            prpDialog2.setTitle("News Detail Loading...");
            prpDialog2.setMessage("Please wait ...");
            prpDialog2.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                String urlstr = strings[0]  + strings[1];

                URL url = new URL(urlstr);

                HttpURLConnection conn=(HttpURLConnection)url.openConnection();
                BufferedReader reader=new BufferedReader(new InputStreamReader(
                        conn.getInputStream()
                ));
                StringBuilder strBuilder = new StringBuilder();

                String line="";

                while((line= reader.readLine())!=null){

                    strBuilder.append(line);
                }
                //Log.i( "DEV1",strBuilder.toString());
                return strBuilder.toString();
            }catch(MalformedURLException e){
                e.printStackTrace();
            }catch(IOException ioe){
                ioe.printStackTrace();
            }
            return  null;
        }

        @Override
        protected void onPostExecute(String s) {
            try {

                JSONObject jobj=new JSONObject(s);

                JSONArray newsjsonarr=jobj.getJSONArray("items");

                for(int i=0;i<newsjsonarr.length();i++)
                {
                    JSONObject obj =newsjsonarr.getJSONObject(i);
                    txtNewsDetTitle.setText(obj.getString("title")+"\n"+new Date(obj.getInt("date")));
                    txtNewsDetBody.setText(obj.getString("text"));
                    //imgNewsDet.setImageResource();
                }


                prpDialog2.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    ActionMode actionMode;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        MenuInflater inflator = getMenuInflater();
        inflator.inflate(R.menu.newsdetailmenu,menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id=item.getItemId();

        switch (id){
            case R.id.detailToHomeMenu:
                Intent i=new Intent(NewsDetail.this,MainActivity.class);
                startActivity(i);
                break;
            case R.id.menuDetail:
                Intent ii=new Intent(NewsDetail.this,CommentActivity.class);
                ii.putExtra("newsId",newsId);
                startActivity(ii);
                break;
        }

        return true;
    }
}
