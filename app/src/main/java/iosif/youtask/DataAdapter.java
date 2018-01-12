package iosif.youtask;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class DataAdapter extends ArrayAdapter<ArrayList> {

    private LayoutInflater inflater;
    private int layout;
    private ArrayList<String> list;

       public DataAdapter(Context context, int resource, ArrayList list) {
        super(context, resource, list);
        this.list = list;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        //объект для оптимизации создания новых view
        ViewHolder holder;
        if (convertView==null){
            //если convertView пустой, создаем новое view
            convertView = inflater.inflate(layout, parent, false);
            holder = new ViewHolder(convertView);
            //добавляем в тег созданный объект
            convertView.setTag(holder);
        }
        else {
            //если convertView не пустой, тогда используем view снова
            holder = (ViewHolder)convertView.getTag();
        }
        //задаем imageView, tvData
        holder.imageView.setImageResource(R.drawable.ic_assignment_white_48dp);
        holder.tvData.setText(position+1 + ". "+ list.get(position));

        return convertView;
    }

    class ViewHolder{

        TextView tvData;
        ImageView imageView;
        ViewHolder(View convertView){
                        imageView = (ImageView) convertView.findViewById(R.id.image);
            tvData = (TextView) convertView.findViewById(R.id.tvData);
        }


    }
}
