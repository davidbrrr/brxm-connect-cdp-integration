package org.example;

import java.util.ArrayList;
import java.util.List;

public class EngagementRequestData {
  List<String> segments;

  public List<String> getSegments() {
    return segments;
  }

  public EngagementRequestData(List<String> segments) {
    this.segments = segments;
  }

  public EngagementRequestData() {
  }

  public void setSegments(List<String> segments) {
    this.segments = segments;
  }

  public boolean add(String s) {
    if (segments == null) {
      segments = new ArrayList<>();
    }
    return segments.add(s);
  }
}
