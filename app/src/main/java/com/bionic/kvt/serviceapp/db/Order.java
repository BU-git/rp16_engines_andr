package com.bionic.kvt.serviceapp.db;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class Order extends RealmObject {
    @PrimaryKey
    private long number;

    private String orderType; //OrderType
    private Date date; //Datum
    private String reference; //Referentie
    private String note; //Notitie

    private Relation relation; //Relatie
    private Employee employee; //Medewerker
    private Installation installation; //Installatie
    private RealmList<Task> tasks; //Taken
    private RealmList<Component> components; //Componenten
    private RealmList<Part> parts; //Onderdelen
    private RealmList<Info> extraInfo; //ExtraInfo

    // Service fields
    private Date importDate;
    private Date lastServerChangeDate;
    private Date lastAndroidChangeDate;
    private int orderStatus;

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

    public RealmList<Task> getTasks() {
        return tasks;
    }

    public void setTasks(RealmList<Task> tasks) {
        this.tasks = tasks;
    }

    public RealmList<Component> getComponents() {
        return components;
    }

    public void setComponents(RealmList<Component> components) {
        this.components = components;
    }

    public RealmList<Part> getParts() {
        return parts;
    }

    public void setParts(RealmList<Part> parts) {
        this.parts = parts;
    }

    public RealmList<Info> getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(RealmList<Info> extraInfo) {
        this.extraInfo = extraInfo;
    }

    public Date getImportDate() {
        return importDate;
    }

    public void setImportDate(Date importDate) {
        this.importDate = importDate;
    }

    public Date getLastServerChangeDate() {
        return lastServerChangeDate;
    }

    public void setLastServerChangeDate(Date lastServerChangeDate) {
        this.lastServerChangeDate = lastServerChangeDate;
    }

    public Date getLastAndroidChangeDate() {
        return lastAndroidChangeDate;
    }

    public void setLastAndroidChangeDate(Date lastAndroidChangeDate) {
        this.lastAndroidChangeDate = lastAndroidChangeDate;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }
}
