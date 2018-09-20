package org.openlmis.report.model.dto;

public enum MonthlyReortProgramType {

    VIA(10), MMIA(1);

    private Integer programId;

    private MonthlyReortProgramType(Integer programId) {
        this.programId = programId;
    }

    public Integer getProgramId() {
        return programId;
    }
}
