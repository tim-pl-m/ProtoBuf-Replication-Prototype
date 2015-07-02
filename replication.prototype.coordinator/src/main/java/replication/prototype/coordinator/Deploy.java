package replication.prototype.coordinator;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import replication.prototype.coordinator.config.NodeType;
import replication.prototype.coordinator.config.ReplicationConfigurationType;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;

@SpringBootApplication
public class Deploy {

	static final Logger logger = LogManager.getLogger(Deploy.class.getName());

	static AmazonEC2 ec2;

	private static void init() throws Exception {

		/*
		 * The ProfileCredentialsProvider will return your [New EU West
		 * (Ireland) Profile] credential profile by reading from the credentials
		 * file located at (/Users/Tim/.aws/credentials).
		 */
		AWSCredentials credentials = null;
		try {
			credentials = new ProfileCredentialsProvider(
					"New EU West (Ireland) Profile").getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException(
					"Cannot load the credentials from the credential profiles file. "
							+ "Please make sure that your credentials file is at the correct "
							+ "location (/Users/Tim/.aws/credentials), and is in valid format.",
					e);
		}
		ec2 = new AmazonEC2Client(credentials);
		// s3 = new AmazonS3Client(credentials);
		// sdb = new AmazonSimpleDBClient(credentials);

		Region r = Region.getRegion(Regions.EU_WEST_1);
		ec2.setRegion(r);
	}

	// login to AWS

	public static void main(String[] args) throws Exception {

		if (args.length > 0) {
			if (args[0].equals("local")) {
				System.out.println("deploy local");
				deployOffline();
			}
		} else
			deployOnline();

	}

	private static void deployOffline() throws Exception {
		// File configFile = new
		// File("src/main/resources/ConfigurationLocal.xml");
		File configFile = new File("src/main/resources/Configuration.xml");
		JAXBContext jaxbContext;
		ReplicationConfigurationType config = null;

		try {
			jaxbContext = JAXBContext
					.newInstance(ReplicationConfigurationType.class);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			config = (ReplicationConfigurationType) jaxbUnmarshaller
					.unmarshal(configFile);

			// set addresses of the nodes
			for (NodeType n : config.getReplicationnodes().getNode()) {

				n.setIpadress("localhost");

				System.out
						.println(n.getLabel() + n.getIpadress() + n.getPort());
			}
			// deploy nodes
			int[] portArray = { 8081, 8082, 8083 };
			int portCounter = 0;
			for (NodeType n : config.getReplicationnodes().getNode()) {
				deployNode(n, config, portArray[portCounter]);
				portCounter++;
			}

			logger.info("Configuration parsed successfully.");
		} catch (JAXBException e) {
			logger.fatal("Configuration could not be parsed.");

		}

	}

	private static void deployNode(NodeType n,
			ReplicationConfigurationType config, int port) throws IOException {
		String startUrl = "http://" + n.getIpadress() + ":" + port
				+ "/startServer/{thisNode}";

		RestTemplate restTemplate = new RestTemplate();
		// debug: check connection and url-parsing
		String runningUrl = "http://" + n.getIpadress() + ":" + port
				+ "/isServerRunning";
		System.out.println(runningUrl);

		Boolean result = restTemplate.getForObject(runningUrl, Boolean.class);
		System.out.println(result);
		// if (!result) {
		// System.out.println("Server not running adequat");
		// return;
		// }

		System.out.println(startUrl);

		restTemplate.getMessageConverters().add(
				new Jaxb2RootElementHttpMessageConverter());
		result = restTemplate.postForObject(startUrl, config, Boolean.class,
				n.getLabel());
		System.out.println("start " + n.getLabel() + " successfull: " + result);

	}

	private static void deployOnline() throws Exception {
		System.out.println("===========================================");
		System.out.println("Welcome to the AWS Java SDK!");
		System.out.println("===========================================");

		init();

		try {
			DescribeAvailabilityZonesResult availabilityZonesResult = ec2
					.describeAvailabilityZones();
			System.out.println("You have access to "
					+ availabilityZonesResult.getAvailabilityZones().size()
					+ " Availability Zones.");

			DescribeInstancesResult describeInstancesRequest = ec2
					.describeInstances();
			List<Reservation> reservations = describeInstancesRequest
					.getReservations();

			Set<Instance> instances = new HashSet<Instance>();
			List<String> adresses = new ArrayList<String>();

			for (Reservation reservation : reservations) {
				instances.addAll(reservation.getInstances());

			}
			for (Instance i : instances) {
				String keyName = i.getKeyName();
				if (keyName != null && keyName.equals("aec-group-2")) {
					System.out.println("-----");
					System.out.println(i.getState());
					System.out.println(i.getKeyName());
					System.out.println(i.getPrivateDnsName());

					System.out.println(i.getPrivateIpAddress());

					System.out.println(i.getPublicIpAddress());
					System.out.println(i.getPublicDnsName());

					System.out.println("-----");
					adresses.add(i.getPublicDnsName());
				}
			}

			System.out.println("There are " + instances.size()
					+ " Amazon EC2 instance(s).");
			System.out.println("Your group has " + adresses.size()
					+ " Amazon EC2 instance(s) running.");
			if (adresses.size() < 3) {
				System.out.println("Not Enough!");
				return;
			}

			File configFile = new File("src/main/resources/Configuration.xml");
			JAXBContext jaxbContext;
			ReplicationConfigurationType config = null;

			try {
				jaxbContext = JAXBContext
						.newInstance(ReplicationConfigurationType.class);

				Unmarshaller jaxbUnmarshaller = jaxbContext
						.createUnmarshaller();
				config = (ReplicationConfigurationType) jaxbUnmarshaller
						.unmarshal(configFile);
				int ipCounter = 0;
				// set addresses of the nodes
				for (NodeType n : config.getReplicationnodes().getNode()) {

					n.setIpadress(adresses.get(ipCounter));
					ipCounter++;
					System.out.println(n.getLabel() + n.getIpadress()
							+ n.getPort());
				}
				// deploy nodes
				for (NodeType n : config.getReplicationnodes().getNode()) {
					deployNode(n, config, 8080);
				}

				logger.info("Configuration parsed successfully.");
			} catch (JAXBException e) {
				logger.fatal("Configuration could not be parsed.");

			}

		} catch (AmazonServiceException ase) {
			System.out.println("Caught Exception: " + ase.getMessage());
			System.out.println("Reponse Status Code: " + ase.getStatusCode());
			System.out.println("Error Code: " + ase.getErrorCode());
			System.out.println("Request ID: " + ase.getRequestId());
		}

	}

}
