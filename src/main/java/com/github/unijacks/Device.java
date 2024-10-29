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

import java.util.NoSuchElementException;

import java.util.ArrayList;
import java.util.List;

/*
 * Represents a single device. Uniquily identifable by their IP.
 */
public class Device implements Comparable<Device> {
    public final static int PING_INTERVAL = 15000;

    private String strIP; // Required
    private int intIP;
    private String name = "Name unset";
    private String macAdress = "MAC address unset";

    private Date lastPingDate;
    private DeviceStatus status;

    // ----------------------------------------- Constructors -----------------------------------------
    // Used to create a new device not in the Devices.txt file.
    public Device(String IP, String name, String macAdress) {
        setIP(IP);
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
                setIP(value);
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
     * Attempts to set the ip of the Device to the new ip.
     * If this is unsucessful then
     * - strIP is set to "Invalid IP"
     * - intIP is set to 000000000000
     */
    private void setIP(String newIP) {
        if (verifyIP(newIP)) {
            // If the new ip is valid then set it.
            strIP = newIP;
            intIP = stripIP(newIP);
            status = new DeviceStatus(DeviceStatus.statusEnum.LOADING, strIP);
        } else {
            // If the new ip is invalid then set the status to invalid ip.
            strIP = "Invalid IP";
            intIP = 000000000000;
            status = new DeviceStatus(DeviceStatus.statusEnum.INVALIDIP, strIP);
        }
    }

    /*
     * Verifys an ip follows the correct format "000.000.000.000".
     */
    private static boolean verifyIP(String newIP) {
        if (newIP == null) {
            return false;
        } // Checks the ip is not null.
        Scanner newIPScanner = new Scanner(newIP);
        newIPScanner.useDelimiter("\\.");
        List<Integer> numbersList = new ArrayList<>(); // Stores the numbers
        try {
            // Loops through the new ip and reccords the numbers between the fullstops.
            while (newIPScanner.hasNext()) {
                int number = newIPScanner.nextInt();
                // Makes sure the numbers are less than 255
                if (number > 255) {
                    newIPScanner.close();
                    return false;
                }
                numbersList.add(number);
            }
        } catch (NoSuchElementException e) {
            newIPScanner.close();
            return false;
        }
        newIPScanner.close();
        // Makes sure there are 4 numbers
        if (numbersList.size() != 4) {
            return false;
        }
        return true;
    }

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
    public HBox getCard() {
        //-----Left Side Children-----
        Label nameLabel = CustomStyle.cardStyleLabel(name);
        Label ipLabel = CustomStyle.cardStyleLabel(strIP);
        Label lastPingDateLabel;
        // Makes sure last ping date is not null
        if (lastPingDate != null) { lastPingDateLabel = CustomStyle.cardStyleLabel(CustomStyle.PING_DATE_FORMAT.format(lastPingDate));} 
        else {lastPingDateLabel = CustomStyle.cardStyleLabel("Last Ping : loading");}

        TilePane bottomRow = new TilePane(getActionButton(), getPingButton());
        bottomRow.setHgap(15);
        bottomRow.setPadding(new Insets(0, 0, 0, 15));
        //-----Left Side Children-----
        VBox leftSide = new VBox(nameLabel, ipLabel, lastPingDateLabel, bottomRow);
        leftSide.setAlignment(Pos.CENTER);
        leftSide.setMaxWidth(200);

        Button rightSide = CustomStyle.cardColourButton("", status.getColor(), 30, 120);
        HBox output = new HBox(leftSide, rightSide);

        output.setAlignment(Pos.CENTER_LEFT);
        output.setSpacing(5);

        output.setPrefWidth(245);
        output.setPrefHeight(150);
        output.setBackground(new Background(new BackgroundFill(CustomStyle.BACK_GROUND_GREY,
                new CornerRadii(CustomStyle.CARD_CORNER_RADII), Insets.EMPTY)));
        output.setBorder(new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID,
                new CornerRadii(CustomStyle.CARD_CORNER_RADII), new BorderWidths(3))));
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
            if (timeSinceLastPing.getTime() < PING_INTERVAL) {return false;}
        }
        lastPingDate = currentDate;
        return true;
    }   

    /*
     * Sends a ping to the devices ip returns a string of the os's response.
     */
    private String sendPing(int numberToReccive, int timout, boolean windows) {
        String pingResult = "";
        String pingCmd;

        // Constructs the ping command depending on the os.
        if (windows) {pingCmd = "ping /n " + numberToReccive + " /w " + timout + " " + strIP;} 
        else {pingCmd = "ping -c " + numberToReccive + " -w " + timout + " " + strIP;}

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
        if (status.invalidIP()) {return false;}
        // If the time since the last ping is lesss than the interval and we do care about the cool down.
        if (!checkLastPingDate() && !ignoreCoolDown){return false;}
        // Sends the ping
        String pingResult = sendPing(2, 1, PrimaryController.windows);
        // Checks if the right number of packets was reccived
        if (checkPingResult(pingResult, 2, PrimaryController.windows)) {
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
