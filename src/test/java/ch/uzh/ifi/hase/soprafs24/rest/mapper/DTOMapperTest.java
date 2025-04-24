package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.entity.Exoplanet;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ExoplanetGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ExoplanetPostDTO;
import ch.uzh.ifi.hase.soprafs24.entity.PhotometricCurve;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PhotometricCurveGetDTO;
import ch.uzh.ifi.hase.soprafs24.entity.DataPoint;
import ch.uzh.ifi.hase.soprafs24.rest.dto.DataPointGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.CommentGetDTO;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * DTOMapperTest
 * Tests if the mapping between the internal and the external/API representation
 * works.
 */
public class DTOMapperTest {
  @Test
  public void testCreateUser_fromUserPostDTO_toUser_success() {
    // create UserPostDTO
    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setUsername("username");

    // MAP -> Create user
    User user = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

    // check content
    assertEquals(userPostDTO.getUsername(), user.getUsername());
  }

  @Test
  public void testGetUser_fromUser_toUserGetDTO_success() {
    // create User
    User user = new User();
    user.setUsername("firstname@lastname");
    user.setStatus(UserStatus.OFFLINE);
    user.setToken("1");

    // MAP -> Create UserGetDTO
    UserGetDTO userGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);

    // check content
    assertEquals(user.getId(), userGetDTO.getId());
    assertEquals(user.getUsername(), userGetDTO.getUsername());
    assertEquals(user.getStatus(), userGetDTO.getStatus());
  }

  @Test
  public void testCreateExoplanet_fromExoplanetPostDTO_toExoplanet_success() {
    // create ExoplanetPostDTO
    ExoplanetPostDTO exoplanetPostDTO = new ExoplanetPostDTO();
    exoplanetPostDTO.setPlanetName("planetName");

    // MAP -> Create user
    Exoplanet exoplanet = DTOMapper.INSTANCE.convertExoplanetPostDTOtoEntity(exoplanetPostDTO);

    // check content
    assertEquals(exoplanetPostDTO.getPlanetName(), exoplanet.getPlanetName());
  }

  @Test
  public void testGetExoplanet_fromExoplanet_toExoplanetGetDTO_success() {

    // create Exoplanet
    Exoplanet exoplanet = new Exoplanet();
    exoplanet.setId("1");
    exoplanet.setPlanetName("planet");
    exoplanet.setHostStarName("hostStar");
    exoplanet.setPhotometricCurveId("2");
    Exoplanet.Comment comment1 = exoplanet.new Comment();
    comment1.setUserId("user123");
    comment1.setMessage("Amazing discovery!");

    // MAP -> Create ExoplanetGetDTO
    ExoplanetGetDTO exoplanetGetDTO = DTOMapper.INSTANCE.convertEntityToExoplanetGetDTO(exoplanet);

    // check content
    assertEquals(exoplanet.getId(), exoplanetGetDTO.getId());
    assertEquals(exoplanet.getPlanetName(), exoplanetGetDTO.getPlanetName());
    assertEquals(exoplanet.getHostStarName(), exoplanetGetDTO.getHostStarName());
    assertEquals(exoplanet.getPhotometricCurveId(), exoplanetGetDTO.getPhotometricCurveId());
    assertEquals(exoplanet.getComments(), exoplanetGetDTO.getComments());
  }

  @Test
  public void testGetPhotometricCurve_fromPhotometricCurve_toPhotometricCurveGetDTO_success() {

    DataPoint dataPoint = new DataPoint();
    DataPoint dataPoint2 = new DataPoint();
    dataPoint.setBrightness(2.2F);
    dataPoint2.setTime(1D);

    // create PhotometricCurve
    PhotometricCurve photometricCurve = new PhotometricCurve();
    photometricCurve.setId("4");
    photometricCurve.setFileName("curve");
    photometricCurve.setExoplanetId("3");
    photometricCurve.setDataPoints(List.of(dataPoint, dataPoint2));
    photometricCurve.setMetadata(Map.of("MD1", "MD1-value", "MD2", "MD2-value"));

    // MAP -> Create PhotometricCurveGetDTO
    PhotometricCurveGetDTO photometricCurveGetDTO = DTOMapper.INSTANCE.convertEntityToPhotometricCurveGetDTO(photometricCurve);

    // check content
    assertEquals(photometricCurve.getId(), photometricCurveGetDTO.getId());
    assertEquals(photometricCurve.getFileName(), photometricCurveGetDTO.getFileName());
    assertEquals(photometricCurve.getExoplanetId(), photometricCurveGetDTO.getExoplanetId());
    assertEquals(photometricCurve.getDataPoints().get(0).getBrightness(), photometricCurveGetDTO.getDataPoints().get(0).getBrightness());
    assertEquals(photometricCurve.getDataPoints().get(1).getTime(), photometricCurveGetDTO.getDataPoints().get(1).getTime());
    assertEquals(photometricCurve.getMetadata(), photometricCurveGetDTO.getMetadata());
  }

  @Test
  public void testGetDataPoint_fromDataPoint_toDataPointGetDTO_success() {

    // create DataPoint
    DataPoint dataPoint = new DataPoint();
    dataPoint.setTime(2.2D);
    dataPoint.setBrightness(2F);
    dataPoint.setBrightnessError(1F);

    // MAP -> Create DataPointGetDTO
    DataPointGetDTO dataPointGetDTO = DTOMapper.INSTANCE.convertEntityToDataPointGetDTO(dataPoint);

    // check content
    assertEquals(dataPoint.getTime(), dataPointGetDTO.getTime());
    assertEquals(dataPoint.getBrightness(), dataPointGetDTO.getBrightness());
    assertEquals(dataPoint.getBrightnessError(), dataPointGetDTO.getBrightnessError());
  }

  @Test
  public void testComment_fromComment_toCommentGetDTO_success() {

    // create Comment
    Exoplanet exoplanet = new Exoplanet();
    Exoplanet.Comment comment = new Exoplanet().new Comment();
    comment.setUserId("1");
    comment.setMessage("Great discovery!");
    comment.setCreatedAt(LocalDateTime.of(2025, 1, 1, 0, 0));

    // MAP -> Create CommentGetDTO
    CommentGetDTO commentGetDTO = DTOMapper.INSTANCE.convertCommentToCommentDTO(comment);

    // check content
    assertEquals(comment.getUserId(), commentGetDTO.getUserId());
    assertEquals(comment.getMessage(), commentGetDTO.getMessage());
    assertEquals(comment.getCreatedAt(), commentGetDTO.getCreatedAt());
  }
}
