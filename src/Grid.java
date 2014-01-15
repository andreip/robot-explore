import java.util.LinkedList;
import java.util.List;

import util.Coord;

public class Grid {
	public int height;
	public int width;
	public char[][] grid;
	
	/* Current positions on the map for robot. */
	private Coord crtPos = null;

	public static final char WALL = '1';
	public static final char SWAMP = 'S';
	public static final char FREE = '0';
	public static final char UNKNOWN = '?';

	public Grid(int h, int w, char[][] g) {
		height = h;
		width = w;
		grid = g;
	}
	public Grid(int h, int w) {
		this(h, w, new char[h][w]);
		setGrid(UNKNOWN);
	}

	public Coord getCurrentPos() {
		if (this.crtPos == null)
			for (int i = 0; i < height; ++i)
				for (int j = 0; j < width; ++j)
					if (grid[i][j] == 'x')
						this.crtPos = new Coord(i,j);
		return this.crtPos;
	}

	public Boolean isSwamp(int i, int j) {
		return this.grid[i][j] == SWAMP;
	}
	public Boolean isSwamp(Coord c) {
		return this.isSwamp(c.x, c.y);
	}

	public Boolean isWall(int i, int j) {
		return this.grid[i][j] == WALL;
	}
	public Boolean isWall(Coord c) {
		return this.isWall(c.x, c.y);
	}

	public Boolean isFree(int i, int j) {
		return this.grid[i][j] == FREE;
	}
	public Boolean isFree(Coord c) {
		return this.isFree(c.x, c.y);
	}

	public Boolean isUnknown(Coord c) {
		return this.grid[c.x][c.y] == UNKNOWN;
	}

	public void setType(Coord c, char type) {
		grid[c.x][c.y] = type;
	}

	public char getType(Coord c) {
		return grid[c.x][c.y];
	}

	/* Set every cell from grid. */
	public void setGrid(char type) {
		for (int i = 0; i < height; ++i)
			for (int j = 0; j < width; ++j)
				grid[i][j] = type;
	}

	public Coord up(Coord c) {
		return new Coord(c.x + 1, c.y);
	}
	public Coord down(Coord c) {
		return new Coord(c.x - 1, c.y);
	}
	public Coord left(Coord c) {
		return new Coord(c.x, c.y - 1);
	}
	public Coord right(Coord c) {
		return new Coord(c.x, c.y + 1);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < height; ++i) {
			for (int j = 0; j < width; ++j)
				sb.append(grid[i][j] + " ");
			sb.append('\n');
		}
		return sb.toString();
	}

	/* Returns a list of all possible moves on the map from
	 * current position.
	 */
	public List<Coord> getAllMoves(Coord currentCoord) {
		List<Coord> coords = new LinkedList<>();

		if (isPermitted(up(currentCoord)))
			coords.add(up(currentCoord));
		if (isPermitted(down(currentCoord)))
			coords.add(down(currentCoord));
		if (isPermitted(left(currentCoord)))
			coords.add(left(currentCoord));
		if (isPermitted(right(currentCoord)))
			coords.add(right(currentCoord));
		return coords;
	}

	private Boolean isPermitted(Coord c) {
		return c.x >= 0 && c.x < height &&
		       c.y >= 0 && c.y < width;
	}
}
