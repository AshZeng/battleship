package world;

import java.util.ArrayList;
import java.util.List;

import world.World.Coordinate;

/**
 * For the game battleship.
 * A class to represent an opponent's state.
 * It holds all the information about the opponent, 
 * gathered and updated during the game
 * @author patstockwell
 *
 */
public class OtherWorld {
	//this first array is populated when making a guess
    public List<Coordinate> allShots = new ArrayList<>();
    //these second three arrays are populated when a response is received
    public List<Coordinate> hits = new ArrayList<>();
    public List<Coordinate> misses = new ArrayList<>();
    public List<Ship> shipsSunk = new ArrayList<>();
}
