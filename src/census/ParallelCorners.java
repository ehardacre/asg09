package census;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class ParallelCorners extends RecursiveTask<Float[]> {

	private CensusGroup[] dataArray;
	private int low, high;
	private int SEQ_CUTOFF = 1000;

	/**
	 * creates a thread that calculates the corners of the map using parallelism
	 * @param dataArray - takes in the entire census information
	 * @param low - the minimum value that this thread must calculate
	 * @param high - the maximum value that this thread must calculate
	 */
	public ParallelCorners(CensusGroup[] dataArray, int low, int high) {

		this.dataArray = dataArray;
		this.high = high;
		this.low = low;
	}

	/**
	 * executes the actual calculations of the corners and their positions based on grid coordinates
	 */
	protected Float[] compute() {

		if (high - low >= SEQ_CUTOFF) {
			ParallelCorners left = new ParallelCorners(dataArray, low,
					(high + low) / 2);
			ParallelCorners right = new ParallelCorners(dataArray,
					(high + low) / 2, high);
			left.fork();
			Float[] rightComp = right.compute();
			Float[] leftComp = left.join();
			Float[] fixed = new Float[4];
			fixed[0] = Math.min(rightComp[0], leftComp[0]);
			fixed[1] = Math.min(rightComp[1], leftComp[1]);
			fixed[2] = Math.max(rightComp[2], leftComp[2]);
			fixed[3] = Math.max(rightComp[3], leftComp[3]);
			return fixed;
		} else {
			float minLat = 181;
			float maxLat = -181;
			float minLon = 181;
			float maxLon = -181;
			for (int i = low; i < high; i++) {
				float lat = dataArray[i].getRealLatitude();
				float lon = dataArray[i].getLongitude();
				minLat = Math.min(minLat, lat);
				maxLat = Math.max(maxLat, lat);
				minLon = Math.min(minLon, lon);
				maxLon = Math.max(maxLon, lon);
			}
			Float[] finish = new Float[4];
			finish[0] = minLon;
			finish[1] = minLat;
			finish[2] = maxLon;
			finish[3] = maxLat;

			return finish;
		}
	}
}
