INSERT INTO odk_xform
(formid,
 name,
 version,
 hash,
 descriptiontext,
 downloadurl,
 xmlstring,
 active)
  VALUES ('build_OpenLMISFacilitiesV2_1095863799',
          'OpenLMISFacilitiesV2',
          '2',
          'hash',
          'XForm used to collect Facility location and pictures.',
          'http://uat.tz.elmis-dev.org/odk-api/getForm/build_OpenLMISFacilitiesV2_1095863799',
          '<h:html xmlns="http://www.w3.org/2002/xforms" xmlns:h="http://www.w3.org/1999/xhtml" xmlns:ev="http://www.w3.org/2001/xml-events" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:jr="http://openrosa.org/javarosa">
  <h:head>
    <h:title>OpenLMISFacilitiesV2</h:title>
    <model>
      <instance>
        <data id="build_OpenLMISFacilitiesV2_1095863799">
          <meta>
            <instanceID/>
          </meta>
		  <country/>
          <province/>
         <facilitiesGroup jr:template="">
		    <district/>
            <facility/>
            <facilityGPSLocation/>
            <firstPicture/>
            <secondPicture/>
            <thirdPicture/>
            <fourthPicture/>
            <fifthPicture/>
          </facilitiesGroup>
          <deviceID/>
		  <subscriberid/>
		  <simserial/>
		  <phonenumber/>
		  <username/>
		  <email/>
		  <start/>
          <end/>
        </data>
      </instance>

	  <itext>
        <translation lang="eng">
          <text id="/data/facilitiesGroup:label">
            <value>OpenLMIS Facilities Location</value>
          </text>
          <text id="/data/facilitiesGroup/facilityGPSLocation:label">
            <value>Capture Facility''s GPS Location</value>
          </text>
          <text id="/data/facilitiesGroup/facilityGPSLocation:hint">
            <value>Please capture the GPS location</value>
          </text>
          <text id="/data/facilitiesGroup/firstPicture:label">
            <value>First Picture</value>
          </text>
          <text id="/data/facilitiesGroup/firstPicture:hint">
            <value>Facility''s first picture.</value>
          </text>
          <text id="/data/facilitiesGroup/secondPicture:label">
            <value>Second Picture</value>
          </text>
          <text id="/data/facilitiesGroup/secondPicture:hint">
            <value>Facility''s second picture.</value>
          </text>
          <text id="/data/facilitiesGroup/thirdPicture:label">
            <value>Third Picture</value>
          </text>
          <text id="/data/facilitiesGroup/thirdPicture:hint">
            <value>Facility''s third picture.</value>
          </text>
          <text id="/data/facilitiesGroup/fourthPicture:label">
            <value>Fourth Picture</value>
          </text>
          <text id="/data/facilitiesGroup/fourthPicture:hint">
            <value>Facility''s fourth picture.</value>
          </text>
          <text id="/data/facilitiesGroup/fifthPicture:label">
            <value>Fifth Picture</value>
          </text>
          <text id="/data/facilitiesGroup/fifthPicture:hint">
            <value>Facility''s fifth picture.</value>
          </text>
        </translation>
      </itext>
      <bind nodeset="/data/meta/instanceID" type="string" readonly="true()" calculate="concat(''uuid:'', uuid())"/>
      <bind nodeset="/data/country" type="select1"/>
	  <bind nodeset="/data/province" type="string"/>
      <bind nodeset="/data/facilitiesGroup/district" type="string" required="true()"/>
      <bind nodeset="/data/facilitiesGroup/facility" type="string" required="true()"/>
      <bind nodeset="/data/facilitiesGroup/facilityGPSLocation" type="geopoint"/>
      <bind nodeset="/data/facilitiesGroup/firstPicture" type="binary"/>
      <bind nodeset="/data/facilitiesGroup/secondPicture" type="binary"/>
      <bind nodeset="/data/facilitiesGroup/thirdPicture" type="binary"/>
      <bind nodeset="/data/facilitiesGroup/fourthPicture" type="binary"/>
      <bind nodeset="/data/facilitiesGroup/fifthPicture" type="binary"/>
      <bind nodeset="/data/deviceID" type="string" jr:preload="property" jr:preloadParams="deviceid"/>
	  <bind nodeset="/data/subscriberid" type="string" jr:preload="property" jr:preloadParams="subscriberid"/>
	  <bind nodeset="/data/simserial" type="string" jr:preload="property" jr:preloadParams="simserial"/>
	  <bind nodeset="/data/phonenumber" type="string" jr:preload="property" jr:preloadParams="phonenumber"/>
	  <bind nodeset="/data/username" type="string" jr:preload="property" jr:preloadParams="username"/>
	  <bind nodeset="/data/email" type="string" jr:preload="property" jr:preloadParams="email"/>
	  <bind nodeset="/data/start" type="dateTime" jr:preload="timestamp" jr:preloadParams="start"/>
      <bind nodeset="/data/end" type="dateTime"    jr:preload="timestamp" jr:preloadParams="end"/>


    </model>
  </h:head>
  <h:body>
	  <select1 ref="/data/country">
      <label>Country</label>
      <item>
        <label>Tanzania</label>
        <value>Tanzania</value>
      </item>
     </select1>
	<input ref="/data/province" query="instance(''provinces'')/root/item[country= /data/country]">
      <label>Province</label>
    </input>

    <group>
      <label ref="jr:itext(''/data/facilitiesGroup:label'')"/>
      <repeat nodeset="/data/facilitiesGroup">
       <input ref="/data/facilitiesGroup/district" query="instance(''districts'')/root/item[country= /data/country  and province= /data/province ]">
      <label>District</label>
		</input>
         <input ref="/data/facilitiesGroup/facility" query="instance(''facilities'')/root/item[country= /data/country  and province= /data/province and district= /data/facilitiesGroup/district ] ">
      <label>Facility</label>
      </input>
        <input ref="/data/facilitiesGroup/facilityGPSLocation">
          <label ref="jr:itext(''/data/facilitiesGroup/facilityGPSLocation:label'')"/>
          <hint ref="jr:itext(''/data/facilitiesGroup/facilityGPSLocation:hint'')"/>
        </input>
        <upload ref="/data/facilitiesGroup/firstPicture" mediatype="image/*">
          <label ref="jr:itext(''/data/facilitiesGroup/firstPicture:label'')"/>
          <hint ref="jr:itext(''/data/facilitiesGroup/firstPicture:hint'')"/>
        </upload>
        <upload ref="/data/facilitiesGroup/secondPicture" mediatype="image/*">
          <label ref="jr:itext(''/data/facilitiesGroup/secondPicture:label'')"/>
          <hint ref="jr:itext(''/data/facilitiesGroup/secondPicture:hint'')"/>
        </upload>
        <upload ref="/data/facilitiesGroup/thirdPicture" mediatype="image/*">
          <label ref="jr:itext(''/data/facilitiesGroup/thirdPicture:label'')"/>
          <hint ref="jr:itext(''/data/facilitiesGroup/thirdPicture:hint'')"/>
        </upload>
        <upload ref="/data/facilitiesGroup/fourthPicture" mediatype="image/*">
          <label ref="jr:itext(''/data/facilitiesGroup/fourthPicture:label'')"/>
          <hint ref="jr:itext(''/data/facilitiesGroup/fourthPicture:hint'')"/>
        </upload>
        <upload ref="/data/facilitiesGroup/fifthPicture" mediatype="image/*">
          <label ref="jr:itext(''/data/facilitiesGroup/fifthPicture:label'')"/>
          <hint ref="jr:itext(''/data/facilitiesGroup/fifthPicture:hint'')"/>
        </upload>
      </repeat>
    </group>
  </h:body>
</h:html>',true);