package com.example.okan_mazmanoglu_hw1;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends ArrayAdapter<NewsItem>
{
    List<NewsItem> nl=new ArrayList<>();;
    Bitmap retBitmap;
    public NewsAdapter(Context context, List<NewsItem> objects)
    {
        super(context,android.R.layout.simple_list_item_1, objects);
        nl=objects;
    }




    @Override
    public View getView(int position,View convertView, ViewGroup parent) {

        View row=convertView;
        ViewHolder holder=null;
        String imagePath;

         //if(row==null)
        //{

            row=((Activity)getContext()).getLayoutInflater().inflate(R.layout.news_row_layout,parent,false);
            holder=new ViewHolder(row,nl.get(position).getImagePath());
            row.setTag(holder);
       // }
       // else
       // {
            //holder=(ViewHolder)row.getTag();
        //}
        holder.getTxtNewsName().setText(getItem(position).getTitle());

        holder.getImgNews();

        holder.getTxtNewsDate().setText(getItem(position).getNewsDate().toString());

        return row;
    }


    class ViewHolder
    {
        ImageView imgNews;
        TextView txtNewsName;
        TextView txtNewsDate;
        String imagepath;
        View base; //tüm layout

        public ViewHolder(View base,String imagePath)
        {
            this.base=base;
            this.imagepath=imagePath;

        }

        public ImageView getImgNews(){


                imgNews=base.findViewById(R.id.imgnews);
                ImageDownloadTask idt=new ImageDownloadTask(imgNews);
                idt.execute(imagepath);

            return imgNews;

        }

        public TextView getTxtNewsName() {
            if(txtNewsName==null){
                txtNewsName=base.findViewById(R.id.txtNewsName);
            }
            return txtNewsName;
        }

        public TextView getTxtNewsDate() {
            if(txtNewsDate==null){
                txtNewsDate=base.findViewById(R.id.txtNewsDate);
            }
            return txtNewsDate;
        }


    }
    class ImageDownloadTask extends AsyncTask<String,Void, Bitmap> {
        ImageView imgv;
        public ImageDownloadTask(ImageView imgv)
        {
            this.imgv=imgv;
        }

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
            this.imgv.setImageBitmap(bitmap);
        }


    }

}
