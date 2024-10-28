package com.github.unijacks;

import java.io.IOException;
import java.lang.String;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.github.CustomStyle;
import com.github.unijacks.Event.eventType;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;

import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

import java.util.Scanner;

import javafx.scene.control.Button;




public class Device implements Comparable<Device>{ 
    public final static int CARD_CORNER_RADII = 15;

    private String strIP = "IP not Set";          //Required 
    private int intIP = 000000000000;

    private String name = "name not Set";        //Optional not Set by defualt
    private String macAdress = "macAdress not Set";   //Optional not Set by defualt

    private statusEnum status = statusEnum.LOADING; //LOADING by default

    //Assosiates each state with a color, action and name
    public enum statusEnum {
        ONLINE("Online",CustomStyle.ONLINE_GREEN,"SSH")
        , LOADING("Loading...",CustomStyle.LOADING_YELLOW,"N/A")
        , OFFLINE("Offline",CustomStyle.OFFLINE_ORANGE,"Wake")
        , INVALIDIP("Invalid IP",CustomStyle.INVALID_IP_PINK,"N/A");
    
        private Color color;
        private String description;
        private String action;
        statusEnum(String description, Color color, String action) {
            this.description=description;
            this.color=color;
            this.action=action;
        }

        public String getDesc() {return description;}
        public Color getColor() {return color;}
        public String getAction() {
            return action;
        }
    }

    //Constructor
    public Device(String IP,String name, String macAdress){
        if(name != null) { this.name = name;}
        if(macAdress != null) { this.macAdress = macAdress;}

        if(!this.setIP(IP)){
            status = statusEnum.INVALIDIP;
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
        if(strIP.equals("000.000.000.000")){status = statusEnum.INVALIDIP;}
        lineScanner.close();
    }

    private Button getActionButton(){
        Button action = CustomStyle.cardColourButton(status.getAction(),CustomStyle.MAIN_TEXT_WHITE,70,30);

        EventHandler<ActionEvent> actionEvent = new EventHandler<ActionEvent>() { 
            public void handle(ActionEvent e) 
            { 
                switch (status) {
                    case OFFLINE:
                        wake();
                        break;

                    case ONLINE:
                        ssh();
                    break;
                
                    default:
                        break;
                }
                
            } 
        };
        action.setOnAction(actionEvent);
        return action;
    }

    private Button getPingButton(){
        Button pingButton = CustomStyle.cardColourButton("Ping",CustomStyle.MAIN_TEXT_WHITE,70,30);
        EventHandler<ActionEvent> actionEvent = new EventHandler<ActionEvent>() { 
            public void handle(ActionEvent e) 
            { 
                ping(); 
            } 
        };
        pingButton.setOnAction(actionEvent);
        return pingButton;
    }


    //Getters
    public String getName(){return name;}
    public String getStrIP() {return strIP;}
    public int getIntIP() {return intIP;}


    private static Label styleLabel(String text){
        Label output = new Label(text);
        output.setFont(CustomStyle.CARD_FONT);
        output.setTextFill(CustomStyle.MAIN_TEXT_WHITE);
        return output;
    }

    public HBox getCard(){
        Label nameLabel = styleLabel(name);
        Label ipLabel = styleLabel(this.strIP);
        TilePane bottomRow = new TilePane(getActionButton(),getPingButton());
        bottomRow.setHgap(15);
        bottomRow.setPadding(new Insets(0,15,0,15));
        VBox leftSide = new VBox(nameLabel,ipLabel,bottomRow);
        leftSide.setAlignment(Pos.CENTER);
        
        Button rightSide = CustomStyle.cardColourButton("", status.getColor(),30,75);
        
        HBox output = new HBox(leftSide,rightSide);

        output.setAlignment(Pos.CENTER);
        output.setPadding(new Insets(0,5,0,5));

        output.setPrefWidth(240);
        output.setPrefHeight(105);        
        output.setBackground(new Background(new BackgroundFill(CustomStyle.BACK_GROUND_GREY, new CornerRadii(CARD_CORNER_RADII), Insets.EMPTY)));
        output.setBorder(new Border(new BorderStroke(Color.WHITE,BorderStrokeStyle.SOLID,new CornerRadii(CARD_CORNER_RADII),new BorderWidths(3))));

        return output;
    }

    //Setters

    /*
     * Attempts to set the ip of the device.
     * Returns true if sucessful
     * Returns false if malformed ip
     */
    private boolean setIP(String newIP){
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

    @FXML
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

    @FXML
    public void wake(){
        //send magic packet
        System.out.println("Magic packet sent to : " + strIP);
    }

    @FXML
    public void ssh(){
        //run ssh script
        System.out.println("SSH script run to : " + strIP);
    }

    public String toString(){
        return "strIP : " +strIP+" | intIP : " + intIP +" | name: " + name + " | macAdress : " + macAdress;
    }

    //Interfaces 

    /*
    * The higher the IP the higher rank.
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

