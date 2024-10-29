package com.github.unijacks;

import javafx.collections.ListChangeListener.Change;
import javafx.scene.paint.Color;
import java.util.Date;  

public class DeviceStatus {
    public enum statusEnum {
        ONLINE
        , UNREACHABLE
        , OFFLINE
        , LOADING
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

    public boolean invalidIP(){return status == statusEnum.INVALIDIP;}

    public void unsucessfulPing(){
        switch (status) {
            case LOADING:
                changeStatus(statusEnum.UNREACHABLE);
                break;

            case ONLINE:
                changeStatus(statusEnum.UNREACHABLE);
                break;

            case UNREACHABLE:
                changeStatus(statusEnum.OFFLINE);
                break;

            case OFFLINE:
                break;
        
            default:
                break;
        }
    }

    public void sucessfulPing(){
        changeStatus(statusEnum.ONLINE);
    }

    private void changeStatus(statusEnum newStatus){
        switch (newStatus) {
            case ONLINE:
                if(status != statusEnum.ONLINE){new Event(Event.eventType.CAME_ONLINE,new Date(),strIP);}
                this.status = newStatus;
                this.color = CustomStyle.ONLINE_GREEN;
                this.description = "Online";
                this.action = "SSH";
                break;

            case UNREACHABLE:
                //if(status != statusEnum.UNREACHABLE){new Event(Event.eventType.WENT_OFFLINE,new Date(),strIP);}
                this.status = newStatus;
                this.color = CustomStyle.UNREACHABLE_YELLOW;
                this.description = "Offline";
                this.action = "Wake";
                break;

            case OFFLINE:
                if(status != statusEnum.OFFLINE){new Event(Event.eventType.WENT_OFFLINE,new Date(),strIP);}
                this.status = newStatus;
                this.color = CustomStyle.OFFLINE_ORANGE;
                this.description = "Offline";
                this.action = "Wake";
                break;

            case LOADING:
                this.status = newStatus;
                this.color = CustomStyle.LOADING_GREY;
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


