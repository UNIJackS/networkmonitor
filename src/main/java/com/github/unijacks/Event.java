package com.github.unijacks;

import java.text.ParseException;
import java.text.SimpleDateFormat;  
import java.util.Date;  
import java.util.Calendar;

import java.io.File;
import java.io.FileNotFoundException;

import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;

import java.util.Map;
import java.util.HashMap;


import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;

/*
 * name : deviceName eventType    dd MM yyyy HH mm ss
 * eg   : desktop    WENT_OFFLINE 29 10 2024 12 33 03
 */
public class Event implements Comparable<Event>{
    private eventType type;
    private Date dateOccurred;
    private String ip;

    private final static SimpleDateFormat HUMAN_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");  
    private final static SimpleDateFormat FILE_NAME_DATE_FORMAT = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");  

    public enum eventType {
        MAGIC_PACKET_SENT("Magic packet sent",CustomColor.magicPacket)
        , WENT_OFFLINE("Device came online",CustomColor.offline)
        , CAME_ONLINE("Device went offline",CustomColor.online);

        private Color color;
        private String description;
        eventType(String description, Color color) {
            this.description=description;
            this.color=color;
        }

        public String getDesc() {return description;}
        public Color getColor() {return color;}
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
        System.out.println("save event called");

        //1 MAGIC_PACKET_SENT 24 10 2024 03 57 00
        //events\event1.txt
        String fileName = "events\\" + this.getFileName() +".txt";
        System.out.println("fileName : " + fileName);
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
        return "ip|" +ip+"|type|" + type.name()+"|dateOccurred|" + HUMAN_DATE_FORMAT.format(dateOccurred);
    }

    public final static int CARD_CORNER_RADII = 10;

    public VBox getCard(Map<String,Device> devicesMap){
        Device deviceInvolved;
        if(devicesMap.keySet().contains(ip)){
            deviceInvolved = devicesMap.get(ip);
        }else{
            deviceInvolved = new Device(null,null,null);
        }
        
        VBox output = new VBox();

        output.getChildren().add(new Label(deviceInvolved.getName()));
        output.getChildren().add(new Label(type.getDesc()));
        output.getChildren().add(new Label(HUMAN_DATE_FORMAT.format(dateOccurred)));

        output.setPrefWidth(200);
        output.setPrefHeight(50);
        output.setBackground(new Background(new BackgroundFill(type.getColor(), new CornerRadii(CARD_CORNER_RADII), Insets.EMPTY)));
        output.setAlignment(Pos.CENTER);
        
        return output;
    }

    @Override
    public int compareTo(Event o) {
        return this.dateOccurred.compareTo(o.dateOccurred);
    }
}
