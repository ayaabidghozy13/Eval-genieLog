package agenda;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
/**
 * Teste des événements simples, sans répétition
 */
public class SimpleEventTest {
    public static final String SIMPLE_EVENT = "Simple event";
    // November 1st, 2020
    LocalDate nov_1_2020 = LocalDate.of(2020, 11, 1);

    // November 1st, 2020, 22:30
    LocalDateTime nov_1_2020_22_30 = LocalDateTime.of(2020, 11, 1, 22, 30);

    // 120 minutes
    Duration min_120 = Duration.ofMinutes(120);

    // 89 minutes
    Duration min_89 = Duration.ofMinutes(89);

    // Un événement simple
    // November 1st, 2020, 22:30, 89 minutes
    Event simple = new Event(SIMPLE_EVENT, nov_1_2020_22_30, min_89);
    
    // Un événement qui chevauche 2 jours
    // November 1st, 2020, 22:30, 120 minutes
    Event overlapping = new Event("Overlapping event", nov_1_2020_22_30, min_120);

    @Test
    public void eventIsInItsStartDay() {
        assertTrue(simple.isInDay(nov_1_2020), "Un événement a lieu dans son jour de début");
        assertTrue(overlapping.isInDay(nov_1_2020), "Un événement a lieu dans son jour de début");
    }

    @Test
    public void eventIsNotInDayBefore() {
        assertFalse(simple.isInDay(nov_1_2020.minusDays(1)),  "Un événement n'a pas lieu avant son jour de début");
        assertFalse(overlapping.isInDay(nov_1_2020.minusDays(1)),  "Un événement n'a pas lieu avant son jour de début");
    }

    @Test
    public void overlappingEventIsInDayAfter() {
        assertFalse(simple.isInDay(nov_1_2020.plusDays(1)),      "Cet événement ne déborde pas sur le jour suivant");
        assertTrue(overlapping.isInDay(nov_1_2020.plusDays(1)),  "Cet événement déborde sur le jour suivant");
    }
    @Test
    public void toStringShowsEventTitle() {
        assertTrue(simple.toString().contains(SIMPLE_EVENT),
            "toString() doit montrer le titre de l'événement");
    }
    
}
