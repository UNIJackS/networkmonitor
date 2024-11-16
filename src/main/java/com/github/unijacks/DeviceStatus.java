package com.github.unijacks;

import javafx.scene.control.Button;

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
        ONLINE("statusOnline","Online","SSH")                     //The device responded to the last ping.
        , UNREACHABLE("statusUnreachable","Unreachable","Wake")  //The device missed the last ping.
        , OFFLINE("statusOffline","Offline","Wake")              //The device missed the last 2 or more pings.
        , LOADING("statusLoading","Loading...","N/A")              //The device has not been pinged.
        , INVALIDIP("statusInvalidIP","Invalid IP","N/A");        //The devices ip is invalid.

        private String styleClassString; 
        private String description; //The String used for the name on the event card
        private String action;      //The String used as the text on the action button

        private statusEnum(String styleClassString, String description,String action){
            this.styleClassString = styleClassString;
            this.description = description;
            this.action = action;
        }

        public String getEnumColor(){return styleClassString;}
        public String getEnumDescription(){return description;}
        public String getEnumAction(){return action;}


        public Button getStatusIndicator(double width, double height ){
            Button statusIndicator = new Button("");
            statusIndicator.setMinWidth(width);
            statusIndicator.setMinHeight(height);
            statusIndicator.getStyleClass().add(styleClassString);
            statusIndicator.getStyleClass().add("statusButton");
            return statusIndicator;
        }
    }

    private statusEnum status = statusEnum.LOADING;
    private String strIP = Device.DEFAULT_STR_IP;

    public DeviceStatus(String strIP){
        if(!verifyIP(strIP)){
            changeStatus(statusEnum.INVALIDIP,false);
        }else{
            this.strIP = strIP;
        }
    }

    public String getDesc(){return status.getEnumDescription();}
    public String getAction(){return status.getEnumAction();}
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
                break;

            case UNREACHABLE:
                this.status = newStatus;
                break;

            case OFFLINE:
                if(status != statusEnum.OFFLINE && !noEvents){new Event(Event.eventType.WENT_OFFLINE,new Date(),strIP);}
                this.status = newStatus;
                break;

            case LOADING:
                this.status = newStatus;
                break;

            case INVALIDIP:
                this.status = newStatus;
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


