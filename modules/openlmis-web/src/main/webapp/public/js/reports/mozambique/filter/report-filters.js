function DistrictFilter(){
    return function(districts, parentId){
        return _.filter(districts, function(input){
            return !parentId || input.parentId == parentId;
        });
    };
}

function ProvinceFilter(){
    return function(provinces, provinceId){
        return _.filter(provinces, function(input){
            return !provinceId || input.id == provinceId;
        });
    };
}

function FacilityFilter(){

    return function(facilities, districts,districtId,provinceId){
        if (districtId) {
            return [].concat(getAllFacilityInDistrict(facilities, districtId));
        } else if (provinceId) {
            var allDistrictsInProvince = (new DistrictFilter())(districts, provinceId);
            return _.reduce(allDistrictsInProvince, function (fullList, district) {
                return fullList.concat(getAllFacilityInDistrict(facilities, district.id));
            }, []);
        }else{
            return facilities;
        }
    };

    function getAllFacilityInDistrict(facilities, districtId) {
        return _.filter(facilities, function(facility){
            return facility.geographicZoneId == districtId;
        });
    }
}