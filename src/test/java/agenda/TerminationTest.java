
package agenda;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests pour la classe Termination afin de garantir la couverture des deux constructeurs 
 * et la gestion correcte des calculs basés sur différentes ChronoUnit.
 */
public class TerminationTest {
    private final LocalDate start = LocalDate.of(2025, 1, 15);
    private final LocalDate startDaily = LocalDate.of(2025, 1, 1);

    @Test
    void testCountConstructor_Daily() {
        // Début: 01/01/2025. N=5 -> 01, 02, 03, 04, 05
        Termination termination = new Termination(startDaily, ChronoUnit.DAYS, 5);
        
        assertEquals(5, termination.getNumberOfOccurrences(), "Le nombre d'occurrences devrait être 5.");
        // Date de fin calculée: 01/01 + (5-1) jours = 05/01
        assertEquals(LocalDate.of(2025, 1, 5), termination.getTerminationDateInclusive(), 
                     "La date de fin pour N=5 quotidien devrait être le 05/01.");
    }
    
    @Test
    void testCountConstructor_Weekly() {
        // Début: 15/01/2025 (Mercredi). N=3 -> 15/01, 22/01, 29/01
        Termination termination = new Termination(start, ChronoUnit.WEEKS, 3);
        
        assertEquals(3, termination.getNumberOfOccurrences(), "Le nombre d'occurrences devrait être 3.");
        // Date de fin calculée: 15/01 + (3-1) semaines = 29/01
        assertEquals(LocalDate.of(2025, 1, 29), termination.getTerminationDateInclusive(), 
                     "La date de fin pour N=3 hebdomadaire devrait être le 29/01.");
    }

    @Test
    void testCountConstructor_Monthly() {
        // Début: 15/01/2025. N=4 -> 15/01, 15/02, 15/03, 15/04
        Termination termination = new Termination(start, ChronoUnit.MONTHS, 4);
        
        assertEquals(4, termination.getNumberOfOccurrences(), "Le nombre d'occurrences devrait être 4.");
        // Date de fin calculée: 15/01 + (4-1) mois = 15/04
        assertEquals(LocalDate.of(2025, 4, 15), termination.getTerminationDateInclusive(), 
                     "La date de fin pour N=4 mensuel devrait être le 15/04.");
    }
    
    @Test
    void testCountConstructor_SingleOccurrence() {
        // N=1 -> Se termine le jour de début
        Termination termination = new Termination(start, ChronoUnit.DAYS, 1);
        
        assertEquals(1, termination.getNumberOfOccurrences(), "Le nombre d'occurrences devrait être 1.");
        assertEquals(start, termination.getTerminationDateInclusive(), 
                     "La date de fin pour N=1 devrait être la date de début.");
    }
    
    @Test
    void testCountConstructor_ZeroOrNegativeOccurrence() {
        // Test du cas limite où N <= 0 (devrait se terminer le jour de début)
        Termination termination = new Termination(start, ChronoUnit.DAYS, 0);
        
        // Le champ numberOfOccurrences n'est pas utilisé dans ce constructeur, mais le calcul de date de fin doit être sûr.
        // Si le code n'est pas censé permettre N<=0, vous devriez idéalement lancer une IllegalArgumentException.
        // Puisque votre code force la date de fin à 'start', nous testons ce comportement.
        assertEquals(start, termination.getTerminationDateInclusive(), 
                     "La date de fin pour N<=0 doit être la date de début.");
    }

    // =======================================================================
    // II. Constructeur : Terminaison par Date Inclusive (LocalDate terminationInclusive)
    // =======================================================================

    @Test
    void testDateConstructor_Daily() {
        LocalDate end = startDaily.plusDays(4); // 05/01/2025
        // 01, 02, 03, 04, 05 -> 5 occurrences
        Termination termination = new Termination(startDaily, ChronoUnit.DAYS, end);
        
        assertEquals(5, termination.getNumberOfOccurrences(), "Le nombre d'occurrences calculé devrait être 5.");
        assertEquals(end, termination.getTerminationDateInclusive(), "La date de fin doit être l'entrée.");
    }

    @Test
    void testDateConstructor_Weekly() {
        // Début: 15/01/2025. Fin: 29/01/2025.
        LocalDate end = start.plusWeeks(2); // 29/01/2025
        // 15/01, 22/01, 29/01 -> 3 occurrences
        Termination termination = new Termination(start, ChronoUnit.WEEKS, end);
        
        assertEquals(3, termination.getNumberOfOccurrences(), "Le nombre d'occurrences calculé devrait être 3.");
    }

    @Test
    void testDateConstructor_Monthly_PartialPeriod() {
        // Début: 15/01/2025. Fin: 01/03/2025.
        // Occurrences: 15/01, 15/02. L'itération pour 15/03 dépasse la fin 01/03.
        LocalDate end = LocalDate.of(2025, 3, 1);
        Termination termination = new Termination(start, ChronoUnit.MONTHS, end);
        
        // La boucle s'arrête APRÈS le 15/02, car 15/03 > 01/03
        assertEquals(2, termination.getNumberOfOccurrences(), 
                     "Le nombre d'occurrences calculé devrait être 2 (15/01 et 15/02).");
    }

    @Test
    void testDateConstructor_EndEqualsStart() {
        // Fin = Début
        Termination termination = new Termination(start, ChronoUnit.DAYS, start);
        
        assertEquals(1, termination.getNumberOfOccurrences(), "Le nombre d'occurrences devrait être 1.");
    }
    
    @Test
    void testDateConstructor_EndBeforeStart() {
        LocalDate end = start.minusDays(5);
        // La boucle while (!current.isAfter(terminationInclusive)) ne s'exécute qu'une seule fois
        Termination termination = new Termination(start, ChronoUnit.DAYS, end);
        
        assertEquals(0, termination.getNumberOfOccurrences(), 
                     "Le nombre d'occurrences devrait être 0 si la date de fin est strictement avant la date de début.");
        // Note: Si le test d'itération est correct, le count sera 0.
    }
}