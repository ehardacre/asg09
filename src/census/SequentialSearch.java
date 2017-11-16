package census;

import java.util.Scanner;

public class SequentialSearch {

	float maxLat = -181;
	float minLat = 181;
	float maxLon = -181;
	float minLon = 181;
	float totalLon, totalLat = 0;
	float latPerVal, lonPerVal = 0;
	private CensusGroup[] allData;
	private int size;

	/**
	 * does a sequential search for O(n) time
	 * @param data - takes in the entire census data
	 * @param gridSize - partitions the map into # of gridsize
	 */
	public SequentialSearch(CensusData data, int gridSize) {
		
		System.out.println("running");

		allData = data.getAllData();
		size = data.getSize();

		for (int i = 0; i < size; i++) {
			float lat = allData[i].getRealLatitude();
			float lon = allData[i].getLongitude();
			minLat = Math.min(minLat, lat);
			maxLat = Math.max(maxLat, lat);
			minLon = Math.min(minLon, lon);
			maxLon = Math.max(maxLon, lon);
		}
		totalLon = maxLon - minLon;
		totalLat = maxLat - minLat;
		latPerVal = totalLat / gridSize;
		lonPerVal = totalLon / gridSize;

		Scanner input = new Scanner(System.in);
		while (true) {
			
			System.out
					.print("Enter Coordinates of the area you would like to search: ");
			String coords = input.nextLine().trim();
			String[] split = coords.split(" ");
			if (split.length != 4) {
				System.out.println("Incorrect argument inputs");
				break;
			}
			int west = Integer.parseInt(split[0]);
			int south = Integer.parseInt(split[1]);
			int east = Integer.parseInt(split[2]);
			int north = Integer.parseInt(split[3]);
			StopWatch sw = new StopWatch();
			sw.start();	
			int pop = getPopulation(west, south, east, north);
			System.out.println("The population is: "
					+ pop);	
			System.out.println((pop / 285230516) * 100 + " Percent of the total");
			long endTime = sw.getTime();
			sw.stop();
			System.out.println("time: " + endTime / 1000);

		}
		input.close();
		
	}

	/**
	 * returns the population based on the coordinates inputted
	 * @param east, west, south, north - the index of the grid locations that the requested query matches
	 */
	private int getPopulation(int west, int south, int east, int north) {

		float latLower = ((south - 1) * latPerVal) + minLat;
		float lonLower = ((west - 1) * lonPerVal) + minLon;
		float latUpper = (north * latPerVal) + minLat;
		float lonUpper = (east * lonPerVal) + minLon;
		int total = 0;

		for (int i = 0; i < size; i++) {

			CensusGroup grp = allData[i];
			float latCheck = grp.getRealLatitude();
			float lonCheck = grp.getLongitude();

			if (latCheck >= latLower && latCheck <= latUpper
					&& lonCheck >= lonLower && lonCheck <= lonUpper) {
				total += grp.getPopulation();
			}
		}

		return total;
	}

}
