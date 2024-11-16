package com.github.unijacks;import java.util.Queue;
import java.util.PriorityQueue;
import java.util.Map;
import java.util.HashMap;

import java.util.Scanner;

import javafx.scene.layout.FlowPane;
import java.io.File;
import java.io.FileNotFoundException;

import java.util.Collections;
/*
 * Manages a set of devices.
 */
public class DeviceManager {
    private Queue<Device> devicesQueue;
    private Map<String, Device> devicesMap;

    private Queue<Device> devicesNotPingedQueue = new PriorityQueue<>();

    /*
     * Loads all the devices and provices a way to manage them
     */
    public DeviceManager() {
        System.out.println("Loading devices ...");
        // Intalises the priority queue and hashmap.
        devicesQueue = new PriorityQueue<>();
        devicesMap = new HashMap<>();
        // Loads devices from Devices.txt.
        loadFromFile();
        System.out.println("Devices loaded sucessfully");
    }

    //Returns a unmodifiable map of the devices.
    public Map<String, Device> getDevicesMap() {return Collections.unmodifiableMap(devicesMap);}

    public int getNumberOfDevices(){return devicesMap.size();}


    /*
     * Takes a flow plane and updates its children to contain the cards of all the devices.
     */
    public void updateFlowPane(FlowPane flowPlaneInput) {
        flowPlaneInput.getChildren().clear();
        Queue<Device> devicesCopy = new PriorityQueue<>(devicesQueue);
        while (!devicesCopy.isEmpty()) {
            Device currentDevice = devicesCopy.poll();
            flowPlaneInput.getChildren().add(currentDevice.getCard());
        }
    }

    /*
     * Creates a scanner of a file at a given path.
     */
    public static Scanner readFile(String pathName) {
        try {
            File myObj = new File(pathName);
            Scanner myReader = new Scanner(myObj);
            return myReader;
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred. When trying to find :" + pathName);
            e.printStackTrace();
            return null;
        }
    }

    // Loads the devices from the DeviceList.txt file.
    public void loadFromFile() {
        // Clears the devices set.
        devicesQueue.clear();
        devicesMap.clear();
        // Creates a scanner from the DeviceList.txt file
        Scanner linesInFileScanner = readFile("DeviceList.txt");
        // Loops through the lines in the file.
        while (linesInFileScanner.hasNextLine()) {
            // Creates a new device from a scanner of the line in the file.
            Device newDevice = new Device(new Scanner(linesInFileScanner.nextLine()));
            devicesQueue.add(newDevice);
            devicesMap.put(newDevice.getStrIP(),newDevice);
        }
        linesInFileScanner.close();
        System.out.println(this);
    }

    /*
     * Prints all the devices currently loaded to the terminal.
     */
    public void printAll() {
        Queue<Device> devicesCopy = new PriorityQueue<>(devicesQueue);
        while (!devicesCopy.isEmpty()) {
            System.out.println(devicesCopy.poll());
        }
    }

    /*
     * Pings all the devices currently loaded.
     */
    public void pingAll() {
        // This could occure if an attempt to ping all the devices 
        if(devicesNotPingedQueue.size() == 0){
            devicesNotPingedQueue = new PriorityQueue<>(devicesQueue);
        }
        while (!devicesNotPingedQueue.isEmpty()) {
            Device currentDevice = devicesNotPingedQueue.poll();
            // Ping returns true if a ping attempt is made.
            // The device could not be pinged if the PING_INTERVAL has not elapsed since the last ping (cooldown).
            boolean pingAttemptMade = currentDevice.ping(false);
            // So if a device is pinged then the loop is broken.
            // This leads to only one ping attempt being made per call.
            // This is done to space out pings as they take quite a long time.
            if(pingAttemptMade){break;}
        }
    }


    // Checks if any status changes have occured since the last check.
    public boolean checkForDeviceChanges(){
        Queue<Device> devicesCopy = new PriorityQueue<>(devicesQueue);
        while (!devicesCopy.isEmpty()) {
            Device currentDevice = devicesCopy.poll();
            if(currentDevice.needsUpdate()){
                return true;
            }
        }
        return false;
    }



    public String toString() {
        String output = "";
        Queue<Device> devicesCopy = new PriorityQueue<>(devicesQueue);
        while (!devicesCopy.isEmpty()) {
            Device currentDevice = devicesCopy.poll();
            output += currentDevice + " \n";
        }
        return output;
    }

}
