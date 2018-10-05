package utilities;

import service.BaseApiService;

/**
 * Created by christian on 14/02/18.
 */

public class Link {

    public static final String MATA_KN="MATA KANAN";
    public static final String MATA_KR="MATA KIRI";
    public static final String ICON_PDF="icon_pdf.png";

    public static final String BASE_URL_API = "http://softchrist.com/diagnosa_mata/php/";
    public static final String BASE_URL_IMAGE = "http://softchrist.com/diagnosa_mata/image/";
    public static final String BASE_URL_VIDEO = "http://softchrist.com/diagnosa_mata/video/";

    // Mendeklarasikan Interface BaseApiService
    public static BaseApiService getAPIService(){
        return RetrofitClient.getClient(BASE_URL_API).create(BaseApiService.class);
    }

    public static BaseApiService getImageService(){
        return RetrofitClient.getClient(BASE_URL_IMAGE).create(BaseApiService.class);
    }

    public static BaseApiService getVideoService(){
        return RetrofitClient.getClient(BASE_URL_VIDEO).create(BaseApiService.class);
    }
}
