package com.github.unijacks;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
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
    InfoManager infoManager = new InfoManager();

    // Labels
    @FXML Label infoHeaderLabel;
    @FXML Label devicesHeaderLabel;
    @FXML Label activityHeaderLabel;

    // Panes
    @FXML BorderPane mainBorderPane;
    @FXML FlowPane devicesFlowPane;
    @FXML FlowPane eventsFlowPane;
    @FXML FlowPane infoFlowPane;

    // Buttons 
    @FXML Button deviceLoadButton;
    @FXML Button devicePingButton;
    @FXML Button devicePrintButton;
    @FXML Button eventLoadButton;
    @FXML Button eventPrintButton;


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
        infoManager.updateFlowPane(infoFlowPane,deviceManager,eventManager);
        System.out.println("Info manager flow pane loaded sucessfuly");


        setFonts();
        setTextColor();
        setButtonColor();
        startUpdateLoop();
        System.out.println("Intalised sucessfully");

    }

    private void setTextColor(){
        infoHeaderLabel.setTextFill(CustomStyle.MAIN_TEXT_WHITE);
        devicesHeaderLabel.setTextFill(CustomStyle.MAIN_TEXT_WHITE);
        activityHeaderLabel.setTextFill(CustomStyle.MAIN_TEXT_WHITE);
    }

    private void setButtonColor(){
        CornerRadii buttonCornerRadii = new CornerRadii(5,5,0,0,false);
        Background buttonBackground = new Background(new BackgroundFill(CustomStyle.BUTTON_GREY, buttonCornerRadii, Insets.EMPTY));
        deviceLoadButton.setBackground(buttonBackground);
        devicePingButton.setBackground(buttonBackground);
        devicePrintButton.setBackground(buttonBackground);

        eventLoadButton.setBackground(buttonBackground);
        eventPrintButton.setBackground(buttonBackground);
    }

    //Sets the font size of the headers and info labels
    private void setFonts(){
        activityHeaderLabel.setFont(CustomStyle.Header_FONT);
        devicesHeaderLabel.setFont(CustomStyle.Header_FONT);
        infoHeaderLabel.setFont(CustomStyle.Header_FONT);
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
        pingDevices();
        loadEvents();

        deviceManager.updateFlowPane(devicesFlowPane);
        eventManager.updateFlowPane(eventsFlowPane, deviceManager.getDevicesMap());
        infoManager.updateFlowPane(infoFlowPane,deviceManager,eventManager);
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
    //Loads the events from the events directory.
    @FXML
    private void loadEvents() throws IOException {eventManager.loadFromFile();
        
    }
    //Prints the loaded events
    @FXML
    private void printEvents() throws IOException {eventManager.printAll();}

}

