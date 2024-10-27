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
    //Global static varables
    public final static int UPDATE_PERIOD = 2; // Seconds
    public final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");  
    public final static SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");  

    //Global managers
    DeviceManager deviceManager = new DeviceManager();
    EventManager eventManager = new EventManager();

    // Labels
    @FXML Label infoHeaderLabel;
    @FXML Label devicesHeaderLabel;
    @FXML Label activityHeaderLabel;
    @FXML Label dateLabel;
    @FXML Label timeLabel;

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


    public void initialize(){
        mainBorderPane.setBackground(new Background(new BackgroundFill(CustomColor.mainBackround, CornerRadii.EMPTY, Insets.EMPTY)));
        deviceManager.updateFlowPane(devicesFlowPane);
        eventManager.updateFlowPane(eventsFlowPane,deviceManager.getDevicesMap());
        setFonts();
        setTextColor();
        setButtonColor();
        startUpdateLoop();
    }

    private void setTextColor(){
        dateLabel.setTextFill(CustomColor.mainText);
        timeLabel.setTextFill(CustomColor.mainText);

        infoHeaderLabel.setTextFill(CustomColor.mainText);
        devicesHeaderLabel.setTextFill(CustomColor.mainText);
        activityHeaderLabel.setTextFill(CustomColor.mainText);
    }

    private void setButtonColor(){
        CornerRadii buttonCornerRadii = new CornerRadii(5,5,0,0,false);
        Background buttonBackground = new Background(new BackgroundFill(CustomColor.button, buttonCornerRadii, Insets.EMPTY));
        deviceLoadButton.setBackground(buttonBackground);
        devicePingButton.setBackground(buttonBackground);
        devicePrintButton.setBackground(buttonBackground);

        eventLoadButton.setBackground(buttonBackground);
        eventPrintButton.setBackground(buttonBackground);
    }

    //Sets the font size of the headers and info labels
    private void setFonts(){
        Font headerFont = new Font(30);
        activityHeaderLabel.setFont(headerFont);
        devicesHeaderLabel.setFont(headerFont);
        infoHeaderLabel.setFont(headerFont);

        Font infoFont = new Font(20);
        dateLabel.setFont(infoFont);
        timeLabel.setFont(infoFont);
    }
    
    // Pings devices and updates the time every UPDATE_PERIOD seconds.
    private void startUpdateLoop(){
        //Update every UPDATE_PERIOD
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.seconds(UPDATE_PERIOD), e -> {
                try {
                    pingDevices();
                    updateTime();

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
    private void updateTime(){
        Date date = new Date();
        dateLabel.setText(DATE_FORMAT.format(date));
        timeLabel.setText(TIME_FORMAT.format(date));
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
    private void pingDevices() throws IOException {
        deviceManager.pingAll();
        deviceManager.updateFlowPane(devicesFlowPane);
        loadEvents();
    }

    //---------------- Event Manager Methods ----------------
    //Loads the events from the events directory.
    @FXML
    private void loadEvents() throws IOException {
        eventManager.loadFromFile();
        eventManager.updateFlowPane(eventsFlowPane, deviceManager.getDevicesMap());
    }
    //Prints the loaded events
    @FXML
    private void printEvents() throws IOException {eventManager.printAll();}

}

