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
    var fixedBodyDom = document.getElementById(elementBodyId);
    fixedBodyDom.onscroll = function () {
      var fixedBodyDomLeft = this.scrollLeft;
      document.getElementById(elementHeaderId).scrollLeft = fixedBodyDomLeft;
    };
  };
  
  var fixedScrollBorHeaderStyles = function (elementHeaderId, elementBodyId) {
    setTimeout(function () {
      var bodyElement = document.getElementById(elementBodyId);
      var headerElement = document.getElementById(elementHeaderId);
      
      if (!bodyElement && !headerElement) {
        return;
      }

      if (bodyElement.offsetHeight < bodyElement.children[0].offsetHeight) {
        headerElement.setAttribute('style', 'width: calc(100% - 10px)');
      } else {
        headerElement.setAttribute('style', '');
      }
    }, 200);
  };

  return {
    programNameFormatter: programNameFormatter,
    onLoadScrollEvent: onLoadScrollEvent,
    fixedScrollBorHeaderStyles: fixedScrollBorHeaderStyles
  };
});