import java.util.concurrent.TimeUnit;

/*
 - Generate .h file  for JNI custom library
javac -h . ArrayletTestJNI.java

 - Compile 
g++ -c -fPIC -I${JAVA_HOME}/include -I${JAVA_HOME}/include/linux ArrayletTestJNI.cpp -o ArrayletTestJNI.o

 - Link
g++ -shared -fPIC -o libarraylet.so ArrayletTestJNI.o -lc

 - Run. Note: path to -Djava.library.path= must be absolute path, normally it'll be $(pwd) 
 - Without Double Map
java -cp . -Djava.library.path=$(pwd) -Xgcpolicy:balanced -Xmx1g ArrayletTestJNI
 - With Double Map
java -cp . -Djava.library.path=$(pwd) -Xgcpolicy:balanced -Xmx1g -XXgc:enableDoubleMapping ArrayletTestJNI
*/

public class ArrayletTestJNI {

	static {
		try {
			System.loadLibrary("arraylet");
		} catch (UnsatisfiedLinkError e) {}
	}

	public native void testPrimitiveArrayCriticalArraylets(double[] array, int arrayLength);
	public native void testPrimitiveArrayCriticalArraylets(char[] array, int arrayLength);
	public native void testStringCriticalArraylets(String str, int arrayLength);

	private static int roundDown(int arrayLength, int multiple) {
		int mult = arrayLength / multiple;
		return multiple * mult; 
	}
	
	public static void main(String[] args) {

		int arrayLength = 8_484_144; //8_390_608 -> Hybrid, 128 + 1 arraylet leaves. 8_454_144 -> Discontiguous 129 leaves
		double[] myList1 = new double[arrayLength];
		System.out.println("About to create an array of chars!");
		char[] charArray = new char[arrayLength];

		System.out.println("Double.SIZE: " + Double.SIZE + ", 524288/Double.SIZE: " + (524288/Double.SIZE));

		System.out.println("New array length: " + arrayLength);

		/* ****************************************************************************************************** */

		StringBuffer strBuffer = new StringBuffer();
		int strLength = 4_194_304; // 4_194_304 exactly 16 leaves if each char is 2 bytes
		for(int i = 0; i < strLength/2048; i++) {
			// System.out.println("Adding 2048 more chars to the string");
			strBuffer.append("11111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111");
		}
		System.out.println("I am about to create a new String!!! Heads up!!");
		String myString = strBuffer.toString();
		strLength = myString.length();
		System.out.println("New String array length: " + strLength);

		// arrayLength = roundDown(arrayLength, 524288/(Double.SIZE/8));

		for(int i = 0; i < arrayLength; i++) {
			myList1[i] = 3.14;
		}

		long startTime = System.nanoTime();
		long middleTime = 0;
		long middleTime2 = 0;
		try {
			new ArrayletTestJNI().testPrimitiveArrayCriticalArraylets(myList1, arrayLength);
			System.out.println("##############################################################################################");
			System.out.println("Just called testPrimitiveArrayCriticalArraylets(), about to call testStringCriticalArraylets()");
			System.out.println("##############################################################################################");
			middleTime = System.nanoTime();
			new ArrayletTestJNI().testStringCriticalArraylets(myString, strLength);
			middleTime2 = System.nanoTime();
			new ArrayletTestJNI().testPrimitiveArrayCriticalArraylets(charArray, arrayLength);
		} catch(UnsatisfiedLinkError e) {
			System.out.println("No natives found for JNI test");
			return;
		}
		long endTime = System.nanoTime();

		System.out.println("myList1 values after testPrimitiveArrayCriticalArraylets() call");
		System.out.println("myList1[0] = " + myList1[0] + 
				 "\nmyList1[1] = " + myList1[1] + 
			       	 "\nmyList1[4096] = " + myList1[4096] + 
				 "\nmyList1[430,080] = " + myList1[430_080] + 
				 "\nmyList1[638,976] = " + myList1[638_976] + 
				 "\nmyList1[1,875,968] = " + myList1[1_875_968] + 
				 "\nmyList1[8,437,760] = " + myList1[8_437_760] +
				 "\nmyList1["+(arrayLength-1)+"] = " + myList1[arrayLength-1]);

		System.out.println("\nmyString values after testStringCriticalArraylets() call");
                System.out.println("myString[0] = " + myString.charAt(0) +
                                 "\nmyString[1] = " + myString.charAt(1) +
                                 "\nmyString[4096] = " + myString.charAt(4096) +
                                 "\nmyString[430,075:430,085] = " + myString.substring(430075, 430085) +
                                 "\nmyString[638,970:638,980] = " + myString.substring(638970, 638980) +
                                 "\nmyString[1,875,960:1,875,970] = " + myString.substring(1_875_960, 1_875_970) +
				 "\nmyString[] = " + myString.substring(1_875_960, 1_875_970) +
                                 "\nmyString["+(strLength-1)+":] = " + myString.substring(strLength-10));

		/*
		for(int i = 0; i < strLength; i += 4096) {
			System.out.print(myString.charAt(i) + ", ");
                        // System.out.print(i + ":" + myList1[i] + ", ");
                }
		*/

		// get difference of two nanoTime values
		long timeElapsed = middleTime - startTime;
		System.out.println("\nExecution time of testPrimitiveArrayCriticalArraylets() in nano seconds : " + timeElapsed);
		System.out.println("Execution time of testPrimitiveArrayCriticalArraylets() in micro seconds : " + timeElapsed/1_000);
		System.out.println("Execution time of testPrimitiveArrayCriticalArraylets() in milli seconds : " + timeElapsed/1_000_000);

		timeElapsed = middleTime2 - middleTime;
                System.out.println("\nExecution time of testStringCriticalArraylets() in nano seconds : " + timeElapsed);
                System.out.println("Execution time of testStringCriticalArraylets() in micro seconds : " + timeElapsed/1_000);
                System.out.println("Execution time of testStringCriticalArraylets() in milli seconds : " + timeElapsed/1_000_000);

		timeElapsed = endTime - middleTime2;
                System.out.println("\nExecution time of testCharCriticalArraylets() in nano seconds : " + timeElapsed);
                System.out.println("Execution time of testCharCriticalArraylets() in micro seconds : " + timeElapsed/1_000);
                System.out.println("Execution time of testCharCriticalArraylets() in milli seconds : " + timeElapsed/1_000_000);
		
		startTime = System.nanoTime();
		// Clean myList1 from memory 
		System.gc();
		endTime = System.nanoTime();
		timeElapsed = endTime - startTime;
		System.out.println("\nTime to collect myList1 with 128 + 1 arraylet leaves in nano seconds : " + timeElapsed);
                System.out.println("Time to collect myList1 with 128 + 1 arraylet leaves  in micro seconds : " + timeElapsed/1_000);
                System.out.println("Time to collect myList1 with 128 + 1 arraylet leaves  in milli seconds : " + timeElapsed/1_000_000);

		double[] myList3 = new double[arrayLength];
		for(int i = 0; i < arrayLength; i++) {
                        myList3[i] = 3.15;
                }
		startTime = System.nanoTime();
		for(int i = 0; i < arrayLength; i += 4096) {
                        // System.out.print(i + ":" + myList1[i] + ", ");
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
