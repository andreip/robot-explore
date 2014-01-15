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
		/* Set current position as explored and safe. */
		this.map.setType(this.crtPos, Grid.FREE);

		/* Do a DFS exploration from the starting point and complete the map. */
		Stack<Coord> moves = new Stack<>();
		moves.add(crtPos);
		boolean[][] visited = new boolean[this.grid.height][this.grid.width];

		while (!moves.isEmpty()) {
			Coord pos = moves.pop();

			/* Check what we can observe from the current position (smell). */
			if (smellSwamp(pos)) {
				/* Try and deduce where the swamp is. We can do this only if
				 * we know all the spots except the one with swamp.
				 */
				detectSwamp(pos);
				continue;
			}

			/* If pos is not labeled as discovered we will go on and search
			 * for his children too.
			 */
			if (!visited[pos.x][pos.y]) {
				visited[pos.x][pos.y] = true; 
				moves.addAll(this.map.getAllMoves(pos));
			}

			/* If we could not move to the new not-visited position,
			 * this means that one is a wall.
			 */
			if (!this.grid.updateCurrentPos(pos))
				this.map.setType(pos, Grid.WALL);
			else
				this.map.setType(pos, Grid.FREE);
		}

		return map;
	}

	private boolean smellSwamp(Coord pos) {
		List<Coord> moves = this.grid.getAllMoves(pos);
		for (Coord c : moves)
			if (this.grid.isSwamp(c))
				return true;
		return false;
	}

	/* Check the map over what we've discovered. */
	private void detectSwamp(Coord pos) {
		List<Coord> moves = this.grid.getAllMoves(pos);

		int unknown = moves.size();
		Coord unknownCoord = null;
		for (Coord c : moves)
			if (this.map.isWall(c) || this.map.isFree(c))
				unknown--;
			else
				unknownCoord = c;

		/* If we've know all but one coord we can deduce that that
		 * one is a swamp, because we can feel the smell of a swamp
		 * near this position.
		 */
		if (unknown == 1)
			this.map.setType(unknownCoord, Grid.SWAMP);
	}
}
