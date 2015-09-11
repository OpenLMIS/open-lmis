var CoverageLineItem = function(lineItem, report){

  $.extend(this, lineItem);

  this.enableCalculations = true;

  CoverageLineItem.prototype.getTotalRegular = function(){
    return Number(this.regularMale) + Number(this.regularFemale);
  };

  CoverageLineItem.prototype.getRegularCoveragePercentage = function(){
    return ((this.getTotalRegular() / this.monthlyTarget) * 100) ;
  };

  CoverageLineItem.prototype.getTotalOutreach = function(){
    return Number(this.outreachMale) + Number(this.outreachFemale);
  };

  CoverageLineItem.prototype.getOutreachCoveragePercentage = function(){
    return ((this.getTotalOutreach() / this.monthlyTarget) * 100 );
  };

  CoverageLineItem.prototype.getMonthlyTotal = function(){
    return this.getTotalOutreach() + this.getTotalRegular();
  };

  CoverageLineItem.prototype.getMonthlyCoverage = function(){
    return ((this.getMonthlyTotal() / this.monthlyTarget) * 100);
  };

  CoverageLineItem.prototype.getTotalAnnualRegular = function(){
    return this.getTotalRegular() + Number(this.previousRegular);
  };

  CoverageLineItem.prototype.getTotalAnnualRegularCoveragePercentage = function(){
    return((this.getTotalAnnualRegular() / this.annualTarget) * 100);
  };

  CoverageLineItem.prototype.getTotalAnnualOutreach = function(){
    return (this.getTotalOutreach() + Number(this.previousRegular));
  };

  CoverageLineItem.prototype.getTotalAnnualOutreachCoveragePercentage = function(){
    return ((this.getTotalAnnualOutreach() / this.annualTarget) * 100);
  };

  CoverageLineItem.prototype.getAnnualTotal = function(){
    return (this.getTotalAnnualOutreach() + this.getTotalAnnualRegular());
  };

  CoverageLineItem.prototype.init = function(){
    // find the right estimate denominator.
    try{
      this.annualTargetObject = _.findWhere(report.facilityDemographicEstimates,{demographicEstimateId: this.vaccineProductDose.denominatorEstimateCategoryId});
      this.annualTarget = Number(this.annualTargetObject.value);
      this.monthlyTarget = Number(this.annualTargetObject.value / 12);
    }catch( e){
      this.enableCalculations =false;
    }
  };



  this.init();

};
