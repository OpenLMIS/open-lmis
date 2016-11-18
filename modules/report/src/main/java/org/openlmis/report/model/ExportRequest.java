package org.openlmis.report.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExportRequest {

    private List<String> reportHeaders;

    private List<TracerDrugRequest> reportContent;
}
