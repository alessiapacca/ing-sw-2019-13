package Map;

import Exceptions.NoSuchSquareException;

import java.util.*;
import java.io.*;       //maybe less
import java.util.Scanner;

public class Map {

    private ArrayList<SpawnPoint> spawnPoints;
    private ArrayList<ArrayList<SquareAbstract>> squares;
    private ArrayList<Room> rooms;


    public Map(int mapNum) {
        String path;
        switch(mapNum) {

            case 1:
                path = "map11.txt";
                break;
            case 2:
                path = "map12.txt";
                break;
            case 3:
                path = "map21.txt";
                break;
            case 4:
                path = "map22.txt";
                break;
            default:
                path = "map11.txt";
        }
        try(Scanner scanner = new Scanner(new File(path))){
            List<String> list = new ArrayList<>();

            while(scanner.hasNextLine()){
                list.add(scanner.nextLine());
            }

            squares = new ArrayList<>();
            for(int i=0; i<list.size(); i++){
                squares.add(new ArrayList<SquareAbstract>());
            }

            int row = 0;
            int col;
            char c;
            while(row < list.size()){
                col = 0;
                while(col < list.get(row).length()){
                    c = list.get(row).charAt(col);
                    if(c=='R'||c=='B'||c=='Y'||c=='G'||c=='W'||c=='P') {
                        SpawnPoint tempSquare = new SpawnPoint(row/2+1, col/2+1, c);
                        squares.get(row).add(tempSquare);
                        spawnPoints.add(tempSquare);
                    }
                    else if(c=='r'||c=='b'||c=='y'||c=='g'||c=='w'||c=='p'){
                        squares.get(row).add(new Square(row/2+1, col/2+1, c));
                    }
                    else if(c==' ' && row%2==0 && col%2==0){
                        squares.get(row).add(null);
                    }
                    col++;
                }
                row++;
            }

            //now we're gonna link all the squares
            row = 0;
            while(row < list.size()){
                col = 0;
                while(col < list.get(row).length()){
                    c = list.get(row).charAt(col);
                    if(c=='-'){
                        squares.get(row/2).get(col/2).seteSquare(squares.get(row/2).get(col/2+1));
                        squares.get(row/2).get(col/2+1).setwSquare(squares.get(row/2).get(col/2));
                    }
                    else if(c=='|'){
                        squares.get(row/2).get(col/2).setsSquare(squares.get(row/2+1).get(col/2));
                        squares.get(row/2+1).get(col/2).setnSquare(squares.get(row/2).get(col/2));
                    }
                    col++;
                }
                row++;
            }
        } catch(FileNotFoundException e){
            e.printStackTrace();
        }
    }

    public ArrayList<SpawnPoint> getSpawnPoints(){
        return (ArrayList<SpawnPoint>) spawnPoints.clone();
    }

    public static SquareAbstract getSquareFromXY(int x, int y) throws NoSuchSquareException {

        try {
            return squares.get(x).get(y);
        }
        catch (IndexOutOfBoundsException e){
            throw new NoSuchSquareException();                                    //TODO throw custom exception
        }
    }

    public static List<SquareAbstract> getSquaresWithSameX(){
        for(int i = 0; i<squares.size(); i++){
            for(int j = 0; j<squares.get(i).size(); j++){

            }
        }
        return;
    }



}