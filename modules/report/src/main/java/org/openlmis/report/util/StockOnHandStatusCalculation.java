package org.openlmis.report.util;

import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.repository.ProgramProductRepository;
import org.openlmis.report.generator.StockOnHandStatus;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class StockOnHandStatusCalculation {
    @Autowired
    private static ProgramProductRepository programProductRepository;

    private static final String hivProgramCode = "MMIA";

    public static StockOnHandStatus getStockOnHandStatus(long cmm, long soh, String productCode) {

        if (0 == soh) {
            return StockOnHandStatus.STOCK_OUT;
        }
        if (cmm == -1) {
            return StockOnHandStatus.REGULAR_STOCK;
        }

        if (soh < 1 * cmm) {
            return StockOnHandStatus.LOW_STOCK;
        } else if ((!isHivProject(productCode) && soh > 2 * cmm) || (isHivProject(productCode) && soh > 3 * cmm)) {
            return StockOnHandStatus.OVER_STOCK;
        }
        return StockOnHandStatus.REGULAR_STOCK;
    }

    private static Boolean isHivProject(String productCode) {
        List<ProgramProduct> programProducts = programProductRepository.getByProductCode(productCode);
        for(ProgramProduct programProduct : programProducts) {
            if(null != programProduct.getProgram().getParent()) {
                return programProduct.getProgram().getParent().getCode().equals(hivProgramCode);
            }
            return programProduct.getProgram().getCode().equals(hivProgramCode);
        }
        return false;
    }
}