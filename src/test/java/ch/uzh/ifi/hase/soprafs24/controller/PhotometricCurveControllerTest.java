package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.service.*;
import ch.uzh.ifi.hase.soprafs24.repository.ExoplanetRepository;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PhotometricCurveController.class)
public class PhotometricCurveControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PhotometricCurveService photometricCurveService;

    @MockBean
    private UserService userService;

    @MockBean
    private SimpMessagingTemplate messagingTemplate;

    @MockBean
    private ExoplanetRepository exoplanetRepository;

    @Test
    void getPhotometricCurveById_returnsCurve() throws Exception {
        PhotometricCurve mockCurve = new PhotometricCurve();
        mockCurve.setId("curve123");

        when(photometricCurveService.getPhotometricCurveById("curve123")).thenReturn(mockCurve);

        mockMvc.perform(get("/photometric-curves/curve123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("curve123"));
    }

    @Test
    void getPhotometricCurveById_notFound() throws Exception {
        when(photometricCurveService.getPhotometricCurveById("invalid")).thenReturn(null);

        mockMvc.perform(get("/photometric-curves/invalid"))
                .andExpect(status().isNotFound());
    }

    @Test
    void uploadPhotometricCurve_returnsCreated() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "data.csv", "text/csv", "time,value\n1,10".getBytes());

        PhotometricCurve mockCurve = new PhotometricCurve();
        mockCurve.setId("curve123");
        mockCurve.setExoplanetId("exo1");

        User mockUser = new User();
        mockUser.setId("user1");

        Exoplanet mockExo = new Exoplanet();
        mockExo.setId("exo1");

        when(photometricCurveService.processAndSavePhotometricCurve(any(), any(), any(), any()))
                .thenReturn(mockCurve);

        when(userService.getUserById("user1")).thenReturn(mockUser);
        when(exoplanetRepository.findById("exo1")).thenReturn(Optional.of(mockExo));

        mockMvc.perform(multipart("/photometric-curves/upload")
                        .file(file)
                        .param("hostStar", "Sun")
                        .param("exoplanet", "Kepler")
                        .param("ownerId", "user1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("curve123"));
    }
}
