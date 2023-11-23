package org.example.model;

import java.util.ArrayList;

public class Segments {

    public Segments() {
    }

    public String id;
    public String name;
    public ArrayList<Segment> segments;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Segment> getSegments() {
        return segments;
    }

    public void setSegments(ArrayList<Segment> segments) {
        this.segments = segments;
    }
}

