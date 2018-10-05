package adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.project.ophago.AddDataPasien2;
import com.example.project.ophago.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    private DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");

    public AdpTransaksi(Context context, int resource, List<ListTransaksi> objects) {
        super(context, resource,  objects);
        this.context = context;
        vi	=	(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Resource		= resource;
        columnslist		= objects;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v	=	convertView;
        if (v == null){
            holder	=	new ViewHolder();
            v= vi.inflate(Resource, null);
            holder.TvNotrans    = 	 (TextView)v.findViewById(R.id.TvColListTransNomor);
            holder.TvNama	    =	 (TextView)v.findViewById(R.id.TvColListTransNama);
            holder.addTrans	    =	 (ImageButton) v.findViewById(R.id.btnColTransAdd);
            v.setTag(holder);
        }else{
            holder 	= (ViewHolder)v.getTag();
        }
        holder.TvNama.setText(columnslist.get(position).getNamaPasien());
        holder.TvNotrans.setText(String.valueOf(columnslist.get(position).getNomorUrut()));

        holder.addTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Intent i = new Intent(getContext(), AddDataPasien2.class);
                    //a.putExtra("status","NEW");
                    i.putExtra("kode",columnslist.get(position).getKodePasien());
                    i.putExtra("nama", columnslist.get(position).getNamaPasien());
                    i.putExtra("alamat", columnslist.get(position).getAlamat());
                    i.putExtra("gender", columnslist.get(position).getGender());
                    i.putExtra("telp", columnslist.get(position).getTelp());
                    Date date1=new SimpleDateFormat("dd-MM-yyy").parse(columnslist.get(position).getBirthday());
                    i.putExtra("tgl", df2.format(date1));
                    getContext().startActivity(i);
                }catch (Exception e){}
            }
        });

        return v;
    }

    static class ViewHolder{
        private TextView TvNotrans;
        private TextView TvNama;
        private ImageButton addTrans;
    }
}
