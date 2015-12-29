package org.openlmis.restapi.service;

import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProgramProductService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.restapi.domain.ProgramWithProducts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public List<ProgramWithProducts> getAllProgramsWithProductsByFacilityCode(String facilityCode) {
        Facility facility = facilityService.getFacilityByCode(facilityCode);

        List<ProgramWithProducts> programsWithProducts = new ArrayList();
        for (Program program : programService.getByFacility(facility.getId())) {
            ProgramWithProducts programWithProducts = new ProgramWithProducts();
            programWithProducts.setProgramCode(program.getCode());
            programWithProducts.setProgramName(program.getName());
            List<Product> products = new ArrayList();
            for (ProgramProduct programProduct : programProductService.getByProgram(program)) {
                products.add(programProduct.getProduct());
            }
            programWithProducts.setProducts(products);
            programsWithProducts.add(programWithProducts);
        }

        return programsWithProducts;
    }

    public List<ProgramWithProducts> getLatestProgramsWithProductsByFacilityId(Long facilityId, Date afterUpdatedTime) {

        List<ProgramWithProducts> programsWithProducts = new ArrayList<>();
        for (Program program : programService.getByFacility(facilityId)) {
            List<Product> products = new ArrayList<>();
            for (ProgramProduct programProduct : programProductService.getByProgram(program)) {
                products.add(programProduct.getProduct());
            }

            programsWithProducts.add(createProgramWithProducts(program, products));
        }
        return programsWithProducts;
    }

    private ProgramWithProducts createProgramWithProducts(Program program, List<Product> products) {
        ProgramWithProducts programWithProducts = new ProgramWithProducts();
        programWithProducts.setProgramCode(program.getCode());
        programWithProducts.setProgramName(program.getName());
        programWithProducts.setProducts(products);
        return programWithProducts;
    }
}
