package org.openlmis.report.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class MessageCollection {
    private List<MessageDto> messages;
}
