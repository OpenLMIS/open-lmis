package org.openlmis.report;

/**
 */
public enum ReportOutputOption {

    HTML("html"),
    PDF("pdf"),
    XLS("xls");

    private String option;

    private ReportOutputOption(String option) {
        this.option = option;
    }
}
