package com.github.unijacks;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.text.SimpleDateFormat;  
/*
 * Used to put all the style constants in one place.
 */
public class CustomStyle {
    //Date Formats
    public final static SimpleDateFormat HUMAN_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");  
    public final static SimpleDateFormat FILE_NAME_DATE_FORMAT = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss"); 
    public final static SimpleDateFormat PING_DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");  

    //Fonts
    public final static Font Header_FONT = new Font(60);
    public final static Font INFO_FONT = new Font(20);
    public final static Font CARD_FONT = new Font(20);

    //Colours 
    public final static Color BACK_GROUND_GREY = Color.web("2d3436"); // Dracula Orchid - Grey
    public final static Color MAIN_TEXT_WHITE = Color.web("ffffff"); // white


    public final static Color ERROR_BLUE = Color.web("0984e3"); // Electron Blue - dark blue

    public final static Color ONLINE_GREEN = Color.web("00b894"); // Mint Green - dark green
    public final static Color UNREACHABLE_YELLOW = Color.web("fdcb6e"); // Bright yarrow - yellow
    public final static Color OFFLINE_ORANGE = Color.web("e17055"); // OrangeVille -  orange
    public final static Color LOADING_GREY = Color.web("2d3436"); // 

    public final static Color INVALID_IP_PINK = Color.web("e84393"); // Prunus Avium - Pink
    public final static Color MAGIC_PACKET_PURPLE = Color.web("6c5ce7"); // Exodius Fruit - purple

    public final static Color BUTTON_GREY = Color.web("636e72"); // 

    //----------------------------------------- Card Methods -----------------------------------------
    public final static int CARD_CORNER_RADII = 15;

    public static Button cardColourButton (String text, Color color,double width, double height ){
        Button output = new Button(text);
        CornerRadii buttonCornerRadii = new CornerRadii(15);
        Background buttonBackground = new Background(new BackgroundFill(color, buttonCornerRadii, Insets.EMPTY));
        output.setBackground(buttonBackground);
        output.setBorder(new Border(new BorderStroke(Color.WHITE,BorderStrokeStyle.SOLID,buttonCornerRadii,new BorderWidths(3))));
        output.setMinHeight(height);
        output.setMinWidth(width);
        return output;
    }

    public static Label cardStyleLabel(String text){
        Label output = new Label(text);
        output.setFont(CustomStyle.CARD_FONT);
        output.setTextFill(CustomStyle.MAIN_TEXT_WHITE);
        return output;
    }


}
