package adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project.ophago.CameraAppActivity2;
import com.example.project.ophago.R;

import java.util.List;

import model.transaksi.TransaksiItemModel;
import model.transaksi.TransaksiModel;

/**
 * Created by christian on 03/05/18.
 */

public class AdpCheckoutListAnatomi2 extends ArrayAdapter<TransaksiItemModel> {

    private List<TransaksiItemModel> columnslist;
    private LayoutInflater vi;
    private int Resource;
    private Context context;
    private ViewHolder holder;
    private AlertDialog alert;
    private TransaksiModel modelku;

    public AdpCheckoutListAnatomi2(Context context, TransaksiModel model, int resource, List<TransaksiItemModel> objects) {
        super(context, resource,  objects);
        this.context = context;
        modelku = model;
        vi	=	(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Resource		= resource;
        columnslist		= objects;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        View v	=	convertView;
        if (v == null){
            holder	=	new ViewHolder();
            v= vi.inflate(Resource, null);
            holder.ImgDelete	=	 (ImageView)v.findViewById(R.id.imgColCheckoutAnatomiHapus);
            holder.imgScreenshoot	=	 (ImageView)v.findViewById(R.id.imgColCheckoutAnatomiScreenshoot);
            holder.TvNamaAnatomi= 	 (TextView)v.findViewById(R.id.TvColCheckoutAnatomiNama2);
            //holder.TvLine	    =	 (TextView)v.findViewById(R.id.TvColCheckoutAnatomiLine2);
            v.setTag(holder);
        }else{
            holder 	= (ViewHolder)v.getTag();
        }

        holder.ImgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(columnslist.size()==1){
                    Toast.makeText(getContext(), "Data tidak dapat dihapus jika hanya ada 1 data!", Toast.LENGTH_LONG).show();
                }else{
                    AlertDialog.Builder msMaintance = new AlertDialog.Builder(getContext());
                    msMaintance.setCancelable(false);
                    msMaintance.setMessage("Yakin akan dihapus? ");
                    msMaintance.setNegativeButton("Ya", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            columnslist.get(position).setStat("D");
                            columnslist.remove(position);
                            notifyDataSetChanged();
                        }
                    });

                    msMaintance.setPositiveButton("Tidak", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alert.dismiss();
                        }
                    });
                    alert	=msMaintance.create();
                    alert.show();
                }
            }
        });

        holder.imgScreenshoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasCamera())
                    Toast.makeText(getContext(), "Device anda belum mempunyai perangkat kamera", Toast.LENGTH_LONG).show();
                else{
                    Intent i = new Intent(getContext(), CameraAppActivity2.class);
                    i.putExtra("line", columnslist.get(position).getLine());
                    i.putExtra("video", columnslist.get(position).getPathVideo()==null?"NONE":columnslist.get(position).getPathVideo());
                    i.putExtra("object", modelku);
                    ((Activity) context).startActivityForResult(i,1);
                }
            }
        });

        holder.TvNamaAnatomi.setText(String.valueOf(position+1)+". "+columnslist.get(position).getAnatomi());
        return v;
    }

    static class ViewHolder{
        private ImageView ImgDelete;
        private ImageView imgScreenshoot;
        private TextView TvNamaAnatomi;
    }

    private boolean hasCamera() {
        if (((Activity) context).getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_ANY)){
            return true;
        } else {
            return false;
        }
    }


}
