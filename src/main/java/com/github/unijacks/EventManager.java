package com.github.unijacks;

import java.util.Queue;
import java.util.PriorityQueue;

import java.io.File;

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
        //new File("/home/jack/Desktop/events").mkdir();
        //Loads all the events from files.
        loadFromFile();
        System.out.println("Events loaded sucessfully");
    }

    public int getNumberOfEvents(){return events.size();}

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
     * Loads the events from the events directory.
     * Returns true if new events were loaded.
     * Returns false if the same events were loaded.
     */
    public boolean loadFromFile(){
        //Stores all the events that are loaded.
        Queue<Event> tempEvents = new PriorityQueue<>() ; 

        //Loads all the events from the events folder.
        File evnentsDirectory = new File("events");
        File[] eventFiles = evnentsDirectory.listFiles(); 
        for(File currentFile : eventFiles){
            tempEvents.add(new Event(currentFile));
        }

        //Checks if there are any new events.
        if(!comparePriorityQueue(events,tempEvents)){
            // If there are then the global events queue is updated
            events = new PriorityQueue<>(tempEvents);
            return true;
        }else{
            return false;
        }
    }

    // Returns true if all elements equal each other.
    private static boolean comparePriorityQueue(Queue<Event> queueOne, Queue<Event> queueTwo){
        if(queueOne.size() != queueTwo.size()){ return false;} // Returns false if the two queuse are diffrent sizes

        Queue<Event> copyQueueOne = new PriorityQueue<>(queueOne);
        Queue<Event> copyQueueTwo = new PriorityQueue<>(queueTwo);

        while(!copyQueueOne.isEmpty()){
            if(!copyQueueOne.poll().equals(copyQueueTwo.poll())){
                return false;
            }
        }
        return true;
        
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
