var VaccineReport = function(report){

  $.extend(this, report);

  VaccineReport.prototype.init = function(){
    function getLineItems(collection, r){
      var lineItems = [];
      angular.forEach(collection, function(coverage){
          lineItems.push(new CoverageLineItem(coverage, r));
      });
      return lineItems;
    }
    if(this.submissionDate !== null)
    {
      var submittedDate = new Date(this.submissionDate);
      this.submissionDate = (submittedDate.getFullYear() + '-' + submittedDate.getMonth() + '-' + submittedDate.getDate());
    }

    this.coverageLineItems = getLineItems(this.coverageLineItems, this);
    this.coverageLineItemViews = _.groupBy(this.coverageLineItems, 'productId');
  };

  this.init();
};
