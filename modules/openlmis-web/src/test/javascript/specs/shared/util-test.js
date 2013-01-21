describe("Util", function () {
  it('should get formatted date string', function() {
    var date = new Date(2013, 1, 8, 11, 30, 59, 0);

    var formattedDate = utils.getFormattedDate(date);

    expect(formattedDate).toEqual('08/02/2013');
  });
});
