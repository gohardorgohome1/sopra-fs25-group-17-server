package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.DataPoint;
import ch.uzh.ifi.hase.soprafs24.entity.Exoplanet;
import ch.uzh.ifi.hase.soprafs24.entity.PhotometricCurve;
import ch.uzh.ifi.hase.soprafs24.repository.ExoplanetRepository;
import ch.uzh.ifi.hase.soprafs24.repository.PhotometricCurveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PhotometricCurveServiceTest {

    private PhotometricCurveService service;
    private PhotometricCurveRepository curveRepository;
    private ExoplanetRepository exoplanetRepository;

    @BeforeEach
    public void setup() {
        curveRepository = mock(PhotometricCurveRepository.class);
        exoplanetRepository = mock(ExoplanetRepository.class);
        service = new PhotometricCurveService(curveRepository, exoplanetRepository);
    }

    @Test
    public void testParseValidDataPoint() {
        String validLine = "2450000.1234 12.34 0.01";
        DataPoint dp = invokeParseDataPoint(validLine);
        assertNotNull(dp);
        assertEquals(2450000.1234, dp.getTime());
        assertEquals(12.34f, dp.getBrightness());
        assertEquals(0.01f, dp.getBrightnessError());
    }

    @Test
    public void testParseInvalidDataPointComment() {
        assertNull(invokeParseDataPoint("# comment"));
        assertNull(invokeParseDataPoint(""));
        assertNull(invokeParseDataPoint("JD Magnitude Error"));
    }

    @Test
    public void testCalculateRadius() {
        float depth = 0.01f;
        float starRadius = 1.0f;
        float result = service.calculateRadius(depth, starRadius);
        assertEquals((float) (109f * Math.sqrt(0.01)), result, 0.001);
    }

    @Test
    public void testCalculateSurfaceGravity() {
        assertEquals(2f, service.calculateSurfaceGravity(8f, 2f), 0.001);
    }

    @Test
    public void testCalculateEscapeVelocity() {
        assertEquals(2f, service.calculateEscapeVelocity(4f, 1f), 0.001);
    }

    @Test
    public void testCalculateDensity() {
        assertEquals(1f, service.calculateDensity(8f, 2f), 0.001);
    }

    @Test
    public void testCalculateESI() {
        float result = service.calculateESI(1f, 1f, 1f, 288f);
        assertTrue(result > 0f && result <= 1);  // Should be high since values match Earth
    }

    @Test
    public void testGetPhotometricCurveById_NotFound() {
        when(curveRepository.findById("123")).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> service.getPhotometricCurveById("123"));
    }

    @Test
    public void testGetPhotometricCurveById_Found() {
        PhotometricCurve curve = new PhotometricCurve();
        when(curveRepository.findById("123")).thenReturn(Optional.of(curve));
        assertEquals(curve, service.getPhotometricCurveById("123"));
    }

    @Test
    public void testProcessAndSavePhotometricCurve() throws Exception {
        String content = """
            # VarAstro Metadata Begin
            # Observer: Jane Doe
            # Telescope: 10-inch Reflector
            # VarAstro Metadata End
            2450000.1234 12.34 0.01
            2450000.2345 12.35 0.01
        """;

        MockMultipartFile file = new MockMultipartFile("file", "curve.varastro", "text/plain", content.getBytes());
        Exoplanet exo = new Exoplanet();
        exo.setId("exo1");

        when(exoplanetRepository.save(any())).thenReturn(exo);
        when(curveRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // Spy on service to mock only the API call
        PhotometricCurveService spyService = spy(service);
        doReturn(Map.of(
            "star_radius", 1f,
            "orbitalPeriod", 365f,
            "mass", 1f,
            "theoretical_temperature", 288f
        )).when(spyService).fetchExoplanetDataFromAPI("TrES 3b");

        PhotometricCurve result = spyService.processAndSavePhotometricCurve(file, "TrES 3", "TrES 3b", "owner123");

        assertEquals("curve.varastro", result.getFileName());
        assertEquals("owner123", result.getOwnerId());
        assertNotNull(result.getDataPoints());
        assertEquals(2, result.getDataPoints().size());
        assertTrue(result.getMetadata().containsKey("Observer"));
    }


    // Utility: call private method via reflection
    private DataPoint invokeParseDataPoint(String line) {
        try {
            var method = PhotometricCurveService.class.getDeclaredMethod("parseDataPoint", String.class);
            method.setAccessible(true);
            return (DataPoint) method.invoke(service, line);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
