package de.bogenliga.application.services.v1.regionen.service;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import de.bogenliga.application.business.regionen.api.RegionenComponent;
import de.bogenliga.application.business.regionen.api.types.RegionenDO;
import de.bogenliga.application.common.errorhandling.ErrorCode;
import de.bogenliga.application.common.errorhandling.exception.BusinessException;
import de.bogenliga.application.common.service.ServiceFacade;
import de.bogenliga.application.common.service.UserProvider;
import de.bogenliga.application.common.validation.Preconditions;
import de.bogenliga.application.services.v1.regionen.mapper.RegionenDTOMapper;
import de.bogenliga.application.services.v1.regionen.model.RegionenDTO;
import de.bogenliga.application.services.v1.regionen.types.RegionTypes;
import de.bogenliga.application.springconfiguration.security.permissions.RequiresPermission;
import de.bogenliga.application.springconfiguration.security.types.UserPermission;

/**
 * I'm a REST resource and handle region CRUD requests over the HTTP protocol
 *
 * @author Giuseppe Ferrera, giuseppe.ferrera@student.reutlingen-university.de
 */
@RestController
@CrossOrigin
@RequestMapping("v1/regionen")

public class RegionenService implements ServiceFacade {

    private static final String PRECONDITION_MSG_REGION_TYPE = "Unknown type for region";
    private static final String PRECONDITION_MSG_REGION = "Region must not be null";
    private static final String PRECONDITION_MSG_REGION_ID = "Region ID must not be negative";
    private static final String PRECONDITION_MSG_NAME = "Name must not be null ";
    private static final String PRECONDITION_MSG_REGION_Kuerzel = "Region Contraction must not be null";
    

    private static final Logger LOG = LoggerFactory.getLogger(RegionenService.class);

    private final RegionenComponent regionenComponent;


    /**
     * Constructor with dependency injection
     *
     * @param regionenComponent to handle the database CRUD requests
     */

    @Autowired
    public RegionenService(final RegionenComponent regionenComponent) {
        this.regionenComponent = regionenComponent;
    }


    /**
     * I return all regions of the database.
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @RequiresPermission(UserPermission.CAN_READ_STAMMDATEN)
    public List<RegionenDTO> findAll() {
        final List<RegionenDO> regionDOList = regionenComponent.findAll();
        return syncUebergeordnetWithUebergeordnetAsName(
                regionDOList.stream().map(RegionenDTOMapper.toDTO).collect(Collectors.toList()));
    }

    @RequestMapping(value = "ID/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RequiresPermission(UserPermission.CAN_READ_STAMMDATEN)
    public RegionenDTO findById(@PathVariable ("id") final long id){
        Preconditions.checkArgument(id >= 0 , "ID must not be negative");

        LOG.debug("Receive 'findById' with requested ID '{}'", id);

        final RegionenDO regionenDO = regionenComponent.findById(id);

        return syncSingleWithDOs(RegionenDTOMapper.toDTO.apply(regionenDO),regionenComponent.findAll());
    }


    @RequestMapping(method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @RequiresPermission(UserPermission.CAN_MODIFY_STAMMDATEN)
    public RegionenDTO update (@RequestBody final RegionenDTO regionenDTO, final Principal principal){
        checkPreconditions(regionenDTO);
        Preconditions.checkArgument(regionenDTO.getId() >= 0, PRECONDITION_MSG_REGION_ID);

        LOG.debug("Receive  'update' request with id '{}', name '{}'; kuerzel '{}',typ '{}'; uebergeordnete_region '{}' ",
                regionenDTO.getId(),
                regionenDTO.getRegionName(),
                regionenDTO.getRegionKuerzel(),
                regionenDTO.getRegionTyp(),
                regionenDTO.getRegionUebergeordnet());


        final RegionenDO newRegionenDo = RegionenDTOMapper.toDO.apply(regionenDTO);
        final long userID = UserProvider.getCurrentUserId(principal);

        final RegionenDO updateRegionenDO = regionenComponent.update(newRegionenDo,userID);
        return syncSingleWithDOs(RegionenDTOMapper.toDTO.apply(updateRegionenDO), regionenComponent.findAll());
    }


    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    @RequiresPermission(UserPermission.CAN_MODIFY_SYSTEMDATEN)
    public void delete (@PathVariable("id") final long id, final Principal principal){
        Preconditions.checkArgument(id >= 0, "ID must not be negative.");

        LOG.debug("Receive 'delete' request with id '{}'", id);

        final RegionenDO regionenDO = new RegionenDO(id);
        final long userId = UserProvider.getCurrentUserId(principal);
        regionenComponent.delete(regionenDO,userId);
    }


    /**
     * I am creating a new Region/RegionenDTO to be saved in the Database
     * @param regionenDTO the new RegionenDTO with the new values.
     * @param principal the current User, who is locked in
     * @return the new RegionenDTO
     */
    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @RequiresPermission(UserPermission.CAN_MODIFY_STAMMDATEN)
    public RegionenDTO create(@RequestBody final RegionenDTO regionenDTO, final Principal principal) {
        checkPreconditions(regionenDTO);
        final long userId = UserProvider.getCurrentUserId(principal);

        //debug
        LOG.debug("Receive 'create' request with name '{}', identifier '{}', region kuerzel '{}', typ '{}', uebergeordnet '{}'",
                regionenDTO.getRegionName(),
                regionenDTO.getId(),
                regionenDTO.getRegionKuerzel(),
                regionenDTO.getRegionTyp(),
                regionenDTO.getRegionUebergeordnet(),
                userId);

        syncSingleWithDOs(regionenDTO, regionenComponent.findAll());

        final RegionenDO regionenDO = RegionenDTOMapper.toDO.apply(regionenDTO);
        final RegionenDO persistedRegionenDO = regionenComponent.create(regionenDO, userId);

        return RegionenDTOMapper.toDTO.apply(persistedRegionenDO);
    }



    /**
     * I return all regions of a specific type from the database
     * @param type of the regions
     * @return list of {@RegionenDTO}
     */
    @RequestMapping(method = RequestMethod.GET,
            value = "{type}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @RequiresPermission(UserPermission.CAN_READ_STAMMDATEN)
    public List<RegionenDTO> findAllByType(@PathVariable("type") final String type) {

        final String upperCaseType = type.toUpperCase();

        checkRegionType(upperCaseType);

        final List<RegionenDO> regionenDOList = regionenComponent.findAllByType(upperCaseType);
        return syncUebergeordnetWithUebergeordnetAsName(regionenDOList.stream().map(RegionenDTOMapper.toDTO)
                .collect(Collectors.toList()),regionenComponent.findAll());
    }


    /**
     * Checks if given type is a member of {@RegionTypes}
     * @param type to be checked
     */
    private void checkRegionType(final String type) {

        try {
            RegionTypes.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BusinessException(ErrorCode.INVALID_ARGUMENT_ERROR, PRECONDITION_MSG_REGION_TYPE);
        }
    }

    private void checkPreconditions(@RequestBody final RegionenDTO regionenDTO) {
        Preconditions.checkNotNull(regionenDTO, PRECONDITION_MSG_REGION);
        Preconditions.checkNotNull(regionenDTO.getRegionName(), PRECONDITION_MSG_NAME);
        Preconditions.checkNotNull(regionenDTO.getRegionKuerzel(), PRECONDITION_MSG_REGION_Kuerzel);
        Preconditions.checkNotNull(regionenDTO.getRegionTyp(), PRECONDITION_MSG_REGION_TYPE);
    }


    /**
     * I am synchronizing the ID with the uebergeordnetAsName of all given RegionenDTOs.
     * Therefore i am calling the syncSingleWithDTOs for each region.
     * @param regionenDTOs all Regions as DTOs.
     * @return the same list of RegionDTOs, but all IDs and uebergeordnetAsName are matching correctly.
     */
    private List<RegionenDTO> syncUebergeordnetWithUebergeordnetAsName(List<RegionenDTO> regionenDTOs){
        regionenDTOs.stream().forEach(region -> syncSingleWithDTOs(region, regionenDTOs));
        return regionenDTOs;
    }


    /**
     * I am synchronizing the ID with the uebergeordnetAsName of all given RegionenDTOs.
     * Therefore i am calling the syncSingleWithDTOs for each region.
     * @param regionenDTOsToSync a list of all Regions you want to sync.
     * @param allRegionenDos a list containing all Regions of the Database
     * @return the same list of RegionDTOs, but all IDs and uebergeordnetAsName are matching correctly.
     */
    private List<RegionenDTO> syncUebergeordnetWithUebergeordnetAsName(List<RegionenDTO> regionenDTOsToSync, List<RegionenDO> allRegionenDos ){
        regionenDTOsToSync.stream().forEach(region -> syncSingleWithDOs(region, allRegionenDos));
        return regionenDTOsToSync;
    }


    /**
     * I am mapping the regionUebergeordnet ID to the matching Name of the corresponding Region.
     * And the other way around:
     * regionUebergeordnet --> ID --> getRegionById--> regionName --> regionUebergeordentAsName
     * regionUebergeordnetAsName --> regionName--> getRegionByName --> ID --> regionUebergeordent
     * @param currentRegion the RegionDTO, which you want to synchronize
     * @param regions a list of RegionDTOs to find the matching regionName, therefore the list
     *                should contain all regions of the database.
     * @return the same currentRegion, but the ID is matching to the uebergeordnetAsName.
     */
    private RegionenDTO syncSingleWithDTOs(RegionenDTO currentRegion, List<RegionenDTO> regions){
        List<RegionenDTO> possibleRegions = null;
        //Case: The region has a superordinate name but not yet the id
        if(currentRegion.getRegionUebergeordnet() == null
                && currentRegion.getRegionUebergeordnetAsName()!=null){

            possibleRegions = regions.stream().filter((region)-> region.getRegionName().equals(currentRegion.getRegionUebergeordnetAsName()))
                .collect(Collectors.toList());

            if(possibleRegions != null && possibleRegions.size() !=0) {
                currentRegion.setRegionUebergeordnet(possibleRegions.get(0).getId());
            }else{
                LOG.debug("Mapping of the regionUebergeordnetAsName to the regionUebergeordnet Id failed.");
            }

            //Case: The region has a superordinate id but not its corresponding name
        }else if(currentRegion.getRegionUebergeordnet() != null
                && currentRegion.getRegionUebergeordnetAsName()==null){

            possibleRegions = regions.stream().filter(region -> region.getId() ==
                    currentRegion.getRegionUebergeordnet()).collect(
                    Collectors.toList());

            if(possibleRegions != null && possibleRegions.size() !=0) {
                currentRegion.setRegionUebergeordnetAsName(possibleRegions.get(0).getRegionName());
            }else{
                LOG.debug("Mapping of the regionUebergeordnet Id to the regionUebergeordnetAsName failed.");
            }
        }
        return currentRegion;
    }


    /**
     * I am mapping the regionUebergeordnet ID to the matching Name of the corresponding Region.
     * And the other way around:
     * regionUebergeordnet --> ID --> getRegionById--> regionName --> regionUebergeordentAsName
     * regionUebergeordnetAsName --> regionName--> getRegionByName --> ID --> regionUebergeordent
     * @param currentRegion the RegionDTO, which you want to synchronize
     * @param regions a list of RegionDOs to find the matching regionName, therefore the list
     *                should contain all regions of the database.
     * @return the same currentRegion, but the ID is matching to the uebergeordnetAsName.
     */
    private RegionenDTO syncSingleWithDOs(RegionenDTO currentRegion, List<RegionenDO> regions){
        //System.out.println("Current: "+ currentRegion +", all: "+regions.size());
        List<RegionenDO> possibleRegions = null;
        //Case: The region has a superordinate name but not yet the id
        if(currentRegion.getRegionUebergeordnet() == null
                && currentRegion.getRegionUebergeordnetAsName()!=null){

            //search for the region with the name of the regionUebergeordnetAsName field
            possibleRegions = regions.stream().filter((region)-> region.getRegionName().equals(currentRegion.getRegionUebergeordnetAsName()))
                    .collect(Collectors.toList());

            if(possibleRegions != null && possibleRegions.size() !=0) {
                currentRegion.setRegionUebergeordnet(possibleRegions.get(0).getId());
            }else{
                LOG.debug("Mapping of the regionUebergeordnetAsName to the regionUebergeordnet Id failed.");
            }
        //Case: The region has a superordinate id but not its corresponding name
        }else if(currentRegion.getRegionUebergeordnet() != null
                && currentRegion.getRegionUebergeordnetAsName()==null){

            possibleRegions = regions.stream().filter(region -> region.getId() ==
                    currentRegion.getRegionUebergeordnet()).collect(
                    Collectors.toList());

            if(possibleRegions != null && possibleRegions.size() !=0) {
                currentRegion.setRegionUebergeordnetAsName(possibleRegions.get(0).getRegionName());
            }else{
                LOG.debug("Mapping of the regionUebergeordnet Id to the regionUebergeordnetAsName failed.");
            }
        }
        return currentRegion;
    }


}
