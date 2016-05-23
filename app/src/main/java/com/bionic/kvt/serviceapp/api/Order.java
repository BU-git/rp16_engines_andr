package com.bionic.kvt.serviceapp.api;

import com.bionic.kvt.serviceapp.GlobalConstants;

import java.util.List;

public class Order {
    // Fields from XML

    /**
     * Order number [nummer in XML}
     * NotNull
     */
    private long number;

    private String orderType; //OrderType
    private long date; //Datum
    private String reference; //Referentie
    private String note; //Notitie

    private Relation relation; //Relatie
    private Employee employee; //Medewerker
    private Installation installation; //Installatie
    private List<Task> tasks; //Taken
    private List<Component> components; //Componenten
    private List<Part> parts; //Onderdelen
    private List<Info> extraInfo; //ExtraInfo

    /**
     * This is time when this order was imported to BO Server.
     * After import it never changed.
     */
    private long importDate;

    /**
     * This is time when this order was changed in BO Server.
     * If order changed in BO this field has to be updated.
     * This field is used by Android App to check if order has to be updated from server.
     * When order is imported to BO Server this time is set the same value as lastServerChangeDate
     * This field is NOT changed in Android App.
     */
    private long lastServerChangeDate;

    /**
     * This is time when this order was changed in Android.
     * If order changed in Android App this field has to be updated.
     * This field should NOT changed in BO Server.
     * When order is imported to BO Server this time is set to NULL
     */
    private long lastAndroidChangeDate;

    /**
     * Order status
     */
    @GlobalConstants.OrderStatus
    private int orderStatus;

    /**
     * This is ID for custom template.
     * If order use default temple customTemplateID == 0;
     * If customTemplateID > 0 this mean that this order use non-default template
     * If non-default template is assigned/changed on server than lastServerChangeDate has to be updated.
     */
    private long customTemplateID;

    public Order() {
    }

    public long getCustomTemplateID() {
        return customTemplateID;
    }

    public void setCustomTemplateID(long customTemplateID) {
        this.customTemplateID = customTemplateID;
    }

    @GlobalConstants.OrderStatus
    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(@GlobalConstants.OrderStatus int orderStatus) {
        this.orderStatus = orderStatus;
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

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
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

    public long getImportDate() {
        return importDate;
    }

    public void setImportDate(long importDate) {
        this.importDate = importDate;
    }

    public long getLastServerChangeDate() {
        return lastServerChangeDate;
    }

    public void setLastServerChangeDate(long lastServerChangeDate) {
        this.lastServerChangeDate = lastServerChangeDate;
    }

    public long getLastAndroidChangeDate() {
        return lastAndroidChangeDate;
    }

    public void setLastAndroidChangeDate(long lastAndroidChangeDate) {
        this.lastAndroidChangeDate = lastAndroidChangeDate;
    }
}
