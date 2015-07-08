package replication.prototype.coordinator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.client.RestTemplate;

public class TimeSynchronizer {
  
  final static int tries = 30;
  static final Logger logger = LogManager.getLogger(TimeSynchronizer.class
      .getName());
  
  public void determineAndSetOffsetForHost(String host)
  {
    long summedEstimatedOffset = 0l;
    long beforeRequest = 0;
    long afterResponse = 0;
    long estimatedOffset = 0;
    RestTemplate restTemplate = null;
    logger.debug("trying to estimmate offset");
    
    for(int i = 0; i < tries; i++) {
      restTemplate = new RestTemplate();
      beforeRequest = System.currentTimeMillis() ;
      Long result = restTemplate.getForObject(host+"/getCurrentTime", Long.class);
      afterResponse = System.currentTimeMillis() ;
      estimatedOffset = Math.round(result - (0.5*(double)(afterResponse-beforeRequest)) - beforeRequest);
      logger.debug("Estimated offset in try {} is {}", i, estimatedOffset);
      summedEstimatedOffset+= estimatedOffset;
    }
    
    estimatedOffset = Math.round(((double)summedEstimatedOffset)/tries);
    logger.debug("Overall estimated offsetis {}", estimatedOffset);

    restTemplate = new RestTemplate();
    restTemplate.postForObject(host+"/setOffset", estimatedOffset, Boolean.class);
  }

}
