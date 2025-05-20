package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Exoplanet;
import ch.uzh.ifi.hase.soprafs24.repository.ExoplanetRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.CommentGetDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import ch.uzh.ifi.hase.soprafs24.rest.dto.CommentPostDTO;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@Import(ExoplanetService.class)
@ActiveProfiles("test")
public class ExoplanetServiceIntegrationTest {
    
    @Autowired
    private ExoplanetRepository exoplanetRepository;

    @Autowired
    private ExoplanetService exoplanetService;

    @BeforeEach
    void setup() {
        exoplanetRepository.deleteAll();
    }

    @Test
    public void getExoplanets_success() {
        Exoplanet exoplanet0 = new Exoplanet();
        exoplanet0.setPlanetName("testPlanet0");
        Exoplanet exoplanet1 = new Exoplanet();
        exoplanet1.setPlanetName("testPlanet1");

        exoplanetRepository.save(exoplanet0);
        exoplanetRepository.save(exoplanet1);

        List<Exoplanet> allExoplanets = exoplanetService.getExoplanets();

        assertNotNull(allExoplanets);
        assertEquals("testPlanet0", allExoplanets.get(0).getPlanetName());
        assertEquals("testPlanet1", allExoplanets.get(1).getPlanetName());
    }

    @Test
    public void deleteExoplanet_success() {
        Exoplanet exoplanet0 = new Exoplanet();
        exoplanet0.setPlanetName("testPlanet0");
        exoplanet0.setId("exoId1");
        Exoplanet exoplanet1 = new Exoplanet();
        exoplanet1.setPlanetName("testPlanet1");
        exoplanet1.setId("exoId2");

        exoplanetRepository.save(exoplanet0);
        exoplanetRepository.save(exoplanet1);

        exoplanetService.deleteExoplanet("exoId1");

        List<Exoplanet> allExoplanets = exoplanetService.getExoplanets();

        assertNotNull(allExoplanets);
        assertEquals("testPlanet1", allExoplanets.get(0).getPlanetName());
    }
    /*@Test
    public void getExoplanetRanking_success() {
        Exoplanet exoplanet0 = new Exoplanet();
        exoplanet0.setPlanetName("testPlanet0");
        exoplanet0.setMass(2.4F);
        Exoplanet exoplanet1 = new Exoplanet();
        exoplanet1.setPlanetName("testPlanet1");
        exoplanet1.setMass(2.2F);

        exoplanetRepository.save(exoplanet0);
        exoplanetRepository.save(exoplanet1);

        List<Exoplanet> ranking = exoplanetService.getExoplanetRanking("mass", "asc");

        assertNotNull(ranking);
        assertEquals("testPlanet1", ranking.get(0).getPlanetName());
        assertEquals("testPlanet0", ranking.get(1).getPlanetName());
    }*/
    @Test
    public void addComment_and_getComments_success() {
        Exoplanet exoplanet0 = new Exoplanet();
        exoplanet0.setPlanetName("testPlanet0");
        exoplanet0.setId("exoId0");

        exoplanetRepository.save(exoplanet0);

        CommentPostDTO commentPostDTO = new CommentPostDTO();
        commentPostDTO.setUserId("userId0");
        commentPostDTO.setMessage("Great Planet!");

        exoplanetService.addComment(exoplanet0.getId(), commentPostDTO);

        List<CommentGetDTO> foundComments = exoplanetService.getComments("exoId0");
        assertEquals(1, foundComments.size());

        CommentGetDTO foundComment = foundComments.get(0);
        assertEquals(commentPostDTO.getUserId(), foundComment.getUserId());
        assertEquals(commentPostDTO.getMessage(), foundComment.getMessage());
        assertNotNull(foundComment.getCreatedAt());
    }
}