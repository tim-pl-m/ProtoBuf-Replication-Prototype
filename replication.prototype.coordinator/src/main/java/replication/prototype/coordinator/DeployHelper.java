package replication.prototype.coordinator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

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
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;

@SpringBootApplication
public class DeployHelper {

	static final Logger logger = LogManager.getLogger(DeployHelper.class
			.getName());
	static final String s = System.getProperty("file.separator");
	private AmazonEC2 ec2;
	/**
	 * This class variable stores all addresses of available e2c regions per
	 * region.
	 */
	private Map<Region, Stack<Instance>> adresses = new HashMap<Region, Stack<Instance>>();

	private String filePath = "src" + s + "main" + s + "resources" + s
			+ "Configuration.xml";

	private ReplicationConfigurationType config = null;

	public DeployHelper(String filePath) {
		this.filePath = filePath;
	}

	public DeployHelper() {
	}

	// login to AWS

	public static void main(String[] args) throws Exception {

		DeployHelper helper = null;

		if (args.length > 0) {
			helper = new DeployHelper("src" + s + "main" + s + "resources" + s
					+ "" + args[0]);
		} else {
			helper = new DeployHelper();
		}
		helper.generalInitializatiom();
		helper.deploy();

	}

	/**
	 * 
	 * @throws JAXBException
	 * @throws Exception
	 */
	private void generalInitializatiom() throws JAXBException {

		File configFile = new File(this.filePath);
		JAXBContext jaxbContext;
		jaxbContext = JAXBContext
				.newInstance(ReplicationConfigurationType.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		this.config = (ReplicationConfigurationType) jaxbUnmarshaller
				.unmarshal(configFile);
		logger.info("Configuration parsed successfully.");

	}

	/**
	 * 
	 * @throws Exception
	 */
	private void initializationForOnlineDeployment() throws Exception {

		/*
		 * The ProfileCredentialsProvider will return your [New EU West
		 * (Ireland) Profile] credential profile by reading from the credentials
		 * file located at (/Users/Tim/.aws/credentials).
		 */
		AWSCredentials credentials = null;
		try {
			credentials = new ProfileCredentialsProvider("AEC")
					.getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException(
					"Cannot load the credentials from the credential profiles file. "
							+ "Please make sure that your credentials file is at the correct "
							+ "location (/Users/Tim/.aws/credentials), and is in valid format.",
					e);
		}
		ec2 = new AmazonEC2Client(credentials);

	}

	/**
	 * 
	 * @param n
	 * @param config
	 * @param port
	 * @throws IOException
	 */
	private void deployNode(NodeType n, ReplicationConfigurationType config,
			int port) throws IOException {
		RestTemplate restTemplate = new RestTemplate();
		String startUrl = "http://" + n.getIpadress() + ":" + port
				+ "/startServer/{thisNode}";

		// debug: check connection and url-parsing
		String runningUrl = "http://" + n.getIpadress() + ":" + port
				+ "/isServerRunning";
		System.out.println(runningUrl);
		Boolean result = restTemplate.getForObject(runningUrl, Boolean.class);
		System.out.println(result);
		// shutdown Server if it is running
		if (result) {
			// /shutDownServer
			String stopUrl = "http://" + n.getIpadress() + ":" + port
					+ "/shutDownServer";

			// debug: check connection and url-parsing
			System.out.println(stopUrl);
			result = restTemplate.getForObject(stopUrl, Boolean.class);
			System.out.println(result);
		}

		// start server with define config(strategy)
		System.out.println(startUrl);
		restTemplate.getMessageConverters().add(
				new Jaxb2RootElementHttpMessageConverter());
		result = restTemplate.postForObject(startUrl, config, Boolean.class,
				n.getLabel());
		System.out.println("start " + n.getLabel() + " successfull: " + result);

	}

	/**
	 * 
	 * @param rg
	 */
	private void getInstanceAdressesForRegion(Region rg) {

		if (!this.adresses.containsKey(rg)) {
			// DescribeAvailabilityZonesResult availabilityZonesResult =
			// ec2.describeAvailabilityZones();
			// System.out.println("You have access to " +
			// availabilityZonesResult.getAvailabilityZones().size() +
			// " Availability Zones.");

			List<String> groupAllowed = new ArrayList<String>();
			groupAllowed.add("aec-group-2");
			groupAllowed.add("aec-group-2-us-east");
			groupAllowed.add("aec-group-2-us-west");
			groupAllowed.add("aec-group-2-singapore");

			Collection<Filter> filters = new ArrayList<Filter>();
			filters.add(new Filter("key-name", groupAllowed));
			DescribeInstancesRequest describeInstancesRequest = new DescribeInstancesRequest();
			describeInstancesRequest.setFilters(filters);
			DescribeInstancesResult describeInstancesResult = ec2
					.describeInstances(describeInstancesRequest);

			List<Reservation> reservations = describeInstancesResult
					.getReservations();
			logger.debug("Foung {} reseverations.", reservations.size());
			Stack<Instance> instances = new Stack<Instance>();

			for (Reservation reservation : reservations) {
				instances.addAll(reservation.getInstances());
			}

			this.adresses.put(rg, instances);
		}

	}

	private void deploy() throws Exception {
		if (this.config.getReplicationnodes().getNode().stream()
				.anyMatch(n -> n.getIpadress() == null)) {
			this.initializationForOnlineDeployment();

		}
		logger.debug("Config file is {}", this.filePath);
		Region currentRegion = null;

		try {
			for (NodeType n : config.getReplicationnodes().getNode().stream()
					.filter(n -> n.getIpadress() == null)
					.collect(Collectors.toList())) {

				n.setWebserverport(8080);
				logger.debug("Node region is {}", n.getLocation());

				currentRegion = Region.getRegion(Regions.valueOf(n
						.getLocation().name()));
				ec2.setRegion(currentRegion);

				getInstanceAdressesForRegion(currentRegion);
				n.setIpadress(this.getNextIpAddress(currentRegion)
						.getPublicDnsName());
				logger.debug(
						"Node with label {} will have public DNS {} and listening on port {}",
						n.getLabel(), n.getIpadress(), n.getPort());

			}
			// deploy nodes
			for (NodeType n : config.getReplicationnodes().getNode()) {
				deployNode(n, config, n.getWebserverport());
			}
			// chill a sec, till system cools down
			Thread.sleep(1000);
			// verify config
			for (NodeType n : config.getReplicationnodes().getNode()) {
				verifyNode(n, config, n.getWebserverport());
			}

		} catch (AmazonServiceException ase) {
			System.out.println("Caught Exception: " + ase.getMessage());
			System.out.println("Reponse Status Code: " + ase.getStatusCode());
			System.out.println("Error Code: " + ase.getErrorCode());
			System.out.println("Request ID: " + ase.getRequestId());
		}

	}

	private void verifyNode(NodeType n, ReplicationConfigurationType config2,
			Integer port) {
		RestTemplate restTemplate = new RestTemplate();
		String runningUrl = "http://" + n.getIpadress() + ":" + port
				+ "/isServerRunning";
		System.out.println(runningUrl);
		Boolean result = restTemplate.getForObject(runningUrl, Boolean.class);
		System.out.println(result);

	}

	private Instance getNextIpAddress(Region currentRegion) throws Exception {
		if (this.adresses.containsKey(currentRegion)
				&& !this.adresses.get(currentRegion).isEmpty()) {
			return this.adresses.get(currentRegion).pop();
		} else
			throw new Exception("There is no instance left..");

	}
}
