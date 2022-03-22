package scheduler;

import client_server_host.IntermediateHost;
import client_server_host.Port;
import client_server_host.RequestMessage;
import elevatorsystem.MovementState;
import requests.*;
import systemwide.Direction;
import systemwide.Origin;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Scheduler handles the requests from all system components
 *
 * @author Liam Tripp, Julian, Ryan Dash
 */
public class Scheduler implements Runnable {

	private static ArrayList<ElevatorMonitor> elevatorMonitorList;
	private final IntermediateHost intermediateHost;
	// private ArrayList<Elevator> elevators;
	// private ArrayList<Floor> floors;

	/**
	 * Constructor for Scheduler.
	 *
	 * @param portNumber the port number associated with the class's DatagramSocket
	 */
	public Scheduler(int portNumber) {
		elevatorMonitorList = new ArrayList<>();
		intermediateHost = new IntermediateHost(portNumber);
	}

	/**
	 * Add ElevatorMonitor to elevatorMonitorList.
	 *
	 * @param elevatorNumber an elevator number corresponding to an elevator
	 */
	public void addElevatorMonitor(int elevatorNumber) {
		elevatorMonitorList.add(new ElevatorMonitor(elevatorNumber));
	}

	/**
	 * Get the current static instance of elevatorMonitorList containing a list of elevator monitors.
	 *
	 * @return a list of elevator monitors
	 */
	public static ArrayList<ElevatorMonitor> getElevatorMonitorList() {
		return elevatorMonitorList;
	}

	/**
	 * Takes a DatagramPacket from the IntermediateHost and processes it.
	 * If it's data (i.e. contains a SystemEvent), it is processed by Scheduler.
	 * Otherwise, it's a request for data and is processed by IntermediateHost.
	 */
	private void receiveAndProcessPacket() {
		while (true) {
			DatagramPacket receivePacket = intermediateHost.receivePacket();

			Object object = intermediateHost.convertToObject(receivePacket);

			if (object instanceof String) {
				Object event;
				if (!intermediateHost.queueIsEmpty()){
					event = intermediateHost.getPacketFromQueue();
					if (event instanceof ElevatorRequest elevatorRequest) {
						int chosenElevator = chooseElevator(elevatorRequest);
						System.err.println("Elevator#" + chosenElevator + " is being sent a request");
						elevatorRequest.setElevatorNumber(chosenElevator);
					}

					System.err.println(event);
				} else {
					event = RequestMessage.EMPTYQUEUE.getMessage();
				}
				intermediateHost.respondToDataRequest(event, receivePacket.getAddress(), receivePacket.getPort());

			} else if (object instanceof SystemEvent systemEvent) {
				intermediateHost.respondToSystemEvent(receivePacket);
				processData(systemEvent);
			}
		}
	}

	/**
	 * Process data that Scheduler's DatagramSocket has received.
	 * Create a new packet and manipulate it according to the packet's Origin.
	 *
	 * @param event a systemEvent to be processed
	 */
	public void processData(SystemEvent event) {

		if (event instanceof ElevatorMonitor elevatorMonitor){
			elevatorMonitorList.get(elevatorMonitor.getElevatorNumber()-1).updateMonitor(elevatorMonitor);
		}
		event.setOrigin(Origin.changeOrigin(event.getOrigin()));
		intermediateHost.addEventToQueue(event);
	}

	/**
	 * Returns an elevator number corresponding to an elevator that is
	 * best suited to perform the given ElevatorRequest based on
	 * expected time to fulfill the request and direction of elevator.
	 *
	 * @param elevatorRequest an ElevatorRequest
	 * @return a number corresponding to an elevator
	 */
	public int chooseElevator(ElevatorRequest elevatorRequest) {

		double elevatorBestExpectedTime = 0.0;
		// Best elevator is an elevator traveling in path that collides with request floor
		double elevatorOkExpectedTime = 0.0;
		// Ok elevator is an elevator that is traveling in the other direction
		double elevatorWorstExpectedTime = 0.0;
		// Worst elevator is an elevator that is traveling in the same direction but missed the request
		int chosenBestElevator = 0;
		int chosenOkElevator = 0;
		int chosenWorstElevator = 0;
		for (ElevatorMonitor monitor : elevatorMonitorList) {

			MovementState state = monitor.getState();
			Direction requestDirection = elevatorRequest.getDirection();
			double tempExpectedTime = monitor.getQueueTime();
			int currentFloor = monitor.getCurrentFloor();
			int desiredFloor = elevatorRequest.getDesiredFloor();
			int elevatorNumber = monitor.getElevatorNumber();
			Direction currentDirection = monitor.getDirection();

			if (currentDirection == Direction.UP){
				currentFloor += 1;
			} else if (currentDirection == Direction.DOWN){
				currentFloor -=1;
			}

			if (monitor.getHasNoRequests()) {
				System.out.println("Elevator#" + elevatorNumber + " is idle");
				return elevatorNumber;

			} else if (state == MovementState.STUCK) {
				System.err.println("Elevator#" + elevatorNumber + " is stuck");

			} else if (monitor.getDirection() == requestDirection) {
				if (elevatorBestExpectedTime == 0 || elevatorBestExpectedTime > tempExpectedTime) {
					if (requestDirection == Direction.DOWN && currentFloor > desiredFloor) {
						//check if request is in path current floor > directed floor going down
						elevatorBestExpectedTime = tempExpectedTime;
						chosenBestElevator = elevatorNumber;

					} else if (requestDirection == Direction.UP && currentFloor < desiredFloor) {
						//check if request is in path current floor < directed floor going up
						elevatorBestExpectedTime = tempExpectedTime;
						chosenBestElevator = elevatorNumber;

					} else if (elevatorOkExpectedTime == 0 || elevatorOkExpectedTime > tempExpectedTime){
						//if request is in the correct direction but not in path of elevator
						elevatorWorstExpectedTime = tempExpectedTime;
						chosenWorstElevator = elevatorNumber;
					}
				}
			} else {
				if (elevatorWorstExpectedTime == 0 || elevatorWorstExpectedTime > tempExpectedTime) {
					//if the elevator traveling in the wrong direction
					elevatorOkExpectedTime = tempExpectedTime;
					chosenOkElevator = elevatorNumber;
				}
			}
		}
		if (chosenBestElevator == 0) {
			if (chosenOkElevator == 0){
				chosenBestElevator = chosenWorstElevator;
			} else {
				chosenBestElevator = chosenOkElevator;
			}
		}
		return chosenBestElevator;
	}

	/**
	 * Simple message requesting and sending between subsystems.
	 * Scheduler
	 * Sends: ApproachEvent, FloorRequest, ElevatorRequest
	 * Receives: ApproachEvent, ElevatorRequest
	 */
	public void run() {
		// take action depending on if using buffers or IntermediateHost
		receiveAndProcessPacket();
	}

	public static void main(String[] args) {
		Scheduler schedulerClient = new Scheduler(Port.CLIENT_TO_SERVER.getNumber());
		Scheduler schedulerServer = new Scheduler(Port.SERVER_TO_CLIENT.getNumber());
		schedulerClient.addElevatorMonitor(1);
		schedulerClient.addElevatorMonitor(2);
		new Thread(schedulerClient, schedulerClient.getClass().getSimpleName()).start();
		new Thread(schedulerServer, schedulerServer.getClass().getSimpleName()).start();

	}
}
