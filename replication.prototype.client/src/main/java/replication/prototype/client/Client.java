package replication.prototype.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import replication.prototype.server.messages.M;
import replication.prototype.server.messages.M.Command;
import replication.prototype.server.messages.M.OperationType;

public class Client {

	static final Logger logger = LogManager.getLogger(Client.class.getName());

	static int port = 0;

	static Socket clientSocket = null;

	// static String address =
	// "ec2-52-18-47-107.eu-west-1.compute.amazonaws.com";

	static String address = "localhost";

	public static void main(String[] args) throws IOException {
		testPort();
		createOperation();
		readOperation();
		int iterations = 100;
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < iterations; i++) {
			createOperation();
		}
		long runTime = System.currentTimeMillis() - startTime;
		System.out.println("runTime:" + runTime);

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
	}

	public static void readOperation() {
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

	public static void createOperation() {

		try {

			// create builder for 'create request'
			Command.Builder createBuilder = Command.newBuilder();
			createBuilder.setOperation(OperationType.UPDATEORCREATE);
			createBuilder.setKey("test");
			createBuilder.setValue("someValue");
			Command createCommand = createBuilder.build();
			createCommand.writeDelimitedTo(clientSocket.getOutputStream());

			// receive response
			M.Response createResponse = M.Response
					.parseDelimitedFrom(clientSocket.getInputStream());

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
