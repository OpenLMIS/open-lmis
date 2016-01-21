package org.openlmis.stockmanagement.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class StockCardEntryKV {
    private String keyColumn;
    private String valueColumn;
    private Date syncedDate;
}
