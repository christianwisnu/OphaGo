package model.transaksi;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by christian on 28/04/18.
 */

public class LaporanHeaderModel implements Serializable {

    private String noTrans;
    private String noPasien;
    private String namaPasien;
    private String alamat;
    private String gender;
    private Date tglLahir;
    private String kesimpulan;
    private Integer umur;
    private String telp;
    private String keluhan;
    private String diagnosa;
    private String saran;
    private String tglTrans;
    private String userBy;
    private String tglNow;
    private Integer bulan;
    private Integer tahun;
    private Integer nomor;
    private String kodeTrans;
    private String userId;
    private String pathGbr1;
    private String pathGbr2;
    private String pathGbr3;
    private String pathGbr4;
    private String pathGbr5;
    private String pathGbr6;
    private List<LaporanItemModel> itemList;

    public LaporanHeaderModel(){
        this.itemList = new ArrayList<LaporanItemModel>();
    }

    public void addItem(LaporanItemModel itemModel){
        getItemList().add(itemModel);
    }

    public String getNoTrans() {
        return noTrans;
    }

    public void setNoTrans(String noTrans) {
        this.noTrans = noTrans;
    }

    public String getNoPasien() {
        return noPasien;
    }

    public void setNoPasien(String noPasien) {
        this.noPasien = noPasien;
    }

    public String getNamaPasien() {
        return namaPasien;
    }

    public void setNamaPasien(String namaPasien) {
        this.namaPasien = namaPasien;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getTglLahir() {
        return tglLahir;
    }

    public void setTglLahir(Date tglLahir) {
        this.tglLahir = tglLahir;
    }

    public String getKesimpulan() {
        return kesimpulan;
    }

    public void setKesimpulan(String kesimpulan) {
        this.kesimpulan = kesimpulan;
    }

    public Integer getUmur() {
        return umur;
    }

    public void setUmur(Integer umur) {
        this.umur = umur;
    }

    public String getTelp() {
        return telp;
    }

    public void setTelp(String telp) {
        this.telp = telp;
    }

    public String getKeluhan() {
        return keluhan;
    }

    public void setKeluhan(String keluhan) {
        this.keluhan = keluhan;
    }

    public String getDiagnosa() {
        return diagnosa;
    }

    public void setDiagnosa(String diagnosa) {
        this.diagnosa = diagnosa;
    }

    public String getSaran() {
        return saran;
    }

    public void setSaran(String saran) {
        this.saran = saran;
    }

    public String getTglTrans() {
        return tglTrans;
    }

    public void setTglTrans(String tglTrans) {
        this.tglTrans = tglTrans;
    }

    public String getUserBy() {
        return userBy;
    }

    public void setUserBy(String userBy) {
        this.userBy = userBy;
    }

    public String getTglNow() {
        return tglNow;
    }

    public void setTglNow(String tglNow) {
        this.tglNow = tglNow;
    }

    public Integer getBulan() {
        return bulan;
    }

    public void setBulan(Integer bulan) {
        this.bulan = bulan;
    }

    public Integer getTahun() {
        return tahun;
    }

    public void setTahun(Integer tahun) {
        this.tahun = tahun;
    }

    public Integer getNomor() {
        return nomor;
    }

    public void setNomor(Integer nomor) {
        this.nomor = nomor;
    }

    public String getKodeTrans() {
        return kodeTrans;
    }

    public void setKodeTrans(String kodeTrans) {
        this.kodeTrans = kodeTrans;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<LaporanItemModel> getItemList() {
        return itemList;
    }

    public void setItemList(List<LaporanItemModel> itemList) {
        this.itemList = itemList;
    }

    public String getPathGbr(Integer nomor) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method m = getClass().getMethod("getPathGbr"+nomor);
        return (String)m.invoke(this);
    }

    public void setPathGbr(Integer nomor, String pathFoto) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        Method m = getClass().getMethod("setPathGbr"+nomor, String.class);
        m.invoke(this, pathFoto);
    }

    public String getPathGbr1() {
        return pathGbr1;
    }

    public void setPathGbr1(String pathGbr1) {
        this.pathGbr1 = pathGbr1;
    }

    public String getPathGbr2() {
        return pathGbr2;
    }

    public void setPathGbr2(String pathGbr2) {
        this.pathGbr2 = pathGbr2;
    }

    public String getPathGbr3() {
        return pathGbr3;
    }

    public void setPathGbr3(String pathGbr3) {
        this.pathGbr3 = pathGbr3;
    }

    public String getPathGbr4() {
        return pathGbr4;
    }

    public void setPathGbr4(String pathGbr4) {
        this.pathGbr4 = pathGbr4;
    }

    public String getPathGbr5() {
        return pathGbr5;
    }

    public void setPathGbr5(String pathGbr5) {
        this.pathGbr5 = pathGbr5;
    }

    public String getPathGbr6() {
        return pathGbr6;
    }

    public void setPathGbr6(String pathGbr6) {
        this.pathGbr6 = pathGbr6;
    }
}
