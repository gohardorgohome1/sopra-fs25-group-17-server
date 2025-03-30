package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.entity.Exoplanet;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ExoplanetGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ExoplanetPostDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * DTOMapper
 * This class is responsible for generating classes that will automatically
 * transform/map the internal representation
 * of an entity (e.g., the User) to the external/API representation (e.g.,
 * UserGetDTO for getting, UserPostDTO for creating)
 * and vice versa.
 * Additional mappers can be defined for new entities.
 * Always created one mapper for getting information (GET) and one mapper for
 * creating information (POST).
 */
@Mapper
public interface DTOMapper {

  DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

  @Mapping(source = "username", target = "username")
  @Mapping(source = "password", target = "password")
  User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "username", target = "username")
  @Mapping(source = "creation_date", target ="creation_date")
  @Mapping(source = "status", target = "status")
  UserGetDTO convertEntityToUserGetDTO(User user);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "fileName", target = "fileName")
  @Mapping(source = "planet", target = "planet")  //check
  @Mapping(source = "dataPoints", target = "dataPoints")  
  PhotometricCurveGetDTO convertEntityToPhotometricCurveGetDTO(PhotometricCurve photometricCurve);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "time", target = "time")
  @Mapping(source = "brightness", target = "brightness")
  DataPointGetDTO convertEntityToDataPointGetDTO(DataPoint dataPoint);

  @Mapping(source = "planetName", target = "planetName")
  @Mapping(source = "nameHostStar", target = "nameHostStar")
  @Mapping(source = "photometricCurveId", target = "photometricCurve.id") // Assign photometric curve by ID
  Exoplanet convertExoplanetPostDTOtoEntity(ExoplanetPostDTO exoplanetPostDTO);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "planetName", target = "planetName")
  @Mapping(source = "hostStarName", target = "hostStarName")
  @Mapping(source = "photometricCurve.id", target = "photometricCurveId") // Include photometric curve reference
  ExoplanetGetDTO convertEntityToExoplanetGetDTO(Exoplanet exoplanet);

}
