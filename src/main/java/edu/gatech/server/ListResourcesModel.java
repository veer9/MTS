package edu.gatech.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ListResourcesModel {
  private String sourceURL;
  private String sourceItem;
  private String createPath;
  private Map<String,String> displayOverrides;

  ListResourcesModel(String sourceURL, String sourceItem, String createPath, Map<String, String> displayOverrides) {
    this.sourceURL = sourceURL;
    this.sourceItem = sourceItem;
    this.createPath = createPath;
    this.displayOverrides = displayOverrides;
  }

  ListResourcesModel(String sourceURL, String sourceItem, String createPath) {
    this(sourceURL, sourceItem, createPath, new HashMap<String, String>());
  }

  ListResourcesModel(String sourceURL, String createPath) {
    this(sourceURL, sourceURL.substring(1), createPath, new HashMap<String, String>());
  }

  public String getSourceURL() { return sourceURL; }
  public String getSourceItems() { return sourceItem; }
  public String getCreatePath() { return createPath; }
  public Map<String, String> getDisplayOverrides() { return displayOverrides; }
  public Set<Map.Entry<String, String>> getOverrideEntries() { return displayOverrides.entrySet(); }
}
