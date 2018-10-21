package domain.tiger.axon;

import java.util.List;

public class userInformation {

    public int cost_max;
    public String category;
    public String usid;
    public List<String> groups;

    public userInformation(){

    }

    public userInformation(int cost_max, String category, String userid) {
        this.cost_max = cost_max;
        this.category = category;
        this.usid = userid;
    }

    public void addGroup(String groupName){
        groups.add(groupName);
    }
}
