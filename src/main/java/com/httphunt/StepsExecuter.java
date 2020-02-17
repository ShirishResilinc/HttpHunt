package com.httphunt;

import java.util.List;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.function.Function;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import com.httphunt.exceptions.FailureStepException;
import com.httphunt.models.*;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

public class StepsExecuter {

	RestAPIConsumer restApiConsumer = new RestAPIConsumer();
	// Array of steps to perform
	// Each element consist of instruction to execute the step
	// The line of code can be reduced more as body creation logic is only different
	private List<Step> totalSteps = Arrays.asList(() -> {
		try {
			System.out.println("Executing step 1");
			final CountInputResponse response = this.restApiConsumer.get("/input").readEntity(CountInputResponse.class);
			System.out.println("Response recieved with text: " + response.getText());
			final CountInputResultBody body = new CountInputResultBody();
			body.setCount(response.getText().length());
			System.out.println("Sending output length" + body.getCount());
			final CountResponse postResponse = this.restApiConsumer.post("/output", Entity.json(body)).readEntity(CountResponse.class);
			System.out.println("Output response recieved" + postResponse);
			return true;	
		} catch (FailureStepException e) {
			return false;
		}
	}, () -> {
		try {
			System.out.println("Executing step 2");
			final CountInputResponse response = this.restApiConsumer.get("/input").readEntity(CountInputResponse.class);
			System.out.println("Response recieved with text: " + response.getText());
			final WordCountResultBody body = new WordCountResultBody();
			body.setWordCount(response.getText().split(" ").length);
			System.out.println("Sending output length" + body.getWordCount());
			final CountResponse postResponse = this.restApiConsumer.post("/output", Entity.json(body)).readEntity(CountResponse.class);
			System.out.println("Output response recieved" + postResponse);
			return true;	
		} catch (FailureStepException e) {
			return false;
		}
	}, () -> {
		try {
			System.out.println("Executing step 3");
			final CountInputResponse response = this.restApiConsumer.get("/input").readEntity(CountInputResponse.class);
			System.out.println("Response recieved with text: " + response.getText());
			final SentenceCount body = new SentenceCount();
			body.setSentenceCount(response.getText().split("([\\.\\?][\\s])+|[\\.\\?]$").length);
			System.out.println("Sending output length" + body.getSentenceCount());
			Response postResponse = this.restApiConsumer.post("/output", Entity.json(body));
			try {
				CountResponse countResponse = postResponse.readEntity(CountResponse.class);
				System.out.println("Output response recieved" + countResponse);
				return true;	
			} catch (Exception e) {
				FailureMessage messageResponse = postResponse.readEntity(FailureMessage.class);
				if (postResponse.getStatus() == 200) {
					System.out.println("Output response recieved" + messageResponse.getMessage());
					return true;
				}
				return false;
			}
		} catch (FailureStepException e) {
			return false;
		}
	}, () -> {
		try {
			System.out.println("Executing step 4");
			final CountInputResponse response = this.restApiConsumer.get("/input").readEntity(CountInputResponse.class);
			System.out.println("Response recieved with text: " + response.getText());
			final VowelsCountResultBody body = new VowelsCountResultBody();
			final Set<String> vowels = Arrays.asList("a","e", "i", "o", "u", "A", "E", "I", "O", "U").stream().collect(Collectors.toSet());
			body.setVowels(Arrays.asList(response.getText().split("")).stream()
					.filter(val -> vowels.contains(val.toLowerCase()))
					.collect(Collectors.groupingBy(Function.identity(), Collectors.counting())));
			System.out.println("Sending output length" + body.toString());
			Response postResponse = this.restApiConsumer.post("/output", Entity.json(body));
			try {
				CountResponse countResponse = postResponse.readEntity(CountResponse.class);
				System.out.println("Output response recieved" + countResponse);
				return true;	
			} catch (Exception e) {
				FailureMessage messageResponse = postResponse.readEntity(FailureMessage.class);
				if (postResponse.getStatus() == 200) {
					System.out.println("Output response recieved" + messageResponse.getMessage());
					return true;
				}
				return false;
			}	
		} catch (Exception | FailureStepException e) {
			return false;
		}
	});
	
	public boolean executeAllSteps() {
		final int currentStep = getCurrentStep();
		// If current step is greater than 4 cosider test completed
		if (currentStep > 4) {
			return true;
		}
		// Show user message how many steps are skipped in current execution
		if (currentStep != 1) {
			System.out.println("Skipping last " + currentStep + " step(s) as they are already executed");
		}
		// Run all steps
		return this.totalSteps.stream().skip(currentStep - 1).map((step) -> step.execute())
					.allMatch((result) -> result == true);
	}
	
	/**
	 * Parse response from challenge API and return steps number
	 * @return current step number
	 */
	private int getCurrentStep() {
		System.out.println("Get current stage number");
		Response getResponse = null;
		int currentStage = 10000;
		try {
			getResponse = this.restApiConsumer.get("");
			StageNumber stageNumberRes = getResponse.readEntity(StageNumber.class);
			String stages = stageNumberRes.getStage();
			if (stages != null) {
				currentStage = Integer.parseInt(stages.split("/")[0]);	
			} else {
				if (getResponse.getStatus() == 200) {
					System.out.println("Whoa test completed!!!");
					return 1000000;
				}
			}
		} catch (FailureStepException | NullPointerException e) {
			e.printStackTrace();
			// Just added random value just more than 4 is needed as of now
			return 1000000000;
		}
		System.out.println("Current step is " + currentStage);
		return currentStage;
	}
}
