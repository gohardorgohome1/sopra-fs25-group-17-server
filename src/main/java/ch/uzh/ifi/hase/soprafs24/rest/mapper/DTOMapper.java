package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.rest.dto.*;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface DTOMapper {

  DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

  // --- User Mapping ---

  @Mapping(source = "username", target = "username")
  @Mapping(source = "password", target = "password")
  User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "username", target = "username")
  @Mapping(source = "creationDate", target = "creation_date")
  @Mapping(source = "status", target = "status")
  UserGetDTO convertEntityToUserGetDTO(User user);

  // --- Exoplanet Mapping ---

  @Mapping(source = "planetName", target = "planetName")
  @Mapping(source = "hostStarName", target = "hostStarName")
  @Mapping(source = "photometricCurveId", target = "photometricCurveId")
  Exoplanet convertExoplanetPostDTOtoEntity(ExoplanetPostDTO dto);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "planetName", target = "planetName")
  @Mapping(source = "hostStarName", target = "hostStarName")
  @Mapping(source = "photometricCurveId", target = "photometricCurveId")
  @Mapping(source = "comments", target = "comments")
  ExoplanetGetDTO convertEntityToExoplanetGetDTO(Exoplanet exoplanet);

  // --- PhotometricCurve Mapping ---

  @Mapping(source = "id", target = "id")
  @Mapping(source = "fileName", target = "fileName")
  @Mapping(source = "exoplanetId", target = "exoplanetId")
  @Mapping(source = "dataPoints", target = "dataPoints")
  @Mapping(source = "metadata", target = "metadata")
  PhotometricCurveGetDTO convertEntityToPhotometricCurveGetDTO(PhotometricCurve photometricCurve);

  // --- DataPoint Mapping ---

  @Mapping(source = "time", target = "time")
  @Mapping(source = "brightness", target = "brightness")
  @Mapping(source = "brightnessError", target = "brightnessError")
  DataPointGetDTO convertEntityToDataPointGetDTO(DataPoint dataPoint);

  List<DataPointGetDTO> convertDataPointList(List<DataPoint> dataPoints);

  @Mapping(source = "userId", target = "userId")
  @Mapping(source = "message", target = "message")
  @Mapping(source = "createdAt", target = "createdAt")
  CommentGetDTO convertCommentToCommentDTO(Exoplanet.Comment comment);

  List<CommentGetDTO> convertCommentList(List<Exoplanet.Comment> comments);
}
