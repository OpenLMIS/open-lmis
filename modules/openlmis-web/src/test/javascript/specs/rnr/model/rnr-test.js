describe('R&R test', function() {

  it('should validate R&R and return false if required field missing', function() {
    var lineItem1 = new RnrLineItem({"lineItem":"lineItem1"});
    var lineItem2 = new RnrLineItem();

    var rnr = {'fullSupplyLineItems': [lineItem1, lineItem2]};

    spyOn(lineItem1, 'validateRequiredFields').andReturn(true);
    spyOn(lineItem1, 'arithmeticallyInvalid').andReturn(false);
    spyOn(lineItem2, 'validateRequiredFields').andReturn(false);
    spyOn(lineItem2, 'arithmeticallyInvalid').andReturn(false);

    var programRnrColumnList=[{"name":"beginningBalance"},{"name":"noOfPatients"}];

    rnr = new Rnr(rnr, programRnrColumnList);

    var valid = rnr.isValid();

    expect(lineItem1.validateRequiredFields.calls.length).toEqual(1);
    expect(lineItem1.arithmeticallyInvalid.calls.length).toEqual(1);
    expect(lineItem2.validateRequiredFields.calls.length).toEqual(1);
    expect(lineItem2.arithmeticallyInvalid.calls.length).toEqual(0);
    expect(valid).toBeFalsy();
  });

  it('should validate R&R and return true if required field is not missing', function() {
    var lineItem1 = new RnrLineItem({"lineItem":"lineItem1"});
    var lineItem2 = new RnrLineItem();

    var rnr = {'fullSupplyLineItems': [lineItem1, lineItem2]};

    spyOn(lineItem1, 'validateRequiredFields').andReturn(true);
    spyOn(lineItem1, 'arithmeticallyInvalid').andReturn(false);
    spyOn(lineItem2, 'validateRequiredFields').andReturn(true);
    spyOn(lineItem2, 'arithmeticallyInvalid').andReturn(false);

    var programRnrColumnList=[{"name":"beginningBalance"},{"name":"noOfPatients"}];

    rnr = new Rnr(rnr, programRnrColumnList);

    var valid = rnr.isValid();

    expect(lineItem1.validateRequiredFields.calls.length).toEqual(1);
    expect(lineItem1.arithmeticallyInvalid.calls.length).toEqual(1);
    expect(lineItem2.validateRequiredFields.calls.length).toEqual(1);
    expect(lineItem2.arithmeticallyInvalid.calls.length).toEqual(1);
    expect(valid).toBeTruthy();
  });

  it('should validate R&R and return false if required field is not missing but arithmetically invalid', function() {
    var lineItem1 = new RnrLineItem({"lineItem":"lineItem1"});
    var lineItem2 = new RnrLineItem();

    var rnr = {'fullSupplyLineItems': [lineItem1, lineItem2]};

    spyOn(lineItem1, 'validateRequiredFields').andReturn(true);
    spyOn(lineItem1, 'arithmeticallyInvalid').andReturn(false);
    spyOn(lineItem2, 'validateRequiredFields').andReturn(true);
    spyOn(lineItem2, 'arithmeticallyInvalid').andReturn(true);

    var programRnrColumnList=[{"name":"beginningBalance"},{"name":"noOfPatients"}];

    rnr = new Rnr(rnr, programRnrColumnList);

    var valid = rnr.isValid();

    expect(lineItem1.validateRequiredFields.calls.length).toEqual(1);
    expect(lineItem1.arithmeticallyInvalid.calls.length).toEqual(1);
    expect(lineItem2.validateRequiredFields.calls.length).toEqual(1);
    expect(lineItem2.arithmeticallyInvalid.calls.length).toEqual(1);
    expect(valid).toBeFalsy();
  });

  it('should validate R&R and return false if required field is not missing but arithmetically invalid', function() {
    var lineItem1 = new RnrLineItem({"lineItem":"lineItem1"});
    var lineItem2 = new RnrLineItem();

    var rnr = {'fullSupplyLineItems': [lineItem1, lineItem2]};

    spyOn(lineItem1, 'validateRequiredFields').andReturn(true);
    spyOn(lineItem1, 'arithmeticallyInvalid').andReturn(true);
    spyOn(lineItem2, 'validateRequiredFields').andReturn(false);
    spyOn(lineItem2, 'arithmeticallyInvalid').andReturn(false);

    var programRnrColumnList=[{"name":"beginningBalance"},{"name":"noOfPatients"}];

    rnr = new Rnr(rnr, programRnrColumnList);

    var valid = rnr.isValid();

    expect(lineItem1.validateRequiredFields.calls.length).toEqual(1);
    expect(lineItem1.arithmeticallyInvalid.calls.length).toEqual(1);
    expect(lineItem2.validateRequiredFields.calls.length).toEqual(0);
    expect(lineItem2.arithmeticallyInvalid.calls.length).toEqual(0);
    expect(valid).toBeFalsy();
  });

});