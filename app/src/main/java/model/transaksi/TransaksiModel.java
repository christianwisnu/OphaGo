package model.transaksi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by christian on 13/04/18.
 */

public class TransaksiModel implements Serializable {

    private TransaksiHeaderModel header;
    private List<TransaksiItemModel> itemList;

    public TransaksiModel(){
        this.itemList = new ArrayList<TransaksiItemModel>();
    }

    public TransaksiHeaderModel getHeader() {
        return header;
    }

    public void setHeader(TransaksiHeaderModel header) {
        this.header = header;
    }

    public List<TransaksiItemModel> getItemList() {
        return itemList;
    }

    public void setItemList(List<TransaksiItemModel> itemList) {
        this.itemList = itemList;
    }

    public void addItem(TransaksiItemModel itemModel){
        itemList.add(itemModel);
    }

    public void removeItem(String anatomi){
        Iterator<TransaksiItemModel> itemIterator = itemList.iterator();
        while(itemIterator.hasNext()){
            TransaksiItemModel item = itemIterator.next();
            if(item.getAnatomi().equals(anatomi)){
                itemIterator.remove();
            }
        }
    }

    public void removeAllItem(){
        Iterator<TransaksiItemModel> itemIterator = itemList.iterator();
        while(itemIterator.hasNext()){
            itemIterator.remove();
        }
    }
}
