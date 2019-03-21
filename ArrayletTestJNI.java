import java.util.concurrent.TimeUnit;

public class ArrayletTestJNI {

	static {
		try {
			System.loadLibrary("arraylet");
		} catch (UnsatisfiedLinkError e) {}
	}

	public native void testPrimitiveArrayCriticalArraylets(double[] array, int arrayLength);
	
	public static void main(String[] args) {

		int arrayLength = 8_390_608; // Hybrid, 128 + 1 arraylet leaves
		double[] myList1 = new double[arrayLength];
		double[] myList2 = new double[880_000]; // Hybrid 13 + 1  arraylet leaves:: 524_288 -> 8 discontiguous 8 arraylet leaves

		for(int i = 0; i < arrayLength; i++) {
			myList1[i] = 3.14;
		}

		long startTime = System.nanoTime();
		try {
			new ArrayletTestJNI().testPrimitiveArrayCriticalArraylets(myList1, arrayLength);
		} catch(UnsatisfiedLinkError e) {
			System.out.println("No natives found for JNI test");
			return;
		}
		long endTime = System.nanoTime();

		System.out.println("myList1 values after testPrimitiveArrayCriticalArraylets() call");
		System.out.println("myList1[0] = " + myList1[0] + ", myList1[1] = " + myList1[1] + ", myList1[4096] = " + myList1[4096]);
		/*
		for(int i = 0; i < arrayLength; i += 4096) {
                        System.out.print(i + ":" + myList1[i] + ", ");
                }
		*/

		// get difference of two nanoTime values
		long timeElapsed = endTime - startTime;
		System.out.println("Execution time of testPrimitiveArrayCriticalArraylets() in nano seconds : " + timeElapsed);
		System.out.println("Execution time of testPrimitiveArrayCriticalArraylets() in micro seconds : " + timeElapsed/1_000);
		System.out.println("Execution time of testPrimitiveArrayCriticalArraylets() in milli seconds : " + timeElapsed/1_000_000);
		
		startTime = System.nanoTime();
		// Clean myList1 from memory 
		System.gc();
		endTime = System.nanoTime();
		timeElapsed = endTime - startTime;
		System.out.println("Time to collect myList1 with 128 + 1 arraylet leaves in nano seconds : " + timeElapsed);
                System.out.println("Time to collect myList1 with 128 + 1 arraylet leaves  in micro seconds : " + timeElapsed/1_000);
                System.out.println("Time to collect myList1 with 128 + 1 arraylet leaves  in milli seconds : " + timeElapsed/1_000_000);

		double[] myList3 = new double[arrayLength];
		for(int i = 0; i < arrayLength; i++) {
                        myList3[i] = 3.15;
                }
		startTime = System.nanoTime();
		for(int i = 0; i < arrayLength; i += 4096) {
                        System.out.print(i + ":" + myList1[i] + ", ");
			myList3[i] = 7.55;
                }
		System.out.println();
		endTime = System.nanoTime();
		timeElapsed = endTime - startTime;
		System.out.println("Execution time of local array creation and modification in nano seconds : " + timeElapsed);
                System.out.println("Execution time of local array creation and modification in micro seconds : " + timeElapsed/1_000);
                System.out.println("Execution time of local array creation and modification in milli seconds : " + timeElapsed/1_000_000);
		
		System.gc();

		double[][] myDoubleList = new double[1000][130_200]; // Hybrid 1+1 arraylet leaf
		for(int i = 0; i < 1000; i++) {
			myDoubleList[i][10] = 4.5;
			myDoubleList[i][5_693] = 2.5;
			myDoubleList[i][29_032] = 6.5;
			myDoubleList[i][77_932] = 0.5;
		}
		// 
		// ************************************************** Total of 2,000 regions occupied by arraylet leaves being kept in heap
		//
		System.out.println("myDoubleList: " + myDoubleList + ", myDoubleList[][]:" + myDoubleList[50][87543]);

		System.out.println("##################################################################################################");
		System.out.println("################################# Second for loop starting #######################################");
		System.out.println("##################################################################################################");

		for(int i = 0; i < 100; i++) {
                        double[] myList8 = new double[134_072]; // Hybrid, 2 + 1 arraylet leaves
			double[] myList9 = new double[880_000]; // Hybrid 13 + 1  arraylet leaves:: 524_288 -> 8 discontiguous 8 arraylet leaves
			double[] myList10 = new double[790_098]; // Hybrid 12 + 1  arraylet leaves
			// ************************************************** Total of 30 arraylet leaves
			for(int j = 0; j < myList8.length; j++) { myList8[j] = i*5.5+2.3*j; }
			for(int j = 0; j < myList9.length; j++) { myList9[j] = i*3.5+9.3*j; }
			myList10[203294] = 12345678.9;
                        System.out.println("Iter: " + i + ". Arrays: 1: " + myList8[2000] + ", 2: " + myList9[4000] + ", 10: " + myList10[203294]); 
                }

		System.out.println("myDoubleList[][]: " + myDoubleList[986][10] + ", next: " + myDoubleList[2][77_932]);

		System.out.println("ArrayletTestJNI DONE.");
	}
}
