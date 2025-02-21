package io.muehlbachler.bswe.service.impl;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.muehlbachler.bswe.configuration.ApiConfiguration;
import io.muehlbachler.bswe.model.location.Coordinates;
import io.muehlbachler.bswe.service.AviationService;
import io.muehlbachler.bswe.service.model.nearestairport.NearestAirportResult;
import io.muehlbachler.bswe.service.model.nearestairport.NearestAirportResultStation;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Implementation of the {@link AviationService} interface.
 */
@AllArgsConstructor
@Service
public class AviationServiceImpl implements AviationService {
  private static final Logger LOG = LoggerFactory.getLogger(AviationServiceImpl.class);

  @Autowired
  private final ApiConfiguration apiConfiguration;
  @Autowired
  private final RestTemplate restTemplate;

  @CircuitBreaker(name = "getNearestAirport", fallbackMethod = "getNearestAirportFallback")
  @Retry(name = "getNearestAirport", fallbackMethod = "getNearestAirportFallback")
  @Override
  public NearestAirportResultStation getNearestAirport(final Coordinates coordinates) {
    if (coordinates == null) {
      return null;
    }

    LOG.info("fetching nearest airport for coordinates {}", coordinates);

    try {
      final MultiValueMap<String, String> headers = new HttpHeaders();
      headers.add(apiConfiguration.getNearestAirport().getAuthorization().getHeader(),
          apiConfiguration.getNearestAirport().getAuthorization().getValue());
      final HttpEntity<String> request = new HttpEntity<>(headers);
      final ResponseEntity<NearestAirportResult[]> result = restTemplate.exchange(
          apiConfiguration.getNearestAirport().getUrl(),
          HttpMethod.GET, request, NearestAirportResult[].class, coordinates.getLatitude(), coordinates.getLongitude());
      if (result == null || result.getStatusCode() != HttpStatus.OK || !result.hasBody()) {
        return null;
      }

      final NearestAirportResult[] results = result.getBody();
      if (results == null || results.length == 0) {
        return null;
      }
      LOG.debug("nearest airport for coordinates {}: {}", coordinates, results[0].getStation());
      return results[0].getStation();
    } catch (RestClientException e) {
      LOG.error("failed to parse airport response: {}", e.getMessage());
      return null;
    }
  }

  /**
   * Fallback method for the getNearestAirport method in case of a failure.
   *
   * @param ex the exception that caused the failure
   * @return null
   */
  protected NearestAirportResultStation getNearestAirportFallback(final Exception ex) {
    LOG.error("failed to fetch nearest airport", ex);
    return null;
  }
}
