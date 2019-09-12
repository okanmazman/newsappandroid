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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

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

public class MainActivity extends AppCompatActivity {
    ListView lstviewNews;
    Spinner cmbCatgories;
    ProgressDialog prpDialog;
    ProgressDialog prpDialogg;
    List<Category> catlist=new ArrayList<Category>();

    List<NewsItem>newslist=new ArrayList<NewsItem>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cmbCatgories=findViewById(R.id.cmbCatgories);

    }
   /* @Override
    public void onRestart()
    {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }*/
    @Override
    protected void onStart() {
        super.onStart();
      CategoriesGetAll catser=new CategoriesGetAll();
      NewsGetAll newsser=new NewsGetAll();

      if(catlist.size()<1){
      catser.execute("http://94.138.207.51:8080/NewsApp/service/news/getallnewscategories");
      newsser.execute("http://94.138.207.51:8080/NewsApp/service/news/getall");
      }


      cmbCatgories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
              int selectedId=catlist.get(position).getId();

              NewsGetByCategoryId ngc=new NewsGetByCategoryId();
              if(selectedId==-1){
                  NewsGetAll newsser=new NewsGetAll();
                  newsser.execute("http://94.138.207.51:8080/NewsApp/service/news/getall");
              }else
              ngc.execute("http://94.138.207.51:8080/NewsApp/service/news/getbycategoryid/",String.valueOf(selectedId));
          }

          @Override
          public void onNothingSelected(AdapterView<?> parent) {
              prpDialog.dismiss();
          }
      });
    }

    //getallcategories

    class CategoriesGetAll extends AsyncTask<String,Void,String>{
        @Override
        protected void onPreExecute() {
            prpDialog=new ProgressDialog(MainActivity.this);
            prpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            prpDialog.setTitle("Loading...");
            prpDialog.setMessage("Please wait mate...");
            prpDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url=new URL(strings[0]);
                HttpURLConnection conn=(HttpURLConnection)url.openConnection();
                BufferedReader reader=new BufferedReader(new InputStreamReader(
                        conn.getInputStream()
                ));
                StringBuilder strBuilder = new StringBuilder();

               String line="";

                while((line= reader.readLine())!=null){

                    strBuilder.append(line);
                }
                Log.i( "DEV1",strBuilder.toString());
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

                catlist.add(new Category(-1,"All"));
               JSONObject jobj=new JSONObject(s);

               JSONArray catsjsonarr=jobj.getJSONArray("items");

                for(int i=0;i<catsjsonarr.length();i++)
                {
                    JSONObject obj =catsjsonarr.getJSONObject(i);
                    Category cat=new Category();
                    cat.setId(obj.getInt("id"));
                    cat.setName(obj.getString("name"));

                    catlist.add(cat);
                    Log.i( "DEV2",obj.get("name").toString());
                }

                ArrayAdapter<Category> spinnerAdapter=
                        new ArrayAdapter<Category>(MainActivity.this,R.layout.support_simple_spinner_dropdown_item,catlist);
                spinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                cmbCatgories.setAdapter(spinnerAdapter);
                prpDialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    class NewsGetAll extends AsyncTask<String,Void,String>
    {
        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            prpDialogg=new ProgressDialog(MainActivity.this);
            prpDialogg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            prpDialogg.setTitle("News Loading...");
            prpDialogg.setMessage("Please wait mate...");
            prpDialogg.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url=new URL(strings[0]);
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
                newslist.removeAll(newslist);
                JSONObject jobj=new JSONObject(s);

                JSONArray newsjsonarr=jobj.getJSONArray("items");

                for(int i=0;i<newsjsonarr.length();i++)
                {
                    JSONObject obj =newsjsonarr.getJSONObject(i);
                    NewsItem news=new NewsItem();

                    news.setId(obj.getInt("id"));
                    news.setTitle(obj.getString("title"));
                    news.setNewsDate(new Date(obj.getInt("date")) );
                    news.setText(obj.getString("text"));
                    news.setImagePath(obj.getString("image"));
                    news.setCategoryName(obj.getString("categoryName"));

                    newslist.add(news);
                }

                NewsAdapter na=new NewsAdapter(MainActivity.this,newslist);

                lstviewNews=findViewById(R.id.lstviewNews);
                lstviewNews.setAdapter(na);
                lstviewNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        NewsItem ni=(NewsItem)lstviewNews.getAdapter().getItem(position);
                        Intent i=new Intent(MainActivity.this,NewsDetail.class);
                        i.putExtra("newsdetail",ni);
                        startActivity(i);
                    }
                });
                prpDialogg.dismiss();
                prpDialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class NewsGetByCategoryId extends AsyncTask<String,Void,String>
    {
        @Override
        protected void onPreExecute() {

            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                String urlstr = strings[0] + strings[1];

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
                newslist.removeAll(newslist);
                JSONObject jobj=new JSONObject(s);

                JSONArray newsjsonarr=jobj.getJSONArray("items");

                for(int i=0;i<newsjsonarr.length();i++)
                {
                    JSONObject obj =newsjsonarr.getJSONObject(i);
                    NewsItem news=new NewsItem();

                    news.setId(obj.getInt("id"));
                    news.setTitle(obj.getString("title"));
                    news.setNewsDate(new Date(obj.getInt("date")) );
                    news.setText(obj.getString("text"));
                    news.setImagePath(obj.getString("image"));
                    news.setCategoryName(obj.getString("categoryName"));

                    newslist.add(news);
                }

                NewsAdapter na=new NewsAdapter(MainActivity.this,newslist);
                lstviewNews=findViewById(R.id.lstviewNews);
                lstviewNews.setAdapter(na);
                lstviewNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        NewsItem ni=(NewsItem)lstviewNews.getAdapter().getItem(position);
                        Intent i=new Intent(MainActivity.this,NewsDetail.class);
                        i.putExtra("newsdetail",ni);
                        startActivity(i);
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    ActionMode actionMode;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflator = getMenuInflater();
        inflator.inflate(R.menu.mainmenu,menu);

        return true;

    }



}
