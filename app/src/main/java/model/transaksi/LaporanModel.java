package model.transaksi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Chris on 28/04/2018.
 */

public class LaporanModel implements Serializable{

    public LaporanModel(){
        this.headerList = new ArrayList<LaporanHeaderModel>();
    }

    private List<LaporanHeaderModel> headerList;

    public List<LaporanHeaderModel> getHeaderList() {
        return headerList;
    }

    public void setHeaderList(List<LaporanHeaderModel> headerList) {
        this.headerList = headerList;
    }

    public void addItem(LaporanHeaderModel itemModel){
        headerList.add(itemModel);
    }

    public void removeItem(String kodePasien){
        Iterator<LaporanHeaderModel> itemIterator = headerList.iterator();
        while(itemIterator.hasNext()){
            LaporanHeaderModel item = itemIterator.next();
            if(item.getNoPasien().equals(kodePasien)){
                itemIterator.remove();
            }
        }
    }
}
