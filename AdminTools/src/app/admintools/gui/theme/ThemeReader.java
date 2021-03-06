/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.admintools.gui.theme;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author lukak
 */
public class ThemeReader {

    public static ArrayList<String> listThemes() {
        ArrayList<String> themeDir = new ArrayList<String>(); //Netbeans takes a shite than complaians
        File[] themes = new File("Assets/Themes/").listFiles(); //Get a array of all files in the themes folder
        for (File theme : themes) { //Go through them all
            if (theme.isDirectory()) {
                themeDir.add(theme.getName());  //Add its name to the returning arraylist if its a directory
            }
        }
        return themeDir;
    }

    public static String getCss(String themeName) {
        return "file:Assets/Themes/" + themeName + "/style.css";
    }

    public static String getConsoleColor(String themeName) throws FileNotFoundException {
        File metaFile = new File("Assets/Themes/" + themeName + "/consolecolor.txt");
        Scanner reader = new Scanner(metaFile);
        String color = reader.nextLine();
        reader.close();
        return color;
    }
}
