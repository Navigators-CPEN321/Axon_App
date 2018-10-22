package domain.tiger.axon;

import java.util.List;

public class preferences{

    public int cost_max;
    public String category, usid;

    public preferences(){

    }

    public preferences (int cost_max, String category, String userid) {
        this.cost_max = cost_max;
        this.category = category;
        this.usid = userid;
    }

}