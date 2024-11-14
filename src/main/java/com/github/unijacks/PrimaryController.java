package com.github.unijacks;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.text.Font;
import javafx.util.Duration;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

import java.text.SimpleDateFormat;  
import java.util.Date;  


public class PrimaryController {
    public final static boolean windows = true;
    //Global static varables
    public final static int UPDATE_PERIOD = 2; // Seconds

    //Global managers
    DeviceManager deviceManager = new DeviceManager();
    EventManager eventManager = new EventManager();
    
    // Labels
    @FXML Label generalTimeLabel;
    @FXML Label generalDateLabel;
    @FXML Label programNumDeviceLabel;
    @FXML Label programNumEventsLabel;
    @FXML Label programPingIntervalLabel;
    @FXML Label programPingTimeoutLabel;
    @FXML Label programPacketsToSendLabel;
    @FXML Label programPacketsToRecciveLabel;

    // Panes
    @FXML BorderPane mainBorderPane;
    @FXML FlowPane devicesFlowPane;
    @FXML FlowPane eventsFlowPane;

    // Buttons 
    @FXML Button deviceLoadButton;
    @FXML Button devicePingButton;
    @FXML Button devicePrintButton;
    @FXML Button eventLoadButton;
    @FXML Button eventPrintButton;

    // Vbox
    @FXML VBox colourKeyVBox;


    public void initialize(){
        System.out.println("Intalising ... ");

        mainBorderPane.setBackground(new Background(new BackgroundFill(CustomStyle.BACK_GROUND_GREY, CornerRadii.EMPTY, Insets.EMPTY)));
        System.out.println("Device manager flow pane loading ...");
        deviceManager.updateFlowPane(devicesFlowPane);
        System.out.println("Device manager flow pane loaded sucessfuly");

        System.out.println("Event manager flow pane loading ...");
        eventManager.updateFlowPane(eventsFlowPane,deviceManager.getDevicesMap());
        System.out.println("Event manager flow pane loaded sucessfuly");

        System.out.println("Info manager flow pane loading ...");
        intializeInfo();
        System.out.println("Info manager flow pane loaded sucessfuly");

        startUpdateLoop();
        System.out.println("Intalised sucessfully");
    }


    private void intializeInfo(){
        for(DeviceStatus.statusEnum currentEnum : DeviceStatus.statusEnum.values()){
            DeviceStatus currentStatus = new DeviceStatus(currentEnum);
            Label descriptionLabel = new Label(" : "+currentStatus.getDesc());
            descriptionLabel.getStyleClass().add("textLabel");
            Button statusIndicator = new Button("");
            statusIndicator.setMinWidth(30);
            statusIndicator.setMinHeight(30);
            statusIndicator.setBackground(new Background(new BackgroundFill(currentStatus.getColor(), new CornerRadii(15), Insets.EMPTY)));
            statusIndicator.getStyleClass().add("statusButton");

            colourKeyVBox.getChildren().add(new HBox(statusIndicator,descriptionLabel));
        }
    }

    
    // Pings devices and updates the time every UPDATE_PERIOD seconds.
    private void startUpdateLoop(){
        //Update every UPDATE_PERIOD
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.seconds(UPDATE_PERIOD), e -> {
                try {
                    update();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    //Updatse the time and date labels to the current time and date.
    @FXML
    private void update() throws IOException{
        // Only updates the device flow pane if a status changes from the ping.
        pingDevices();
        if(deviceManager.checkForDeviceChanges()){deviceManager.updateFlowPane(devicesFlowPane);}
        // Only updates the events flow pane if new events are loaded.
        if(loadEvents()){eventManager.updateFlowPane(eventsFlowPane, deviceManager.getDevicesMap());}
            
        updateInfo();
    }



    @FXML
    private void updateInfo(){
        //General
        Date currentDate = new Date();
        generalTimeLabel.setText("Date : " + CustomStyle.JUST_DATE_FORMAT.format(currentDate));
        generalDateLabel.setText("Time : " + CustomStyle.JUST_TIME_FORMAT.format(currentDate));
        //Program
        programNumDeviceLabel.setText("Devices : " + deviceManager.getNumberOfDevices());
        programNumEventsLabel.setText("Events : " + eventManager.getNumberOfEvents());
        programPingIntervalLabel.setText("Ping Interval : " + (Device.PING_INTERVAL/1000) + " sec");
        programPingTimeoutLabel.setText("Ping Timeout : " + (Device.PING_TIMEOUT) + " sec");
        programPacketsToSendLabel.setText("Packets to send : " + (Device.PACKETS_TO_SEND));
        programPacketsToRecciveLabel.setText("Packets to reccive : " + (Device.PACKETS_TO_RECCIVE));
    }


    @FXML
    private void switchToSecondary() throws IOException {App.setRoot("secondary");}

    //---------------- Device Manager Methods ----------------
    //Loads the devices from the DeviceList.txt file.
    @FXML
    private void loadDevices() throws IOException {deviceManager.loadFromFile();}
    //Prints the loaded devices
    @FXML
    private void printDevices() throws IOException {deviceManager.printAll();}
    //pings the loaded devices
    @FXML
    private void pingDevices() throws IOException {deviceManager.pingAll();}

    //---------------- Event Manager Methods ----------------
    // Loads the events from the events directory.
    @FXML
    private boolean loadEvents() throws IOException {return eventManager.loadFromFile();}
    //Prints the loaded events
    @FXML
    private void printEvents() throws IOException {eventManager.printAll();}

}

