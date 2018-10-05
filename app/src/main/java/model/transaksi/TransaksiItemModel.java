package model.transaksi;

import java.io.Serializable;

/**
 * Created by christian on 13/04/18.
 */

public class TransaksiItemModel implements Serializable {

    private Integer line;
    private String stat;
    private String anatomi;
    private String pathVideo;
    /*private String pathGbr1;
    private String pathGbr2;
    private String pathGbr3;
    private String pathGbr4;
    private String pathGbr5;*/

    public Integer getLine() {
        return line;
    }

    public void setLine(Integer line) {
        this.line = line;
    }

    public String getAnatomi() {
        return anatomi;
    }

    public void setAnatomi(String anatomi) {
        this.anatomi = anatomi;
    }

    public String getPathVideo() {
        return pathVideo;
    }

    public void setPathVideo(String pathVideo) {
        this.pathVideo = pathVideo;
    }

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }
}