package replication.prototype.server.controller;

import java.io.IOException;
import java.net.UnknownHostException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import replication.prototype.server.Server;
import replication.prototype.server.environment.ReplicationConfigurationType;
import replication.prototype.server.environment.UnknownIdentityException;

@RestController
@EnableAutoConfiguration
public class StarterController {

	static final Logger logger = LogManager.getLogger(StarterController.class
			.getName());
	private Server server = null;

	@RequestMapping(value = "/startServer/{thisNode}", method = RequestMethod.POST, produces = "application/xml", consumes = "application/xml")
	public boolean startServer(
			@RequestBody ReplicationConfigurationType config,
			@PathVariable String thisNode) {
		logger.debug("Received request to start server {}", thisNode);
		try {
			if (this.server != null) {
				logger.debug("Another server instance seems to be running.");

				try {
					this.server.shutDown();
				} catch (InterruptedException e) {
					logger.fatal("Another instance of a server is already running. This instance could not have been stopped.");
					return false;

				}
				logger.debug("That instance has been stopped.");
			}

			this.server = new Server(config, thisNode);
			this.server.boot();

		} catch (UnknownHostException | UnknownIdentityException e) {
			logger.fatal("The server's identity could not be determined");
			return false;
		} catch (IOException e) {
			logger.fatal("Fatal communication error. Application is no longer runnable.");
			return false;
		}

		return true;
	}

	// TODO delete if not used later
	public boolean startServerlocally(ReplicationConfigurationType config,
			String thisNode) {
		logger.debug("start server {}", thisNode);
		try {
			if (this.server != null) {
				logger.debug("Another server instance seems to be running.");

				try {
					this.server.shutDown();
				} catch (InterruptedException e) {
					logger.fatal("Another instance of a server is already running. This instance could not have been stopped.");
					return false;

				}
				logger.debug("That instance has been stopped.");
			}

			this.server = new Server(config, thisNode);
			this.server.boot();

		} catch (UnknownHostException | UnknownIdentityException e) {
			logger.fatal("The server's identity could not be determined");
			return false;
		} catch (IOException e) {
			logger.fatal("Fatal communication error. Application is no longer runnable.");
			return false;
		}

		return true;
	}

	@RequestMapping(value = "/deploy/{mode}")
	public boolean deploy(@RequestBody ReplicationConfigurationType rep,
			String mode) {
		return false;
	}

	@RequestMapping(value = "/isServerRunning")
	public boolean isServerRunning() {
		logger.debug("Request for server's state");
		return this.server != null;
	}

	@RequestMapping(value = "/shutDownServer")
	public boolean shutdownServer() {
		if (this.server != null) {
			try {
				return this.server.shutDown();
			} catch (InterruptedException e) {
				logger.fatal("The server could not be stopped.    ");
				return false;
			}
		}
		return true;
	}

	public static void main(String[] args) throws Exception {

		if (args.length > 0) {
			// if (args[0].equals("local"))
			// System.out.println("set port dependend on xml");
			// else
			SpringApplication.run(StarterController.class, args);
		}
	}

}
