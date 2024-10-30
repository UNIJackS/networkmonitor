# Network Monitor Repository 
Hi, this is my repository for a network monitor written in Java. I designed it to run on a Raspberry Pi Zero.

## Setup
For the jar file to run it must be placed in a directory with a Devices.txt file and a directory called events.
The program will store events in the events directory. 
The program will load the devices to monitor from the Devices.txt file. 

## Devices.txt File
- This is used to load the devices to monitor.
- Each line corresponds to a different device and should follow the following format.
- Format: Identifyer|Value|Identifyer|Value
- Example: ip|8.8.8.8|name|Google.
- In this example, the identifiers are ip and name with corresponding values of 8.8.8.8 and Google respectively 
- Possible Identifiers are "ip","name" and "macAdress".
- The order of Identifiers and corresponding values does not matter. However, an Identifier and its value must follow each other.
- The macAdress value is currently not used.
- The verticle line "|" delimiter allows for spaces " "in names. However, this means that names can not contain the "|" character.

## Events Directory 
- This is used to store the events generated by the program.
- Events could be a device going offline or online.
- File Contents Format: ip|ip of device|type|event type enum|dateOccurred|DD/MM/YYYY HH:MM:SS
- File Conennts Example: ip|8.8.8.8|type|CAME_ONLINE|dateOccurred|29/10/2024 14:48:01
- The **names of the files are NOT RELEVANT** as the program loads all the files in the events directory regardless of name. However, the generated event file names do follow a pattern.
- Windows does not allow for verticle lines "|" in names so they are replaced with underscores "\_".Navigating files with spaces in the name through a CLI is a pain so the spaces " " in the date are also replaced with underscores "\_".
- File Name Format: ip=ipOfDevice_type=eventTypeEnum_dateOccured=date.txt
- File Name Example: ip=8.8.8.8_type=CAME_ONLINE_dateOccurred=29_10_2024_14_48_01.txt

