package domain.tiger.axon;

import java.util.List;

public class Preferences {

    public int cost_max;
    public String category, usid;

    public Preferences(){

    }

    public Preferences(int cost_max, String category, String userid) {
        this.cost_max = cost_max;
        this.category = category;
        this.usid = userid;
    }

}