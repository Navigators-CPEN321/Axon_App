package domain.tiger.axon;
/*
Holds user preference information
 */
public class Preferences {

    public int cost_max;
    public String category, usid;
    public double longitude, latitude;

    public Preferences(){

    }

    public Preferences(int cost_max, String category, String userid, double longitude, double latitude) {
        this.cost_max = cost_max;
        this.category = category;
        this.usid = userid;
        this.longitude = longitude;
        this.latitude = latitude;
    }

}