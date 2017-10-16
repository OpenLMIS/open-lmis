package org.openlmis.restapi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.NoArgsConstructor;
import org.openlmis.programs.domain.malaria.MalariaProgram;
import org.openlmis.programs.repository.MalariaProgramRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@NoArgsConstructor
public class RestMalariaProgramController extends BaseController {
    @Autowired
    private MalariaProgramRepository malariaProgramRepository;

    @RequestMapping(value = "/rest-api/malaria-programs", method = POST, headers = ACCEPT_JSON)
    public ResponseEntity create(@Valid @RequestBody MalariaProgram malariaProgram) throws JsonProcessingException {
        try {
            MalariaProgram savedMalariaProgram = malariaProgramRepository.save(malariaProgram);
            return new ResponseEntity(savedMalariaProgram, HttpStatus.CREATED);
        } catch (Exception exception) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
