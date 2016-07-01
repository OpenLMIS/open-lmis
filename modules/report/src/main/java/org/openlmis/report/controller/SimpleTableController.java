/*
 * Electronic Logistics Management Information System (eLMIS) is a supply
 * chain management system for health commodities in a developing country
 * setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for
 * the U.S. Agency for International Development (USAID). It was prepared
 * under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
  * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public
  * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.report.controller;

import lombok.NoArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.openlmis.core.utils.DateUtil;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.report.mapper.AppInfoMapper;
import org.openlmis.report.mapper.RequisitionReportsMapper;
import org.openlmis.report.model.dto.RequisitionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@NoArgsConstructor
@RequestMapping(value = "/reports")
public class SimpleTableController extends BaseController {

    @Autowired
    private RequisitionReportsMapper requisitionReportsMapper;

    @Autowired
    private AppInfoMapper appInfoMapper;

    @Value("${export.tmp.path}")
    protected String EXPORT_TMP_PATH;

    private static final Logger logger = LoggerFactory.getLogger(SimpleTableController.class);

    @RequestMapping(value = "/requisition-report", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> requisitionReport(
            @RequestParam(value = "startTime", required = true) Date startTime,
            @RequestParam(value = "endTime", required = true) Date endTime) {
        List<RequisitionDTO> requisitions = requisitionReportsMapper.getRequisitionList(startTime, endTime);

        for (RequisitionDTO requisitionDTO : requisitions) {
            requisitionDTO.assignType();
        }
        return OpenLmisResponse.response("rnr_list", requisitions);
    }

    @RequestMapping(value = "/app-version-report", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> appVersionReport() {
        return OpenLmisResponse.response("app_versions", appInfoMapper.queryAll());
    }

    @RequestMapping(value = "/export", method = GET, headers = BaseController.ACCEPT_JSON)
    public void export(HttpServletRequest request, HttpServletResponse response) {

        String zipDirectory = UUID.randomUUID().toString() + "/";

        File directory = new File(EXPORT_TMP_PATH + zipDirectory);
        directory.mkdirs();

        String zipName = "export.zip";

        response.setContentType("Content-type: application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=" + zipName);

        File zipFile = generateZipFile(zipDirectory, zipName);

        if (zipFile != null) {
            try {
                FileInputStream fileInputStream = new FileInputStream(zipFile);
                IOUtils.copy(fileInputStream, response.getOutputStream());
                response.flushBuffer();
                fileInputStream.close();
                FileUtils.deleteDirectory(directory);
            } catch (IOException e) {
                logger.error("error occurred when download export.zip : " + e.getMessage());
            }
        }

    }

    private File generateZipFile(String zipDirectory, String zipName) {
        File zipFile = null;

        ZipOutputStream zipOutputStream = null;


        try {
            List<File> files = generateFiles(zipDirectory);
            zipFile = new File(EXPORT_TMP_PATH + zipDirectory + zipName);

            byte[] buffer = new byte[8 * 1024];
            FileOutputStream fileOutputStream = new FileOutputStream(zipFile);
            zipOutputStream = new ZipOutputStream(fileOutputStream);

            for (File srcFile : files) {
                FileInputStream fileInputStream = new FileInputStream(srcFile);
                zipOutputStream.putNextEntry(new ZipEntry(srcFile.getName()));

                int length;
                while ((length = fileInputStream.read(buffer)) > 0) {
                    zipOutputStream.write(buffer, 0, length);
                }
                zipOutputStream.closeEntry();
                fileInputStream.close();
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            try {
                if (zipOutputStream != null) {
                    zipOutputStream.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }

        return zipFile;
    }

    private HashMap<String, URI> getURIMaps() {
        HashMap<String, URI> map = new HashMap<>();
        try {
            map.put("facilities.csv", new URI("http://localhost:5555/cube/facilities/facts?format=csv"));
            map.put("products.csv", new URI("http://localhost:5555/cube/requisition_line_items/members/products?format=csv"));
            map.put("latest_stock_at_different_facilities.csv", new URI("http://localhost:5555/cube/vw_stock_movements/members/stock?format=csv"));
            map.put("movement_history.csv", new URI("http://localhost:5555/cube/vw_stock_movements/members/movement?format=csv"));
            map.put("requisition_mmia.csv", new URI("http://localhost:5555/cube/requisition_line_items/facts?cut=products:MMIA&format=csv"));
            map.put("requisition_via.csv", new URI("http://localhost:5555/cube/requisition_line_items/facts?cut=products:ESS_MEDS&format=csv"));
            map.put("regimens.csv", new URI("http://localhost:5555/cube/requisitions/members/regimen?format=csv"));
            map.put("patient_quantification.csv", new URI("http://localhost:5555/cube/requisitions/members/patient_quantification?format=csv"));
        } catch (URISyntaxException e) {
            logger.error(e.getMessage());
        }
        return map;
    }

    private List<File> generateFiles(String zipDirectory) {
        List<File> files = new ArrayList<>();
        RestTemplate restTemplate = new RestTemplate();

        for (Map.Entry<String, URI> iterator : getURIMaps().entrySet()) {
            Writer bufferedWriter = null;
            try {
                File tempFile = new File(EXPORT_TMP_PATH + zipDirectory + iterator.getKey());
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(tempFile));
                bufferedWriter = new BufferedWriter(outputStreamWriter);
                bufferedWriter.write(restTemplate.exchange(iterator.getValue(), HttpMethod.GET, new HttpEntity<>(""), String.class).getBody());
                bufferedWriter.close();
                files.add(tempFile);
            } catch (IOException e) {
                logger.error(e.getMessage());
            } finally {
                try {
                    if (bufferedWriter != null) {
                        bufferedWriter.close();
                    }
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        }
        return files;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DateUtil.FORMAT_DATE_TIME);
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }
}
