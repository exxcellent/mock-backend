package de.bogenliga.application.services.v1.schuetzenstatistik.service;

import de.bogenliga.application.business.schuetzenstatistik.api.SchuetzenstatistikComponent;
import de.bogenliga.application.business.schuetzenstatistik.api.types.SchuetzenstatistikDO;
import de.bogenliga.application.services.v1.schuetzenstatistik.mapper.SchuetzenstatistikDTOMapper;
import de.bogenliga.application.services.v1.schuetzenstatistik.model.SchuetzenstatistikDTO;

import de.bogenliga.application.common.service.ServiceFacade;
import de.bogenliga.application.common.validation.Preconditions;
import de.bogenliga.application.springconfiguration.security.permissions.RequiresPermission;
import de.bogenliga.application.springconfiguration.security.types.UserPermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * I'm a REST resource and handle liga CRUD requests over the HTTP protocol
 *
 * @author Giuseppe Ferrera, giuseppe.ferrera@student.reutlingen-university.de
 */
@RestController
@CrossOrigin
@RequestMapping("v1/mannschaft")
public class SchuetzenstatistikService implements ServiceFacade {
    private static final String PRECONDITION_MSG_VERANSTALTUNG_ID = "Veranstaltung Id must not be negative";
    private static final String PRECONDITION_MSG_WETTKAMPF_ID = "Wettkampf Id must not be negative";

    private final Logger logger = LoggerFactory.getLogger(SchuetzenstatistikService.class);

    private final SchuetzenstatistikComponent schuetzenstatistikComponent;


    /**
     * Constructor with dependency injection
     *
     * @param schuetzenstatistikComponent to handle the database CRUD requests
     */
    @Autowired
    public SchuetzenstatistikService(final SchuetzenstatistikComponent schuetzenstatistikComponent) {
        this.schuetzenstatistikComponent = schuetzenstatistikComponent;
    }


    /**
     * I return the current "schuetzenstatistik" for a "veranstaltung" entries of the database.
     *
     * @return lost of {@link SchuetzenstatistikDTO} as JSON
     */
    @GetMapping(
            value = "veranstaltung={id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @RequiresPermission(UserPermission.CAN_READ_DEFAULT)
    public List<SchuetzenstatistikDTO> getSchuetzenstatistikVeranstaltung(@PathVariable("id") final long id) {

        Preconditions.checkArgument(id >= 0, PRECONDITION_MSG_VERANSTALTUNG_ID);
        logger.debug("Receive 'Schuetzenstatistik für Veranstaltung' request with ID '{}'", id);

        final List<SchuetzenstatistikDO> schuetzenstatistikDOList = schuetzenstatistikComponent.getSchuetzenstatistikVeranstaltung(id);

        return schuetzenstatistikDOList.stream().map(SchuetzenstatistikDTOMapper.toDTO).collect(Collectors.toList());
    }

    /**
     * I return the current "schuetzenstatistik" for a "wettkampftid (tag)" entries of the database.
     *
     * @return lost of {@link SchuetzenstatistikDTO} as JSON
     */
    @GetMapping(
            value = "wettkampf={id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @RequiresPermission(UserPermission.CAN_READ_DEFAULT)
    public List<SchuetzenstatistikDTO> getSchuetzenstatistikWettkampf(@PathVariable("id") final long id) {

        Preconditions.checkArgument(id >= 0, PRECONDITION_MSG_WETTKAMPF_ID);
        logger.debug("Receive 'Schuetzenstatistik für Wettkampf' request with ID '{}'", id);

        final List<SchuetzenstatistikDO> schuetzenstatistikDOList = schuetzenstatistikComponent.getSchuetzenstatistikWettkampf(id);

        return schuetzenstatistikDOList.stream().map(SchuetzenstatistikDTOMapper.toDTO).collect(Collectors.toList());
    }


}
