package com.github.unijacks;

import java.io.IOException;
import java.lang.String;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.github.unijacks.Event.eventType;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;

import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

import java.util.Scanner;



public class Device implements Comparable<Device>{ 
    public final static int CARD_CORNER_RADII = 10;

    private String strIP;          //Required 
    private int intIP;

    private String name = "not Set";        //Optional not Set by defualt
    private String macAdress = "not Set";   //Optional not Set by defualt

    private statusEnum status = statusEnum.LOADING; //LOADING by default

    //Assosiates each state with a color and name
    public enum statusEnum {
        ONLINE("Online",CustomColor.online)
        , LOADING("Loading...",CustomColor.loading)
        , OFFLINE("Offline",CustomColor.offline)
        , INVALIDIP("Invalid IP",CustomColor.invalidIP);
    
        private Color color;
        private String description;
        statusEnum(String description, Color color) {
            this.description=description;
            this.color=color;
        }

        public String getDesc() {return description;}
        public Color getColor() {return color;}
    }

    //Constructor
    public Device(String IP,String name, String macAdress){
        if(name != null) { this.name = name;}
        if(macAdress != null) { this.macAdress = macAdress;}

        if(!this.setIP(IP)){
            status = statusEnum.INVALIDIP;
            this.strIP = null;
        }else{
            status = statusEnum.LOADING;
        }
    }

    public Device(Scanner lineScanner){
        lineScanner.useDelimiter("\\|");
        while (lineScanner.hasNext()) {
            String identifyer = lineScanner.next();
            if (!lineScanner.hasNext()) {break;} // Prevents errors from only an identifyer being present
            String value = lineScanner.next();

            if(identifyer.equals("ip")){setIP(value);}
            if(identifyer.equals("name")){name = value;}
            if(identifyer.equals("macAdress")){macAdress = value;}
        }
        if(strIP == null){status = statusEnum.INVALIDIP;}
        lineScanner.close();
    }

    //Getters
    public String getName(){return name;}
    public String getStrIP() {return strIP;}
    public int getIntIP() {return intIP;}


    public VBox getCard(){
        Label fistLineLabel = new Label(name);
        Label secondLineLabel = new Label(status.getDesc());
        Label thirdLineLabel = new Label(this.strIP);   

        VBox output = new VBox(fistLineLabel,secondLineLabel,thirdLineLabel);
        output.setPrefWidth(100);
        output.setPrefHeight(50);
        
        output.setBackground(new Background(new BackgroundFill(status.getColor(), new CornerRadii(CARD_CORNER_RADII), Insets.EMPTY)));
        output.setAlignment(Pos.CENTER);

        return output;
    }

    //Setters

    /*
     * Attempts to set the ip of the device.
     * Returns true if sucessful
     * Returns false if malformed ip
     */
    public boolean setIP(String newIP){
        if(newIP == null){
            status = statusEnum.INVALIDIP;
            return false;
        }
        //Used to make sure there are 3 "." dots.
        int dotCount = 0;
        //Used to make sure there are numbers between dots
        int numberCount =0;

        String stripedIP = ""; //IP with the dots stripped out.
        for(int charIndex =0; charIndex < newIP.length(); charIndex +=1){
            char currentChar = newIP.charAt(charIndex);
            if(currentChar == '.'){
                dotCount += 1;   
                if(numberCount == 0){
                    status = statusEnum.INVALIDIP;
                    return false;
                }
                numberCount = 0;
            }else if(Character.isDigit(currentChar)){
                stripedIP += currentChar;
                numberCount +=1;
                //Make sure there are at most 3 numbers between dots
                if(numberCount > 3){
                    status = statusEnum.INVALIDIP;
                    return false;
                }
            }else{
                // Returns false if there is anything but numbers and '.'s.
                status = statusEnum.INVALIDIP;
                return false;
            }
        }

        // If there are 3 dots and the final char is a number then accept the ip
        if(dotCount == 3 && Character.isDigit(newIP.charAt(newIP.length()-1))){
            this.strIP = newIP;
            this.intIP = Integer.valueOf(stripedIP);
            return true;
        }else{
            return false;
        }
    }

    public void ping(){
        if(status == statusEnum.INVALIDIP){return;}
        
        InetAddress geek;
        try {
            geek = InetAddress.getByName(strIP);
            System.out.println("Sending Ping Request to " + strIP); 
            if (geek.isReachable(200)){
                new Event(eventType.CAME_ONLINE,new Date(),strIP);
                //if(status == statusEnum.OFFLINE){}
                status = statusEnum.ONLINE; 
            }else{
                if(status == statusEnum.ONLINE){new Event(eventType.WENT_OFFLINE,new Date(),strIP);}
                status = statusEnum.OFFLINE;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            status = statusEnum.LOADING;
            e.printStackTrace();
        } 
    }

    public String toString(){
        return "strIP : " +strIP+" | intIP : " + intIP +" | name: " + name + " | macAdress : " + macAdress;
    }

    //Interfaces 

    /*
    * The higher the ID the higher rank.
     */
    public int compareTo(Device o) {


        if(this.intIP > o.intIP){
            return 1;
        }else if( this.intIP == o.intIP){
            return 0;
        }else{
            return -1;
        }
    }

}

