services.factory('UnitService', function (messageService) {

  var programNameFormatter = function (programName) {
    if (programName === 'VIA Classica') {
      return messageService.get("label.programname.balancerequisition");
    }

    if (programName === 'Testes Rápidos Diag') {
      return messageService.get("label.programname.repaidtest");
    }

    if (programName === 'Malaria') {
      return messageService.get("label.programname.al");
    }
    return programName;
  };
  
  var onLoadScrollEvent = function(elementBodyId, elementHeaderId) {
    var fixedBodyDom = document.getElementById(elementId);
    fixedBodyDom.onscroll = function () {
      var fixedBodyDomLeft = this.scrollLeft;
      document.getElementById(elementHeaderId).scrollLeft = fixedBodyDomLeft;
    };
  };

  return {
    programNameFormatter: programNameFormatter,
    onLoadScrollEvent: onLoadScrollEvent
  };
});