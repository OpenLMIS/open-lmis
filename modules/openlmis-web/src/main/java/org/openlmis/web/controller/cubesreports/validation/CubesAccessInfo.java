package org.openlmis.web.controller.cubesreports.validation;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import lombok.Getter;
import lombok.Setter;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.domain.moz.MozFacilityTypes;
import org.openlmis.report.model.dto.Facility;

import java.util.Arrays;
import java.util.List;

import static com.google.common.collect.FluentIterable.from;
import static org.openlmis.core.domain.moz.MozFacilityTypes.*;

@Getter
public class CubesAccessInfo {
    private static final String CUT_SEPARATOR = "cut=";
    private static final String FACILITY_DIMENSION = "facility:";
    private static final String LOCATION_DIMENSION = "location:";

    private String cubesQueryString;

    private MozFacilityTypes currentUserFacilityType;
    private String facility;
    private String district;
    private String province;

    @Setter
    private boolean isValid;

    public static CubesAccessInfo createInstance(MozFacilityTypes currentUserFacilityType, String cubesQueryString) {
        CubesAccessInfo cubesAccessInfo = new CubesAccessInfo();
        cubesAccessInfo.currentUserFacilityType = currentUserFacilityType;
        cubesAccessInfo.cubesQueryString = cubesQueryString;

        assignLocations(cubesQueryString, cubesAccessInfo);

        return cubesAccessInfo;
    }

    public String fillMissingLocation(List<GeographicZone> geographicZones, List<Facility> facilities) {
        boolean isMissingProvinceForDPM = currentUserFacilityType == DPM && province == null;
        boolean isMissingDistrictForDDM = currentUserFacilityType == DDM && district == null;
        boolean isMissingFacility = (currentUserFacilityType == CSRUR_I || currentUserFacilityType == CSRUR_II) && facility == null;

        if (isMissingProvinceForDPM) {
            fillProvince(geographicZones);
        } else if (isMissingDistrictForDDM) {
            fillProvince(geographicZones);
            fillDistrict(geographicZones);
        } else if (isMissingFacility) {
            fillFacility(facilities);
        }

        return cubesQueryString;
    }

    private void fillFacility(final List<Facility> facilities) {
        FluentIterable<String> facilityCodes = from(facilities).transform(new Function<Facility, String>() {
            @Override
            public String apply(Facility facility) {
                return facility.getCode();
            }
        });
        String facility = Joiner.on(';').join(facilityCodes);
        this.facility = facility;
        cubesQueryString += "|" + FACILITY_DIMENSION + facility;
    }

    private void fillProvince(List<GeographicZone> geographicZones) {
        if (province == null) {
            cubesQueryString += "|" + LOCATION_DIMENSION;

            final Optional<GeographicZone> province = from(geographicZones).firstMatch(isLevel("province"));
            if (province.isPresent()) {
                this.province = province.get().getCode();
                fillDimension(new Function<String, String>() {
                    @Override
                    public String apply(String dimension) {
                        if (isLocationDimension(dimension)) {
                            return LOCATION_DIMENSION + province.get().getCode();
                        } else {
                            return dimension;
                        }
                    }
                });
            }
        }
    }

    private void fillDistrict(List<GeographicZone> geographicZones) {
        final Optional<GeographicZone> district = from(geographicZones).firstMatch(isLevel("district"));
        if (district.isPresent()) {
            this.district = district.get().getCode();
            fillDimension(new Function<String, String>() {
                @Override
                public String apply(String dimension) {
                    if (isLocationDimension(dimension)) {
                        return dimension + "," + district.get().getCode();
                    } else {
                        return dimension;
                    }
                }
            });
        }
    }

    private void fillDimension(Function<String, String> dimensionFunc) {
        if (cubesQueryString.contains(CUT_SEPARATOR)) {
            String[] split = cubesQueryString.split(CUT_SEPARATOR);
            String[] dimensions = split[1].split("\\|");
            FluentIterable<String> transform = from(Arrays.asList(dimensions)).transform(dimensionFunc);
            cubesQueryString = split[0] + CUT_SEPARATOR + Joiner.on('|').join(transform);
        }
    }

    private Predicate<GeographicZone> isLevel(final String levelCode) {
        return new Predicate<GeographicZone>() {
            @Override
            public boolean apply(GeographicZone zone) {
                return zone.getLevel().getCode().equals(levelCode);
            }
        };
    }

    private static void assignLocations(String queryString, CubesAccessInfo cubesAccessInfo) {
        if (queryString.contains(CUT_SEPARATOR)) {
            String[] dimensions = queryString.split(CUT_SEPARATOR)[1].split("\\|");

            for (String dimension : dimensions) {
                if (isFacilityDimension(dimension)) {
                    cubesAccessInfo.facility = dimension.split(":")[1];
                } else if (isLocationDimension(dimension)) {
                    assignProvinceAndDistrict(cubesAccessInfo, dimension);
                }
            }
        }
    }

    private static boolean isLocationDimension(String dimension) {
        return dimension.startsWith(LOCATION_DIMENSION);
    }

    private static boolean isFacilityDimension(String dimension) {
        return dimension.startsWith(FACILITY_DIMENSION);
    }

    private static void assignProvinceAndDistrict(CubesAccessInfo cubesAccessInfo, String dimension) {
        String provinceAndDistrictInString = dimension.split(":")[1];
        String[] provinceAndDistrict = provinceAndDistrictInString.split(",");
        cubesAccessInfo.province = provinceAndDistrict[0];
        if (provinceAndDistrict.length > 1) {
            cubesAccessInfo.district = provinceAndDistrict[1];
        }
    }
}
