import java.util.List;
import java.util.Stack;

import util.Coord;

public class Robot {
	private Grid grid;
	/* The map the robot has to discover. */
	private Grid map;
	private Coord crtPos;

	public Robot(Grid grid) {
		this.grid = grid;
		this.map = new Grid(this.grid.height, this.grid.width);
		this.crtPos = this.grid.getCurrentPos();
	}
	
	public Grid explore() {
		Coord startPos = this.crtPos;

		/* Set current position as explored and safe. */
		setTypeAndPrintMap(this.map, this.crtPos, Grid.FREE);

		boolean mapChanged = true;
		while (mapChanged) {
			/* Set the starting position and do DFS exploration. */
			this.crtPos = startPos;
			mapChanged = do_explore();
		}

		return map;
	}

	public boolean do_explore() {
		/* Keep a record of whether we discovered new fields on the map, so we
		 * may know when to stop trying.
		 */
		boolean mapChanged = false;

		/* Do a DFS exploration from the starting point and complete the map. */
		Stack<Coord> moves = new Stack<>();
		moves.add(crtPos);
		boolean[][] visited = new boolean[this.grid.height][this.grid.width];

		while (!moves.isEmpty()) {
			Coord pos = moves.pop();

			/* If we could not move to the new not-visited position,
			 * this means that one is a wall.
			 */
			if (!this.updateCurrentPos(pos)) {
				mapChanged |= setTypeAndPrintMap(this.map, pos, Grid.WALL);
				/* Cannot add neightbors w/ the position is a wall. */
				continue;
			} else
				mapChanged |= setTypeAndPrintMap(this.map, pos, Grid.FREE);


			/* Check what we can observe from the current position (smell). */
			if (smellSwamps(pos) > 0) {
				/* Try and deduce where the swamp is. We can do this only if
				 * we know all the spots except the one with swamp.
				 */
				boolean canMove = checkSwamps(pos);
				/* If the detectSwamp function tells us we have not identified
				 * all the swamps, then we cannot choose any neighbors, so we'll
				 * leave this position.
				 */
				if (!canMove)
					continue;
			}

			/* If pos is not labeled as discovered we will go on and search
			 * for his children too.
			 * Or if the move is a backwards move, it's fine.
			 */
			visited[pos.x][pos.y] = true;
			List<Coord> nextMoves = this.grid.getAllMoves(pos);
			for (Coord c : nextMoves) {
				/* Don't re-add visited positions. */
				if (visited[c.x][c.y] && !this.map.isSwamp(c))
					continue;
				/* If pos(x,y), c(x+1,y) then rev will be rev(x-1,y).
				 * Similar in reverse for the rest of them.
				 */
				moves.push(c);
			}
		}

		return mapChanged;
	}

	/* Set type only if it's not already set and print map. */
	private boolean setTypeAndPrintMap(Grid g, Coord pos, char type) {
		/* Set the new type only if the old type is unknown. */
		if (!g.isUnknown(pos))
			return false;

		if (g.getType(pos) == type)
			return false;
		g.setType(pos, type);
		printMap();
		return true;
	}

	/* The grid won't update the current position if that
	 * position is a wall.
	 */
	private boolean updateCurrentPos(Coord newPos) {
		if (this.grid.isWall(newPos))
			return false;
		this.crtPos = newPos;
		return true;
	}

	private void printMap() {
		System.out.println(this.map);
	}

	/* The smell has intensities, so it can differentiate
	 * between a lower number of swamps and a higher one.
	 */
	private int smellSwamps(Coord pos) {
		int nrSwamps = 0;

		List<Coord> moves = this.grid.getAllMoves(pos);
		for (Coord c : moves)
			if (this.grid.isSwamp(c))
				nrSwamps++;
		return nrSwamps;
	}

	/* Check the map over what we've discovered.
	 * Returns true if we could not find all the swamps
	 * and we're still uncertain where it might be
	 * (so if the next move is risky or not)
	 */
	private boolean checkSwamps(Coord pos) {
		/* A list of all possible moves. */
		List<Coord> moves = this.grid.getAllMoves(pos);
		/* The number of swamps detected through smelling. */
		int nrSwamps = smellSwamps(pos);

		/* Check how many swamps we've identified. */
		int foundSwamps = 0;
		for (Coord c : moves)
			if (this.map.isSwamp(c))
				foundSwamps++;

		/* If no more unidentified swamps exist, we can proceed
		 * with our exploration.
		 */
		if (foundSwamps == nrSwamps)
			return true;

		/* If we've got one unknown swamp and one unknown cell in total,
		 * than that's a swamp.
		 * Else we can't say a thing, it's risky.
		 */
		int unknown = 0;
		Coord unknownCoord = null;
		for (Coord c : moves)
			if (this.map.isUnknown(c)) {
				unknown++;
				unknownCoord = c;
			}

		if (unknown > 1)
			return false;
		/* The unknown is a swamp. */
		if (unknown == 1)
			setTypeAndPrintMap(this.map, unknownCoord, Grid.SWAMP);
		return true;
	}
}
