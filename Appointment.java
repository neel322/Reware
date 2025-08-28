package HealthTracker;

import javax.swing.JOptionPane;
import java.util.Arrays;
import java.util.List;

public class Appointment {
    private String date;
    private String time;
    private String reason;
    private String doctor;
    private String specialization;
    private String status;

    public static final String SCHEDULED = "Scheduled";
    public static final String COMPLETED = "Completed";
    public static final String CANCELLED = "Cancelled";
    public static final String RESCHEDULED = "Rescheduled";

    public static final List<String> VALID_STATUSES = Arrays.asList(
            SCHEDULED, COMPLETED, CANCELLED, RESCHEDULED
    );

    public static class Node {
        public Appointment appointment;
        public Node next;

        public Node(Appointment appointment) {
            this.appointment = appointment;
            this.next = null;
        }
    }

    public static Node head=null;

    public Appointment(String date, String time, String reason, String doctor, String specialization, String status) {
        this.date = date;
        this.time = time;
        this.reason = reason;
        this.doctor = doctor;
        this.specialization = specialization;
        this.status = status;
    }


    public static void addAppointment(String date, String time, String reason, String doctor, String specialization, String status) {
        if (!VALID_STATUSES.contains(status)) {
            JOptionPane.showMessageDialog(null,
                    "Invalid status! Valid statuses: " + VALID_STATUSES,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Appointment newAppointment = new Appointment(date, time, reason, doctor, specialization, status);
        Node newNode = new Node(newAppointment);

        if (head == null) {
            head = newNode;
        } else {
            Node current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }

        JOptionPane.showMessageDialog(null,
                "Appointment added successfully!\nStatus: " + status,
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void addAppointmentSilently(String date, String time, String reason,
                                              String doctor, String specialization, String status) {
        if (!VALID_STATUSES.contains(status)) {
            return;
        }

        Appointment newAppointment = new Appointment(date, time, reason, doctor, specialization, status);
        Node newNode = new Node(newAppointment);

        if (head == null) {
            head = newNode;
        } else {
            Node current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
    }

    public static void updateAppointmentStatus(String date, String time, String newStatus) {
        if (!VALID_STATUSES.contains(newStatus)) {
            JOptionPane.showMessageDialog(null,
                    "Invalid status! Valid statuses: " + VALID_STATUSES,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Node current = head;
        boolean found = false;

        while (current != null) {
            if (current.appointment.getDate().equals(date) &&
                    current.appointment.getTime().equals(time)) {
                String oldStatus = current.appointment.getStatus();
                current.appointment.setStatus(newStatus);
                found = true;

                JOptionPane.showMessageDialog(null,
                        String.format("Status updated successfully!\nFrom: %s\nTo: %s", oldStatus, newStatus),
                        "Status Updated",
                        JOptionPane.INFORMATION_MESSAGE);
                break;
            }
            current = current.next;
        }

        if (!found) {
            JOptionPane.showMessageDialog(null,
                    "Appointment not found with given date and time!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void removeAppointment(String date, String time) {
        Node current = head;
        Node previous = null;
        boolean found = false;

        while (current != null) {
            if (current.appointment.getDate().equals(date) &&
                    current.appointment.getTime().equals(time)) {
                found = true;

                if (previous == null) {
                    head = current.next;
                } else {
                    previous.next = current.next;
                }

                JOptionPane.showMessageDialog(null,
                        "Appointment removed successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                break;
            }
            previous = current;
            current = current.next;
        }

        if (!found) {
            JOptionPane.showMessageDialog(null,
                    "Appointment not found with given date and time!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void removeAppointmentSilently(String date, String time) {
        Node current = head;
        Node previous = null;

        while (current != null) {
            if (current.appointment.getDate().equals(date) &&
                    current.appointment.getTime().equals(time)) {

                if (previous == null) {
                    head = current.next;
                } else {
                    previous.next = current.next;
                }

                break;
            }
            previous = current;
            current = current.next;
        }
    }

    public void setStatus(String status) {
        if (VALID_STATUSES.contains(status)) {
            this.status = status;
        } else {
            JOptionPane.showMessageDialog(null,
                    "Invalid status! Valid statuses: " + VALID_STATUSES,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public String getStatus() {
        return status;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Appointment that = (Appointment) obj;
        return java.util.Objects.equals(date, that.date) &&
                java.util.Objects.equals(time, that.time) &&
                java.util.Objects.equals(doctor, that.doctor);
    }
}
