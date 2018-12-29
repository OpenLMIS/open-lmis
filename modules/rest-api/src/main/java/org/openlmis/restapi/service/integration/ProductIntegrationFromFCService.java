package org.openlmis.restapi.service.integration;

import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.ProductCategoryService;
import org.openlmis.core.service.ProductService;
import org.openlmis.core.service.ProgramProductService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.restapi.config.IntegrationFCConfig;
import org.openlmis.restapi.domain.integration.ProductIntegrationDTO;
import org.openlmis.restapi.domain.integration.ProductSupportedProgramDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ProductIntegrationFromFCService extends IntegrationFromFCService<ProductIntegrationDTO>{

    private static final String method = "product/getAllChangesV1";

    private ProductService productService;

    private ProgramService programService;

    private ProgramProductService programProductService;

    private ProductCategoryService productCategoryService;

    @Autowired
    public ProductIntegrationFromFCService(RestTemplate restTemplate,
                                           IntegrationFCConfig integrationFCConfig,
                                           ProductService productService,
                                           ProgramService programService,
                                           ProgramProductService programProductService,
                                           ProductCategoryService productCategoryService) {
        super(restTemplate, integrationFCConfig);
        this.productService = productService;
        this.programService = programService;
        this.programProductService = programProductService;
        this.productCategoryService = productCategoryService;
    }

    @Override
    List<ProductIntegrationDTO> getDataFromFC(String date) {
        return getDataTemplate(date, method, ProductIntegrationDTO[].class);
    }

    @Override
    void toDb(List<ProductIntegrationDTO> data) {
        for(ProductIntegrationDTO productIntegrationDTO : data) {
            Product productFromFc = convertProduct(productIntegrationDTO);
            Product product = productService.getByCode(productFromFc.getCode());
            if(product != null) {
                product.setActive(productFromFc.getActive());
                product.setPrimaryName(productFromFc.getPrimaryName());
            } else {
                product = productFromFc;
            }
            productService.save(product, false);
            List<ProgramProduct> programProducts = convertProgramProduct(productIntegrationDTO);
            programProductService.saveAll(programProducts, product);
        }
    }

    private List<ProgramProduct> convertProgramProduct(ProductIntegrationDTO productIntegrationDTO) {
        Set<String> productSupportedProgramCodes = getProgramCodes(productIntegrationDTO);
        List<ProgramProduct> programProducts = new ArrayList<>();
        for(String programCode : productSupportedProgramCodes) {
            ProgramProduct programProduct = getProgramProduct(programCode, productIntegrationDTO.getCode());
            programProducts.add(programProduct);

        }
        return programProducts;
    }

    private Set<String> getProgramCodes(ProductIntegrationDTO productIntegrationDTO) {
        List<ProductSupportedProgramDTO> productSupportedProgramDTOS = productIntegrationDTO.getProductSupportedProgramDTOS();
        Set<String> productSupportedProgramCodes = new HashSet<>();
        for(ProductSupportedProgramDTO productSupportedProgramDTO : productSupportedProgramDTOS) {
            productSupportedProgramCodes.add(productSupportedProgramDTO.getProgramCode());
        }
        return productSupportedProgramCodes;
    }

    private ProgramProduct getProgramProduct(String programCode, String productCode) {
        ProgramProduct originalProgramProduct = programProductService.getByProgramCodeAndProductCode(programCode, productCode);
        Program program = programService.getByCode(programCode);
        if(null == program) {
            throw new DataException(String.format("Program code %s could not be found", programCode));
        }

        ProgramProduct programProduct = new ProgramProduct();
        //set default value because we could not get it from FC, and default value should be double confirmed with client
        programProduct.setDosesPerMonth(1);
        //set default value because we could not get it from FC, and default value should be double confirmed with client
        programProduct.setProductCategory(productCategoryService.getByCode("11"));
        programProduct = null == originalProgramProduct ? programProduct : originalProgramProduct;
        programProduct.setProgram(program);
        programProduct.setActive(program.getActive());
        return programProduct;
    }

    private Product convertProduct(ProductIntegrationDTO productIntegrationDTO) {
        Product product = new Product();
        BeanUtils.copyProperties(productIntegrationDTO, product);
        return product;
    }
}