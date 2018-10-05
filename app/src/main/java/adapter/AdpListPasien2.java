package adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.project.ophago.R;

import java.util.ArrayList;

import model.list.ListPasien;

import static android.app.Activity.RESULT_OK;

/**
 * Created by christian on 03/05/18.
 */

public class AdpListPasien2 extends RecyclerView.Adapter<AdpListPasien2.ViewHolder> implements Filterable {
    private Context context;
    private ArrayList<ListPasien> mArrayList;
    private ArrayList<ListPasien> mFilteredList;

    public AdpListPasien2(Context contextku, ArrayList<ListPasien> arrayList) {
        context = contextku;
        mArrayList = arrayList;
        mFilteredList = arrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_listpasien2, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.tv_kode.setText(mFilteredList.get(i).getUserId());
        viewHolder.tv_nama.setText(mFilteredList.get(i).getNamaPasien());
        viewHolder.tv_alamat.setText(mFilteredList.get(i).getAlamat());
        viewHolder.tv_gender.setText(mFilteredList.get(i).getGender());
        viewHolder.tv_telp.setText(mFilteredList.get(i).getNoHp());
        viewHolder.tv_birthday.setText(mFilteredList.get(i).getTglLahir());
    }

    @Override
    public int getItemCount() {
        return mFilteredList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mFilteredList = mArrayList;
                } else {
                    ArrayList<ListPasien> filteredList = new ArrayList<>();
                    for (ListPasien entity : mArrayList) {
                        if (entity.getUserId().toLowerCase().contains(charString) ||
                                entity.getNamaPasien().toLowerCase().contains(charString) ) {
                            filteredList.add(entity);
                        }
                    }
                    mFilteredList = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredList = (ArrayList<ListPasien>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView tv_kode,tv_nama, tv_alamat, tv_gender, tv_telp, tv_birthday;

        public ViewHolder(View view) {
            super(view);
            tv_kode = (TextView)view.findViewById(R.id.txt_view_pasien_kode2);
            tv_nama = (TextView)view.findViewById(R.id.txt_view_pasien_nama2);
            tv_alamat = (TextView)view.findViewById(R.id.txt_view_pasien_alamat2);
            tv_gender = (TextView)view.findViewById(R.id.txt_view_pasien_gender2);
            tv_telp = (TextView)view.findViewById(R.id.txt_view_pasien_telp2);
            tv_birthday = (TextView)view.findViewById(R.id.txt_view_pasien_tgl2);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent();
            intent.putExtra("kode", tv_kode.getText().toString());
            intent.putExtra("nama", tv_nama.getText().toString());
            intent.putExtra("alamat", tv_alamat.getText().toString());
            intent.putExtra("gender", tv_gender.getText().toString());
            intent.putExtra("telp", tv_telp.getText().toString());
            intent.putExtra("tgl", tv_birthday.getText().toString());
            ((Activity)context).setResult(RESULT_OK, intent);
            ((Activity)context).finish();
        }
    }
}
