package agenda;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AgendaTest {
    Agenda agenda;

    // Dates de base
    LocalDate nov_1_2020 = LocalDate.of(2020, 11, 1);
    LocalDate nov_2_2020 = LocalDate.of(2020, 11, 2);
    LocalDate nov_8_2020 = LocalDate.of(2020, 11, 8); // Semaine suivante
    LocalDate jan_5_2021 = LocalDate.of(2021, 1, 5);
    LocalDateTime nov_1_2020_22_30 = LocalDateTime.of(2020, 11, 1, 22, 30);
    Duration min_120 = Duration.ofMinutes(120); // 2 heures

    Event simple;
    Event fixedTermination;
    Event fixedRepetitions;
    Event neverEnding;

    @BeforeEach
    public void setUp() {
        agenda = new Agenda();
        
        // Setup original
        simple = new Event("Simple event", nov_1_2020_22_30, min_120);

        fixedTermination = new Event("Fixed termination weekly", nov_1_2020_22_30, min_120);
        fixedTermination.setRepetition(ChronoUnit.WEEKS);
        fixedTermination.setTermination(jan_5_2021);

        fixedRepetitions = new Event("Fixed termination weekly", nov_1_2020_22_30, min_120);
        fixedRepetitions.setRepetition(ChronoUnit.WEEKS);
        fixedRepetitions.setTermination(10);

        neverEnding = new Event("Never Ending", nov_1_2020_22_30, min_120);
        neverEnding.setRepetition(ChronoUnit.DAYS);

        agenda.addEvent(simple);
        agenda.addEvent(fixedTermination);
        agenda.addEvent(fixedRepetitions);
        agenda.addEvent(neverEnding);
    }

    // --- Tests des Getters / Setters (addEvent) pour la Couverture ---

    @Test
    public void testAddEventAndGetAllEvents() {
        // Test du setter (addEvent) et du getter (getAllEvents)
        assertEquals(4, agenda.getAllEvents().size(), "L'agenda devrait contenir 4 événements après setUp.");
        assertTrue(agenda.getAllEvents().contains(simple), "L'événement simple doit être dans la liste.");
        
        Event newEvent = new Event("Test Getter", LocalDateTime.now(), Duration.ofHours(1));
        agenda.addEvent(newEvent);
        assertEquals(5, agenda.getAllEvents().size(), "L'agenda doit contenir 5 événements après l'ajout.");
        assertTrue(agenda.getAllEvents().contains(newEvent), "Le nouvel événement doit être ajouté.");
    }
    
    // --- Tests de Limites et de Logique pour eventsInDay ---

    @Test
    public void testEventsInDay_EmptyAgenda() {
        Agenda emptyAgenda = new Agenda();
        assertTrue(emptyAgenda.eventsInDay(nov_1_2020).isEmpty(), "Un agenda vide ne doit retourner aucun événement.");
    }

    @Test
    public void testEventsInDay_DayWithNoEvents() {
        // Le 2 novembre n'a que le NeverEnding (quotidien)
        // Les événements fixedTermination et fixedRepetitions sont hebdomadaires
        assertEquals(1, agenda.eventsInDay(nov_2_2020).size(),
                "Seul l'événement quotidien doit avoir lieu le 2 novembre.");
        assertTrue(agenda.eventsInDay(nov_2_2020).contains(neverEnding));
        assertFalse(agenda.eventsInDay(nov_2_2020).contains(simple));
    }

    @Test
    public void testEventsInDay_WeeklyRepetitionDay() {
        // Le 8 novembre est la répétition hebdomadaire (simple est fini)
        List<Event> events = agenda.eventsInDay(nov_8_2020);
        assertEquals(3, events.size(),
                "Le 8 novembre doit contenir 3 événements : les 2 événements répétitifs fixes et le neverEnding.");
        assertFalse(events.contains(simple), "L'événement simple ne se répète pas.");
        assertTrue(events.contains(fixedTermination));
        assertTrue(events.contains(fixedRepetitions));
        assertTrue(events.contains(neverEnding));
    }

    @Test
    public void testEventsInDay_AfterTermination() {
        // Février 2021 est après la terminaison de fixedTermination (Jan 5, 2021)
        LocalDate afterTermination = LocalDate.of(2021, 2, 1);
        
        List<Event> events = agenda.eventsInDay(afterTermination);
        // Seulement NeverEnding peut avoir lieu à cette date (si la date est loin, 
        // les autres événements répétitifs sont probablement terminés).
        // Le 1er février n'est pas un dimanche, mais il est quotidien.
        assertEquals(1, events.size(), "Seul l'événement NeverEnding devrait subsister.");
        assertTrue(events.contains(neverEnding));
    }

    // --- Tests pour findByTitle ---

    @Test
    public void testFindByTitle_Success() {
        Event e = new Event("Titre Unique", LocalDateTime.now(), min_120);
        agenda.addEvent(e);
        
        List<Event> found = agenda.findByTitle("Titre Unique");
        assertEquals(1, found.size(), "Doit trouver l'événement avec le titre unique.");
        assertTrue(found.contains(e));
    }
    
    @Test
    public void testFindByTitle_MultipleResults() {
        Event e1 = new Event("Titre Dupliqué", LocalDateTime.now(), min_120);
        Event e2 = new Event("Titre Dupliqué", LocalDateTime.now().plusHours(1), min_120);
        agenda.addEvent(e1);
        agenda.addEvent(e2);
        
        List<Event> found = agenda.findByTitle("Titre Dupliqué");
        assertEquals(2, found.size(), "Doit trouver les deux événements dupliqués.");
    }

    @Test
    public void testFindByTitle_NotFound() {
        assertTrue(agenda.findByTitle("Inexistant").isEmpty(), "Ne doit rien trouver pour un titre inexistant.");
    }
    
    // --- Tests pour isFreeFor (Limites de Chevauchement) ---
    
    @Test
    public void testIsFreeFor_PerfectOverlap() {
        // Commence à 22:30 et dure 2h (chevauche 'simple')
        Event overlapping = new Event("Overlap", nov_1_2020_22_30, min_120);
        assertFalse(agenda.isFreeFor(overlapping), "Doit échouer pour un chevauchement parfait.");
    }

    @Test
    public void testIsFreeFor_PartialOverlap_StartInside() {
        // Commence à 23:00 (dans 'simple') et dure 1h (fin à 00:00)
        Event overlapping = new Event("Overlap", nov_1_2020_22_30.plusMinutes(30), Duration.ofHours(1));
        assertFalse(agenda.isFreeFor(overlapping), "Doit échouer si le début est dans l'événement existant.");
    }

    @Test
    public void testIsFreeFor_PartialOverlap_EndInside() {
        // Commence à 21:30 et se termine à 23:30 (fin dans 'simple')
        Event overlapping = new Event("Overlap", nov_1_2020_22_30.minusHours(1), Duration.ofHours(2));
        assertFalse(agenda.isFreeFor(overlapping), "Doit échouer si la fin est dans l'événement existant.");
    }
    
    @Test
    public void testIsFreeFor_JustBeginsAtEnd() {
        // Simple event se termine à 00:30. Le nouvel event commence à 00:30.
        LocalDateTime startJustAfter = nov_1_2020_22_30.plus(min_120); // 00:30 (02/11)
        Event e = new Event("Starts Just After", startJustAfter, Duration.ofMinutes(30));
        
        // (existingStart < newEventEnd) AND (existingEnd > newEventStart)
        // (22:30 < 01:00) AND (00:30 > 00:30) -> Faux, car 00:30 n'est pas > 00:30 (la fin est non-inclusive).
        assertTrue(agenda.isFreeFor(e), "Doit réussir s'il commence à la fin exacte d'un autre événement.");
    }

    @Test
    public void testIsFreeFor_FreeSlot() {
        // Commence bien avant 'simple'
        LocalDateTime freeSlot = nov_1_2020_22_30.minusHours(5);
        Event e = new Event("Free Slot", freeSlot, min_120);
        assertTrue(agenda.isFreeFor(e), "Doit réussir s'il n'y a pas de chevauchement.");
    }
}
