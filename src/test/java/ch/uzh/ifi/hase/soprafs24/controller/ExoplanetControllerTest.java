package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Exoplanet;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ExoplanetPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ExoplanetGetDTO;
import ch.uzh.ifi.hase.soprafs24.service.ExoplanetService;
import ch.uzh.ifi.hase.soprafs24.repository.ExoplanetRepository;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@WebMvcTest(ExoplanetController.class)
public class ExoplanetControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ExoplanetService exoplanetService;

  @MockBean
  private DTOMapper dtoMapper;

  @MockBean
  private ExoplanetRepository exoplanetRepository;

  @MockBean
  private UserService userService;

  @MockBean
  private SimpMessagingTemplate messagingTemplate;

  // Delete Exoplanet by ID
  // Tests: Delete /exoplanets/1 Status: 204 NO_CONTENT
  @Test
  public void whenDeleteExoplanet_thenStatus204() throws Exception {

    mockMvc.perform(delete("/exoplanets/1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent()); // has no return value

    verify(exoplanetService, times(1)).deleteExoplanet("1"); // verify that it is called once
  }
  // Get ranking (with asc and dsc)
  // Tests: Get /exoplanets/ranking Status: 200 OK
  @Test
  public void givenListOfExoplanets_whenGetRanking_thenCallRepository() throws Exception {

    Exoplanet exoplanet1 = new Exoplanet();
    exoplanet1.setPlanetName("exoplanet1");
    exoplanet1.setId("1");
    Exoplanet exoplanet2 = new Exoplanet();
    exoplanet2.setPlanetName("exoplanet2");
    exoplanet2.setId("2");

    given(exoplanetService.getExoplanetRanking("mass", "asc"))
      .willReturn(List.of(exoplanet1, exoplanet2));

    mockMvc.perform(get("/exoplanets/ranking")
      .param("sortBy", "mass")
      .param("order", "asc")
      .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$", hasSize(2)))
      .andExpect(jsonPath("$[0].planetName").value("exoplanet1"))
      .andExpect(jsonPath("$[0].id").value("1"))
      .andExpect(jsonPath("$[1].planetName").value("exoplanet2"))
      .andExpect(jsonPath("$[1].id").value("2"));

    given(exoplanetService.getExoplanetRanking("esi", "desc")) // Testing a second possibility of sorting & order
      .willReturn(List.of(exoplanet2, exoplanet1));

    mockMvc.perform(get("/exoplanets/ranking")
      .param("sortBy", "esi")
      .param("order", "desc")
      .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$", hasSize(2)))
      .andExpect(jsonPath("$[0].planetName").value("exoplanet2"))
      .andExpect(jsonPath("$[0].id").value("2"))
      .andExpect(jsonPath("$[1].planetName").value("exoplanet1"))
      .andExpect(jsonPath("$[1].id").value("1"));
  }
  // Get ranking with invalid sorting & order
  // Tests: Get /exoplanets/ranking Status: 400 BAD_REQUEST
  @Test
  public void whenGetRankingWrongParameters_thenThrowException() throws Exception {
    // No need to initialize exoplanets since the test checks for Status code 400 (with correct sortBy but missing Exoplanets, Status code 200 would be the case -> Test would fail anyway)
    mockMvc.perform(get("/exoplanets/ranking")
      .param("sortBy", "mas") // mass spelled incorrectly
      .param("order", "asc")
      .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

    mockMvc.perform(get("/exoplanets/ranking")
      .param("sortBy", "temperature")
      .param("order", "dsc") // desc spelled incorrectly
      .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  /**
   * Helper Method to convert userPostDTO into a JSON string such that the input
   * can be processed
   * Input will look like this: {"name": "Test User", "username": "testUsername"}
   * 
   * @param object
   * @return string
   */
  private String asJsonString(final Object object) {
    try {
      return new ObjectMapper().writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format("The request body could not be created.%s", e.toString()));
    }
  }
}
