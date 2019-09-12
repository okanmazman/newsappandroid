package com.example.okan_mazmanoglu_hw1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommentActivity extends AppCompatActivity {

    ListView lstcomments;
    ProgressDialog prpDialog3;
    List<CommentItem> cilist=new ArrayList<CommentItem>();
    String newsId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        newsId= getIntent().getStringExtra("newsId");

        lstcomments=findViewById(R.id.lstviewcomment);

        NewsCommentsGetById cm=new NewsCommentsGetById();
        cm.execute("http://94.138.207.51:8080/NewsApp/service/news/getcommentsbynewsid/",newsId);

    }
    @Override
    public void onRestart()
    {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    class NewsCommentsGetById extends AsyncTask<String,Void,String> {
        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            prpDialog3 = new ProgressDialog(CommentActivity.this);
            prpDialog3.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            prpDialog3.setTitle("Comments Loading...");
            prpDialog3.setMessage("Please wait ...");
            prpDialog3.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                String urlstr = strings[0] + strings[1];

                URL url = new URL(urlstr);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()
                ));
                StringBuilder strBuilder = new StringBuilder();

                String line = "";

                while ((line = reader.readLine()) != null) {

                    strBuilder.append(line);
                }
                //Log.i( "DEV1",strBuilder.toString());
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
            try {

                JSONObject jobj = new JSONObject(s);

                JSONArray newsjsonarr = jobj.getJSONArray("items");

                for (int i = 0; i < newsjsonarr.length(); i++) {
                    JSONObject obj = newsjsonarr.getJSONObject(i);
                    CommentItem ci = new CommentItem();
                    ci.setId(obj.getInt("id"));
                    ci.setName(obj.getString("name"));
                    ci.setMessage(obj.getString("text"));

                    cilist.add(ci);

                }


                CommentAdapter ca = new CommentAdapter(CommentActivity.this, cilist);
                lstcomments.setAdapter(ca);
                prpDialog3.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
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

        int id=item.getItemId();

        switch (id){
            case R.id.commentToHomeMenu:
                Intent i=new Intent(CommentActivity.this,MainActivity.class);
                Toast.makeText(this,"Home clicked",Toast.LENGTH_SHORT).show();
                startActivity(i);
                break;
            case R.id.commentMenu:
                Intent ii=new Intent(CommentActivity.this,PostCommentActivity.class);
                ii.putExtra("newsId",newsId);
                startActivity(ii);
                break;
                default:
                    break;
        }

        return true;

    }
}
