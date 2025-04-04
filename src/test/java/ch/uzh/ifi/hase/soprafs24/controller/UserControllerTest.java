/*
package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
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

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
/*
@WebMvcTest(UserController.class)
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @Test

  public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
    // given
    User user = new User();
    user.setUsername("firstname@lastname");
    user.setStatus(UserStatus.OFFLINE);

    List<User> allUsers = Collections.singletonList(user);

    // this mocks the UserService -> we define above what the userService should
    // return when getUsers() is called
    given(userService.getUsers()).willReturn(allUsers);

    // when
    MockHttpServletRequestBuilder getRequest = get("/users").contentType(MediaType.APPLICATION_JSON);

    // then
    mockMvc.perform(getRequest).andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].username", is(user.getUsername())))
        .andExpect(jsonPath("$[0].status", is(user.getStatus().toString())));
  }
  @Test
  // Tests: GET /users/[userId] Status: 200 OK
  public void getUserById_UserExists_ShouldReturnUser() throws Exception {
    // given
    User user = new User();
    user.setId(1L);
    user.setUsername("testUsername");
    user.setStatus(UserStatus.ONLINE);

    given(userService.getUserById(1L)).willReturn(user);

    // when
    MockHttpServletRequestBuilder getRequest = get("/users/1")
        .contentType(MediaType.APPLICATION_JSON);

    // then
    mockMvc.perform(getRequest)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(user.getId().intValue())))
        .andExpect(jsonPath("$.username", is(user.getUsername())))
        .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
  }

  @Test
  // Tests: GET /users/[userId] Status: 404 ERROR
  public void user_with_UserId_not_found() throws Exception {
    // given
    given(userService.getUserById(99L))
        .willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    // when
    MockHttpServletRequestBuilder getRequest = get("/users/99")
        .contentType(MediaType.APPLICATION_JSON);

    // then
    mockMvc.perform(getRequest)
        .andExpect(status().isNotFound()); // 404 Not Found


  }




  @Test
  //Tests: POST /users Status: 201 OK
  public void createUser_validInput_userCreated() throws Exception {
    // given
    User user = new User();
    user.setId(1L);
    user.setUsername("testUsername");
    user.setToken("1");
    user.setStatus(UserStatus.ONLINE);

    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setUsername("testUsername");

    given(userService.createUser(Mockito.any())).willReturn(user);

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder postRequest = post("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(userPostDTO));

    // then
    mockMvc.perform(postRequest)
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(user.getId().intValue())))
        .andExpect(jsonPath("$.username", is(user.getUsername())))
        .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
  }

  @Test
  // Tests: POST /users Status: 409 ERROR
  public void createUser_failed_username_already_exists() throws Exception {
    // given
    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setUsername("testUsername"); // Existing username
  
    given(userService.createUser(Mockito.any()))
        .willThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists"));
  
    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder postRequest = post("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(userPostDTO));
  
    mockMvc.perform(postRequest)
        .andExpect(status().isConflict()); // 409 Conflict


  }

  @Test
  // Tests: PUT /users/[userId] Status: 204 OK
  public void update_User_profile() throws Exception {
    // given
    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setUsername("updatedUsername");

    User updatedUser = new User();
    updatedUser.setId(1L);
    updatedUser.setUsername("updatedUsername");

    // Mocking successful update (return updated user instead of null)
    given(userService.updateUser(Mockito.eq(1L), Mockito.any())).willReturn(updatedUser);

    // when
    MockHttpServletRequestBuilder putRequest = put("/users/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(userPostDTO));

    // then
    mockMvc.perform(putRequest)
        .andExpect(status().isNoContent()); // 204 No Content


  }


  @Test
  // Tests: PUT /users/[userId] Status: 404 ERROR
  public void user_with_UserId_not_found_PUT() throws Exception {
    // given
    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setUsername("updatedUsername");

    given(userService.updateUser(Mockito.eq(99L), Mockito.any()))
        .willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    // when
    MockHttpServletRequestBuilder putRequest = put("/users/99")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(userPostDTO));

    // then
    mockMvc.perform(putRequest)
        .andExpect(status().isNotFound()); // 404 Not Found


  }










  /**
   * Helper Method to convert userPostDTO into a JSON string such that the input
   * can be processed
   * Input will look like this: {"name": "Test User", "username": "testUsername"}
   * 
   * @param object
   * @return string
   */
  /*
  private String asJsonString(final Object object) {
    try {
      return new ObjectMapper().writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format("The request body could not be created.%s", e.toString()));
    }
  }
}
*/