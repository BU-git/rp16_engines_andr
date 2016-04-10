package com.bionic.kvt.serviceapp.models;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public class Order {
    // Fields from XML

    /**
     * Order number [nummer in XML}
     * NotNull
     */
    private long number;

    private String orderType; //OrderType
    private Date date; //Datum
    private String reference; //Referentie
    private String note; //Notitie

    private Relation relation; //Relatie
    private Employee employee; //Medewerker
    private Installation installation; //Installatie
    private List<Task> tasks; //Taken
    private List<Component> components; //Componenten
    private List<Part> parts; //Onderdelen
    private List<Info> extraInfo; //ExtraInfo

    // Service fields

    /**
     * This is time when this order was imported to BO Server.
     * After import it never changed.
     * NotNull
     */
    private Timestamp importTimestamp;

    /**
     * This is time when this order was changed in BO Server.
     * If order changed in BO this field has to be updated.
     * This field will be used by Android App to check if order has to be updated from server.
     * When order is imported to BO Server this time is set the same value as lastServerChangeTimestamp
     * This field will NOT changed in Android App.
     * NotNull
     */
    private Timestamp lastServerChangeTimestamp;

    /**
     * This is time when this order was changed in Android.
     * If order changed in Android App this field has to be updated.
     * This field will be used by Android App to check if order need to be updated to server.
     * This field will NOT changed in BO Server.
     * When order is imported to BO Server this time is set the same value as lastServerChangeTimestamp
     * NotNull
     */
    private Timestamp lastAndroidChangeTimestamp;

    public Order() {
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Relation getRelation() {
        return relation;
    }

    public void setRelation(Relation relation) {
        this.relation = relation;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Installation getInstallation() {
        return installation;
    }

    public void setInstallation(Installation installation) {
        this.installation = installation;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public List<Component> getComponents() {
        return components;
    }

    public void setComponents(List<Component> components) {
        this.components = components;
    }

    public List<Part> getParts() {
        return parts;
    }

    public void setParts(List<Part> parts) {
        this.parts = parts;
    }

    public List<Info> getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(List<Info> extraInfo) {
        this.extraInfo = extraInfo;
    }

    public Timestamp getImportTimestamp() {
        return importTimestamp;
    }

    public void setImportTimestamp(Timestamp importTimestamp) {
        this.importTimestamp = importTimestamp;
    }

    public Timestamp getLastServerChangeTimestamp() {
        return lastServerChangeTimestamp;
    }

    public void setLastServerChangeTimestamp(Timestamp lastServerChangeTimestamp) {
        this.lastServerChangeTimestamp = lastServerChangeTimestamp;
    }

    public Timestamp getLastAndroidChangeTimestamp() {
        return lastAndroidChangeTimestamp;
    }

    public void setLastAndroidChangeTimestamp(Timestamp lastAndroidChangeTimestamp) {
        this.lastAndroidChangeTimestamp = lastAndroidChangeTimestamp;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Order{");
        sb.append("number=").append(number);
        sb.append(", orderType='").append(orderType).append('\'');
        sb.append(", date=").append(date);
        sb.append(", reference='").append(reference).append('\'');
        sb.append(", note='").append(note).append('\'');
        sb.append(", relation=").append(relation);
        sb.append(", employee=").append(employee);
        sb.append(", installation=").append(installation);
        sb.append(", tasks=").append(tasks);
        sb.append(", components=").append(components);
        sb.append(", parts=").append(parts);
        sb.append(", extraInfo=").append(extraInfo);
        sb.append(", importTimestamp=").append(importTimestamp);
        sb.append(", lastServerChangeTimestamp=").append(lastServerChangeTimestamp);
        sb.append(", lastAndroidChangeTimestamp=").append(lastAndroidChangeTimestamp);
        sb.append('}');
        return sb.toString();
    }
}
