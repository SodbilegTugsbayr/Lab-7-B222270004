package edu.sc.csce747.MeetingPlanner;

import java.util.ArrayList;
import java.util.List;

public class PlannerService {

  /**
   * Try to apply a meeting to a room and all attendees.
   * Returns null on success, or an error message if a TimeConflictException
   * occurred.
   */
  public static String applyMeeting(Room where, List<Person> attendees, Meeting meeting) {
    try {
      where.addMeeting(meeting);
      for (Person p : attendees) {
        p.addMeeting(meeting);
      }
      return null;
    } catch (TimeConflictException e) {
      return e.getMessage();
    }
  }

  /**
   * Returns the maximum valid day for a given month.
   */
  private static int maxDayOfMonth(int month) {
    switch (month) {
      case 2:
        return 29; // Simplified: allow Feb 29
      case 4:
      case 6:
      case 9:
      case 11:
        return 30;
      default:
        return 31;
    }
  }

  /**
   * Check for vacation conflicts across the requested range and, if none, book
   * the vacation days.
   * Returns a list of conflict messages (empty if booking succeeded).
   */
  public static List<String> scheduleVacation(Person who, int sMonth, int sDay, int eMonth, int eDay)
      throws TimeConflictException {

    List<String> conflicts = new ArrayList<>();

    // === Check conflicts ===
    if (sMonth == eMonth) {
      for (int day = sDay; day <= eDay; day++) {
        try {
          if (who.isBusy(sMonth, day, 0, 23)) {
            conflicts.add("There is a conflict for date " + sMonth + "/" + day);
          }
        } catch (TimeConflictException e) {
          throw e; // Re-throw the original exception
        }
      }
    } else {
      // Start month
      for (int day = sDay; day <= maxDayOfMonth(sMonth); day++) {
        try {
          if (who.isBusy(sMonth, day, 0, 23)) {
            conflicts.add("There is a conflict for date " + sMonth + "/" + day);
          }
        } catch (TimeConflictException e) {
          throw e;
        }
      }
      // Intervening months
      for (int month = sMonth + 1; month < eMonth; month++) {
        for (int day = 1; day <= maxDayOfMonth(month); day++) {
          try {
            if (who.isBusy(month, day, 0, 23)) {
              conflicts.add("There is a conflict for date " + month + "/" + day);
            }
          } catch (TimeConflictException e) {
            throw e;
          }
        }
      }
      // End month
      for (int day = 1; day <= eDay; day++) {
        try {
          if (who.isBusy(eMonth, day, 0, 23)) {
            conflicts.add("There is a conflict for date " + eMonth + "/" + day);
          }
        } catch (TimeConflictException e) {
          throw e;
        }
      }
    }

    if (!conflicts.isEmpty()) {
      return conflicts;
    }

    // === Book vacation ===
    if (sMonth == eMonth) {
      for (int day = sDay; day <= eDay; day++) {
        Meeting vacation = new Meeting(sMonth, day, 0, 23, new ArrayList<>(), new Room(), "vacation");
        try {
          who.addMeeting(vacation);
        } catch (TimeConflictException e) {
          conflicts.add(e.getMessage());
        }
      }
    } else {
      // Start month
      for (int day = sDay; day <= maxDayOfMonth(sMonth); day++) {
        Meeting vacation = new Meeting(sMonth, day, 0, 23, new ArrayList<>(), new Room(), "vacation");
        try {
          who.addMeeting(vacation);
        } catch (TimeConflictException e) {
          conflicts.add(e.getMessage());
        }
      }
      // Intervening months
      for (int month = sMonth + 1; month < eMonth; month++) {
        for (int day = 1; day <= maxDayOfMonth(month); day++) {
          Meeting vacation = new Meeting(month, day, 0, 23, new ArrayList<>(), new Room(), "vacation");
          try {
            who.addMeeting(vacation);
          } catch (TimeConflictException e) {
            conflicts.add(e.getMessage());
          }
        }
      }
      // End month
      for (int day = 1; day <= eDay; day++) {
        Meeting vacation = new Meeting(eMonth, day, 0, 23, new ArrayList<>(), new Room(), "vacation");
        try {
          who.addMeeting(vacation);
        } catch (TimeConflictException e) {
          conflicts.add(e.getMessage());
        }
      }
    }

    return conflicts;
  }
}