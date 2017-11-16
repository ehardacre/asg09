package census;

import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;

public class ParallelController {

	CensusGroup[] allData;
	private int size, population, gridSize;
	private float maxLat, minLat, maxLon, minLon, totalLon, totalLat, latPerVal,
	lonPerVal;
	
	private Float[] corners, coordExtreme;
	int[] cardinal;
	ForkJoinPool fj = ForkJoinPool.commonPool();

	/**
	 * Handles all of the parallelism
	 * @author: Elvis Kahoro (ekahoro) and Ethan Hardacre (ehardacre)
	 * @param data - the entire data from the census file
	 * @parma gridSize - the size of the grid that the map is partitioned into
	 */
	public ParallelController(CensusData data, int gridSize) {
		allData = data.getAllData();
		size = data.getSize();
		this.gridSize = gridSize;
		findCorners();
		StopWatch sw = new StopWatch();
		sw.start();
		getInput();		
		long endTime = sw.getTime();
		sw.stop();
		System.out.println("time: " + endTime / 1000);
	}

	/**
	 * sets the four corners of the map, in an array
	 */
	public void findCorners() {
		corners = fj.invoke(new ParallelCorners(allData, 0, size));
		for (int i = 0; i < 4; i++)
			System.out.println(corners[i]);
	}

	/**
	 * takes in an input from the user and returns the population within the grid
	 */
	public void getInput() {
		
			Scanner input = new Scanner(System.in);
			while(true){
			System.out
					.print("Enter Coordinates of the area you would like to search: ");
			String coords = input.nextLine().trim();
			String[] split = coords.split(" ");
			
			if(split.length != 4) {
				System.out.println("Incorrect argumet inputs");
				break;
			}
			
			cardinal = new int[4];
			coordExtreme = new Float[4];

			
			cardinal[0] = Integer.parseInt(split[0]);
			cardinal[1] = Integer.parseInt(split[1]);
			cardinal[2] = Integer.parseInt(split[2]);
			cardinal[3] = Integer.parseInt(split[3]);
			
			totalLon = corners[2] - corners[0];
			totalLat = corners[3] - corners[1];
			latPerVal = totalLat / gridSize;
			lonPerVal = totalLon / gridSize;
			
			coordExtreme[0] = ((cardinal[0] - 1) * lonPerVal) + corners[0];
			coordExtreme[1] = ((cardinal[1] - 1) * latPerVal) + corners[1];
			coordExtreme[2] = (cardinal[2] * lonPerVal) + corners[0];
			coordExtreme[3] = (cardinal[3] * latPerVal) + corners[1];
			
			StopWatch sw = new StopWatch();
			sw.start();
			population = fj.invoke(new ParallelSearch(allData, gridSize, coordExtreme, 0, size));
			long endTime = sw.getTime();
			sw.stop();
			System.out.println("time: " + endTime / 1000);
			
			
			System.out.println(population);
			System.out.println((population / 285230516) * 100 + " Percent of the total");
			}
			input.close();
	}

}
