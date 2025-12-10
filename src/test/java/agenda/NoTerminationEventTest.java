package agenda;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Teste des événements répétitifs sans fin, mais avec des exceptions
 */
public class NoTerminationEventTest {
    // November 1st, 2020
    LocalDate nov_1_2020 = LocalDate.of(2020, 11, 1);

    // November 1st, 2020, 22:30
    LocalDateTime nov_1__2020_22_30 = LocalDateTime.of(2020, 11, 1, 22, 30);

    // 120 minutes
    Duration min_120 = Duration.ofMinutes(120);

    // Un événement répétitif quotidien, sans fin
    Event neverEnding;

    @BeforeEach
    void setUp() {
        // November 1st, 2020, 22:30, 120 minutes
        neverEnding = new Event("Never Ending", nov_1__2020_22_30, min_120);
        neverEnding.setRepetition(ChronoUnit.DAYS);
    }

    @Test
    public void eventIsInItsStartDay() {
        assertTrue(neverEnding.isInDay(nov_1_2020),
            "Un événement a lieu dans son jour de début");
    }

    @Test
    public void eventIsNotInDayBefore() {
        assertFalse(neverEnding.isInDay(nov_1_2020.minusDays(1)),
            "Un événement n'a pas lieu avant son jour de début");
    }

    @Test
    public void eventOccurs10DayAfter() {
        assertTrue(neverEnding.isInDay(nov_1_2020.plusDays(10)),
            "Cet événement doit se produire tous les jours");
    }
    
    @Test
    public void eventIsNotInExceptionDays() {
        neverEnding.addException(nov_1_2020.plusDays(2)); // ne se produit pas à J+2
        neverEnding.addException(nov_1_2020.plusDays(4)); // ne se produit pas à J+4
        assertTrue(neverEnding.isInDay(nov_1_2020.plusDays(1)),
            "Cet événement se produit tous les jours sauf exceptions");
        assertFalse(neverEnding.isInDay(nov_1_2020.plusDays(2)),
            "Cet événement ne se produit pas à J+2");
        assertTrue(neverEnding.isInDay(nov_1_2020.plusDays(3)),
            "Cet événement se produit tous les jours sauf exceptions");
        assertFalse(neverEnding.isInDay(nov_1_2020.plusDays(4)),
            "Cet événement ne se produit pas à J+4");
    }
    
}
