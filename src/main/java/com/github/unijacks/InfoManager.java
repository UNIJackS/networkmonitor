package com.github.unijacks;

import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;

import java.text.SimpleDateFormat;  
import java.util.Date; 

public class InfoManager {
    
    private VBox getColorKey(){
        VBox output = CustomStyle.cardBox(new VBox());
        output.getChildren().add(CustomStyle.cardTitleLabel("Colour Key"));
        for(DeviceStatus.statusEnum currentEnum : DeviceStatus.statusEnum.values()){
            DeviceStatus currentStatus = new DeviceStatus(currentEnum);

            output.getChildren().add(
                new HBox(CustomStyle.cardColourButton("", currentStatus.getColor()
                , 30, 30),CustomStyle.cardTextLabel(" : "+currentStatus.getDesc())));
        }
        output.setAlignment(Pos.CENTER_LEFT);
        output.setSpacing(10);
        output.setMinWidth(260);
        output.setPadding(new Insets(0,15,15,15));

        return output;
    }

    private VBox getGeneral(){
        VBox output = CustomStyle.cardBox(new VBox());
        output.getChildren().add(CustomStyle.cardTitleLabel("General"));
        Date currentDate = new Date();
        output.getChildren().add(CustomStyle.cardTextLabel("Date : " + CustomStyle.JUST_DATE_FORMAT.format(currentDate)));
        output.getChildren().add(CustomStyle.cardTextLabel("Time : " + CustomStyle.JUST_TIME_FORMAT.format(currentDate)));

        output.setAlignment(Pos.CENTER_LEFT);
        output.setSpacing(10);
        output.setMinWidth(260);
        output.setPadding(new Insets(0,15,15,15));
        return output;
    }

    private VBox getProgram(DeviceManager deviceManager, EventManager eventManager){
        VBox output = CustomStyle.cardBox(new VBox());
        output.getChildren().add(CustomStyle.cardTitleLabel("Program"));
        output.getChildren().add(CustomStyle.cardTextLabel("Devices : " + deviceManager.getNumberOfDevices()));
        output.getChildren().add(CustomStyle.cardTextLabel("Events : " + eventManager.getNumberOfEvents()));
        output.getChildren().add(CustomStyle.cardTextLabel("Ping Interval : " + (Device.PING_INTERVAL/1000) + " sec"));
        output.getChildren().add(CustomStyle.cardTextLabel("Ping Timeout : " + (Device.PING_TIMEOUT) + " sec"));
        output.getChildren().add(CustomStyle.cardTextLabel("Packets to send : " + (Device.PACKETS_TO_SEND)));
        output.getChildren().add(CustomStyle.cardTextLabel("Packets to reccive : " + (Device.PACKETS_TO_RECCIVE)));

        
        output.setAlignment(Pos.CENTER_LEFT);
        output.setSpacing(10);
        output.setMinWidth(260);
        output.setPadding(new Insets(0,15,15,15));
        return output;
    }



    public void updateFlowPane(FlowPane infoFlowPane,DeviceManager deviceManager, EventManager eventManager){
        infoFlowPane.getChildren().clear();
        infoFlowPane.getChildren().add(getColorKey());
        infoFlowPane.getChildren().add(getGeneral());
        infoFlowPane.getChildren().add(getProgram(deviceManager,eventManager));
    }

}
