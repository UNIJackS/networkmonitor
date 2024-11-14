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

            output.getChildren().add(new HBox(CustomStyle.cardColourButton("", currentStatus.getColor(), 30, 30),CustomStyle.cardTextLabel(" : "+currentStatus.getDesc())));
        }

        return output;
    }





    public void updateFlowPane(FlowPane infoFlowPane,DeviceManager deviceManager, EventManager eventManager){
        infoFlowPane.getChildren().clear();
        infoFlowPane.getChildren().add(getColorKey());
    }

}
