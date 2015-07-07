package replication.prototype.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import replication.prototype.client.util.LatencyMeasurementLogEvent;
import replication.prototype.server.messages.M;
import replication.prototype.server.messages.M.Command;
import replication.prototype.server.messages.M.OperationType;

public class Client {

	static final Logger logger = LogManager.getLogger(Client.class.getName());
	static final Marker LATENCY_MARKER = MarkerManager.getMarker("LATENCY");

	static Socket clientSocket = null;
	static Date begin = null;
	static int port = 0;

	static String address = "";

	static long breakInMilliseconds = 1000;

	// static String address = "localhost";

	public static void main(String[] args) throws IOException,
			InterruptedException {

		address = args[0];
		testPort();
		// executeCreateOperation("test");
		executeReadOperation();
		int iterations = 500;
		long startTime = System.currentTimeMillis();

		begin = new java.util.Date();
		for (int i = 0; i < iterations; i++) {
			// TODO parallelisierung für last(i.e. strategie3)
			executeCreateOperation("id:" + Integer.toString(i));
			Thread.sleep(breakInMilliseconds);
		}
		long runTime = System.currentTimeMillis() - startTime;
		System.out.println("runTime:" + runTime + " Milliseconds");

		clientSocket.close();
	}

	public static void getAdress() {

	}

	public static void testPort() {
		try {
			port = 7183;
			clientSocket = new Socket(address, port);
		} catch (Exception e) {

			// e.printStackTrace();
			logger.info("wrong port");
		}
		// nodeA
		try {
			port = 7281;
			clientSocket = new Socket(address, port);
		} catch (Exception e) {

			// e.printStackTrace();
			logger.info("wrong port");
		}
		// nodeB
		try {
			int port = 7384;
			// nodeC
			clientSocket = new Socket(address, port);
		} catch (Exception e) {
			logger.info("wrong port");
			// e.printStackTrace();
		}
		// node C
		try {
			int port = 7484;
			// nodeC
			clientSocket = new Socket(address, port);
		} catch (Exception e) {
			logger.info("wrong port");
			// e.printStackTrace();

			// node D
		}
	}

	public static void executeReadOperation() {
		try {

			// create builder for 'read request'
			Command.Builder readBuilder = Command.newBuilder();
			readBuilder.setOperation(OperationType.READ);
			readBuilder.setKey("test");
			Command readCommand = readBuilder.build();
			readCommand.writeDelimitedTo(clientSocket.getOutputStream());

			M.Response readResponse = M.Response
					.parseDelimitedFrom(clientSocket.getInputStream());

			logger.info("Response of command of type: '"
					+ readResponse.getOperation() + "'' for key='"
					+ readCommand.getKey() + "' and value='"
					+ readResponse.getValue());

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void executeCreateOperation(String id) {

		try {

			// create builder for 'create request'
			Command.Builder createBuilder = Command.newBuilder();
			createBuilder.setOperation(OperationType.UPDATEORCREATE);
			// with hash, but seems to make no difference
			// createBuilder.setKey("test" + id);
			createBuilder.setKey(Integer.toString(Integer.toString(
					(int) Math.floor((Math.random() * 500) + 1)).hashCode()));
			createBuilder.setValue("someValue");
			createBuilder.setId(id);
			Command createCommand = createBuilder.build();
			LatencyMeasurementLogEvent logEvent = new LatencyMeasurementLogEvent();
			logEvent.setCommandId(id);
			logEvent.setTimestampBegin(new java.util.Date());
			createCommand.writeDelimitedTo(clientSocket.getOutputStream());

			// receive response
			M.Response createResponse = M.Response
					.parseDelimitedFrom(clientSocket.getInputStream());
			logEvent.setTimestampEnd(new java.util.Date());
			logger.info(LATENCY_MARKER, logEvent.toCsv(begin));

			logger.info("Response of command of type: '"
					+ createResponse.getOperation() + "' for key='"
					+ createCommand.getKey() + "' and value='"
					+ createResponse.getValue() + "' was found in input stream");

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
