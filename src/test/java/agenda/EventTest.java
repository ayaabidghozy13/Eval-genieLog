package agenda;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class EventTest {

    // --- Constantes de Temps ---
    private final LocalDate nov_1_2020 = LocalDate.of(2020, 11, 1);
    private final LocalDateTime nov_1_2020_22_30 = LocalDateTime.of(2020, 11, 1, 22, 30);
    private final Duration min_120 = Duration.ofMinutes(120); // 2 heures (Fin 00:30 le 2/11)
    private final Duration min_89 = Duration.ofMinutes(89); // 1h29 (Fin 23:59 le 1/11)
    
    // --- Événements pour Setup ---
    private Event simpleEvent;
    private Event repetitiveEvent;

    // --- Constante de titre du SimpleEventTest ---
    public static final String SIMPLE_EVENT_TITLE = "Simple event";


    @BeforeEach
    void setUp() {
        // Événement simple utilisé pour les tests de getters/setters/répétition
        simpleEvent = new Event("Test Simple Event", nov_1_2020_22_30, min_120);
        // Événement répétitif
        repetitiveEvent = new Event("Repetitive Event", nov_1_2020_22_30, min_120);
    }
    
    // =======================================================================
    // I. Tests des Getters, Setters et toString (Couverture des accesseurs)
    // =======================================================================

    @Test
    void testConstructorAndGetters() {
        // Teste les getters de l'événement de base
        assertEquals("Test Simple Event", simpleEvent.getTitle());
        assertEquals(nov_1_2020_22_30, simpleEvent.getStart());
        assertEquals(min_120, simpleEvent.getDuration());
    }

    @Test
    public void toStringShowsEventTitle() {
        // Teste la méthode toString (issue du SimpleEventTest, utilisant une nouvelle instance)
        Event simpleTest = new Event(SIMPLE_EVENT_TITLE, nov_1_2020_22_30, min_89);
        assertTrue(simpleTest.toString().contains(SIMPLE_EVENT_TITLE), "toString() doit montrer le titre de l'événement.");
    }
    
    @Test
    void testSetRepetition_Getter() {
        repetitiveEvent.setRepetition(ChronoUnit.DAYS);
        assertNotNull(repetitiveEvent.getRepetition(), "L'objet Repetition doit être créé.");
        // Test de la fréquence stockée
        // assertEquals(ChronoUnit.DAYS, repetitiveEvent.getRepetition().getFrequency()); 
    }

    // --- Tests des Getters de Terminaison sur des cas par défaut/non définis ---

    @Test
    void testGetters_NonRepetitiveOrNoTermination() {
        // 1. Simple event (repetition == null)
        assertEquals(0, simpleEvent.getNumberOfOccurrences());
        assertNull(simpleEvent.getTerminationDate());

        // 2. Repetitive event sans terminaison définie
        repetitiveEvent.setRepetition(ChronoUnit.DAYS);
        assertEquals(0, repetitiveEvent.getNumberOfOccurrences(), "Doit retourner 0 si la terminaison n'est pas définie.");
        assertNull(repetitiveEvent.getTerminationDate(), "Doit retourner null si la terminaison n'est pas définie.");
    }

    @Test
    void testSetTerminationToSimpleEvent_IgnoredAndSafe() {
        // Teste les limites: appeler setTermination sur un événement non répétitif
        assertDoesNotThrow(() -> simpleEvent.setTermination(nov_1_2020.plusDays(10)));
        assertDoesNotThrow(() -> simpleEvent.setTermination(5L));
        
        // Vérifie que les getters ne retournent pas de valeurs inattendues
        assertEquals(0, simpleEvent.getNumberOfOccurrences());
        assertNull(simpleEvent.getTerminationDate());
    }

    @Test
    void testAddExceptionToSimpleEvent_Ignored() {
        // Teste les limites: appeler addException sur un événement non répétitif
        assertDoesNotThrow(() -> simpleEvent.addException(nov_1_2020.plusDays(1)));
        // isInDay est toujours basé sur la logique simple.
        assertTrue(simpleEvent.isInDay(nov_1_2020));
    }
    
    // =======================================================================
    // II. Tests d'Événements Simples (isInDay)
    // =======================================================================
    
    @Test
    public void eventIsInItsStartDay() {
        // Utilise les événements du SimpleEventTest
        Event simple = new Event(SIMPLE_EVENT_TITLE, nov_1_2020_22_30, min_89);
        Event overlapping = new Event("Overlapping event", nov_1_2020_22_30, min_120);

        assertTrue(simple.isInDay(nov_1_2020), "Un événement a lieu dans son jour de début");
        assertTrue(overlapping.isInDay(nov_1_2020), "Un événement a lieu dans son jour de début");
    }

    @Test
    public void eventIsNotInDayBefore() {
        // Utilise les événements du SimpleEventTest
        Event simple = new Event(SIMPLE_EVENT_TITLE, nov_1_2020_22_30, min_89);
        Event overlapping = new Event("Overlapping event", nov_1_2020_22_30, min_120);
        
        assertFalse(simple.isInDay(nov_1_2020.minusDays(1)), "Un événement n'a pas lieu avant son jour de début");
        assertFalse(overlapping.isInDay(nov_1_2020.minusDays(1)), "Un événement n'a pas lieu avant son jour de début");
    }

    @Test
    public void overlappingEventIsInDayAfter() {
        // Utilise les événements du SimpleEventTest
        Event simple = new Event(SIMPLE_EVENT_TITLE, nov_1_2020_22_30, min_89);
        Event overlapping = new Event("Overlapping event", nov_1_2020_22_30, min_120);

        // simple (fin 23:59)
        assertFalse(simple.isInDay(nov_1_2020.plusDays(1)), "Cet événement ne déborde pas sur le jour suivant");
        // overlapping (fin 00:30)
        assertTrue(overlapping.isInDay(nov_1_2020.plusDays(1)), "Cet événement déborde sur le jour suivant");
    }

    // --- Couverture des limites multi-jours (issue des tests de couverture) ---
    
    @Test
    void testMultiDayEvent_StartsJustBeforeMidnight() {
        // Déborde de 1 minute sur le jour suivant
        LocalDateTime start = nov_1_2020_22_30.plusMinutes(60 + 59); // 23:59
        Duration duration = Duration.ofMinutes(2); 
        Event veryShortOverlap = new Event("Court chevauchement", start, duration);
        
        assertTrue(veryShortOverlap.isInDay(nov_1_2020));
        assertTrue(veryShortOverlap.isInDay(nov_1_2020.plusDays(1)));
        assertFalse(veryShortOverlap.isInDay(nov_1_2020.plusDays(2)));
    }
    
    // =======================================================================
    // III. Tests d'Événements Répétitifs (isInDay & Limites)
    // =======================================================================

    @Test
    void testRepetition_Boundary_DayBeforeStart() {
        repetitiveEvent.setRepetition(ChronoUnit.DAYS);
        // Couvre la ligne `if (aDay.isBefore(startDay)) return false;`
        assertFalse(repetitiveEvent.isInDay(nov_1_2020.minusDays(1)), "Doit être faux pour le jour avant le début.");
    }
    
    @Test
    void testRepetition_Boundary_TerminationIsStart() {
        // Répétition quotidienne, se termine le jour de début (1 occurrence)
        repetitiveEvent.setRepetition(ChronoUnit.DAYS);
        repetitiveEvent.setTermination(nov_1_2020); 
        
        assertTrue(repetitiveEvent.isInDay(nov_1_2020));
        assertFalse(repetitiveEvent.isInDay(nov_1_2020.plusDays(1)), "Doit exclure le jour suivant.");
    }

    @Test
    void testRepetitionWithDateTermination_Boundary() {
        repetitiveEvent.setRepetition(ChronoUnit.DAYS);
        LocalDate terminationDate = nov_1_2020.plusDays(5);
        repetitiveEvent.setTermination(terminationDate);

        assertTrue(repetitiveEvent.isInDay(terminationDate), "Le jour de terminaison doit être inclus.");
        assertFalse(repetitiveEvent.isInDay(terminationDate.plusDays(1)), "Le jour après la terminaison doit être exclus.");
    }

    @Test
    void testRepetition_LargeDifference() {
        repetitiveEvent.setRepetition(ChronoUnit.YEARS);
        LocalDate testDate = nov_1_2020.plusYears(10); // 10 ans après
        
        // Vérifie la robustesse du calcul de la date d'occurrence `startDay.plus(diff, unit)`
        assertTrue(repetitiveEvent.isInDay(testDate));
        assertFalse(repetitiveEvent.isInDay(testDate.plusDays(1)));
    }
    
    @Test
    void testMonthlyRepetition_FixedDayLogic() {
        // Teste la logique de la date de répétition pour les mois (gestion des fins de mois différentes)
        LocalDateTime start31 = LocalDateTime.of(2025, 1, 31, 10, 0);
        Event e = new Event("Mensuel", start31, Duration.ofHours(1));
        e.setRepetition(ChronoUnit.MONTHS);

        // 1. 31/01
        assertTrue(e.isInDay(start31.toLocalDate())); 
        // 2. 28/02 (car 2025 non bissextile)
        assertTrue(e.isInDay(LocalDate.of(2025, 2, 28))); 
        // 3. 31/03
        assertTrue(e.isInDay(LocalDate.of(2025, 3, 31)));
        
        // Fausse date d'occurrence
        assertFalse(e.isInDay(LocalDate.of(2025, 3, 1))); 
    }
    
    @Test
    void testExceptionHasPriority() {
        repetitiveEvent.setRepetition(ChronoUnit.DAYS);
        LocalDate exceptionDay = nov_1_2020.plusDays(1);
        repetitiveEvent.addException(exceptionDay); 

        // S'assure que la ligne `if (repetition.getExceptions().contains(aDay)) return false;` est couverte
        assertFalse(repetitiveEvent.isInDay(exceptionDay), "L'exception doit avoir la priorité.");
        assertTrue(repetitiveEvent.isInDay(nov_1_2020.plusDays(2)), "Le jour après l'exception est une occurrence.");
    }

    @Test
    void testSetTerminationByCount_LargeNumber() {
        // Teste la robustesse des calculs de terminaison
        repetitiveEvent.setRepetition(ChronoUnit.WEEKS);
        long largeCount = 1000L;
        repetitiveEvent.setTermination(largeCount);

        assertEquals(largeCount, repetitiveEvent.getNumberOfOccurrences(), "Le nombre d'occurrences doit être correct.");
        assertEquals(nov_1_2020.plusWeeks(largeCount - 1), repetitiveEvent.getTerminationDate(), 
                     "La date de terminaison doit être correctement calculée pour un grand nombre.");
    }
}