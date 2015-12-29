package org.openlmis.restapi.service;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProgramProductService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.restapi.domain.LatestProgramsWithProducts;
import org.openlmis.restapi.domain.ProgramWithProducts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class RestProgramsService {

    @Autowired
    private ProgramService programService;

    @Autowired
    private FacilityService facilityService;

    @Autowired
    private ProgramProductService programProductService;

    @Deprecated
    public List<ProgramWithProducts> getAllProgramsWithProductsByFacilityCode(String facilityCode) {

        return FluentIterable.from(programService.getByFacility(facilityService.getFacilityByCode(facilityCode).getId())).transform(new Function<Program, ProgramWithProducts>() {
            @Override
            public ProgramWithProducts apply(Program input) {
                return createProgramWithProducts(input);
            }
        }).toList();
    }

    public LatestProgramsWithProducts getLatestProgramsWithProductsByFacilityId(Long facilityId, Date afterUpdatedTime) {
        ImmutableList<ProgramWithProducts> programsWithProducts = FluentIterable.from(programService.getByFacility(facilityId)).transform(new Function<Program, ProgramWithProducts>() {
            @Override
            public ProgramWithProducts apply(Program input) {
                return createProgramWithProducts(input);
            }
        }).toList();
        return new LatestProgramsWithProducts(programsWithProducts, new Date());
    }

    private List<Product> getAllProductsForProgram(Program program) {
        return FluentIterable.from(programProductService.getByProgram(program)).transform(new Function<ProgramProduct, Product>() {
            @Override
            public Product apply(ProgramProduct input) {
                return input.getProduct();
            }
        }).toList();

    }

    private ProgramWithProducts createProgramWithProducts(Program program) {
        ProgramWithProducts programWithProducts = new ProgramWithProducts();
        programWithProducts.setProgramCode(program.getCode());
        programWithProducts.setProgramName(program.getName());
        programWithProducts.setProducts(getAllProductsForProgram(program));
        return programWithProducts;
    }
}
