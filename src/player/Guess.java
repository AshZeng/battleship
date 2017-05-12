package player;

import world.World;
import world.World.Coordinate;

/**
 * This class defines the format of a guess
 *
 * @author Jeffrey, Youhan
 */
public class Guess {
    /** row of cell to fire at. */
    public int row = 0;
    /** column of cell to fire at. */
    public int column = 0;

    /**
     * Prints out guess information.
     */
    @Override
    public String toString() {
        return "guesses/fires at row " + row + " column " + column + '.';
    }
    
    public Coordinate createCoordinate(){
    	World w = new World();
    	Coordinate c = w.new Coordinate();
    	c.row = this.row;
    	c.column = this.column;
    	return c;
    }
}
