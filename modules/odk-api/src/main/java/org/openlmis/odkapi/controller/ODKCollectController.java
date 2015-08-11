/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openlmis.odkapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Controller
public class ODKCollectController extends BaseController {

    @Autowired
    ServletContext servletContext;

    @RequestMapping(value = "/odk-api/collect/{fileName}")
    @ResponseBody
    public ResponseEntity<byte[]> getODKCollectApkFile(@PathVariable String fileName, HttpServletResponse response)
    {
        final HttpHeaders headers = new HttpHeaders();
        String filePath, requestedFile ;
        if (fileName.equals(ODK_COLLECT_APP_APK_REQUEST_PARAMETER))
        {
            filePath = "/public/apk/" + ODK_COLLECT_APP_APK_FILE_NAME;
            requestedFile = ODK_COLLECT_APP_APK_FILE_NAME;

        }

        else if (fileName.equals(ODK_COLLECT_APP_ITEMSETS_REQUEST_PARAMETER))
        {
            filePath = "/public/apk/" + ODK_COLLECT_APP_ITEMSETS_CSV_FILE_NAME;
            requestedFile = ODK_COLLECT_APP_ITEMSETS_CSV_FILE_NAME;
        }
        else
        {
            headers.setContentType(MediaType.TEXT_PLAIN);
            return new ResponseEntity<byte[]>(CAN_NOT_READ_FILE.getBytes(), headers, HttpStatus.NOT_FOUND);
        }
        File toServeUp = new File( servletContext.getRealPath(filePath) );
        InputStream inputStream = null;

        try
        {
            inputStream = new FileInputStream(toServeUp);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            headers.setContentType(MediaType.TEXT_PLAIN);
            return new ResponseEntity<byte[]>(FILE_NOT_FOUND_ERROR_MESSAGE.getBytes(), headers, HttpStatus.NOT_FOUND);
        }

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + requestedFile + "\"");

        Long fileSize = toServeUp.length();
        response.setContentLength(fileSize.intValue());

        OutputStream outputStream = null;

        try
        {
            outputStream = response.getOutputStream();
        }
        catch (IOException e)
        {
            headers.setContentType(MediaType.TEXT_PLAIN);
            return new ResponseEntity<byte[]>(OUTPUT_STREAM_CANNOT_BE_GENERATED.getBytes(), headers, HttpStatus.NOT_FOUND);
        }

        byte[] buffer = new byte[1024];

        int read = 0;
        try
        {

            while ((read = inputStream.read(buffer)) != -1)
            {
                outputStream.write(buffer, 0, read);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();

        }
        catch (Exception e)
        {
            headers.setContentType(MediaType.TEXT_PLAIN);
            return new ResponseEntity<byte[]>(CAN_NOT_READ_FILE.getBytes(), headers, HttpStatus.NOT_FOUND);
        }


        return new ResponseEntity<byte[]>(buffer, headers, HttpStatus.OK);

    }

}
