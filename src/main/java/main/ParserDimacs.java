package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * ParserDimacs in Java
 *
 * @author Arefeva Ilona
 * @version 1.0
 */
public class ParserDimacs {

    String countVertex;
    ArrayList<String> coordinatesVertex;

    public ParserDimacs() {
        countVertex = " n = 0";
        coordinatesVertex = new ArrayList<String>();
    }

    public void parse(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String currentLine;
            while ((currentLine = br.readLine()) != null) {
                if (currentLine.startsWith("n ")) {
                    countVertex = currentLine;
                }
                if (Character.isDigit(currentLine.charAt(0))) {
                    coordinatesVertex.add(currentLine);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> coordinatesVertex() {
        return coordinatesVertex;
    }

}