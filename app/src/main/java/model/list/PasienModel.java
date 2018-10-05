package model.list;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by christian on 10/04/18.
 */

public class PasienModel implements Parcelable {

    private String kode;
    private String nama;
    private String alamat;
    private String telp;
    private String tgl;
    private String gender;

    public PasienModel(){}

    private PasienModel(Parcel in) {
        kode = in.readString();
        nama = in.readString();
        alamat = in.readString();
        telp = in.readString();
        tgl = in.readString();
        gender = in.readString();
    }

    public static final Creator<PasienModel> CREATOR = new Creator<PasienModel>() {
        @Override
        public PasienModel createFromParcel(Parcel in) {
            return new PasienModel(in);
        }

        @Override
        public PasienModel[] newArray(int size) {
            return new PasienModel[size];
        }
    };

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return this.hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(kode);
        dest.writeString(nama);
        dest.writeString(telp);
        dest.writeString(alamat);
        dest.writeString(tgl);
        dest.writeString(gender);
    }

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getTelp() {
        return telp;
    }

    public void setTelp(String telp) {
        this.telp = telp;
    }

    public String getTgl() {
        return tgl;
    }

    public void setTgl(String tgl) {
        this.tgl = tgl;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
