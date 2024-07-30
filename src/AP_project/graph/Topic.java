//package AP_project.test;
package AP_project.graph;


import java.util.ArrayList;
import java.util.List;

public class Topic {
    public String name;
    private List<Agent> subs ;
    private List<Agent> pubs ;
    private String result = "";

    Topic(String name){
        this.name=name;
        this.subs=new ArrayList<>();
        this.pubs=new ArrayList<>();
    }

    public void subscribe(Agent a){
        if(!subs.contains(a)){
            subs.add(a);
        }
    }
    public void unsubscribe(Agent a){
        subs.remove(a);
    }

    public void publish(Message m){
        setResult(m.asText);

        for(Agent a:subs){
            a.callback(this.name,m);
        }
    }

    public void addPublisher(Agent a){
        if(!pubs.contains(a)){
            pubs.add(a);
        }
    }

    public void removePublisher(Agent a){
        pubs.remove(a);
    }

    public List<Agent> getSubscribers() {
        return new ArrayList<>(subs);
    }

    public List<Agent> getPublishers() {
        return new ArrayList<>(pubs);
    }


    public String getName() {
        return name;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setName(String name){
        this.name = name;
    }

}
