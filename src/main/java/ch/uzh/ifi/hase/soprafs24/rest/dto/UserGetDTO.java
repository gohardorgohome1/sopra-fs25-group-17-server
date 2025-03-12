package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class UserGetDTO {

  private Long id;
  private String name;
  private String birthday;
  private String username;
  private String password;
  private LocalDateTime creation_date;
  private UserStatus status;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getBirthday(){
    return birthday;
  }

  public void setBirthday(String birthday){
    this.birthday = birthday;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
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
