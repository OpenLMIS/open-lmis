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
  VALUES ('build_znz-survey-questions_1423591615',
          'ZNZ Survey',
          '10',
          'hash',
          'XForm used to perform survey in ZNZ.',
          'http://uat.tz.elmis-dev.org/odk-api/getForm/build_znz-survey-questions_1423591615',
          '<h:html xmlns="http://www.w3.org/2002/xforms" xmlns:h="http://www.w3.org/1999/xhtml" xmlns:ev="http://www.w3.org/2001/xml-events" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:jr="http://openrosa.org/javarosa">
	<h:head>
		<h:title>ZNZ Survey</h:title>
		<model>
			<instance>
				<data id="build_znz-survey-questions_1160561495">
					<meta>
						<instanceID/>
					</meta>
					<country />
					<zone />
					<region />
					<district />
					<facility />
					<a_storage>
						<adequate_storage_space/>
						<adequate_shelves/>
						<store_room_clean/>
						<products_arranged_appropriately/>
						<products_stored_issued/>
						<medicines_stored_separately/>
						<cold_chain_followed/>
						<products_free_from_dusts/>
						<products_free_from_moisture/>
						<products_free_from_sunlight/>
						<store_room_prevented_from_infestation/>
						<adequate_security/>
						<fire_extinguisher_available/>
						<store_room_condition_conductive/>
						<control_for_unauthorized_personnel/>
					</a_storage>
					<lmis_tools_record_keeping>
						<store_ledger_available/>
						<store_ledgers_in_store_room/>
						<bin_cards_available/>
						<bin_cards_kept_with_products/>
						<ending_balances_equal_to_stocks/>
						<losses_adjustments_correctly_filled/>
						<ledgers_bin_cards_filled_correctly/>
						<physical_stock_counts_exercises_conducted/>
						<ddr_available/>
						<invoices_kept_in_file/>
						<last_supervision_visit_in_file/>
					</lmis_tools_record_keeping>
					<r_and_r>
						<randr_available/>
						<opening_ending_balances_equal/>
						<ending_balance_corresponds_to_ledger/>
						<consumption_estimation_correctly_filled/>
						<stock_out_adjustment_correct/>
						<quantity_required_correctly_filled/>
						<column_of_costs_filled_correctly/>
						<randr_forms_filled/>
					</r_and_r>
					<facility_gps_location/>
					<first_picture/>
					<second_picture/>
					<third_picture/>
					<deviceID/>
				</data>
			</instance>
			<itext>
				<translation lang="eng">
					<text id="/data/a_storage:label">
						<value>A-STORAGE</value>
					</text>
					<text id="/data/a_storage/adequate_storage_space:label">
						<value>Is there adequate storage space?</value>
					</text>
					<text id="/data/a_storage/adequate_storage_space:option0">
						<value>Yes</value>
					</text>
					<text id="/data/a_storage/adequate_storage_space:option1">
						<value>No</value>
					</text>
					<text id="/data/a_storage/adequate_storage_space:option2">
						<value>NA</value>
					</text>
					<text id="/data/a_storage/adequate_shelves:label">
						<value>Are there adequate shelves?</value>
					</text>
					<text id="/data/a_storage/store_room_clean:label">
						<value>Is the store room clean?</value>
					</text>
					<text id="/data/a_storage/products_arranged_appropriately:label">
						<value>Are the products arranged appropriately?</value>
					</text>
					<text id="/data/a_storage/products_stored_issued:label">
						<value>Are products being stored/issued according to FEFO?</value>
					</text>
					<text id="/data/a_storage/medicines_stored_separately:label">
						<value>Are medicines being stored separately from chemicals and hazard products?</value>
					</text>
					<text id="/data/a_storage/cold_chain_followed:label">
						<value>Is cold chain being followed? (if applicable)</value>
					</text>
					<text id="/data/a_storage/products_free_from_dusts:label">
						<value>Are the products free from dusts ? </value>
					</text>
					<text id="/data/a_storage/products_free_from_moisture:label">
						<value>Are the products free from moisture and high temperature?</value>
					</text>
					<text id="/data/a_storage/products_free_from_sunlight:label">
						<value>Are the products free from direct sunlight?</value>
					</text>
					<text id="/data/a_storage/store_room_prevented_from_infestation:label">
						<value>Is the store room prevented from infestation by vermin and pests?</value>
					</text>
					<text id="/data/a_storage/adequate_security:label">
						<value>Is there adequate security in store room and dispensing area (locks, windows barred, etc)?</value>
					</text>
					<text id="/data/a_storage/fire_extinguisher_available:label">
						<value>Is the Fire extinguisher or Bucket of Sand available at the health facility?</value>
					</text>
					<text id="/data/a_storage/store_room_condition_conductive:label">
						<value>Is the general condition of the Store room conducive? (walls, ceiling, paint, cracks) ? </value>
					</text>
					<text id="/data/a_storage/control_for_unauthorized_personnel:label">
						<value>Is there any control for unauthorized personnel?</value>
					</text>
					<text id="/data/lmis_tools_record_keeping:label">
						<value>FILLING OF LOGISTIC MANAGEMENT INFORMATION SYSTEM (LMIS) TOOLS AND RECORD KEEPING</value>
					</text>
					<text id="/data/lmis_tools_record_keeping/store_ledger_available:label">
						<value>Are Stores Ledger  available in the facility?</value>
					</text>
					<text id="/data/lmis_tools_record_keeping/store_ledgers_in_store_room:label">
						<value>Are Store ledgers kept in a store room?</value>
					</text>
					<text id="/data/lmis_tools_record_keeping/bin_cards_available:label">
						<value>Are the Bin Cards available?</value>
					</text>
					<text id="/data/lmis_tools_record_keeping/bin_cards_kept_with_products:label">
						<value>Are Bin Cards kept appropriately on shelves with correct products?</value>
					</text>
					<text id="/data/lmis_tools_record_keeping/ending_balances_equal_to_stocks:label">
						<value>Are Ending balance on Store Ledger/Bin Cards equal to physical stocks?</value>
					</text>
					<text id="/data/lmis_tools_record_keeping/losses_adjustments_correctly_filled:label">
						<value>Are Losses and Adjustments column correctly filled?  </value>
					</text>
					<text id="/data/lmis_tools_record_keeping/ledgers_bin_cards_filled_correctly:label">
						<value>Are Store Ledgers and Bin Cards correctly filled, including physical inventory data?</value>
					</text>
					<text id="/data/lmis_tools_record_keeping/physical_stock_counts_exercises_conducted:label">
						<value>Are the physical stock counts exercises conducted every month and recorded in to the store ledger?</value>
					</text>
					<text id="/data/lmis_tools_record_keeping/ddr_available:label">
						<value>Are Daily Dispensing Registers (DDR) available and correctly filled?</value>
					</text>
					<text id="/data/lmis_tools_record_keeping/invoices_kept_in_file:label">
						<value>Are Invoices &amp; Copies of delivery notes well kept in a file and easily accessed?</value>
					</text>
					<text id="/data/lmis_tools_record_keeping/last_supervision_visit_in_file:label">
						<value>Copy of last supervision visit well kept in a file and easily accessed?</value>
					</text>
					<text id="/data/r_and_r:label">
						<value>REPORT AND REQUEST (R&amp;R)</value>
					</text>
					<text id="/data/r_and_r/randr_available:label">
						<value>Is an R&amp;R Book available?</value>
					</text>
					<text id="/data/r_and_r/opening_ending_balances_equal:label">
						<value>Opening Balance (A) of current R&amp;R = Ending Balance (D) of previous R&amp;R form?</value>
					</text>
					<text id="/data/r_and_r/ending_balance_corresponds_to_ledger:label">
						<value>Ending Balance (D) on each R&amp;R forms is corresponding to the Balance on Store ledger and amount at the dispensing?</value>
					</text>
					<text id="/data/r_and_r/consumption_estimation_correctly_filled:label">
						<value>Estimation of Consumptions (E) = [A+B±C-D] are correctly filled according to this formula?</value>
					</text>
					<text id="/data/r_and_r/stock_out_adjustment_correct:label">
						<value>Stock out adjustment (X &amp; Y) done correctly?</value>
					</text>
					<text id="/data/r_and_r/quantity_required_correctly_filled:label">
						<value>Quantity required (F) = (Yx2) – D if there is stock out or F = (E X 2) – D if there is no stock out, are correctly filled according to the formula?</value>
					</text>
					<text id="/data/r_and_r/column_of_costs_filled_correctly:label">
						<value>Column of Costs (I) = (G x H) filled-in correctly according to the formula?</value>
					</text>
					<text id="/data/r_and_r/randr_forms_filled:label">
						<value>Are R&amp;R forms filled-in and reviewed by DHMTs in a second week of ordering?</value>
					</text>
					<text id="/data/facility_gps_location:label">
						<value>Capture Facility''s GPS Location</value>
					</text>

					<text id="/data/first_picture:label">
						<value>First Picture</value>
					</text>

					<text id="/data/second_picture:label">
						<value>Second Picture</value>
					</text>

					<text id="/data/third_picture:label">
						<value>Third Picture</value>
					</text>
				</translation>
			</itext>
			<bind nodeset="/data/meta/instanceID" type="string" readonly="true()" calculate="concat(''uuid:'', uuid())"/>
			<bind nodeset="/data/country" type="select1" />
			<bind nodeset="/data/zone" type="string" />
			<bind nodeset="/data/region" type="string" />
			<bind nodeset="/data/district" type="string" />
			<bind nodeset="/data/facility" type="string" required="true()"/>
			<bind nodeset="/data/a_storage/adequate_storage_space" type="select1" required="true()"/>
			<bind nodeset="/data/a_storage/adequate_shelves" type="select1" required="true()"/>
			<bind nodeset="/data/a_storage/store_room_clean" type="select1" required="true()"/>
			<bind nodeset="/data/a_storage/products_arranged_appropriately" type="select1" required="true()"/>
			<bind nodeset="/data/a_storage/products_stored_issued" type="select1" required="true()"/>
			<bind nodeset="/data/a_storage/medicines_stored_separately" type="select1" required="true()"/>
			<bind nodeset="/data/a_storage/cold_chain_followed" type="select1" required="true()"/>
			<bind nodeset="/data/a_storage/products_free_from_dusts" type="select1" required="true()"/>
			<bind nodeset="/data/a_storage/products_free_from_moisture" type="select1" required="true()"/>
			<bind nodeset="/data/a_storage/products_free_from_sunlight" type="select1" required="true()"/>
			<bind nodeset="/data/a_storage/store_room_prevented_from_infestation" type="select1" required="true()"/>
			<bind nodeset="/data/a_storage/adequate_security" type="select1" required="true()"/>
			<bind nodeset="/data/a_storage/fire_extinguisher_available" type="select1" required="true()"/>
			<bind nodeset="/data/a_storage/store_room_condition_conductive" type="select1" required="true()"/>
			<bind nodeset="/data/a_storage/control_for_unauthorized_personnel" type="select1" required="true()"/>
			<bind nodeset="/data/lmis_tools_record_keeping/store_ledger_available" type="select1" required="true()"/>
			<bind nodeset="/data/lmis_tools_record_keeping/store_ledgers_in_store_room" type="select1" required="true()"/>
			<bind nodeset="/data/lmis_tools_record_keeping/bin_cards_available" type="select1" required="true()"/>
			<bind nodeset="/data/lmis_tools_record_keeping/bin_cards_kept_with_products" type="select1" required="true()"/>
			<bind nodeset="/data/lmis_tools_record_keeping/ending_balances_equal_to_stocks" type="select1" required="true()"/>
			<bind nodeset="/data/lmis_tools_record_keeping/losses_adjustments_correctly_filled" type="select1" required="true()"/>
			<bind nodeset="/data/lmis_tools_record_keeping/ledgers_bin_cards_filled_correctly" type="select1" required="true()"/>
			<bind nodeset="/data/lmis_tools_record_keeping/physical_stock_counts_exercises_conducted" type="select1" required="true()"/>
			<bind nodeset="/data/lmis_tools_record_keeping/ddr_available" type="select1" required="true()"/>
			<bind nodeset="/data/lmis_tools_record_keeping/invoices_kept_in_file" type="select1" required="true()"/>
			<bind nodeset="/data/lmis_tools_record_keeping/last_supervision_visit_in_file" type="select1" required="true()"/>
			<bind nodeset="/data/r_and_r/randr_available" type="select1" required="true()"/>
			<bind nodeset="/data/r_and_r/opening_ending_balances_equal" type="select1" required="true()"/>
			<bind nodeset="/data/r_and_r/ending_balance_corresponds_to_ledger" type="select1" required="true()"/>
			<bind nodeset="/data/r_and_r/consumption_estimation_correctly_filled" type="select1" required="true()"/>
			<bind nodeset="/data/r_and_r/stock_out_adjustment_correct" type="select1" required="true()"/>
			<bind nodeset="/data/r_and_r/quantity_required_correctly_filled" type="select1" required="true()"/>
			<bind nodeset="/data/r_and_r/column_of_costs_filled_correctly" type="select1" required="true()"/>
			<bind nodeset="/data/r_and_r/randr_forms_filled" type="select1" required="true()"/>
			<bind nodeset="/data/facility_gps_location" type="geopoint"/>
			<bind nodeset="/data/first_picture" type="binary"/>
			<bind nodeset="/data/second_picture" type="binary"/>
			<bind nodeset="/data/third_picture" type="binary"/>
			<bind nodeset="/data/deviceID" type="string" jr:preload="property" jr:preloadParams="deviceid"/>
		</model>
	</h:head>
	<h:body>
		<select1 ref="/data/country">
			<label>Country</label>
			<item>
				<label>Mozambique</label>
				<value>Mozambique</value>
			</item>
			<item>
				<label>Tanzania</label>
				<value>Tanzania</value>
			</item>
			<item>
				<label>Zanzibar</label>
				<value>Zanzibar</value>
			</item>
		</select1>
		<input ref="/data/zone" query="instance(''zones'')/root/item[country= /data/country ]">
			<label>Zone</label>
		</input>
		<input ref="/data/region" query="instance(''regions'')/root/item[country= /data/country  and zone= /data/zone ]">
			<label>Region</label>
		</input>

		<input ref="/data/district" query="instance(''districts'')/root/item[country= /data/country  and zone= /data/zone and region= /data/region ]">
			<label>District</label>
		</input>

		<input ref="/data/facility" query="instance(''facilities'')/root/item[country= /data/country  and zone= /data/zone and region= /data/region and district= /data/district ] ">
			<label>Facility</label>
		</input>

		<group>
			<label ref="jr:itext(''/data/a_storage:label'')"/>
			<select1 ref="/data/a_storage/adequate_storage_space">
				<label ref="jr:itext(''/data/a_storage/adequate_storage_space:label'')"/>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option0'')"/>
					<value>1</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option1'')"/>
					<value>0</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option2'')"/>
					<value>-1</value>
				</item>
			</select1>
			<select1 ref="/data/a_storage/adequate_shelves">
				<label ref="jr:itext(''/data/a_storage/adequate_shelves:label'')"/>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option0'')"/>
					<value>1</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option1'')"/>
					<value>0</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option2'')"/>
					<value>-1</value>
				</item>
			</select1>
			<select1 ref="/data/a_storage/store_room_clean">
				<label ref="jr:itext(''/data/a_storage/store_room_clean:label'')"/>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option0'')"/>
					<value>1</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option1'')"/>
					<value>0</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option2'')"/>
					<value>-1</value>
				</item>
			</select1>
			<select1 ref="/data/a_storage/products_arranged_appropriately">
				<label ref="jr:itext(''/data/a_storage/products_arranged_appropriately:label'')"/>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option0'')"/>
					<value>1</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option1'')"/>
					<value>0</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option2'')"/>
					<value>-1</value>
				</item>
			</select1>
			<select1 ref="/data/a_storage/products_stored_issued">
				<label ref="jr:itext(''/data/a_storage/products_stored_issued:label'')"/>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option0'')"/>
					<value>1</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option1'')"/>
					<value>0</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option2'')"/>
					<value>-1</value>
				</item>
			</select1>
			<select1 ref="/data/a_storage/medicines_stored_separately">
				<label ref="jr:itext(''/data/a_storage/medicines_stored_separately:label'')"/>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option0'')"/>
					<value>1</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option1'')"/>
					<value>0</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option2'')"/>
					<value>-1</value>
				</item>
			</select1>
			<select1 ref="/data/a_storage/cold_chain_followed">
				<label ref="jr:itext(''/data/a_storage/cold_chain_followed:label'')"/>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option0'')"/>
					<value>1</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option1'')"/>
					<value>0</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option2'')"/>
					<value>-1</value>
				</item>
			</select1>
			<select1 ref="/data/a_storage/products_free_from_dusts">
				<label ref="jr:itext(''/data/a_storage/products_free_from_dusts:label'')"/>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option0'')"/>
					<value>1</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option1'')"/>
					<value>0</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option2'')"/>
					<value>-1</value>
				</item>
			</select1>
			<select1 ref="/data/a_storage/products_free_from_moisture">
				<label ref="jr:itext(''/data/a_storage/products_free_from_moisture:label'')"/>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option0'')"/>
					<value>1</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option1'')"/>
					<value>0</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option2'')"/>
					<value>-1</value>
				</item>
			</select1>
			<select1 ref="/data/a_storage/products_free_from_sunlight">
				<label ref="jr:itext(''/data/a_storage/products_free_from_sunlight:label'')"/>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option0'')"/>
					<value>1</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option1'')"/>
					<value>0</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option2'')"/>
					<value>-1</value>
				</item>
			</select1>
			<select1 ref="/data/a_storage/store_room_prevented_from_infestation">
				<label ref="jr:itext(''/data/a_storage/store_room_prevented_from_infestation:label'')"/>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option0'')"/>
					<value>1</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option1'')"/>
					<value>0</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option2'')"/>
					<value>-1</value>
				</item>
			</select1>
			<select1 ref="/data/a_storage/adequate_security">
				<label ref="jr:itext(''/data/a_storage/adequate_security:label'')"/>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option0'')"/>
					<value>1</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option1'')"/>
					<value>0</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option2'')"/>
					<value>-1</value>
				</item>
			</select1>
			<select1 ref="/data/a_storage/fire_extinguisher_available">
				<label ref="jr:itext(''/data/a_storage/fire_extinguisher_available:label'')"/>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option0'')"/>
					<value>1</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option1'')"/>
					<value>0</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option2'')"/>
					<value>-1</value>
				</item>
			</select1>
			<select1 ref="/data/a_storage/store_room_condition_conductive">
				<label ref="jr:itext(''/data/a_storage/store_room_condition_conductive:label'')"/>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option0'')"/>
					<value>1</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option1'')"/>
					<value>0</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option2'')"/>
					<value>-1</value>
				</item>
			</select1>
			<select1 ref="/data/a_storage/control_for_unauthorized_personnel">
				<label ref="jr:itext(''/data/a_storage/control_for_unauthorized_personnel:label'')"/>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option0'')"/>
					<value>1</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option1'')"/>
					<value>0</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option2'')"/>
					<value>-1</value>
				</item>
			</select1>
		</group>
		<group>
			<label ref="jr:itext(''/data/lmis_tools_record_keeping:label'')"/>
			<select1 ref="/data/lmis_tools_record_keeping/store_ledger_available">
				<label ref="jr:itext(''/data/lmis_tools_record_keeping/store_ledger_available:label'')"/>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option0'')"/>
					<value>1</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option1'')"/>
					<value>0</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option2'')"/>
					<value>-1</value>
				</item>
			</select1>
			<select1 ref="/data/lmis_tools_record_keeping/store_ledgers_in_store_room">
				<label ref="jr:itext(''/data/lmis_tools_record_keeping/store_ledgers_in_store_room:label'')"/>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option0'')"/>
					<value>1</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option1'')"/>
					<value>0</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option2'')"/>
					<value>-1</value>
				</item>
			</select1>
			<select1 ref="/data/lmis_tools_record_keeping/bin_cards_available">
				<label ref="jr:itext(''/data/lmis_tools_record_keeping/bin_cards_available:label'')"/>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option0'')"/>
					<value>1</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option1'')"/>
					<value>0</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option2'')"/>
					<value>-1</value>
				</item>
			</select1>
			<select1 ref="/data/lmis_tools_record_keeping/bin_cards_kept_with_products">
				<label ref="jr:itext(''/data/lmis_tools_record_keeping/bin_cards_kept_with_products:label'')"/>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option0'')"/>
					<value>1</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option1'')"/>
					<value>0</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option2'')"/>
					<value>-1</value>
				</item>
			</select1>
			<select1 ref="/data/lmis_tools_record_keeping/ending_balances_equal_to_stocks">
				<label ref="jr:itext(''/data/lmis_tools_record_keeping/ending_balances_equal_to_stocks:label'')"/>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option0'')"/>
					<value>1</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option1'')"/>
					<value>0</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option2'')"/>
					<value>-1</value>
				</item>
			</select1>
			<select1 ref="/data/lmis_tools_record_keeping/losses_adjustments_correctly_filled">
				<label ref="jr:itext(''/data/lmis_tools_record_keeping/losses_adjustments_correctly_filled:label'')"/>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option0'')"/>
					<value>1</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option1'')"/>
					<value>0</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option2'')"/>
					<value>-1</value>
				</item>
			</select1>
			<select1 ref="/data/lmis_tools_record_keeping/ledgers_bin_cards_filled_correctly">
				<label ref="jr:itext(''/data/lmis_tools_record_keeping/ledgers_bin_cards_filled_correctly:label'')"/>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option0'')"/>
					<value>1</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option1'')"/>
					<value>0</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option2'')"/>
					<value>-1</value>
				</item>
			</select1>
			<select1 ref="/data/lmis_tools_record_keeping/physical_stock_counts_exercises_conducted">
				<label ref="jr:itext(''/data/lmis_tools_record_keeping/physical_stock_counts_exercises_conducted:label'')"/>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option0'')"/>
					<value>1</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option1'')"/>
					<value>0</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option2'')"/>
					<value>-1</value>
				</item>
			</select1>
			<select1 ref="/data/lmis_tools_record_keeping/ddr_available">
				<label ref="jr:itext(''/data/lmis_tools_record_keeping/ddr_available:label'')"/>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option0'')"/>
					<value>1</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option1'')"/>
					<value>0</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option2'')"/>
					<value>-1</value>
				</item>
			</select1>
			<select1 ref="/data/lmis_tools_record_keeping/invoices_kept_in_file">
				<label ref="jr:itext(''/data/lmis_tools_record_keeping/invoices_kept_in_file:label'')"/>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option0'')"/>
					<value>1</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option1'')"/>
					<value>0</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option2'')"/>
					<value>-1</value>
				</item>
			</select1>
			<select1 ref="/data/lmis_tools_record_keeping/last_supervision_visit_in_file">
				<label ref="jr:itext(''/data/lmis_tools_record_keeping/last_supervision_visit_in_file:label'')"/>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option0'')"/>
					<value>1</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option1'')"/>
					<value>0</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option2'')"/>
					<value>-1</value>
				</item>
			</select1>
		</group>
		<group>
			<label ref="jr:itext(''/data/r_and_r:label'')"/>
			<select1 ref="/data/r_and_r/randr_available">
				<label ref="jr:itext(''/data/r_and_r/randr_available:label'')"/>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option0'')"/>
					<value>1</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option1'')"/>
					<value>0</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option2'')"/>
					<value>-1</value>
				</item>
			</select1>
			<select1 ref="/data/r_and_r/opening_ending_balances_equal">
				<label ref="jr:itext(''/data/r_and_r/opening_ending_balances_equal:label'')"/>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option0'')"/>
					<value>1</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option1'')"/>
					<value>0</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option2'')"/>
					<value>-1</value>
				</item>
			</select1>
			<select1 ref="/data/r_and_r/ending_balance_corresponds_to_ledger">
				<label ref="jr:itext(''/data/r_and_r/ending_balance_corresponds_to_ledger:label'')"/>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option0'')"/>
					<value>1</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option1'')"/>
					<value>0</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option2'')"/>
					<value>-1</value>
				</item>
			</select1>
			<select1 ref="/data/r_and_r/consumption_estimation_correctly_filled">
				<label ref="jr:itext(''/data/r_and_r/consumption_estimation_correctly_filled:label'')"/>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option0'')"/>
					<value>1</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option1'')"/>
					<value>0</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option2'')"/>
					<value>-1</value>
				</item>
			</select1>
			<select1 ref="/data/r_and_r/stock_out_adjustment_correct">
				<label ref="jr:itext(''/data/r_and_r/stock_out_adjustment_correct:label'')"/>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option0'')"/>
					<value>1</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option1'')"/>
					<value>0</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option2'')"/>
					<value>-1</value>
				</item>
			</select1>
			<select1 ref="/data/r_and_r/quantity_required_correctly_filled">
				<label ref="jr:itext(''/data/r_and_r/quantity_required_correctly_filled:label'')"/>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option0'')"/>
					<value>1</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option1'')"/>
					<value>0</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option2'')"/>
					<value>-1</value>
				</item>
			</select1>
			<select1 ref="/data/r_and_r/column_of_costs_filled_correctly">
				<label ref="jr:itext(''/data/r_and_r/column_of_costs_filled_correctly:label'')"/>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option0'')"/>
					<value>1</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option1'')"/>
					<value>0</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option2'')"/>
					<value>-1</value>
				</item>
			</select1>
			<select1 ref="/data/r_and_r/randr_forms_filled">
				<label ref="jr:itext(''/data/r_and_r/randr_forms_filled:label'')"/>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option0'')"/>
					<value>1</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option1'')"/>
					<value>0</value>
				</item>
				<item>
					<label ref="jr:itext(''/data/a_storage/adequate_storage_space:option2'')"/>
					<value>-1</value>
				</item>
			</select1>
		</group>
		<input ref="/data/facility_gps_location">
			<label ref="jr:itext(''/data/facility_gps_location:label'')"/>

		</input>
		<upload ref="/data/first_picture" mediatype="image/*">
			<label ref="jr:itext(''/data/first_picture:label'')"/>

		</upload>
		<upload ref="/data/second_picture" mediatype="image/*">
			<label ref="jr:itext(''/data/second_picture:label'')"/>

		</upload>
		<upload ref="/data/third_picture" mediatype="image/*">
			<label ref="jr:itext(''/data/third_picture:label'')"/>

		</upload>
	</h:body>
</h:html>
',true, 4);