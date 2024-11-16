package com.github.unijacks;

import java.text.ParseException;
import java.text.SimpleDateFormat;  
import java.util.Date;  

import java.io.File;
import java.io.FileNotFoundException;

import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;

import java.util.Map;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


/*
 * name : deviceName eventType    dd MM yyyy HH mm ss
 * eg   : desktop    WENT_OFFLINE 29 10 2024 12 33 03
 */
public class Event implements Comparable<Event>{
    private eventType type;
    private Date dateOccurred;
    private String ip; 

    public final static SimpleDateFormat HUMAN_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");  
    public final static SimpleDateFormat FILE_NAME_DATE_FORMAT = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");


    public enum eventType {
        MAGIC_PACKET_SENT("eventMagicPacket","Magic packet sent")
        , WENT_OFFLINE("eventOffline","Device went offline")
        , CAME_ONLINE("eventOnline","Device came online");

        private String styleClassString; 
        private String description;
        eventType( String styleClassString,String description) {
            this.description=description;
            this.styleClassString=styleClassString;
        }

        public String getDesc() {return description;}

        public Button getTypeIndicator(double width, double height ){
            Button statusIndicator = new Button("");
            statusIndicator.setMinWidth(width);
            statusIndicator.setMinHeight(height);
            statusIndicator.getStyleClass().add(styleClassString);
            return statusIndicator;
        }
    }

    //Creates an event from provided data and saves it to a file
    public Event(eventType type,Date dateOccurred,String ip){
        this.dateOccurred = dateOccurred;
        this.type = type;
        this.ip = ip;
        saveEvent();
    }

    //Loads an event from a given file.
    public Event(File inputFile){
        Scanner fileScanner = safeScanner(inputFile);
        fileScanner.useDelimiter("\\|");

        // Reads the ip, name and or macAdress
        while(fileScanner.hasNext()){
            String identifyer = fileScanner.next();
            //System.out.println("identifyer : " + identifyer);
            if (!fileScanner.hasNext()) {break;} // Prevents errors 
            String value = fileScanner.next();
            //System.out.println("value : " + value);
            if(identifyer.equals("ip")){ip = value;}
            if(identifyer.equals("type")){type = eventType.valueOf(value);}
            if(identifyer.equals("dateOccurred")){dateOccurred = parseDateSafe(value);}
        }        

        if(dateOccurred == null){
            System.out.println("date is null");
        }
    }

    public Date parseDateSafe(String strDateOccured){
        //Attempts to parse the rest of the scanner into a date.
        try {
            return HUMAN_DATE_FORMAT.parse(strDateOccured);
        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("null date :" + strDateOccured);
            return null;
        }
    }

    //Creates a scanner with error handeling
    private Scanner safeScanner(File inputFile){
        try {
            return new Scanner(inputFile);
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred. When trying to find :" + inputFile);
            e.printStackTrace();
            return null;
        }
    }

    //Getters
    public Date getDateOccurred() {return dateOccurred;}
    public String getEventType() {return type.name();}

    private void saveEvent(){
        //1 MAGIC_PACKET_SENT 24 10 2024 03 57 00
        //events\event1.txt
        String fileName = "events/" + this.getFileName() +".txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(this.toString());
            writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred. When trying to write :" + fileName);
            e.printStackTrace();
        }
    }

    public String getFileName(){
        return "ip=" +ip+"_type=" + type.name()+"_dateOccurred=" + FILE_NAME_DATE_FORMAT.format(dateOccurred);
    }

    public String toString() {
        return "ip|" +ip+"|type|" + type.name()+"|dateOccurred|" +HUMAN_DATE_FORMAT.format(dateOccurred);
    }




    public HBox getCard(Map<String,Device> devicesMap){
        Device deviceInvolved;
        if(devicesMap.keySet().contains(ip)){
            deviceInvolved = devicesMap.get(ip);
        }else{
            deviceInvolved = new Device(null,null,null);
        }

        Label nameLabel = new Label(deviceInvolved.getName());
        Label typeLabel = new Label(type.getDesc());
        Label dateLabel = new Label(HUMAN_DATE_FORMAT.format(dateOccurred));

        nameLabel.getStyleClass().add("textLabel");
        typeLabel.getStyleClass().add("textLabel");
        dateLabel.getStyleClass().add("textLabel");
   
        VBox leftSide = new VBox(nameLabel,typeLabel,dateLabel);
        leftSide.setAlignment(Pos.CENTER);
        leftSide.setMinWidth(210);
        
        Button rightSide = type.getTypeIndicator(30,75);
        
        HBox output = new HBox(leftSide,rightSide); 

        output.setMaxWidth(260);
        output.setMaxHeight(105);

        output.getStyleClass().add("card");
        
        output.setAlignment(Pos.CENTER_LEFT);
        output.setSpacing(5);
        return output;
    }

    @Override
    public int compareTo(Event o) {
        return o.dateOccurred.compareTo(this.dateOccurred);
    }

    public boolean equals(Event obj) {
        return this.toString().equals(obj.toString());
    }
}
