package com.httphunt;

public class Main {
	
	public static void main(String[] args) {
		System.out.println("Http hunt is started stay braced!!!");
		// If true all steps are executed successfully else some error occured
		if (new StepsExecuter().executeAllSteps()) {
			System.out.println("Gotcha Done... Huh... See you in interview");
		} else {
			System.out.println("Ohhh!!! Something went wrrrong... Finished the whole thing... need to check logic as its still in beta ;p");
		}
		
	}
}
