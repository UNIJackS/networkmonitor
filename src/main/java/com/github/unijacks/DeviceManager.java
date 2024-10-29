package com.github.unijacks;


import java.util.Queue;
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
    private void loadFromFile() {
        // Clears the devices set.
        devicesQueue.clear();
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
        Queue<Device> devicesCopy = new PriorityQueue<>(devicesQueue);
        while (!devicesCopy.isEmpty()) {
            Device currentDevice = devicesCopy.poll();
            // Ping only returns true when it actually attempts to ping a device.
            // The loop is broken when we actually attempt ping a device to space out the device pings.
            if(currentDevice.ping(false)){break;}
        }
    }

}
