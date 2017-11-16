package census;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class ParallelSearch extends RecursiveTask<Integer> {

	private Float[] coordExtreme;
	private CensusGroup[] dataArray;

	private final int SEQ_CUTOFF = 22000;

	private int low, high, size, gridSize;

	/**
	 * returns the population based on the coordinates inputted
	 * @param dataArray - the entire data inputted
	 * @param gridSize - the grid size
	 * @param coordExtreme - an aray with the coordinates of the US map
	 * @param low - the minimum value assigned to this thread
	 * @param high - the maximum value assigned to this thread 
	 */
	public ParallelSearch(CensusGroup[] dataArray, int gridSize,
			Float[] coordExtreme, int low, int high) {

		this.dataArray = dataArray;
		this.high = high;
		this.low = low;
		this.gridSize = gridSize;
		this.coordExtreme = coordExtreme;
		

	}

	/**
	 * runs the calculations for returning the population query utilizing threads
	 */
	protected Integer compute() {
		

		if (high - low >= SEQ_CUTOFF) {

			ParallelSearch left = new ParallelSearch(dataArray, gridSize,
					coordExtreme, low, (high + low) / 2);
			ParallelSearch right = new ParallelSearch(dataArray, gridSize,
					coordExtreme, (high + low) / 2, high);
			left.fork();
			int rightAns = right.compute();
			int leftAns = left.join();
			return leftAns + rightAns;

		} else {

			int total = 0;
			for (int i = low; i < high; i++) {

				CensusGroup grp = dataArray[i];
				float latCheck = grp.getRealLatitude();
				float lonCheck = grp.getLongitude();

				if (latCheck >= coordExtreme[1] && latCheck <= coordExtreme[3]
						&& lonCheck >= coordExtreme[0] && lonCheck <= coordExtreme[2]) {
					total += grp.getPopulation();
				}
			}
			return total;
		}
	}

}
