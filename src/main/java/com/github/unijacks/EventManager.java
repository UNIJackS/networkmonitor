package com.github.unijacks;

import java.util.Queue;
import java.util.PriorityQueue;

import java.io.File;
import java.io.FileNotFoundException;

import java.util.Scanner;

import javafx.scene.layout.FlowPane;

import java.util.Date;  
import java.util.Map;


public class EventManager {
    private Queue<Event> events; //organised newest to oldest

    public EventManager(){
        System.out.println("Events loading ...");
        //Clears the queue
        events = new PriorityQueue<>();
        //Makes sure the evnets folder exists
        new File("/events").mkdir();
        //Loads all the events from files.
        loadFromFile();
        System.out.println("Events loaded sucessfully");
    }

    //Adds an event to the queue and saves it as a file.
    public void addEvent(Event.eventType type,String ip){
        events.add(new Event(type,new Date(),ip));
    }

    public void updateFlowPane(FlowPane flowPlaneInput,Map<String,Device> devicesMap){
        flowPlaneInput.getChildren().clear();
        Queue<Event> eventsCopy = new PriorityQueue<>(events);
        while(!eventsCopy.isEmpty()){
            Event currentEvent = eventsCopy.poll();
            flowPlaneInput.getChildren().add(currentEvent.getCard(devicesMap));
        }
    }


    /*
     * Loads the devices from the DeviceList.txt file.
     */
    public void loadFromFile(){
        // Clears the devices set.
        events.clear();
        File evnentsDirectory = new File("events");
        File[] eventFiles = evnentsDirectory.listFiles(); 
        for(File currentFile : eventFiles){
            events.add(new Event(currentFile));
        }
        
    }

    /*
     * Prints all the loaded events
     */
    public void printAll(){
        Queue<Event> eventsCopy = new PriorityQueue<>(events);
        while(!eventsCopy.isEmpty()){
            Event currentEvent = eventsCopy.poll();
            System.out.println(currentEvent);
        }
    }

}
