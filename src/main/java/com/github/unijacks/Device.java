package com.github.unijacks;

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
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.control.Button;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.NoSuchElementException;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Scanner;

import java.lang.String;

/*
 * Represents a single device. Uniquily identifable by their IP.
 */
public class Device implements Comparable<Device> {
    public final static int PING_INTERVAL = 15000;
    public final static int PACKETS_TO_SEND = 2;
    public final static int PACKETS_TO_RECCIVE = 2;
    public final static int PING_TIMEOUT = 1;


    public final static String DEFAULT_STR_IP = "IP Not Set";
    public final static int DEFAULT_INT_IP = 000000000000;

    private String strIP = DEFAULT_STR_IP; // Required
    private int intIP = DEFAULT_INT_IP;
    private String name = "Name Not Set";
    private String macAdress = "MAC address Not Set";

    private Date lastPingDate;
    private DeviceStatus status;

    // ----------------------------------------- Constructors -----------------------------------------
    // Used to create a new device not in the Devices.txt file.
    public Device(String IP, String name, String macAdress) {
        status = new DeviceStatus(IP);
        if(!status.isIPInvalid()){
            this.strIP = IP;
            this.intIP = stripIP(IP);
        }
        if (name != null) {
            this.name = name;
        }
        if (macAdress != null) {
            this.macAdress = macAdress;
        }
    }

    // used to load a device from a line in the Devices.txt file.
    public Device(Scanner lineScanner) {
        lineScanner.useDelimiter("\\|");
        while (lineScanner.hasNext()) {
            String identifyer = lineScanner.next();
            // Prevents errors from an identifyer with no value
            if (!lineScanner.hasNext()) {
                break;
            }
            String value = lineScanner.next();

            if (identifyer.equals("ip")) {
                status = new DeviceStatus(value);
                if(!status.isIPInvalid()){
                    this.strIP = value;
                    this.intIP = stripIP(value);
                }
            }
            if (identifyer.equals("name")) {
                name = value;
            }
            if (identifyer.equals("macAdress")) {
                macAdress = value;
            }
        }
        lineScanner.close();
    }

    // ----------------------------------------- IP Methods -----------------------------------------
    /*
     * Retruns the ip with the fullstops removed
     */
    private int stripIP(String strIP) {
        String strippedIP = "";
        for (int charIndex = 0; charIndex < strIP.length(); charIndex += 1) {
            if (Character.isDigit(strIP.charAt(charIndex))) {
                strippedIP += strIP.charAt(charIndex);
            }
        }
        return Integer.valueOf(strippedIP);
    }

    // ----------------------------------------- Getters -----------------------------------------
    public String getName() {return name;}
    public String getStrIP() {return strIP;}
    public int getIntIP() {return intIP;}
    /*
     * Retruns a button with the approprate method bound to it for the device status.
     */
    private Button getActionButton() {
        // Creates a button of the correct style
        Button action = CustomStyle.cardColourButton(status.getAction(), CustomStyle.MAIN_TEXT_WHITE, 70, 30);
        // Assigns the button the correct method.
        EventHandler<ActionEvent> actionEvent = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                switch (status.getStatusEnum()) {
                    case OFFLINE: // If the device is offline then the user should be able to wake it.
                        wake();
                        break;

                    case ONLINE: // If the device is online then the user should be able to ssh into it.
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

    /*
     * Retruns a button with the ping method bound to it.
     */
    private Button getPingButton() {
        //Creates a button of the correct style.
        Button pingButton = CustomStyle.cardColourButton("Ping", CustomStyle.MAIN_TEXT_WHITE, 70, 30);
        //Binds the ping method to it.
        EventHandler<ActionEvent> actionEvent = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                ping(true);
            }
        };
        pingButton.setOnAction(actionEvent);
        return pingButton;
    }

    /*
     * Retruns a card with the devices information on it.
     * 
     *     * * * * * * * * * * * * * 
     *     *       name        /\  *
     *     *        ip         |c| *
     *     *    lastPingDate   |c| *
     *     *  <action>  <ping> \/  *
     *     * * * * * * * * * * * * * 
     *     c = status colour
     */
    public VBox getCard() {
        Button statusIndicator = CustomStyle.cardColourButton("", status.getColor(), 30, 30);
        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("textLabel");
        HBox nameLabelBox = new HBox(nameLabel);
        nameLabelBox.setAlignment(Pos.CENTER);
        nameLabelBox.setMinWidth(90);
        HBox topRow = new HBox(statusIndicator,nameLabelBox);
        topRow.setPadding(new Insets(10,10,0,10));
        topRow.setSpacing(10);
        topRow.setAlignment(Pos.CENTER_LEFT);


        Label ipLabel = new Label(strIP);
        Label lastPingDateLabel;
        // Makes sure last ping date is not null
        if (lastPingDate != null) { lastPingDateLabel = new Label(CustomStyle.JUST_TIME_FORMAT.format(lastPingDate));} 
        else {lastPingDateLabel = new Label("Last Ping : loading");}

        ipLabel.getStyleClass().add("textLabel");
        lastPingDateLabel.getStyleClass().add("textLabel");

        HBox bottomRow = new HBox(getActionButton(), getPingButton());
        bottomRow.setSpacing(10);
        bottomRow.setAlignment(Pos.CENTER);
        VBox output = CustomStyle.cardBox(new VBox(topRow, ipLabel,lastPingDateLabel,bottomRow), 195, 145);

        output.setAlignment(Pos.CENTER);
        output.setSpacing(0);
        return output;
    }


    // ----------------------------------------- Ping Methods -----------------------------------------
    /*
     * Checks if the time since the last ping is greater than the PING_INTERVAL.
     * Returns true if the lastPingDate is null or the time since last ping is greater than the PING_INTERVAL.
     * Retrusn false if the time since last ping is less than the PING_INTERVAL.
     */
    private boolean checkLastPingDate(){
        //Gets the current time 
        Date currentDate = new Date();
        if (lastPingDate != null) {
            // Subtracts the time since the epoch in milliseonds of the current time from the last ping time.
            Date timeSinceLastPing = new Date(currentDate.getTime() - lastPingDate.getTime());
            // Checsk if this time is less than the PING_INTERVAL.
            if (timeSinceLastPing.getTime() < PING_INTERVAL) {
                return false;}
        }
        return true;
    }   

    /*
     * Sends a ping to the devices ip returns a string of the os's response.
     */
    private String sendPing(int numberToSend, int timout, boolean windows) {
        String pingResult = "";
        String pingCmd;

        // Constructs the ping command depending on the os.
        if (windows) {pingCmd = "ping /n " + numberToSend + " /w " + timout + " " + strIP;} 
        else {pingCmd = "ping -c " + numberToSend + " -w " + timout + " " + strIP;}

        // Attempts to run the command and read the response.
        try {
            Runtime r = Runtime.getRuntime();
            Process p = r.exec(pingCmd);

            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                pingResult += inputLine;
            }
            in.close();

        } catch (IOException e) {
            System.out.println(e);
        }

        return pingResult;
    }

    /*
     * Takes a string and uses a scanner to search for the number of packets reccived.
     * if the number is equal to the numberToReccive then true is returned.
     */
    private boolean checkPingResult(String pingResult, int numberToReceive, boolean windows) {
        Scanner outputScanner = new Scanner(pingResult);
        String prevous = "";
        while (outputScanner.hasNext()) {
            String current = outputScanner.next();
            // The format for the windows ping is : Received = 1,
            // The format for the linux ping is : 1 received
            // This neccesitates the two diffrent search types.
            if (windows) {
                if (current.equals("Received")) {
                    outputScanner.next(); // Skips the equals
                    // Checks if the next thing is the number to receive 
                    if (outputScanner.next().equals(numberToReceive + ",")) {
                        outputScanner.close();
                        return true;
                    } else {
                        outputScanner.close();
                        return false;
                    }
                }
            } else {
                if (current.equals("received")) {
                    // Checks if the prevously scanned string is the number to receive
                    if (prevous.equals(String.valueOf(numberToReceive))) {
                        outputScanner.close();
                        return true;
                    } else {
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

    /*
     * Takes a string and uses a scanner to search for the number of packets reccived.
     * Only returns true if a ping is sent
     */
    @FXML
    public boolean ping(boolean ignoreCoolDown) {
        if (status.isIPInvalid()) {return false;}
        // If the time since the last ping is lesss than the interval and we do care about the cool down.
        if (!checkLastPingDate() && !ignoreCoolDown){return false;}

        lastPingDate = new Date();
        // Sends the ping
        String pingResult = sendPing(PACKETS_TO_SEND, PING_TIMEOUT, PrimaryController.windows);
        // Checks if the right number of packets was reccived
        if (checkPingResult(pingResult, PACKETS_TO_RECCIVE, PrimaryController.windows)) {
            status.sucessfulPing();
        } else {
            status.unsucessfulPing();
        }
        return true;
    }

    @FXML
    public void wake() {
        // send magic packet
        System.out.println("Magic packet sent to : " + strIP);
    }

    @FXML
    public void ssh() {
        // run ssh script
        System.out.println("SSH script run to : " + strIP);
    }

    public String toString() {
        return "strIP : " + strIP + " | intIP : " + intIP + " | name: " + name + " | macAdress : " + macAdress;
    }

    /*
     * The higher the IP the higher rank.
     */
    public int compareTo(Device o) {
        return Integer.compare(this.intIP, o.intIP);
    }

}
