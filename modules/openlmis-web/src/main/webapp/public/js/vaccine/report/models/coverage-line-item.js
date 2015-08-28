var CoverageLineItem = function(lineItem, report){

  $.extend(this, lineItem);

  CoverageLineItem.prototype.isValid = function(){
    return this.trackMale;
  };

  CoverageLineItem.prototype.getTotalRegular = function(){
    return Number(this.regularMale) + Number(this.regularFemale);
  };

  CoverageLineItem.prototype.getRegularCoveragePercentage = function(){
    return (this.getTotalRegular() / this.monthlyTarget) * 100;
  };

  CoverageLineItem.prototype.getTotalOutreach = function(){
    return Number(this.outreachMale) + Number(this.outreachFemale);
  };

  CoverageLineItem.prototype.getOutreachCoveragePercentage = function(){
    return (this.getTotalOutreach() / this.monthlyTarget) * 100 ;
  };

  CoverageLineItem.prototype.getMonthlyTotal = function(){
    return this.getTotalOutreach() + this.getTotalRegular();
  };

  CoverageLineItem.prototype.getMonthlyCoverage = function(){
    return (this.getMonthlyTotal() / this.monthlyTarget) * 100;
  };

  CoverageLineItem.prototype.init = function(){
    // find the right estimate denominator.
    this.annualTargetObject = _.findWhere(report.facilityDemographicEstimates,{demographicEstimateId: this.vaccineProductDose.denominatorEstimateCategoryId});
    this.monthlyTarget = Number(this.annualTargetObject.value / 12);
  };



  this.init();

};
