package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Exoplanet;
import ch.uzh.ifi.hase.soprafs24.repository.ExoplanetRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.CommentPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.CommentGetDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.doThrow;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

import java.beans.Transient;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

public class ExoplanetServiceTest {

  @Mock
  private ExoplanetRepository exoplanetRepository;

  @InjectMocks
  private ExoplanetService exoplanetService;

  private Exoplanet testExoplanet;
  private Exoplanet testExoplanet2;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);

    // given
    testExoplanet = new Exoplanet();
    testExoplanet.setId("1");
    testExoplanet.setPlanetName("testExoplanet");
    testExoplanet2 = new Exoplanet();
    testExoplanet2.setId("2");
    testExoplanet2.setPlanetName("testExoplanet2");

    // when -> any object is being saved in the userRepository -> return the dummy
    // testExoplanet
    Mockito.when(exoplanetRepository.save(Mockito.any())).thenReturn(testExoplanet);
  }
  // Delete an Exoplanet with valid Input
  @Test
  public void deleteExoplanet_validInputs_success() {

    // when -> any object is being saved in the exoplanetRepository -> return the dummy
    // testExoplanet
    when(exoplanetRepository.findById("1")).thenReturn(Optional.of(testExoplanet));
    exoplanetService.deleteExoplanet("1");

    // then
    verify(exoplanetRepository, times(1)).findById("1");
    verify(exoplanetRepository, times(1)).delete(testExoplanet);
  }
  // Delete an Exoplanet with invalid Input
  @Test
  public void deleteExoplanet_invalidInputs_trowsResponseStatusException() {

    // when -> any object is being saved in the exoplanetRepository -> return the dummy
    // testExoplanet
    doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Exoplanet not found"))
      .when(exoplanetRepository).findById("99");

    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
      exoplanetService.deleteExoplanet("99");
    });
    
    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    assertEquals("Exoplanet not found", exception.getReason());
  }
  // Get Ranking with valid input (Check all possible cases)
  @Test
  public void getRanking_validInputs_success() {
    when(exoplanetRepository.findAllByOrderByMassAsc()).thenReturn(List.of(testExoplanet, testExoplanet2));
    when(exoplanetRepository.findAllByOrderByRadiusAsc()).thenReturn(List.of(testExoplanet, testExoplanet2));
    when(exoplanetRepository.findAllByOrderByTheoreticalTemperatureAsc()).thenReturn(List.of(testExoplanet, testExoplanet2));
    when(exoplanetRepository.findAllByOrderByDensityAsc()).thenReturn(List.of(testExoplanet, testExoplanet2));
    when(exoplanetRepository.findAllByOrderByEarthSimilarityIndexAsc()).thenReturn(List.of(testExoplanet, testExoplanet2));
    when(exoplanetRepository.findAllByOrderByMassDesc()).thenReturn(List.of(testExoplanet2, testExoplanet));
    when(exoplanetRepository.findAllByOrderByRadiusDesc()).thenReturn(List.of(testExoplanet2, testExoplanet));
    when(exoplanetRepository.findAllByOrderByTheoreticalTemperatureDesc()).thenReturn(List.of(testExoplanet2, testExoplanet));
    when(exoplanetRepository.findAllByOrderByDensityDesc()).thenReturn(List.of(testExoplanet2, testExoplanet));
    when(exoplanetRepository.findAllByOrderByEarthSimilarityIndexDesc()).thenReturn(List.of(testExoplanet2, testExoplanet));

    List<Exoplanet> ranking1 = exoplanetService.getExoplanetRanking("mass", "asc");
    assertEquals(List.of(testExoplanet, testExoplanet2), ranking1);
    List<Exoplanet> ranking2 = exoplanetService.getExoplanetRanking("radius", "asc");
    assertEquals(List.of(testExoplanet, testExoplanet2), ranking2);
    List<Exoplanet> ranking3 = exoplanetService.getExoplanetRanking("temperature", "asc");
    assertEquals(List.of(testExoplanet, testExoplanet2), ranking3);
    List<Exoplanet> ranking4 = exoplanetService.getExoplanetRanking("density", "asc");
    assertEquals(List.of(testExoplanet, testExoplanet2), ranking4);
    List<Exoplanet> ranking5 = exoplanetService.getExoplanetRanking("ESI", "asc"); // Also tests all capital letters
    assertEquals(List.of(testExoplanet, testExoplanet2), ranking5);
    List<Exoplanet> ranking6 = exoplanetService.getExoplanetRanking("mass", "desc");
    assertEquals(List.of(testExoplanet2, testExoplanet), ranking6);
    List<Exoplanet> ranking7 = exoplanetService.getExoplanetRanking("radius", "desc");
    assertEquals(List.of(testExoplanet2, testExoplanet), ranking7);
    List<Exoplanet> ranking8 = exoplanetService.getExoplanetRanking("temperature", "desc");
    assertEquals(List.of(testExoplanet2, testExoplanet), ranking8);
    List<Exoplanet> ranking9 = exoplanetService.getExoplanetRanking("density", "desc");
    assertEquals(List.of(testExoplanet2, testExoplanet), ranking9);
    List<Exoplanet> ranking10 = exoplanetService.getExoplanetRanking("esi", "desc");
    assertEquals(List.of(testExoplanet2, testExoplanet), ranking10);
  }
  // Get Ranking with invalid input (only checking sorting critereum since ordering switches to descending on every input that is not "asc")
  @Test
  public void getExoplanet_invalidInput_throwsResponseStatusException() {

    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
      exoplanetService.getExoplanetRanking("mas", "asc"); // Typo in sorting criterium
    });
  }
  @Test
  public void addComment_validInput_success() {

  // given
  CommentPostDTO commentPostDTO = new CommentPostDTO();
  commentPostDTO.setUserId("user123");
  commentPostDTO.setMessage("Amazing discovery!");
  CommentPostDTO commentPostDTO2 = new CommentPostDTO(); // Creating a second comment to make sure it does not overwrite the first one
  commentPostDTO2.setUserId("another_user");
  commentPostDTO2.setMessage("Thank you!");

  when(exoplanetRepository.findById("1")).thenReturn(Optional.of(testExoplanet));

  // when
  exoplanetService.addComment("1", commentPostDTO);
  exoplanetService.addComment("1", commentPostDTO2);

  // then
  assertEquals(2, testExoplanet.getComments().size());

  Exoplanet.Comment addedComment0 = testExoplanet.getComments().get(0);
  assertEquals("user123", addedComment0.getUserId());
  assertEquals("Amazing discovery!", addedComment0.getMessage());
  assertNotNull(addedComment0.getCreatedAt());

  Exoplanet.Comment addedComment1 = testExoplanet.getComments().get(1);
  assertEquals("another_user", addedComment1.getUserId());
  assertEquals("Thank you!", addedComment1.getMessage());
  assertNotNull(addedComment1.getCreatedAt());

  verify(exoplanetRepository, times(2)).save(testExoplanet);
  }
  @Test
  public void getComments_validInput_success() {

  // given
  CommentPostDTO commentPostDTO = new CommentPostDTO();
  commentPostDTO.setUserId("user123");
  commentPostDTO.setMessage("Amazing discovery!");
  CommentPostDTO commentPostDTO2 = new CommentPostDTO(); // Creating a second comment to make sure it does not overwrite the first one
  commentPostDTO2.setUserId("another_user");
  commentPostDTO2.setMessage("Thank you!");

  when(exoplanetRepository.findById("1")).thenReturn(Optional.of(testExoplanet));

  exoplanetService.addComment("1", commentPostDTO);
  exoplanetService.addComment("1", commentPostDTO2);

  // when
  List<CommentGetDTO> comments = exoplanetService.getComments("1");

  // then
  assertEquals(2, comments.size());
  assertEquals("Amazing discovery!", comments.get(0).getMessage());
  assertEquals("Thank you!", comments.get(1).getMessage());
  }
}