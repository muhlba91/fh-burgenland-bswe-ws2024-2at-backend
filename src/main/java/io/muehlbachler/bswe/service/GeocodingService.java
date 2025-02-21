package io.muehlbachler.bswe.service;

import io.muehlbachler.bswe.model.location.Coordinates;

/**
 * A service to handle all geocoding related actions.
 */
public interface GeocodingService {
  /**
   * Fetches the coordinates for the given location.
   *
   * @param location the location for which the coordinates should be fetched
   * @return the coordinates for the given location
   */
  Coordinates fetchCoordinates(String location);
}
