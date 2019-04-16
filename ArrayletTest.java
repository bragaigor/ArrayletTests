/* To run
 - compile
javac ArrayletTest.java

 - run without double mapping
java -Xgcpolicy:balanced -Xmx1g ArrayletTest

 - run with double mapping
java -Xgcpolicy:balanced -Xmx1g -XXgc:enableDoubleMapping ArrayletTest

 - Translate trace if any
traceformat trace trace.out
 */

public class ArrayletTest {
	
	public static void main(String[] args) {

		System.out.println("##########################################################################Creating myList 00..., size 131,072");
                double[] myList00 = new double[131_072]; // Exactly the size of region. This used to be the null arraylet leaf proble	m
                for(int i = 0; i < myList00.length; i++) {
                        myList00[i] = i*5.5+100;
                }

		System.out.println("\t ***************************** myList00[] " + myList00[myList00.length - 1]);

		System.out.println("##########################################################################Creating myList 0..., size 65,536");
                double[] myList0 = new double[65_536]; // Exactly the size of region. This used to be the null arraylet leaf problem
                for(int i = 0; i < myList0.length; i++) {
                        myList0[i] = i*5.5+100;
                }
		
		System.out.println("##########################################################################Creating myList 1... size 240,000");
		double[] myList = new double[240_000];
		for(int i = 0; i < myList.length; i++) {
			myList[i] = i*1.5+100;
		}

		System.out.println("##########################################################################Creating myList 2..., size 262,144");
		double[] myList2 = new double[262_144]; // exactly 4 arraylet leaves with params -Xgcpolicy:balanced -Xmx1g passed in to java
                for(int i = 0; i < myList2.length; i++) {
                        myList2[i] = i*4.5+100;
                }

		System.out.println("##########################################################################Creating myList 3..., size 262,144 - 1");
                double[] myList21 = new double[262_144 - 1]; // exactly 4 arraylet leaves with params -Xgcpolicy:balanced -Xmx1g passed in to java
                for(int i = 0; i < myList21.length; i++) {
                        myList21[i] = i*4.5+100;
                }

		System.out.println("##########################################################################Creating myList 4..., size 262,144 * 2");
		double[] myList3 = new double[262_144 * 2];
                for(int i = 0; i < myList3.length; i++) {
                        myList3[i] = i*9.5+100;
                }

		System.out.println("##########################################################################Creating myList 5..., size 860,921, 14 leaves");
                double[] myList31 = new double[860_921]; // 14 arraylet leaves
                for(int i = 0; i < myList31.length; i++) {
                        myList31[i] = i*9.5+100;
                }

		System.out.println("##########################################################################Creating myList 6..., size 1,271,004, 20 leaves");
                double[] myList32 = new double[1_271_004]; // 20 arraylet leaves
                for(int i = 0; i < myList32.length; i++) {
                        myList32[i] = i*9.5+100;
                }

		System.out.println("##########################################################################Creating myList 7..., size 65,534");
		double[] myList4 = new double[65_534];
                for(int i = 0; i < myList4.length; i++) {
                        myList4[i] = i*4.5+100;
                }

		System.out.println("##########################################################################Creating myList 8..., size 65,536 - 1");
		double[] myList5 = new double[65_535]; // 
                for(int i = 0; i < myList5.length; i++) {
                        myList5[i] = i*5.5+100;
                }

		System.out.println("##########################################################################Creating myList 9..., size 65,536");
		double[] myList6 = new double[65_536]; // Exactly the size of region. This used to be the null arraylet leaf problem
                for(int i = 0; i < myList6.length; i++) {
                        myList6[i] = i*5.5+100;
                }

		System.out.println("##########################################################################Creating myList 10..., size 65,537");
		double[] myList7 = new double[65_537];
                for(int i = 0; i < myList7.length; i++) {
                        myList7[i] = i*5.5+100;
                }

		// Tests loop over a bunch of arraylet creations. Goal: check if we call allocate more than 10_000 regions (need GC help)
		// Look for exact size discontiguous arraylet where last arraylet leaf is NULL

		System.out.println("##################################################################################################");
                System.out.println("################################# Second for loop starting #######################################");
                System.out.println("##################################################################################################");

		double[][] myDoubleList = new double[1000][130_200]; // Hybrid 1+1 arraylet leaf
		for(int i = 0; i < 200; i++) {
			myDoubleList[i][10] = 4.5;
			myDoubleList[i][5_693] = 2.5;
			myDoubleList[i][29_032] = 6.5;
			myDoubleList[i][77_932] = 0.5;
		}
		// 
		// ************************************************** Total of 2,000 regions occupied by arraylet leaves being kept in heap
		//
		myDoubleList[191][116132] = 12.98;
		System.out.println("myDoubleList: " + myDoubleList + ":\nmyDoubleList[20][10]:" + myDoubleList[20][10] + 
								     "\nmyDoubleList[50][5693]:" + myDoubleList[50][5693] + 
								     "\nmyDoubleList[110][29032]:" + myDoubleList[110][29_032] + 
								     "\nmyDoubleList[176][77932]:" + myDoubleList[176][77_932] +
								     "\nmyDoubleList[191][116132]:" + myDoubleList[191][116132]);

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

		System.out.println("myDoubleList[][]: " + myDoubleList[86][10] + ", next: " + myDoubleList[762][77_932]);

		try {
			System.out.println("Starting Sleep Test. About to sleep for 2 seconds... ZzZZzZzzZZzzzZzZ");
			Thread.sleep(2);
			System.out.println("Wokeup from sleep!");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
