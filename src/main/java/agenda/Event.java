
package agenda;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class Event {

    private final String myTitle;
    private final LocalDateTime myStart;
    private final Duration myDuration;

    private Repetition repetition = null; 

    public Event(String title, LocalDateTime start, Duration duration) {
        this.myTitle = Objects.requireNonNull(title);
        this.myStart = Objects.requireNonNull(start);
        this.myDuration = Objects.requireNonNull(duration);
    }


    public void setRepetition(ChronoUnit frequency) {
        this.repetition = new Repetition(frequency);
    }

    public void addException(LocalDate date) {
        if (repetition == null) {
            return;
        }
        repetition.addException(date);
    }

    public void setTermination(LocalDate terminationInclusive) {
        if (repetition == null) {
            return;
        }
        repetition.setTermination(new Termination(myStart.toLocalDate(), repetition.getFrequency(), terminationInclusive));
    }

    public void setTermination(long numberOfOccurrences) {
        if (repetition == null) {
            return;
        }
        repetition.setTermination(new Termination(myStart.toLocalDate(), repetition.getFrequency(), numberOfOccurrences));
    }


    public int getNumberOfOccurrences() {
        if (repetition == null || repetition.getTermination() == null) {
            return 0; 
        }
        return repetition.getTermination().getNumberOfOccurrences();
    }

    public LocalDate getTerminationDate() {
        if (repetition == null || repetition.getTermination() == null) {
            return null;  
        }
        return repetition.getTermination().getTerminationDateInclusive();
    }
    

    /**
     * Tests if an event occurs on a given day
     * (Logique basée sur la version fournie, en supposant que Repetition est à jour)
     *
     * @param aDay the day to test
     * @return true if the event occurs on that day, false otherwise
     */
    public boolean isInDay(LocalDate aDay) {
        LocalDate startDay = myStart.toLocalDate();
        
        if (repetition == null) {
            LocalDate endDay = myStart.plus(myDuration).toLocalDate();
            return !aDay.isBefore(startDay) && !aDay.isAfter(endDay);
        }

        if (aDay.isBefore(startDay)) return false;

        Termination term = repetition.getTermination();
        if (term != null) {
            LocalDate end = term.getTerminationDateInclusive();
            if (aDay.isAfter(end)) {
                return false;
            }
        }

        
        if (repetition.getExceptions().contains(aDay)) return false;

        ChronoUnit unit = repetition.getFrequency();
        long diff = unit.between(startDay, aDay);
        
        if (diff < 0) return false; 
        
        LocalDate calculatedDate = startDay.plus(diff, unit);
        
        return calculatedDate.equals(aDay);
    }
    

    public String getTitle() { return myTitle; }
    public LocalDateTime getStart() { return myStart; }
    public Duration getDuration() { return myDuration; }
    
    public Repetition getRepetition() { return repetition; }

    @Override
    public String toString() {
        return "Event{title='%s', start=%s, duration=%s}".formatted(myTitle, myStart, myDuration);
    }
}
