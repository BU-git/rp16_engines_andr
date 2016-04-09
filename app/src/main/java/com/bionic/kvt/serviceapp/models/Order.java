package com.bionic.kvt.serviceapp.models;

import java.util.Date;
import java.util.List;

public class Order {

    private class Relation {
        private int number; //nummer
        private String name; //Naam
        private String town; //Plaats
        private String contactPerson; //ContactPersoon
        private String telephone; //Telefoon
    }

    private class Employee {
        private int number; //nummer
        private String name; //Naam
        private String email; //Email
        private String kenteken; //Kenteken ?? NO ON SCHEMA!
    }

    private class Installation {
        private String name; //Naam
        private String address; //Adres
        private String postCode; //PostCode
        private String town; //Plaats
    }

    private class Task { //Taak
        private Byte vornr; //VORNR
        private String ltxa1; //LTXA1
        private String steus; //STEUS
        private String ktsch; //KTSCH
    }

    private class Component { //Component
        private String eqart; //EQART
        private Integer equnr; //EQUNR
        private String herst; //HERST
        private String typbz; //TYPBZ
        private String sernr; //SERNR
    }

    private class Part { //Onderdeel
        private Byte bdmng; //BDMNG
        private String matnr; //MATNR
        private String mattx; //MATTX
    }

    private class Info { //Info
        private String kindOfLine; //SoortRegel
        private String prePost; //PrePost
        private String key; //Sleutel
        private Byte line; //Regel
        private String description; //Omschrijving
    }

    private int number; //nummer
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
}
