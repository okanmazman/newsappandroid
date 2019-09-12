package com.example.okan_mazmanoglu_hw1;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CommentAdapter extends ArrayAdapter<CommentItem>
{
    public CommentAdapter(Context context, List<CommentItem> objects)
    {
        super(context,android.R.layout.simple_list_item_1, objects);
    }


    @Override
    public View getView(int position,View convertView, ViewGroup parent) {

        View row=convertView;
        ViewHolder holder=null;

        if(row==null)
        {
            row=((Activity)getContext()).getLayoutInflater().inflate(R.layout.comment_row_layout,parent,false);
            holder=new ViewHolder(row);
            row.setTag(holder);

        }else
        {
            holder=(ViewHolder)row.getTag();
        }



        holder.getCommentPerson().setText(getItem(position).getName());
        holder.getCOmmentBody().setText(getItem(position).getMessage());


        return row;
    }


    class ViewHolder
    {

        TextView txtCommentPerson;
        TextView txtCommentBody;
        View base; //tüm layout

        public ViewHolder(View base){this.base=base;}



        public TextView getCommentPerson() {
            if(txtCommentPerson==null){
                txtCommentPerson=base.findViewById(R.id.txtCommentPerson);
            }
            return txtCommentPerson;
        }

        public TextView getCOmmentBody() {
            if(txtCommentBody==null){
                txtCommentBody=base.findViewById(R.id.txtCommentBody);
            }
            return txtCommentBody;
        }



    }


}
