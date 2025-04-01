package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;

import java.time.LocalDateTime;

public class UserGetDTO {

  private String id;
  private String username;
  private LocalDateTime creation_date;
  private UserStatus status;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public LocalDateTime getCreation_date() {
    return creation_date;
  }

  public void setCreation_date(LocalDateTime creation_date){
    this.creation_date = creation_date;
  }


  public UserStatus getStatus() {
    return status;
  }

  public void setStatus(UserStatus status) {
    this.status = status;
  }
}
