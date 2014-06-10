package org.openlmis.report.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class GeoZoneTree {

  private int id;
  private String name;
  private int parentId;

  private List<GeoZoneTree> children;
}
