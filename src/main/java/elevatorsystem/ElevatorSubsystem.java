package elevatorsystem;

import misc.*;
import requests.ElevatorRequest;
import requests.FloorRequest;
import requests.ServiceRequest;
import misc.BoundedBuffer;
import systemwide.Origin;


/**
 * ElevatorSubsystem manages the elevators and their requests to the Scheduler
 * 
 * @author Liam Tripp, Julian, Ryan Dash
 */
public class ElevatorSubsystem implements Runnable {

	private final BoundedBuffer elevatorSubsystemBuffer; // Elevator Subsystem - Scheduler link
	private Origin origin;

	/**
	 * Constructor for ElevatorSubsystem.
	 *
	 * @param buffer the buffer the ElevatorSubsystem passes messages to and receives messages from
	 */
	public ElevatorSubsystem(BoundedBuffer buffer) {
		this.elevatorSubsystemBuffer = buffer;
		origin = Origin.ELEVATOR_SYSTEM;
	}

	/**
	 * Simple message requesting and sending between subsystems.
	 * 
	 */
	public void run() {
		// need to get proper number from somewhere - maybe instantiate a FileInputReader, read in the inputs
		InputFileReader inputFileReader = new InputFileReader();
		int numberOfInputs = inputFileReader.readInputFile("inputs").size();
		for (int i = 0; i < numberOfInputs * 2; i++) {
			ServiceRequest request = receiveRequest();
			sendRequest(request);
			// sendRequest(new FloorRequest(request.getTime().plus(69, ChronoUnit.MILLIS), ((ElevatorRequest) request).getDesiredFloor(), request.getDirection(),  request.getFloorNumber()));
		}
		/*
		while(true) {
			// A sleep to allow communication between Floor Subsystem and Scheduler to
			// happen first
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.err.println(e);
			}

			// Receiving Data from Scheduler
			if (receiveRequest()) {
				System.out.println("Elevator SubSystem received Request Successful");
			} else {
				System.out.println("Failed Successful");
			}

			// Sending Data to Scheduler
			if (sendRequest(floorRequest)) { // Expect elevator # at floor #
				System.out.println("Elevator SubSystem Sent Request to Scheduler Successful");
			} else {
				System.out.println("Failed Successful");
			}
		}
		 */
	}

	/**
	 * Puts a request into a buffer.
	 * 
	 * @param request the message being sent
	 * @return true if request is successful, false otherwise
	 */
	public boolean sendRequest(ServiceRequest request) {
		System.out.println(Thread.currentThread().getName() + " requested for: " + request);
		elevatorSubsystemBuffer.addLast(request, origin);

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			System.err.println(e);
		}

		return true;
	}

	/**
	 * Removes a request from the Buffer.
	 *
	 * @return serviceRequest a request by a person on a floor or in an elevator
	 */
	public ServiceRequest receiveRequest() {
		ServiceRequest request = elevatorSubsystemBuffer.removeFirst(origin);
		System.out.println(Thread.currentThread().getName() + " received the request: " + request);

		try {
			Thread.sleep(0);
		} catch (InterruptedException e) {
			System.err.println(e);
		}
		return request;
	}

	/**
	 * Checks the buffer for messages
	 *
	 * @return true if request is successful, false otherwise
	 */
	public boolean receiveRequestBoolean() {
		ServiceRequest request = elevatorSubsystemBuffer.removeFirst(origin);
		System.out.println(Thread.currentThread().getName() + " received the request: " + request);
		if (request instanceof ElevatorRequest elevatorRequest){
			FloorRequest floorRequest = new FloorRequest(elevatorRequest, 1);
		}  else if (request instanceof FloorRequest){
			System.err.println("Incorrect Request. This is for a Floor");
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			System.err.println(e);
		}

		return true;
	}
}
