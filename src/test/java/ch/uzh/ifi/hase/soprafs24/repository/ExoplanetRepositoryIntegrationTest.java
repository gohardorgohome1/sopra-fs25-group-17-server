package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Exoplanet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ActiveProfiles("test")
public class ExoplanetRepositoryIntegrationTest {

    @Autowired
    private ExoplanetRepository exoplanetRepository;

    @BeforeEach
    void setup() {
        exoplanetRepository.deleteAll();
    }

    @Test
    void saveExoplanet_success() {
        Exoplanet exoplanet = new Exoplanet();
        exoplanet.setPlanetName("testExoplanet");
        exoplanet.setId("exoId1");
        exoplanet.setHostStarName("testStar");
        exoplanet.setFractionalDepth(2.2F);

        Exoplanet saved = exoplanetRepository.save(exoplanet);

        assertEquals(exoplanet.getId(), saved.getId());
        assertEquals(exoplanet.getPlanetName(), saved.getPlanetName());
        assertEquals(exoplanet.getHostStarName(), saved.getHostStarName());
        assertEquals(exoplanet.getFractionalDepth(), saved.getFractionalDepth());
    }

    @Test
    void findByPlanetName_success() {
        Exoplanet exoplanet = new Exoplanet();
        exoplanet.setPlanetName("mongoTestPlanet");
        exoplanet.setId("ExoId2");
        exoplanet.setFractionalDepth(2.4F);

        exoplanetRepository.save(exoplanet);

        Exoplanet found = exoplanetRepository.findByPlanetName("mongoTestPlanet");

        assertNotNull(found);
        assertEquals(exoplanet.getPlanetName(), found.getPlanetName());
    }

    @Test
    void findAllByOrderByMassAsc_success() {
        Exoplanet exoplanet0 = new Exoplanet();
        exoplanet0.setPlanetName("mongoTestPlanet0");
        exoplanet0.setMass(2.4F);
        Exoplanet exoplanet1 = new Exoplanet();
        exoplanet1.setPlanetName("mongoTestPlanet1");
        exoplanet1.setMass(2.2F);

        exoplanetRepository.saveAll(List.of(exoplanet0, exoplanet1));

        List<Exoplanet> found = exoplanetRepository.findAllByOrderByMassAsc();

        assertNotNull(found);
        assertEquals(exoplanet1.getPlanetName(), found.get(0).getPlanetName());
        assertEquals(exoplanet0.getPlanetName(), found.get(1).getPlanetName());
    }
}