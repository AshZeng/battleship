package player;

public enum Direction {
	NORTH, SOUTH, EAST, WEST;
	
	private static Direction[] directions = {NORTH, SOUTH, EAST, WEST};
	
	/**
	 * snippet suggested on StackOverflow
	 * https://stackoverflow.com/questions/17006239/whats-the-best-way-to-implement-next-and-previous-on-an-enum-type
	 * @return
	 */
    public Direction next()
    {
        return directions[(this.ordinal()+1) % directions.length];
    }
}
