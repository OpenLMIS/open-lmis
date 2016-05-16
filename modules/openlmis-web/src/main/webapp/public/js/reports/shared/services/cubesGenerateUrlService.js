services.factory('CubesGenerateUrlService', function () {
    var baseUrl = "/cubesreports/cube/";

    var generateAggregateUrl = function (cubesName, drillDowns, cuts) {
        return baseUrl + cubesName + "/aggregate" + "?drilldown=" + drillDowns.join("|") + "&cut=" + generateCutsWithDivider(cuts, ';');
    };

    var generateFactsUrl = function (cubesName, cuts) {
        return baseUrl + cubesName + "/facts" + "?cut=" + generateCutsWithDivider(cuts, ';');
    };

    var generateMembersUrl = function (cubesName, cut) {
        return baseUrl + cubesName + "/members/" + cut.dimension + "?cut=" + generateCutsWithDivider([cut], ',');
    };

    var generateFactsUrlWithParams = function (cubesName, cuts, params) {
        return generateFactsUrl(cubesName, cuts) + "&" + jQuery.param(params);
    };

    function generateCutsWithDivider(cuts, divider) {
        return _.map(cuts, function (cut) {
            return cut.dimension + ":" + cut.values.join(divider);
        }).join("|");
    }

    return {
        generateAggregateUrl: generateAggregateUrl,
        generateFactsUrl: generateFactsUrl,
        generateMembersUrl : generateMembersUrl,
        generateFactsUrlWithParams: generateFactsUrlWithParams
    };
});