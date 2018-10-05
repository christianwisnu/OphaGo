package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.project.ophago.R;

import java.util.List;

import model.list.ListTransaksi;

/**
 * Created by Chris on 12/04/2018.
 */

public class AdpTransaksi extends ArrayAdapter<ListTransaksi> {

    private List<ListTransaksi> columnslist;
    private LayoutInflater vi;
    private int Resource;
    private ViewHolder holder;
    private Context context;

    public AdpTransaksi(Context context, int resource, List<ListTransaksi> objects) {
        super(context, resource,  objects);
        this.context = context;
        vi	=	(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Resource		= resource;
        columnslist		= objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v	=	convertView;
        if (v == null){
            holder	=	new ViewHolder();
            v= vi.inflate(Resource, null);
            holder.TvNotrans    = 	 (TextView)v.findViewById(R.id.TvColListTransNomor);
            holder.TvNama	    =	 (TextView)v.findViewById(R.id.TvColListTransNama);
            v.setTag(holder);
        }else{
            holder 	= (ViewHolder)v.getTag();
        }
        holder.TvNama.setText(columnslist.get(position).getNamaPasien());
        holder.TvNotrans.setText(String.valueOf(columnslist.get(position).getNomorUrut()));
        return v;
    }

    static class ViewHolder{
        private TextView TvNotrans;
        private TextView TvNama;
    }
}
