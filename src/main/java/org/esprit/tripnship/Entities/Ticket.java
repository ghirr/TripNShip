package org.esprit.tripnship.Entities;

import java.util.Date;

public class Ticket {
    private int ticketId;
    private int  planificationId;
    private int userId;
    private Status status;

    public Ticket( int ticketId,int planificationId,int userId,Status status ) {
        this.status = status;
        this.userId = userId;
        this.planificationId = planificationId;
        this.ticketId = ticketId;
    }

    public int getTicketId() {
        return ticketId;
    }

    public int getPlanificationId() {
        return planificationId;
    }

    public int getUserId() {
        return userId;
    }

    public Status getStatus() {
        return status;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public void setPlanificationId(int planificationId) {
        this.planificationId = planificationId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "ticketId=" + ticketId +
                ", planificationId=" + planificationId +
                ", userId=" + userId +
                ", status=" + status +
                '}';
    }
}

