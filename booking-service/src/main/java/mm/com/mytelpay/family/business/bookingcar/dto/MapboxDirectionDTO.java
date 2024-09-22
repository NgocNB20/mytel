package mm.com.mytelpay.family.business.bookingcar.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MapboxDirectionDTO {

  private List<Route> routes;
  private List<Waypoint> waypoints;
  private String code;
  private String uuid;

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Route {

    private String weightName;
    private Double weight;
    private Double duration;
    private Double distance;
    private List<Leg> legs;
    private String geometry;
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Waypoint {

    private Double distance;
    private String name;
    private List<Double> location;
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Leg {

    private List<Object> viaWaypoints ;
    private List<Object> admins ;
    private Double weight;
    private Double duration;
    private List<Object> steps ;
    private Double distance;
    private String summary;
  }
}
