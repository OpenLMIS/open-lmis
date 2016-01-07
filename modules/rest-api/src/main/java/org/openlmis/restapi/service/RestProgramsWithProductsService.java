package org.openlmis.restapi.service;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.*;
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
    private ProductService productService;

    @Autowired
    private ProgramProductService programProductService;

    @Deprecated
    public List<ProgramWithProducts> getAllProgramsWithProductsByFacilityCode(String facilityCode) {
        Facility facility = facilityService.getFacilityByCode(facilityCode);
        return getProgramWithProductsByFacilityIdAndAfterUpdatedTime(facility.getId(), null, facility.getFacilityType());
    }

    public List<ProgramWithProducts> getLatestProgramsWithProductsByFacilityId(Long facilityId, Date afterUpdatedTime) {
        Facility facility = facilityService.getById(facilityId);
        if (facility == null) {
            throw new DataException("error.facility.unknown");
        }
        return getProgramWithProductsByFacilityIdAndAfterUpdatedTime(facilityId, afterUpdatedTime, facility.getFacilityType());
    }

    private List<ProgramWithProducts> getProgramWithProductsByFacilityIdAndAfterUpdatedTime(Long facilityId, final Date afterUpdatedTime, final FacilityType facilityType) {
        return FluentIterable.from(programService.getByFacility(facilityId)).transform(new Function<Program, ProgramWithProducts>() {
            @Override
            public ProgramWithProducts apply(Program input) {
                return createProgramWithProducts(input, afterUpdatedTime, facilityType);
            }
        }).toList();
    }

    private List<Product> getProductsForProgramAfterUpdatedTime(Program program, Date afterUpdatedTime, FacilityType facilityType) {
        List<ProgramProduct> programProducts;
        if (afterUpdatedTime == null) {
            programProducts = programProductService.getByProgram(program);
        } else {
            programProducts = programProductService.getProductsByProgramAfterUpdatedDateByFacilityType(program, afterUpdatedTime, facilityType);
        }

        return FluentIterable.from(programProducts).transform(new Function<ProgramProduct, Product>() {
            @Override
            public Product apply(ProgramProduct input) {
                return input.getProduct();
            }
        }).toList();
    }

    private ProgramWithProducts createProgramWithProducts(Program program, Date afterUpdatedTime, FacilityType facilityType) {
        ProgramWithProducts programWithProducts = new ProgramWithProducts();
        programWithProducts.setProgramCode(program.getCode());
        programWithProducts.setProgramName(program.getName());
        programWithProducts.setProducts(getProductsForProgramAfterUpdatedTime(program, afterUpdatedTime, facilityType));
        return programWithProducts;
    }
}
