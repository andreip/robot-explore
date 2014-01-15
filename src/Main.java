import java.io.File;
import java.util.Scanner;

public class Main {
	private static Scanner sc;

	public static void main(String[] args) {
		/* Read map from file. */
		if (args.length == 0) {
			System.err.println("Run with first param as path to map.");
			System.exit(1);
		}

		try {
			sc = new Scanner(new File(args[0]));
			int height = sc.nextInt();
			int width = sc.nextInt();
			sc.nextLine();
			char[][] grid = new char[height][width];
			for (int i = 0; i < height; ++i) {
				String[] line = sc.nextLine().split(" ");
				for (int j = 0; j < width; ++j) {
					grid[i][j] = line[j].charAt(0);
				}
			}

			Grid g = new Grid(height, width, grid);
			Grid explored = new Robot(g).explore();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
