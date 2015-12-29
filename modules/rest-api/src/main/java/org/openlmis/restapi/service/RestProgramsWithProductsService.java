package org.openlmis.restapi.service;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProgramProductService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.restapi.domain.ProgramWithProducts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class RestProgramsWithProductsService {

    @Autowired
    private ProgramService programService;

    @Autowired
    private FacilityService facilityService;

    @Autowired
    private ProgramProductService programProductService;

    @Deprecated
    public List<ProgramWithProducts> getAllProgramsWithProductsByFacilityCode(String facilityCode) {
        return getProgramWithProductsByFacilityIdAndAfterUpdatedTime(facilityService.getFacilityByCode(facilityCode).getId(), null);
    }

    public List<ProgramWithProducts> getLatestProgramsWithProductsByFacilityId(Long facilityId, Date afterUpdatedTime) {
        return getProgramWithProductsByFacilityIdAndAfterUpdatedTime(facilityId, afterUpdatedTime);
    }

    private List<ProgramWithProducts> getProgramWithProductsByFacilityIdAndAfterUpdatedTime(Long facilityId, final Date afterUpdatedTime) {
        return FluentIterable.from(programService.getByFacility(facilityId)).transform(new Function<Program, ProgramWithProducts>() {
            @Override
            public ProgramWithProducts apply(Program input) {
                return createProgramWithProducts(input, afterUpdatedTime);
            }
        }).toList();
    }

    private List<Product> getProductsForProgramAfterUpdatedTime(Program program, Date afterUpdatedTime) {
        List<ProgramProduct> programProducts;
        if (afterUpdatedTime == null) {
            programProducts = programProductService.getByProgram(program);
        } else {
            programProducts = programProductService.getProductsByProgramAfterUpdatedDate(program, afterUpdatedTime);
        }

        return FluentIterable.from(programProducts).transform(new Function<ProgramProduct, Product>() {
            @Override
            public Product apply(ProgramProduct input) {
                return input.getProduct();
            }
        }).toList();
    }

    private ProgramWithProducts createProgramWithProducts(Program program, Date afterUpdatedTime) {
        ProgramWithProducts programWithProducts = new ProgramWithProducts();
        programWithProducts.setProgramCode(program.getCode());
        programWithProducts.setProgramName(program.getName());
        programWithProducts.setProducts(getProductsForProgramAfterUpdatedTime(program, afterUpdatedTime));
        return programWithProducts;
    }
}
