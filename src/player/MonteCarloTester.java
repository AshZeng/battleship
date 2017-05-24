package player;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import world.*;
import world.OppWorld.cellState;
import world.World.Coordinate;

public class MonteCarloTester {
	
	GreedyGuessPlayer g;
	World w;
	MonteCarloGuessPlayer m;
	
	@Before
	public void setup(){
		w = new World();
		g = new GreedyGuessPlayer();
		m = new MonteCarloGuessPlayer();
		w.numRow = 10;
		w.numColumn = 10;
		m.initialisePlayer(w);
		m.opponentsWorld.oppWorld[0][0] = cellState.Miss;
		m.opponentsWorld.oppWorld[3][3] = cellState.Hit;
		m.opponentsWorld.oppWorld[9][9] = cellState.Miss;
	}

	@Test
	public void test() {
		printBoard(m.opponentsWorld.size5AircraftCarrier.ShipConfigurationCounts);
		System.out.println();
		printBoard(m.opponentsWorld.size4Battleship.ShipConfigurationCounts);
		System.out.println();
		printBoard(m.opponentsWorld.size3Submarine.ShipConfigurationCounts);
		System.out.println();
		printBoard(m.opponentsWorld.size3Cruiser.ShipConfigurationCounts);
		System.out.println();
		printBoard(m.opponentsWorld.size2Destroyer.ShipConfigurationCounts);
		System.out.println();
		m.opponentsWorld.total.ShipConfigurationCounts[9][1] = 79;
		printBoard(m.opponentsWorld.total.ShipConfigurationCounts);
		System.out.println();
		assertTrue(true);
	}
	
	@Test
	public void notContainedInGuessList1(){
		Coordinate co = w.new Coordinate();
		co.row = 3;
		co.column = 3;
		Coordinate coa = w.new Coordinate();
		coa.row = 1;
		coa.column = 6;
		Guess gs = new Guess();
		gs.row = 3;
		gs.column = 3;
		List<Guess> list = new ArrayList<>();
		list.add(gs);
		assertFalse(m.notContainedInGuessList(co, list));
	}
	
	@Test
	public void notContainedInGuessList2(){
		Coordinate co = w.new Coordinate();
		co.row = 2;
		co.column = 8;
		Coordinate coa = w.new Coordinate();
		coa.row = 7;
		coa.column = 7;
		Guess gs = new Guess();
		gs.row = 3;
		gs.column = 3;
		List<Guess> list = new ArrayList<>();
		list.add(gs);
		assertTrue(m.notContainedInGuessList(co, list));
	}
	
	@Test
	public void sameAs1(){
		Coordinate co = w.new Coordinate();
		Guess gs = new Guess();
		co.row = 3;
		co.column = 3;
		gs.row = 3;
		gs.column = 3;
		assertTrue(m.sameAs(co, gs));
	}
	
	@Test
	public void sameAs2(){
		Coordinate co = w.new Coordinate();
		Guess gs = new Guess();
		co.row = 5;
		co.column = 5;
		gs.row = 1;
		gs.column = 3;
		assertFalse(m.sameAs(co, gs));
	}
	
	@Test
	public void getShipConfigurationCountForOneCell1(){
		assertTrue(m.getShipConfigurationCountForOneCell(0, 0, 4) == 2);
	}
	
	
	@Test
	public void getLowerBoundForColumn1(){
		assertTrue(m.getLowerBoundForColumn(3, 4, 4) == 3);
	}
	@Test
	public void getLowerBoundForColumn2(){
		assertTrue(m.getLowerBoundForColumn(6, 0, 2) == -1);
	}
	@Test
	public void getUpperBoundForColumn1(){
		assertTrue(m.getUpperBoundForColumn(3, 2, 4) == 3);
	}
	@Test
	public void getUpperBoundForColumn2(){
		assertTrue(m.getUpperBoundForColumn(6, 9, 2) == 10);
	}
	@Test
	public void getUpperBoundForRow1(){
		assertTrue(m.getUpperBoundForRow(1, 3, 5) == 3);
	}
	@Test
	public void getUpperBoundForRow2(){
		assertTrue(m.getUpperBoundForRow(8, 9, 5) == 9);
	}
	@Test
	public void getLowerBoundForRow1(){
		assertTrue(m.getLowerBoundForRow(1, 0, 3) == 0);
	}
	@Test
	public void getLowerBoundForRow2(){
		assertTrue(m.getLowerBoundForRow(2, 2, 5) == -1);
	}
	@Test
	public void isObstacle1(){
		assertTrue(m.isObstacle(0, 0));
	}
	@Test
	public void isObstacle2(){
		assertTrue(m.isObstacle(3, 3));
	}
	@Test
	public void isObstacle3(){
		assertFalse(m.isObstacle(1, 0));
	}
	@Test
	public void isOutOfBounds1(){
		assertTrue(m.isOutOfBounds(-1, 4));
	}
	@Test
	public void isOutOfBounds2(){
		assertTrue(m.isOutOfBounds(12, 4));
	}
	@Test
	public void isOutOfBounds3(){
		assertTrue(m.isOutOfBounds(3, -4));
	}
	@Test
	public void isOutOfBounds4(){
		assertTrue(m.isOutOfBounds(6, 54));
	}
	@Test
	public void isOutOfBounds5(){
		assertFalse(m.isOutOfBounds(6, 4));
	}
	@Test
	public void isOutOfBounds6(){
		assertFalse(m.isOutOfBounds(9, 0));
	}
	@Test
	public void isOutOfBounds7(){
		assertTrue(m.isOutOfBounds(10, 0));
	}
	@Test
	public void isOutOfBounds8(){
		assertTrue(m.isOutOfBounds(0, 10));
	}
	
	private void printBoard(int[][] board) {
		for(int x = 0; x < 10; ++x){
			for(int y = 0; y < 10; ++y) {
				System.out.format("%02d ", board[x][y]);
			}
			System.out.println();
		}	
	}
}
