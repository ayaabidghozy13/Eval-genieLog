package agenda;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Termination {

    private final ChronoUnit frequency;
    private final LocalDate start;
    private LocalDate terminationInclusive;
    private long numberOfOccurrences;

    public LocalDate getTerminationDateInclusive() {
        return terminationInclusive;
    }

    public int getNumberOfOccurrences() {
        return (int) numberOfOccurrences;
    }
    /**
     * Constructs a fixed termination event ending after a number of iterations
     * @param start the start time of this event
     * @param frequency one of :
     * <UL>
     * <LI>ChronoUnit.DAYS for daily repetitions</LI>
     * <LI>ChronoUnit.WEEKS for weekly repetitions</LI>
     * <LI>ChronoUnit.MONTHS for monthly repetitions</LI>
     * </UL>
     * @param numberOfOccurrences the number of occurrences of this repetitive event
     */
    public Termination(LocalDate start, ChronoUnit frequency, LocalDate terminationInclusive) {
        this.start = start;
        this.frequency = frequency;
        this.terminationInclusive = terminationInclusive;
        determineNumberOfOccurrences();
    }

    /**
     * Constructs a  termination at a given date
     * @param start the start time of this event
     * @param frequency one of :
     * <UL>
     * <LI>ChronoUnit.DAYS for daily repetitions</LI>
     * <LI>ChronoUnit.WEEKS for weekly repetitions</LI>
     * <LI>ChronoUnit.MONTHS for monthly repetitions</LI>
     * </UL>
     * @param terminationInclusive the date when this event ends
     * @see ChronoUnit#between(Temporal, Temporal)
     */
    public Termination(LocalDate start, ChronoUnit frequency, long numberOfOccurrences) {
        this.start = start;
        this.frequency = frequency;
        this.numberOfOccurrences = numberOfOccurrences;
        determineTerminationDate();
    }

    private void determineNumberOfOccurrences() {
        long count = 0;
        LocalDate current = start;
        while (!current.isAfter(terminationInclusive)) {
            count++;
            current = current.plus(1, frequency);
        }
        this.numberOfOccurrences = count;
    }

    private void determineTerminationDate() {
        if (numberOfOccurrences <= 0) {
            this.terminationInclusive = start; 
            return;
        }
        this.terminationInclusive = start.plus(numberOfOccurrences - 1, frequency);
    }

}
