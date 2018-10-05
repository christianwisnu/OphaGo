package utilities;

import model.list.ListPasien;
import model.list.PasienModel;

/**
 * Created by Chris on 25/03/2018.
 */

public class JSONResponse {
    private ListPasien[] pasien;

    public ListPasien[] getPasien() {
        return pasien;
    }

    private PasienModel[] pasienmodel;

    public PasienModel[] getPasienmodel() {
        return pasienmodel;
    }
}
