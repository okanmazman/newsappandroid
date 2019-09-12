package com.example.okan_mazmanoglu_hw1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class PostCommentActivity extends AppCompatActivity {
String newsId;
EditText txtCommenter;
EditText txtCommentBody;
Button btnPost;
ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_comment);
        newsId= getIntent().getStringExtra("newsId");

        txtCommentBody=findViewById(R.id.txtCommentMessage);
        txtCommenter=findViewById(R.id.txtCommenter);
        btnPost=findViewById(R.id.btnPostComment);

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewsCommentsGetById ncbid=new NewsCommentsGetById();
                ncbid.execute("http://94.138.207.51:8080/NewsApp/service/news/savecomment");

            }
        });

    }

    class NewsCommentsGetById extends AsyncTask<String,Void,String> {
        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            pDialog = new ProgressDialog(PostCommentActivity.this);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setTitle("Comments Loading...");
            pDialog.setMessage("Please wait ...");
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                String urlstr = strings[0] ;

                URL url = new URL(urlstr);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type","application/json");
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);

                conn.connect();


                JSONObject jsonParam = new JSONObject();
                try {
                    jsonParam.put("name", txtCommenter.getText());
                    jsonParam.put("text", txtCommentBody.getText());
                    jsonParam.put("news_id", newsId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
               OutputStream printout=conn.getOutputStream();
                printout.write(jsonParam.toString().getBytes());
                printout.flush();
                printout.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()
                ));
                StringBuilder strBuilder = new StringBuilder();

                String line = "";

                while ((line = reader.readLine()) != null) {

                    strBuilder.append(line);
                }
                Log.i( "DEV1",strBuilder.toString());
                return strBuilder.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            pDialog.dismiss();
            String message;
            int code=-1;
            try {
                JSONObject jobj=new JSONObject(s);
                message=jobj.getString("serviceMessageText");
                code=jobj.getInt("serviceMessageCode");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(code==0)
            {
                Toast toast = Toast.makeText(PostCommentActivity.this, "Error occured!Try again!", Toast.LENGTH_SHORT);
                toast.show();
            }
            else{
                Intent i = new Intent(PostCommentActivity.this,CommentActivity.class);
                startActivity(i);
            }


        }
    }

    ActionMode actionMode;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflator = getMenuInflater();
        inflator.inflate(R.menu.commentmenu,menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
                Intent i=new Intent(PostCommentActivity.this,MainActivity.class);
                startActivity(i);
        return true;

    }
}
