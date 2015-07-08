package replication.prototype.server.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
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

  static final Logger logger = LogManager.getLogger(StarterController.class.getName());
  private long timeOffset = 0;
  private Server server = null;


  @RequestMapping(value = "/setOffset", method = RequestMethod.POST, produces = "application/json",
      consumes = "application/json")
  public boolean setOffset(@RequestBody long offset) {
    this.timeOffset = offset;
    return true;
  }

  @RequestMapping(value = "/getCurrentTime", method = RequestMethod.GET,
      produces = "application/json")
  public long setOffset() {
    return new java.util.Date().getTime();
  }


  @RequestMapping(value = "/startServer/{thisNode}", method = RequestMethod.POST,
      produces = "application/json", consumes = "application/xml")
  public boolean startServer(@RequestBody ReplicationConfigurationType config,
      @PathVariable String thisNode) {
    logger.debug("Received request to start server {}", thisNode);
    try {
      if (this.server != null) {
        logger.debug("Another server instance seems to be running.");
        this.server.setConfig(config);
        this.server.rebootWithNewConfig();
      } else {
        this.server = new Server(config, thisNode, this.timeOffset);
        this.server.boot();
      }



    } catch (UnknownHostException | UnknownIdentityException e) {
      logger.fatal("The server's identity could not be determined");
      return false;
    } catch (IOException e) {
      logger.fatal("Fatal communication error. Application is no longer runnable {}.", e);
      return false;
    } catch (JAXBException e) {
      logger.error("Error while marshalling business objects.");
      return false;
    }

    return true;
  }


  @RequestMapping(value = "/isServerRunning")
  public boolean isServerRunning() {
    logger.debug("Request for server's state");
    return this.server != null;
  }



  @RequestMapping(value = "/getCurrentCommitLog", method = RequestMethod.GET)
  public void getFile(HttpServletResponse response) throws IOException {

    InputStream is = new FileInputStream("commits.log");
    IOUtils.copy(is, response.getOutputStream());
    response.flushBuffer();
  }



  public static void main(String[] args) throws Exception {

    SpringApplication.run(StarterController.class, args);

  }
}
