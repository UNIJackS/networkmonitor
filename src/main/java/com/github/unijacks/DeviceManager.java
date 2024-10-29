package com.github.unijacks;


import java.util.Queue;
import java.util.PriorityQueue;
import java.util.Map;
import java.util.HashMap;

import java.util.Scanner;

import javafx.scene.layout.FlowPane;
import java.io.File;
import java.io.FileNotFoundException;

public class DeviceManager {
    private Queue<Device> devices;
    private Map<String, Device> devicesMap;

    public DeviceManager() {
        System.out.println("Loading devices ...");
        // Intalises the priority queue
        devices = new PriorityQueue<>();
        // Loads all the devices into the priority queue from file
        loadFromFile();
        // Loads all the devices from the priority queue into the map
        devicesMap = new HashMap<>();

        System.out.println("Devices loaded sucessfully");
    }

    public Map<String, Device> getDevicesMap() {
        if (devicesMap.size() == 0) {
            Queue<Device> devicesCopy = new PriorityQueue<>(devices);
            while (!devicesCopy.isEmpty()) {
                Device currentDevice = devicesCopy.poll();
                devicesMap.put(currentDevice.getStrIP(), currentDevice);
            }
        }
        return devicesMap;
    }

    public void updateFlowPane(FlowPane flowPlaneInput) {
        flowPlaneInput.getChildren().clear();
        Queue<Device> devicesCopy = new PriorityQueue<>(devices);
        while (!devicesCopy.isEmpty()) {
            Device currentDevice = devicesCopy.poll();
            flowPlaneInput.getChildren().add(currentDevice.getCard());
        }
    }

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
        devices.clear();
        // Creates a scanner from the DeviceList.txt file
        Scanner linesInFileScanner = readFile("DeviceList.txt");
        // Loops through the lines in the file.
        while (linesInFileScanner.hasNextLine()) {
            String line = linesInFileScanner.nextLine(); // Gets the nextline from the DeviceList.txt file scanner
            devices.add(new Device(new Scanner(line)));
        }
        linesInFileScanner.close();
    }

    public void printAll() {
        Queue<Device> devicesCopy = new PriorityQueue<>(devices);
        while (!devicesCopy.isEmpty()) {
            System.out.println(devicesCopy.poll());
        }
    }

    public void pingAll() {
        Queue<Device> devicesCopy = new PriorityQueue<>(devices);
        while (!devicesCopy.isEmpty()) {
            Device currentDevice = devicesCopy.poll();
            if(currentDevice.ping(false)){
                break;
            }
        }
    }

}
