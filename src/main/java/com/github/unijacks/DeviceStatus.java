package com.github.unijacks;

import javafx.collections.ListChangeListener.Change;
import javafx.scene.paint.Color;
import java.util.Date;  

public class DeviceStatus {
    public enum statusEnum {
        ONLINE
        , LOADING
        , OFFLINE
        , INVALIDIP;
    }

    private statusEnum status;
    private Color color;
    private String description;
    private String action;
    private String strIP;

    public DeviceStatus(statusEnum status,String strIP){
        this.strIP = strIP;
        changeStatus(status);
    }

    public String getAction(){return action;}
    public Color getColor(){return color;}
    public String getDesc(){return description;}
    public statusEnum getStatusEnum(){return status;}

    public void changeStatus(statusEnum newStatus){
        switch (newStatus) {
            case ONLINE:
                System.out.println("change to online started...");
                System.out.println("current status : " + status.name());
                if(status != statusEnum.ONLINE){
                    System.out.println("created came online event");
                    new Event(Event.eventType.CAME_ONLINE,new Date(),strIP);}
                this.status = newStatus;
                this.color = CustomStyle.ONLINE_GREEN;
                this.description = "Online";
                this.action = "SSH";
                break;

            case OFFLINE:
                System.out.println("change to offline started...");
                System.out.println("current status : " + status.name());

                if(status != statusEnum.OFFLINE){
                    System.out.println("created went offline event");

                    new Event(Event.eventType.WENT_OFFLINE,new Date(),strIP);}
                this.status = newStatus;
                this.color = CustomStyle.OFFLINE_ORANGE;
                this.description = "Offline";
                this.action = "Wake";
                break;

            case LOADING:
                this.status = newStatus;
                this.color = CustomStyle.LOADING_YELLOW;
                this.description = "Loading...";
                this.action = "N/A";
                break;

            case INVALIDIP:
                this.status = newStatus;
                this.color = CustomStyle.INVALID_IP_PINK;
                this.description = "Invalid IP";
                this.action = "N/A";
                break;
        
            default:
                break;
        }
    }

    
}


