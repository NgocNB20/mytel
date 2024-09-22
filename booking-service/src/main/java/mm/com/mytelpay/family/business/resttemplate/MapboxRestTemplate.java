package mm.com.mytelpay.family.business.resttemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mm.com.mytelpay.family.business.BaseBusiness;
import mm.com.mytelpay.family.business.bookingcar.dto.CalculatorDistanceReqDTO;
import mm.com.mytelpay.family.business.bookingcar.dto.MapboxDirectionDTO;
import mm.com.mytelpay.family.business.bookingcar.dto.MapboxDirectionDTO.Route;
import mm.com.mytelpay.family.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

@Component
public class MapboxRestTemplate extends BaseBusiness {

    @Autowired
    public RestTemplate restTemplate;

    @Value("${external.mapbox.url}")
    private String mapboxBaseUrl;

    @Value("${external.mapbox.direction.driving}")
    private String directionDriving;

    @Value("${external.mapbox.access_token}")
    private String mapboxAccessToken;

    double SEMI_MAJOR_AXIS_MT = 6378137;
    double SEMI_MINOR_AXIS_MT = 6356752.314245;
    double FLATTENING = 1 / 298.257223563;
    double ERROR_TOLERANCE = 1e-12;

    public MapboxDirectionDTO getDirectionDriving(CalculatorDistanceReqDTO distanceReqDTO, String requestId) {
      MapboxDirectionDTO directionDTO;
        try {
            Map<String, String> uriVariables = new HashMap<>();
            ResponseEntity<MapboxDirectionDTO> commonResponse = restTemplate.getForEntity(mapboxBaseUrl.concat(directionDriving)
                    .concat(String.format("%s,%s;%s,%s", distanceReqDTO.getFromLongitude(), distanceReqDTO.getFromLatitude(),
                        distanceReqDTO.getToLongitude(), distanceReqDTO.getToLatitude())
                    .concat("?overview=full")
                    .concat("&access_token=" + mapboxAccessToken)
                    ),
                MapboxDirectionDTO.class, uriVariables);
            logger.info(Constants.LOG_INFO_REST_TEMPLATE, directionDriving, commonResponse.getStatusCode(), uriVariables);
            if (ObjectUtils.isEmpty(commonResponse)) {
                throw new Exception("commonResponse is null or empty");
            }
            directionDTO = commonResponse.getBody();
            if (ObjectUtils.isEmpty(directionDTO)) {
                throw new Exception("directionDTO is null or empty");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            double distance = calculateDistance(distanceReqDTO.getFromLatitude(), distanceReqDTO.getFromLongitude(),
               distanceReqDTO.getToLatitude(), distanceReqDTO.getToLongitude());
            directionDTO = new MapboxDirectionDTO();
            Route route = new Route();
            route.setDistance(distance);
            directionDTO.setRoutes(List.of(route));
        }
        return directionDTO;
    }

    private double calculateDistance(double latitude1, double longitude1, double latitude2, double longitude2) {
      double U1 = Math.atan((1 - FLATTENING) * Math.tan(Math.toRadians(latitude1)));
      double U2 = Math.atan((1 - FLATTENING) * Math.tan(Math.toRadians(latitude2)));

      double sinU1 = Math.sin(U1);
      double cosU1 = Math.cos(U1);
      double sinU2 = Math.sin(U2);
      double cosU2 = Math.cos(U2);

      double longitudeDifference = Math.toRadians(longitude2 - longitude1);
      double previousLongitudeDifference;

      double sinSigma, cosSigma, sigma, sinAlpha, cosSqAlpha, cos2SigmaM;

      do {
        sinSigma = Math.sqrt(Math.pow(cosU2 * Math.sin(longitudeDifference), 2) +
            Math.pow(cosU1 * sinU2 - sinU1 * cosU2 * Math.cos(longitudeDifference), 2));
        cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * Math.cos(longitudeDifference);
        sigma = Math.atan2(sinSigma, cosSigma);
        sinAlpha = cosU1 * cosU2 * Math.sin(longitudeDifference) / sinSigma;
        cosSqAlpha = 1 - Math.pow(sinAlpha, 2);
        cos2SigmaM = cosSigma - 2 * sinU1 * sinU2 / cosSqAlpha;
        if (Double.isNaN(cos2SigmaM)) {
          cos2SigmaM = 0;
        }
        previousLongitudeDifference = longitudeDifference;
        double C = FLATTENING / 16 * cosSqAlpha * (4 + FLATTENING * (4 - 3 * cosSqAlpha));
        longitudeDifference = Math.toRadians(longitude2 - longitude1) + (1 - C) * FLATTENING * sinAlpha *
            (sigma + C * sinSigma * (cos2SigmaM + C * cosSigma * (-1 + 2 * Math.pow(cos2SigmaM, 2))));
      } while (Math.abs(longitudeDifference - previousLongitudeDifference) > ERROR_TOLERANCE);

      double uSq = cosSqAlpha * (Math.pow(SEMI_MAJOR_AXIS_MT, 2) - Math.pow(SEMI_MINOR_AXIS_MT, 2)) / Math.pow(SEMI_MINOR_AXIS_MT, 2);

      double A = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
      double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));

      double deltaSigma = B * sinSigma * (cos2SigmaM + B / 4 * (cosSigma * (-1 + 2 * Math.pow(cos2SigmaM, 2))
          - B / 6 * cos2SigmaM * (-3 + 4 * Math.pow(sinSigma, 2)) * (-3 + 4 * Math.pow(cos2SigmaM, 2))));

      double distanceMt = SEMI_MINOR_AXIS_MT * A * (sigma - deltaSigma);
      return distanceMt / 1000;
    }
}