package agenda;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Description : An agenda that stores events
 */
public class Agenda {
    
    private final List<Event> events = new ArrayList<>(); // Utilisation de List pour plus de généralité

    /**
     * Adds an event to this agenda
     *
     * @param e the event to add
     */
    public void addEvent(Event e) {
        events.add(e);
    }

    /**
     * Computes the events that occur on a given day
     *
     * @param day the day to test
     * @return a list of events that occur on that day
     */
    public List<Event> eventsInDay(LocalDate day) {
        List<Event> eventsOnDay = new ArrayList<>();
        for (Event e : events) {
            if (e.isInDay(day)) {
                eventsOnDay.add(e);
            }
        }
        return eventsOnDay;
        // Alternative Stream: return events.stream().filter(event -> event.isInDay(day)).collect(Collectors.toList());
    }
    

    /**
     * Trouver les événements de l'agenda en fonction de leur titre
     * @param title le titre à rechercher
     * @return les événements qui ont le même titre
     */
    public List<Event> findByTitle(String title) {
        return events.stream()
                .filter(event -> event.getTitle().equals(title))
                .collect(Collectors.toList());
    }
    
    /**
     * Déterminer s’il y a de la place dans l'agenda pour un événement (aucun autre événement au même moment)
     * @param e L'événement à tester (on se limitera aux événements sans répétition)
     * @return vrai s’il y a de la place dans l'agenda pour cet événement
     */
    public boolean isFreeFor(Event e) {
        // Définition de l'intervalle du nouvel événement
        LocalDateTime newEventStart = e.getStart();
        LocalDateTime newEventEnd = e.getStart().plus(e.getDuration());

        for (Event existingEvent : events) {
            
            LocalDateTime existingStart = existingEvent.getStart();
            LocalDateTime existingEnd = existingEvent.getStart().plus(existingEvent.getDuration());

            boolean overlap = existingStart.isBefore(newEventEnd) && existingEnd.isAfter(newEventStart);

            if (overlap) {
                return false;
            }
        }
        
        return true;
    }
    
    public List<Event> getAllEvents() {
        return events;
    }
} 