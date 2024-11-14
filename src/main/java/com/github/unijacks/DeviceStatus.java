package com.github.unijacks;

import javafx.collections.ListChangeListener.Change;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;  

/*
 * Stores a devices status
 */
public class DeviceStatus {
    public static enum statusEnum {
        ONLINE          //The device responded to the last ping.
        , UNREACHABLE   //The device missed the last ping.
        , OFFLINE       //The device missed the last 2 or more pings.
        , LOADING       //The device has not been pinged.
        , INVALIDIP;    //The devices ip is invalid.
    }

    private statusEnum status = statusEnum.LOADING;
    private Color color;
    private String description;
    private String action;
    private String strIP;

    public DeviceStatus(String strIP){
        changeStatus(status,true); //Sets the colour and other values for the loading state.

        if(!verifyIP(strIP)){
            changeStatus(statusEnum.INVALIDIP,false);
        }else{
            this.strIP = strIP;
        }
    }

    public DeviceStatus(statusEnum status){
        changeStatus(status,true);
    }

    public String getAction(){return action;}
    public Color getColor(){return color;}
    public String getDesc(){return description;}
    public statusEnum getStatusEnum(){return status;}

    public boolean isIPInvalid(){return status == statusEnum.INVALIDIP;}

    public void unsucessfulPing(){
        switch (status) {
            case LOADING:
                changeStatus(statusEnum.UNREACHABLE,false);
                break;

            case ONLINE:
                changeStatus(statusEnum.UNREACHABLE,false);
                break;

            case UNREACHABLE:
                changeStatus(statusEnum.OFFLINE,false);
                break;
        
            default:
                break;
        }
    }

    public void sucessfulPing(){
        changeStatus(statusEnum.ONLINE,false);
    }

    /*
     * ONLY CONSTRUCTOR SHOULD CAUSE NO EVENTS
     */
    private void changeStatus(statusEnum newStatus, boolean noEvents){
        switch (newStatus) {
            case ONLINE:
                if(status != statusEnum.ONLINE && !noEvents){new Event(Event.eventType.CAME_ONLINE,new Date(),strIP);}
                this.status = newStatus;
                this.color = CustomStyle.ONLINE_GREEN;
                this.description = "Online";
                this.action = "SSH";
                break;

            case UNREACHABLE:
                this.status = newStatus;
                this.color = CustomStyle.UNREACHABLE_YELLOW;
                this.description = "Unreachable";
                this.action = "Wake";
                break;

            case OFFLINE:
                if(status != statusEnum.OFFLINE && !noEvents){new Event(Event.eventType.WENT_OFFLINE,new Date(),strIP);}
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

    /*
     * Verifys an ip follows the correct format "000.000.000.000".
     */
    private static boolean verifyIP(String newIP) {
        if (newIP == null) {
            return false;
        } // Checks the ip is not null.
        Scanner newIPScanner = new Scanner(newIP);
        newIPScanner.useDelimiter("\\.");
        List<Integer> numbersList = new ArrayList<>(); // Stores the numbers
        try {
            // Loops through the new ip and reccords the numbers between the fullstops.
            while (newIPScanner.hasNext()) {
                int number = newIPScanner.nextInt();
                // Makes sure the numbers are less than 255
                if (number > 255) {
                    newIPScanner.close();
                    return false;
                }
                numbersList.add(number);
            }
        } catch (NoSuchElementException e) {
            newIPScanner.close();
            return false;
        }
        newIPScanner.close();
        // Makes sure there are 4 numbers
        if (numbersList.size() != 4) {
            return false;
        }
        return true;
    }

    
}


