package census;

import java.util.Scanner;

public class SmartSequentialSearch {

	CensusGroup[] allData;
	int size;
	float maxLat = -181;
	float minLat = 181;
	float maxLon = -181;
	float minLon = 181;
	float totalLon, totalLat = 0;
	float latPerVal, lonPerVal = 0;
	int[][] popTotal;
	int gridSize;
	
	/**
	 * does a sequential search for O(n) time
	 * @param data - takes in the entire census data
	 * @param gridSize - partitions the map into # of gridsize
	 */
	public SmartSequentialSearch(CensusData data, int gridSize){
		this.gridSize = gridSize;
		popTotal = new int[gridSize][gridSize];
		for(int i = 0; i < gridSize; i++){
			for(int j = 0; i < gridSize; i++){
				popTotal[i][j] = 0;
			}
		}
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
		for(int i = 0; i < size; i++){
			CensusGroup grp = allData[i];
			int indexLat = (int) ((grp.getRealLatitude() - minLat)/ latPerVal);
			int indexLon = (int) ((grp.getLongitude() - minLon)/ lonPerVal);
			indexLon = Math.min(indexLon, gridSize-1);
			indexLat = Math.min(indexLat, gridSize-1);
			
			popTotal[indexLat][indexLon] = popTotal[indexLat][indexLon] + grp.getPopulation();
		}
		
		for(int i = 0; i < gridSize; i++){
			for(int j = 0; j < gridSize; j++){
				if(i != 0){
					popTotal[i][j] = popTotal[i][j] + popTotal[i-1][j];
				}
				if(j != 0){
					popTotal[i][j] = popTotal[i][j] + popTotal[i][j-1];
				}
				if(i != 0 && j != 0){
					popTotal[i][j] = popTotal[i][j] - popTotal[i-1][j-1];
				}
			}
		}
		
		Scanner input = new Scanner(System.in);
		for(int i = 0; i < 10; i++){
			getPopulation(1,1,10,10);
		}
		while (true) {
			
			System.out
					.print("SS Enter Coordinates of the area you would like to search: ");
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
			int population = getPopulation(west, south, east, north);
			System.out.println("The population is: "
					+ population);	
			System.out.println((population / 285230516) * 100 + " Percent of the total");
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
	public int getPopulation(int west, int south, int east, int north){
		
		int topRight = popTotal[north-1][east-1];
		int bottom = 0;
		if(south != 1){
			bottom = popTotal[south-2][east-1];
		}
		int left = 0;
		if(west != 1){
			left = popTotal[north-1][west-2];
		}
		int bottomLeft = 0;
		if(west != 1 && south != 1){
			bottomLeft = popTotal[south-2][west-2];
		}
		
		return topRight - bottom - left + bottomLeft;
	}
}

