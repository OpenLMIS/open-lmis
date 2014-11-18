DELETE FROM odk_xform WHERE id = 3;

INSERT INTO odk_xform
(formid,
 name,
 version,
 hash,
 descriptiontext,
 downloadurl,
 xmlstring,
 active,
 odkxformsurveytypeid)
  VALUES ('build_StockStatus_1403568516',
          'StockStatusSurvey',
          '1',
          'hash',
          'XForm used to collect commodity stock status.',
          'http://uat.tz.elmis-dev.org/odk-api/getForm/build_StockStatus_1403568516',
          '<h:html xmlns="http://www.w3.org/2002/xforms" xmlns:h="http://www.w3.org/1999/xhtml" xmlns:ev="http://www.w3.org/2001/xml-events" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:jr="http://openrosa.org/javarosa">
  <h:head>
    <h:title>Stock Status</h:title>
    <model>
      <instance>
        <data id="build_StockStatus_1403568516">
          <meta>
            <instanceID/>
          </meta>
          <stock_status_survey jr:template="">
            <MSD_Code/>
            <Commodity_Name/>
            <Managed/>
            <Physical_inventory/>
            <QtyExpiredToday/>
            <StockCardAvailable/>
            <StockData3Months/>
            <SO_seven_days/>
            <TotalDaysStockedout3months/>
            <Issued3Months/>
            <DaysDataAvailable/>
          </stock_status_survey>
          <deviceID/>
        </data>
      </instance>
      <itext>
        <translation lang="eng">
          <text id="/data/stock_status_survey:label">
            <value>Stock Status Questions</value>
          </text>
          <text id="/data/stock_status_survey/MSD_Code:label">
            <value>MSD Code</value>
          </text>
          <text id="/data/stock_status_survey/Commodity_Name:label">
            <value>Commodity Name </value>
          </text>
          <text id="/data/stock_status_survey/Commodity_Name:option0">
            <value>ALu 1x6 (strip of 6)</value>
          </text>
          <text id="/data/stock_status_survey/Commodity_Name:option1">
            <value>ALu 2x6 (strip of 12)</value>
          </text>
          <text id="/data/stock_status_survey/Commodity_Name:option2">
            <value>AL 3x6 (strip of 18)</value>
          </text>
          <text id="/data/stock_status_survey/Commodity_Name:option3">
            <value>AL 4x6 (strip of 24)</value>
          </text>
          <text id="/data/stock_status_survey/Commodity_Name:option4">
            <value>Sulphadoxine/Pyrimethamine (SP) tablet</value>
          </text>
          <text id="/data/stock_status_survey/Commodity_Name:option5">
            <value>Artesunate injection</value>
          </text>
          <text id="/data/stock_status_survey/Commodity_Name:option6">
            <value>Quinine tablet</value>
          </text>
          <text id="/data/stock_status_survey/Commodity_Name:option7">
            <value>Quinine injection</value>
          </text>
          <text id="/data/stock_status_survey/Commodity_Name:option8">
            <value>mRDT (test)</value>
          </text>
          <text id="/data/stock_status_survey/Commodity_Name:option9">
            <value>Diazepam injection</value>
          </text>
          <text id="/data/stock_status_survey/Commodity_Name:option10">
            <value>Amoxicillin (250 mg) capsule</value>
          </text>
          <text id="/data/stock_status_survey/Commodity_Name:option11">
            <value>Amoxicillin (suspension)</value>
          </text>
          <text id="/data/stock_status_survey/Commodity_Name:option12">
            <value>Paracetamol  (500 mg) tablet</value>
          </text>
          <text id="/data/stock_status_survey/Commodity_Name:option13">
            <value>Cotrimoxazole (480 mg) tablet</value>
          </text>
          <text id="/data/stock_status_survey/Commodity_Name:option14">
            <value>Cotrimoxazole  (susp)</value>
          </text>
          <text id="/data/stock_status_survey/Commodity_Name:option15">
            <value>Ferrous Folic Acid (FeFol) tablet</value>
          </text>
          <text id="/data/stock_status_survey/Commodity_Name:option16">
            <value>Dextrose 5% (500 ml)</value>
          </text>
          <text id="/data/stock_status_survey/Commodity_Name:option17">
            <value>Albendazole tablet (200 mg)</value>
          </text>
          <text id="/data/stock_status_survey/Commodity_Name:option18">
            <value>ORS sachet</value>
          </text>
          <text id="/data/stock_status_survey/Commodity_Name:option19">
            <value>Condoms (piece)</value>
          </text>
          <text id="/data/stock_status_survey/Commodity_Name:option20">
            <value>Depo (injectables) vial</value>
          </text>
          <text id="/data/stock_status_survey/Commodity_Name:option21">
            <value>Implants (piece)</value>
          </text>
          <text id="/data/stock_status_survey/Commodity_Name:option22">
            <value>IUDs (piece)</value>
          </text>
          <text id="/data/stock_status_survey/Commodity_Name:option23">
            <value>Progestin-only (Microval / Microlut) cycle</value>
          </text>
          <text id="/data/stock_status_survey/Commodity_Name:option24">
            <value>Combined oral (Microgynon) cycle</value>
          </text>
          <text id="/data/stock_status_survey/Commodity_Name:option25">
            <value>Oxytocin injection</value>
          </text>
          <text id="/data/stock_status_survey/Commodity_Name:option26">
            <value>Misoprostol tablets</value>
          </text>
          <text id="/data/stock_status_survey/Commodity_Name:option27">
            <value>Magnesium Sulphate injection</value>
          </text>
          <text id="/data/stock_status_survey/Managed:label">
            <value>Is this commodity managed at this health facility? </value>
          </text>
          <text id="/data/stock_status_survey/Managed:option0">
            <value>Yes</value>
          </text>
          <text id="/data/stock_status_survey/Managed:option1">
            <value>No</value>
          </text>
          <text id="/data/stock_status_survey/Physical_inventory:label">
            <value>What is the physical count of usable stock for this commodity today?</value>
          </text>
          <text id="/data/stock_status_survey/QtyExpiredToday:label">
            <value>What is the quantity of this commodity that is expired as of today''s visit? </value>
          </text>
          <text id="/data/stock_status_survey/StockCardAvailable:label">
            <value>Is the ledger book or stock card available for this commodity</value>
          </text>
          <text id="/data/stock_status_survey/StockCardAvailable:option0">
            <value>Yes</value>
          </text>
          <text id="/data/stock_status_survey/StockCardAvailable:option1">
            <value>No</value>
          </text>
          <text id="/data/stock_status_survey/StockData3Months:label">
            <value>Does the ledger book or stock card have any data from the last three months for this commodity? </value>
          </text>
          <text id="/data/stock_status_survey/StockData3Months:option0">
            <value>Yes</value>
          </text>
          <text id="/data/stock_status_survey/StockData3Months:option1">
            <value>No</value>
          </text>
          <text id="/data/stock_status_survey/SO_seven_days:label">
            <value>According the the ledger book or stock card, were there any stockouts in the last three months that lasted longer than 7 days? </value>
          </text>
          <text id="/data/stock_status_survey/SO_seven_days:option0">
            <value>Yes</value>
          </text>
          <text id="/data/stock_status_survey/SO_seven_days:option1">
            <value>No</value>
          </text>
          <text id="/data/stock_status_survey/TotalDaysStockedout3months:label">
            <value>According to the ledger book or stock card, what is the total number of days that this product was stocked out over the most recent three months? </value>
          </text>
          <text id="/data/stock_status_survey/TotalDaysStockedout3months:hint">
            <value></value>
          </text>
          <text id="/data/stock_status_survey/Issued3Months:label">
            <value>According to the ledger book or stock card, how much of this commodity was issued from this facility during the most recent three months? </value>
          </text>
          <text id="/data/stock_status_survey/DaysDataAvailable:label">
            <value>How many days of data are available in the ledger book or stock card? </value>
          </text>
        </translation>
      </itext>
      <bind nodeset="/data/meta/instanceID" type="string" readonly="true()" calculate="concat(''uuid:'', uuid())"/>
      <bind nodeset="/data/stock_status_survey/MSD_Code" type="string" required="true()"/>
      <bind nodeset="/data/stock_status_survey/Commodity_Name" type="select1" required="true()"/>
      <bind nodeset="/data/stock_status_survey/Managed" type="select1" required="true()"/>
      <bind nodeset="/data/stock_status_survey/Physical_inventory" type="int" required="true()"/>
      <bind nodeset="/data/stock_status_survey/QtyExpiredToday" type="int" required="true()"/>
      <bind nodeset="/data/stock_status_survey/StockCardAvailable" type="select1" required="true()"/>
      <bind nodeset="/data/stock_status_survey/StockData3Months" type="select1" required="true()"/>
      <bind nodeset="/data/stock_status_survey/SO_seven_days" type="select1" required="true()"/>
      <bind nodeset="/data/stock_status_survey/TotalDaysStockedout3months" type="int" required="true()"/>
      <bind nodeset="/data/stock_status_survey/Issued3Months" type="int" required="true()"/>
      <bind nodeset="/data/stock_status_survey/DaysDataAvailable" type="int" required="true()"/>
      <bind nodeset="/data/deviceID" type="string" jr:preload="property" jr:preloadParams="deviceid"/>
    </model>
  </h:head>
  <h:body>
    <group>
      <label ref="jr:itext(''/data/stock_status_survey:label'')"/>
      <repeat nodeset="/data/stock_status_survey">
        <input ref="/data/stock_status_survey/MSD_Code">
          <label ref="jr:itext(''/data/stock_status_survey/MSD_Code:label'')"/>
        </input>
        <select1 ref="/data/stock_status_survey/Commodity_Name">
          <label ref="jr:itext(''/data/stock_status_survey/Commodity_Name:label'')"/>
          <item>
            <label ref="jr:itext(''/data/stock_status_survey/Commodity_Name:option0'')"/>
            <value>ALu 1x6 (strip of 6)</value>
          </item>
          <item>
            <label ref="jr:itext(''/data/stock_status_survey/Commodity_Name:option1'')"/>
            <value>ALu 2x6 (strip of 12)</value>
          </item>
          <item>
            <label ref="jr:itext(''/data/stock_status_survey/Commodity_Name:option2'')"/>
            <value>AL 3x6 (strip of 18)</value>
          </item>
          <item>
            <label ref="jr:itext(''/data/stock_status_survey/Commodity_Name:option3'')"/>
            <value>AL 4x6 (strip of 24)</value>
          </item>
          <item>
            <label ref="jr:itext(''/data/stock_status_survey/Commodity_Name:option4'')"/>
            <value>Sulphadoxine/Pyrimethamine (SP) tablet</value>
          </item>
          <item>
            <label ref="jr:itext(''/data/stock_status_survey/Commodity_Name:option5'')"/>
            <value>Artesunate injection</value>
          </item>
          <item>
            <label ref="jr:itext(''/data/stock_status_survey/Commodity_Name:option6'')"/>
            <value>Quinine tablet</value>
          </item>
          <item>
            <label ref="jr:itext(''/data/stock_status_survey/Commodity_Name:option7'')"/>
            <value>Quinine injection</value>
          </item>
          <item>
            <label ref="jr:itext(''/data/stock_status_survey/Commodity_Name:option8'')"/>
            <value>mRDT (test)</value>
          </item>
          <item>
            <label ref="jr:itext(''/data/stock_status_survey/Commodity_Name:option9'')"/>
            <value>Diazepam injection</value>
          </item>
          <item>
            <label ref="jr:itext(''/data/stock_status_survey/Commodity_Name:option10'')"/>
            <value>Amoxicillin (250 mg) capsule</value>
          </item>
          <item>
            <label ref="jr:itext(''/data/stock_status_survey/Commodity_Name:option11'')"/>
            <value>Amoxicillin (suspension)</value>
          </item>
          <item>
            <label ref="jr:itext(''/data/stock_status_survey/Commodity_Name:option12'')"/>
            <value>Paracetamol  (500 mg) tablet</value>
          </item>
          <item>
            <label ref="jr:itext(''/data/stock_status_survey/Commodity_Name:option13'')"/>
            <value>Cotrimoxazole (480 mg) tablet</value>
          </item>
          <item>
            <label ref="jr:itext(''/data/stock_status_survey/Commodity_Name:option14'')"/>
            <value>Cotrimoxazole  (susp)</value>
          </item>
          <item>
            <label ref="jr:itext(''/data/stock_status_survey/Commodity_Name:option15'')"/>
            <value>Ferrous Folic Acid (FeFol) tablet</value>
          </item>
          <item>
            <label ref="jr:itext(''/data/stock_status_survey/Commodity_Name:option16'')"/>
            <value>Dextrose 5% (500 ml)</value>
          </item>
          <item>
            <label ref="jr:itext(''/data/stock_status_survey/Commodity_Name:option17'')"/>
            <value>Albendazole tablet (200 mg)</value>
          </item>
          <item>
            <label ref="jr:itext(''/data/stock_status_survey/Commodity_Name:option18'')"/>
            <value>ORS sachet</value>
          </item>
          <item>
            <label ref="jr:itext(''/data/stock_status_survey/Commodity_Name:option19'')"/>
            <value>Condoms (piece)</value>
          </item>
          <item>
            <label ref="jr:itext(''/data/stock_status_survey/Commodity_Name:option20'')"/>
            <value>Depo (injectables) vial</value>
          </item>
          <item>
            <label ref="jr:itext(''/data/stock_status_survey/Commodity_Name:option21'')"/>
            <value>Implants (piece)</value>
          </item>
          <item>
            <label ref="jr:itext(''/data/stock_status_survey/Commodity_Name:option22'')"/>
            <value>IUDs (piece)</value>
          </item>
          <item>
            <label ref="jr:itext(''/data/stock_status_survey/Commodity_Name:option23'')"/>
            <value>Progestin-only (Microval / Microlut) cycle</value>
          </item>
          <item>
            <label ref="jr:itext(''/data/stock_status_survey/Commodity_Name:option24'')"/>
            <value>Combined oral (Microgynon) cycle</value>
          </item>
          <item>
            <label ref="jr:itext(''/data/stock_status_survey/Commodity_Name:option25'')"/>
            <value>Oxytocin injection</value>
          </item>
          <item>
            <label ref="jr:itext(''/data/stock_status_survey/Commodity_Name:option26'')"/>
            <value>Misoprostol tablets</value>
          </item>
          <item>
            <label ref="jr:itext(''/data/stock_status_survey/Commodity_Name:option27'')"/>
            <value>Magnesium Sulphate injection</value>
          </item>
        </select1>
        <select1 ref="/data/stock_status_survey/Managed">
          <label ref="jr:itext(''/data/stock_status_survey/Managed:label'')"/>
          <item>
            <label ref="jr:itext(''/data/stock_status_survey/Managed:option0'')"/>
            <value>Yes</value>
          </item>
          <item>
            <label ref="jr:itext(''/data/stock_status_survey/Managed:option1'')"/>
            <value>No</value>
          </item>
        </select1>
        <input ref="/data/stock_status_survey/Physical_inventory">
          <label ref="jr:itext(''/data/stock_status_survey/Physical_inventory:label'')"/>
        </input>
        <input ref="/data/stock_status_survey/QtyExpiredToday">
          <label ref="jr:itext(''/data/stock_status_survey/QtyExpiredToday:label'')"/>
        </input>
        <select1 ref="/data/stock_status_survey/StockCardAvailable">
          <label ref="jr:itext(''/data/stock_status_survey/StockCardAvailable:label'')"/>
          <item>
            <label ref="jr:itext(''/data/stock_status_survey/StockCardAvailable:option0'')"/>
            <value>Yes</value>
          </item>
          <item>
            <label ref="jr:itext(''/data/stock_status_survey/StockCardAvailable:option1'')"/>
            <value>No</value>
          </item>
        </select1>
        <select1 ref="/data/stock_status_survey/StockData3Months">
          <label ref="jr:itext(''/data/stock_status_survey/StockData3Months:label'')"/>
          <item>
            <label ref="jr:itext(''/data/stock_status_survey/StockData3Months:option0'')"/>
            <value>Yes</value>
          </item>
          <item>
            <label ref="jr:itext(''/data/stock_status_survey/StockData3Months:option1'')"/>
            <value>No</value>
          </item>
        </select1>
        <select1 ref="/data/stock_status_survey/SO_seven_days">
          <label ref="jr:itext(''/data/stock_status_survey/SO_seven_days:label'')"/>
          <item>
            <label ref="jr:itext(''/data/stock_status_survey/SO_seven_days:option0'')"/>
            <value>Yes</value>
          </item>
          <item>
            <label ref="jr:itext(''/data/stock_status_survey/SO_seven_days:option1'')"/>
            <value>No</value>
          </item>
        </select1>
        <input ref="/data/stock_status_survey/TotalDaysStockedout3months">
          <label ref="jr:itext(''/data/stock_status_survey/TotalDaysStockedout3months:label'')"/>
          <hint ref="jr:itext(''/data/stock_status_survey/TotalDaysStockedout3months:hint'')"/>
        </input>
        <input ref="/data/stock_status_survey/Issued3Months">
          <label ref="jr:itext(''/data/stock_status_survey/Issued3Months:label'')"/>
        </input>
        <input ref="/data/stock_status_survey/DaysDataAvailable">
          <label ref="jr:itext(''/data/stock_status_survey/DaysDataAvailable:label'')"/>
        </input>
      </repeat>
    </group>
  </h:body>
</h:html>',true, 2);