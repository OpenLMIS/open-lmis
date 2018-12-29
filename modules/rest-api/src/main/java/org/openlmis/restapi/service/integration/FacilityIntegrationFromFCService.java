package org.openlmis.restapi.service.integration;

import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.FacilityRepository;
import org.openlmis.core.repository.ProgramRepository;
import org.openlmis.core.repository.ProgramSupportedRepository;
import org.openlmis.core.repository.mapper.FacilityTypeMapper;
import org.openlmis.core.repository.mapper.GeographicZoneMapper;
import org.openlmis.restapi.config.IntegrationFCConfig;
import org.openlmis.restapi.domain.integration.FacilityIntegrationDTO;
import org.openlmis.restapi.domain.integration.ProgramSupportedIntegrationDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class FacilityIntegrationFromFCService extends IntegrationFromFCService<FacilityIntegrationDTO> {

    private static final String method = "client/getAllChangesV1";

    private FacilityRepository facilityRepository;

    private ProgramRepository programRepository;

    private ProgramSupportedRepository programSupportedRepository;

    private GeographicZoneMapper geographicZoneMapper;

    private FacilityTypeMapper facilityTypeMapper;

    @Autowired
    public FacilityIntegrationFromFCService(RestTemplate restTemplate,
                                            IntegrationFCConfig integrationFCConfig,
                                            ProgramRepository programRepository,
                                            FacilityRepository facilityRepository,
                                            GeographicZoneMapper geographicZoneMapper,
                                            FacilityTypeMapper facilityTypeMapper,
                                            ProgramSupportedRepository programSupportedRepository) {
        super(restTemplate, integrationFCConfig);
        this.programRepository = programRepository;
        this.facilityRepository = facilityRepository;
        this.geographicZoneMapper = geographicZoneMapper;
        this.facilityTypeMapper = facilityTypeMapper;
        this.programSupportedRepository = programSupportedRepository;

    }

    @Override
    List<FacilityIntegrationDTO> getDataFromFC(String date) {
        return getDataTemplate(date, method, FacilityIntegrationDTO[].class);
    }

    @Override
    void toDb(List<FacilityIntegrationDTO> data) {

        Map<String, List<ProgramSupportedIntegrationDTO>> ps = new HashMap<>();
        List<Facility> facilitiesFromFc = convertPrograms(data, ps);
        List<Facility> updatedFacilities = new ArrayList<>();
        classificationOperationForFacility(facilitiesFromFc, updatedFacilities);
        logger.info(String.format("Get facility from FC, need to add[%d], need to update[%d]",
                facilitiesFromFc.size(), updatedFacilities.size()));
        facilityRepository.toPersistDbByOperationType(facilitiesFromFc, updatedFacilities);
        programSupportedRepository.updateFacilitiesSupportedPrograms(constructFacilities(ps));

    }

    private List<Facility> constructFacilities(Map<String, List<ProgramSupportedIntegrationDTO>> ps) {
        List<Facility> facilities = new ArrayList<>();
        List<Program> programs =  programRepository.getAll();
        for (Map.Entry<String, List<ProgramSupportedIntegrationDTO>> entry : ps.entrySet()) {
            Facility facility = facilityRepository.getByCode(entry.getKey());
            List<ProgramSupported> programsSupportedToPersist = new ArrayList<>();
            for (ProgramSupportedIntegrationDTO programSupported : entry.getValue()) {
                ProgramSupported programSupportedToPersist = new ProgramSupported();
                programSupportedToPersist.setFacilityId(facility.getId());
                programSupportedToPersist.setStartDate(facility.getGoLiveDate());
                programSupportedToPersist.setProgram(getProgram(programs, programSupported));
                programSupportedToPersist.setActive(programSupported.isActive());
                programsSupportedToPersist.add(programSupportedToPersist);
            }
            facility.setSupportedPrograms(programsSupportedToPersist);
            facilities.add(facility);
        }
        return facilities;
    }

    private void classificationOperationForFacility(List<Facility> facilitiesFromFc, List<Facility> updatedFacilities) {
        for (Iterator<Facility> facilityFcIt = facilitiesFromFc.iterator(); facilityFcIt.hasNext(); ) {
            Facility facilityFromFc = facilityFcIt.next();
            Facility facility = facilityRepository.getByCode(facilityFromFc.getCode());
            if (facility != null) {
                if (!facility.equals(facilityFromFc)) {
                    updatedFacilities.add(facilityFromFc);
                }
                facilityFcIt.remove();
            }

        }
    }

    private Program getProgram(List<Program> programs, ProgramSupportedIntegrationDTO programSupported){
        for(Program program : programs) {
            if(program.getCode().equals(programSupported.getProgramCode())) {
                return program;
            }
        }
        logger.error(String.format("please supply valid program code [%s] for program supported", programSupported.getProgramCode()));
        throw new DataException("please supply valid program code");

    }


    private List<Facility> convertPrograms(List<FacilityIntegrationDTO> data, Map<String, List<ProgramSupportedIntegrationDTO>> ps) {

        List<GeographicZone> geographicZones = geographicZoneMapper.getAllGeographicZones();
        List<FacilityType> facilityTypes = facilityTypeMapper.getAllTypes();
        List<Facility> facilities = new ArrayList<>();
        for (FacilityIntegrationDTO source : data) {
            Facility target = new Facility();
            BeanUtils.copyProperties(source, target);
            target.setFax("".equals(source.getFax()) ? null : source.getFax());
            target.setMainPhone("".equals(source.getMainPhone()) ? null : source.getMainPhone());
            target.setGeographicZone(getGeographicZone(geographicZones, source));
            target.setFacilityType(getFacilityType(facilityTypes, source));
            if (target.getGeographicZone() != null && target.getFacilityType() != null) {
                facilities.add(target);
                ps.put(target.getCode(), source.getProgramsSupported());
            } else {
                logger.error(String.format("The facility[%s] can not find geographic[%s] or facility type[%s]",
                        target.getCode(), source.getGeographicZoneName(), source.getFacilityTypeName()));
            }

        }

        return facilities;
    }

    private FacilityType getFacilityType(List<FacilityType> facilityTypes, FacilityIntegrationDTO source) {
        for (FacilityType facilityType : facilityTypes) {
            if (facilityType.getName().equals(source.getFacilityTypeName())) {
                return facilityType;
            }
        }
        return null;
    }

    private GeographicZone getGeographicZone(List<GeographicZone> geographicZones, FacilityIntegrationDTO source) {
        for (GeographicZone geographicZone : geographicZones) {
            if (geographicZone.getName().equals(source.getGeographicZoneName())) {
                return geographicZone;
            }
        }
        return null;
    }

}
