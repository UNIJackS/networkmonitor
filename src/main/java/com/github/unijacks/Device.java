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
import javafx.util.Duration;

import java.util.Scanner;

import javafx.scene.control.Button;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.util.InputMismatchException;
import java.util.NoSuchElementException;

import java.util.ArrayList;
import java.util.List;



public class Device implements Comparable<Device>{
    public final static int PING_INTERVAL = 15000;

    private String strIP;          //Required 
    private int intIP;
    private String name;        
    private String macAdress;   
    
    private Date lastPingDate;
    private DeviceStatus status;    

    //Constructor
    public Device(String IP,String name, String macAdress){
        setIP(IP);
        if(name != null) {this.name = name;}
        if(macAdress != null) {this.macAdress = macAdress;}
    }


    private void setIP(String newIP){
        if(verifyIP(newIP)){
            strIP = newIP;
            intIP = stripIP(newIP);
            status = new DeviceStatus(DeviceStatus.statusEnum.LOADING, strIP);
        }else{
            strIP = "Invalid IP";
            intIP = 000000000000;
            status = new DeviceStatus(DeviceStatus.statusEnum.INVALIDIP, strIP);
        }
    }

    /*
     * Verifys an ip follows the correct format "000.000.000.000".
     */
    private static boolean verifyIP(String newIP){
        if(newIP == null){return false;}

        Scanner newIPScanner = new Scanner(newIP);
        newIPScanner.useDelimiter(".");

        List<Integer> numbersList = new ArrayList<>();
        try {
            while(newIPScanner.hasNext()){
                int number = newIPScanner.nextInt();
                System.out.println("number :" + number);
                if(number > 255){
                    newIPScanner.close();
                    return false;
                }
                numbersList.add(newIPScanner.nextInt());
            }
        } catch (NoSuchElementException e) {
            newIPScanner.close();
            return false;
        }
        newIPScanner.close();

        if(numbersList.size() != 4){return false;} 
        return true;       
    }

    private int stripIP(String strIP){
        String strippedIP = "";
        for(int charIndex =0; charIndex < strIP.length(); charIndex +=1){
            if(Character.isDigit(strIP.charAt(charIndex))){strippedIP += strIP.charAt(charIndex);}
        }
        return Integer.valueOf(strippedIP);
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
        if(strIP.equals("000.000.000.000")){status.changeStatus(DeviceStatus.statusEnum.INVALIDIP);}
        lineScanner.close();
    }

    private Button getActionButton(){
        Button action = CustomStyle.cardColourButton(status.getAction(),CustomStyle.MAIN_TEXT_WHITE,70,30);

        EventHandler<ActionEvent> actionEvent = new EventHandler<ActionEvent>() { 
            public void handle(ActionEvent e) 
            { 
                switch (status.getStatusEnum()) {
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
                ping(true); 
            } 
        };
        pingButton.setOnAction(actionEvent);
        return pingButton;
    }


    //Getters
    public String getName(){return name;}
    public String getStrIP() {return strIP;}
    public int getIntIP() {return intIP;}

    public HBox getCard(){
        Label nameLabel = CustomStyle.cardStyleLabel(name);
        Label ipLabel = CustomStyle.cardStyleLabel(strIP);
        Label lastPingDateLabel;
        if(lastPingDate != null){
            lastPingDateLabel = CustomStyle.cardStyleLabel(CustomStyle.PING_DATE_FORMAT.format(lastPingDate));
        }else{
            lastPingDateLabel = CustomStyle.cardStyleLabel("Last Ping : loading");
        }
         
        TilePane bottomRow = new TilePane(getActionButton(),getPingButton());
        bottomRow.setHgap(15);
        bottomRow.setPadding(new Insets(0,0,0,15));
        
        VBox leftSide = new VBox(nameLabel,ipLabel,lastPingDateLabel,bottomRow);
        leftSide.setAlignment(Pos.CENTER);
        leftSide.setMaxWidth(200);
        
        Button rightSide = CustomStyle.cardColourButton("", status.getColor(),30,120);
        HBox output = new HBox(leftSide,rightSide);
        
        output.setAlignment(Pos.CENTER_LEFT);
        output.setSpacing(5);

        output.setPrefWidth(245);
        output.setPrefHeight(150);        
        output.setBackground(new Background(new BackgroundFill(CustomStyle.BACK_GROUND_GREY, new CornerRadii(CustomStyle.CARD_CORNER_RADII), Insets.EMPTY)));
        output.setBorder(new Border(new BorderStroke(Color.WHITE,BorderStrokeStyle.SOLID,new CornerRadii(CustomStyle.CARD_CORNER_RADII),new BorderWidths(3))));

        return output;
    }


    
    // Sends a ping request to the device 
    // Returns the string output 
    private String sendPing(int numberToReccive, int timout,boolean windows){
        String pingResult = "";
        String pingCmd;

        if(windows){pingCmd = "ping /n "+numberToReccive+ " /w "+ timout+ " " + strIP;
        }else{pingCmd = "ping -c "+numberToReccive+ " -w "+ timout+ " " + strIP;}

        try {
            Runtime r = Runtime.getRuntime();
            Process p = r.exec(pingCmd);

            BufferedReader in = new BufferedReader(new
            InputStreamReader(p.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                //System.out.println(inputLine);
                pingResult += inputLine;
            }
            in.close();
            //System.out.println("pingResult : "+ pingResult);
        
        } catch (IOException e) {
            System.out.println(e);
        }

        return pingResult;
    }

    private boolean checkPingResult(String pingResult,int numberToReccive,boolean windows){
        Scanner outputScanner = new Scanner(pingResult);
        String prevous = "";
        while(outputScanner.hasNext()){
            String current = outputScanner.next();
            if(windows){
                if(current.equals("Received")){
                    outputScanner.next(); // Skips the equals
                    if(outputScanner.next().equals(numberToReccive + ",")){
                        outputScanner.close();
                        return true;
                    }else{
                        outputScanner.close();
                        return false;
                    }
                }

            }else{
                if(current.equals("received")){
                    if(prevous.equals(String.valueOf(numberToReccive))){
                        outputScanner.close();
                        return true;
                    }else{
                        outputScanner.close();
                        return false;
                    }
                }
            }
            prevous = current;
        }
        outputScanner.close();
        return false;
    }



    @FXML
    public boolean ping(boolean ignoreCoolDown){
        Date currentDate = new Date();
        if(lastPingDate != null){
            Date timeSinceLastPing = new Date(currentDate.getTime()-lastPingDate.getTime());
            if(timeSinceLastPing.getTime() < PING_INTERVAL && !ignoreCoolDown){return false;}
        }
        lastPingDate = currentDate;

        if(status.getStatusEnum() == DeviceStatus.statusEnum.INVALIDIP){return true;}

        String pingResult = sendPing(1,1,PrimaryController.windows);

        if(checkPingResult(pingResult, 1,PrimaryController.windows)){
            //Could be reached so online
            //System.out.println("checkPingResult : true");
            status.changeStatus(DeviceStatus.statusEnum.ONLINE);
        }else{
            //Could not be reached so offline
           // System.out.println("checkPingResult : false");
            status.changeStatus(DeviceStatus.statusEnum.OFFLINE);
        }
        return true;
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

