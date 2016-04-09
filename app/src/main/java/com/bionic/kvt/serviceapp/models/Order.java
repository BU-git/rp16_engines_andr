package com.bionic.kvt.serviceapp.models;

import java.util.Date;
import java.util.List;

public class Order {

    private class Relation {
        private Integer number; //nummer
        private String name; //Naam
        private String location; //Plaats
        private String contactPerson; //ContactPersoon
        private String telephone; //Telefoon
    }

    private class Employee {
        private Integer number; //nummer
        private String name; //Naam
        private String email; //Email
        private String kenteken; //Kenteken ?? NO ON SCHEMA!
    }

    private class Installation {
        private String name; //Naam
        private String address; //Adres
        private String postCode; //PostCode
        private String location; //Plaats
    }

    private class Task { //Taak
        private Byte VORNR; //VORNR
        private String LTXA1; //LTXA1
        private String STEUS; //STEUS
        private String KTSCH; //KTSCH
    }

    private class Component { //Component
        private String EQART; //EQART
        private Integer EQUNR; //EQUNR
        private String HERST; //HERST
        private String TYPBZ; //TYPBZ
        private String SERNR; //SERNR
    }

    private class Part { //Onderdeel
        private Byte BDMNG; //BDMNG
        private String MATNR; //MATNR
        private String MATTX; //MATTX
    }

    private class Info { //Info
        private String soortRegel; //SoortRegel
        private String prePost; //PrePost
        private String sleutel; //Sleutel
        private Byte regel; //Regel
        private String omschrijving; //Omschrijving
    }

    private Integer orderNumber; //nummer
    private String orderType; //OrderType
    private Relation relation; //Relatie
    private Employee employee; //Medewerker
    private Date date; //Datum
    private String reference; //Referentie
    private Installation installation; //Installatie
    private List<Task> tasks; //Taken
    private List<Component> components; //Componenten
    private List<Part> parts; //Onderdelen
    private String note; //Notitie
    private List<Info> extraInfo; //ExtraInfo
}
