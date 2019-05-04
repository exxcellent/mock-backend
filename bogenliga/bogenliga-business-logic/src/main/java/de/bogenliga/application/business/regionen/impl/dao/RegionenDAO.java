package de.bogenliga.application.business.regionen.impl.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import de.bogenliga.application.business.match.impl.dao.QueryBuilder;
import de.bogenliga.application.business.regionen.impl.entity.RegionenBE;
import de.bogenliga.application.common.component.dao.BusinessEntityConfiguration;
import de.bogenliga.application.common.component.dao.DataAccessObject;
import de.bogenliga.application.common.component.dao.BasicDAO;


/**
 * @author Dennis Goericke, dennis.goericke@student.reutlingen-university.de
 */
@Repository
public class RegionenDAO implements DataAccessObject {

    //define logger context
    private static final Logger LOGGER = LoggerFactory.getLogger(RegionenDAO.class);

    //table name in the DB
    private static final String TABLE = "region";

    //business entity parameters
    private static final String REGION_BE_ID = "regionID";
    private static final String REGION_BE_NAME = "regionName";
    private static final String REGION_BE_KUERZEL = "regionKuerzel";
    private static final String REGION_BE_TYP = "regionTyp";
    private static final String REGION_BE_UEBERGEORDNET = "regionUebergeordnet";

    private static final String REGION_TABLE_ID = "region_id";
    private static final String REGION_TABLE_NAME = "region_name";
    private static final String REGION_TABLE_KUERZEL = "region_kuerzel";
    private static final String REGION_TABLE_TYP = "region_typ";
    private static final String REGION_TABLE_UEBERGEORDNET = "region_uebergeordnet";


    private static final BusinessEntityConfiguration<RegionenBE> REGIONEN = new BusinessEntityConfiguration<>(
            RegionenBE.class, TABLE, getColumsToFieldsMap(), LOGGER);


    /**
     * SQL queries
     */
    private static final String FIND_ALL = new QueryBuilder()
            .selectAll()
            .from(TABLE)
            .orderBy(REGION_TABLE_ID)
            .compose().toString();

    private static final String FIND_BY_ID = new QueryBuilder()
            .selectAll()
            .from(TABLE)
            .whereEquals(REGION_TABLE_ID)
            .compose().toString();

    private static final String FIND_ALL_BY_TYPE = new QueryBuilder()
            .selectAll()
            .from(TABLE)
            .whereEquals(REGION_TABLE_TYP)
            .orderBy(REGION_TABLE_NAME)
            .compose().toString();

    private final BasicDAO basicDAO;


    /**
     * Initialize the transaction manager to provide a database connection
     *
     * @param basicDAO to handle the commonly used DB operations
     */

    @Autowired
    public RegionenDAO(final BasicDAO basicDAO) {
        this.basicDAO = basicDAO;
    }


    private static Map<String, String> getColumsToFieldsMap() {
        final Map<String, String> columnsToFieldMap = new HashMap<>();

        columnsToFieldMap.put(REGION_TABLE_ID, REGION_BE_ID);
        columnsToFieldMap.put(REGION_TABLE_NAME, REGION_BE_NAME);
        columnsToFieldMap.put(REGION_TABLE_KUERZEL, REGION_BE_KUERZEL);
        columnsToFieldMap.put(REGION_TABLE_TYP, REGION_BE_TYP);
        columnsToFieldMap.put(REGION_TABLE_UEBERGEORDNET, REGION_BE_UEBERGEORDNET);

        columnsToFieldMap.putAll(BasicDAO.getTechnicalColumnsToFieldsMap());
        return columnsToFieldMap;

    }


    /**
     * Return all the Region entries
     *
     * @return List with Region Business Entities
     */

    public List<RegionenBE> findAll() {
        return basicDAO.selectEntityList(REGIONEN, FIND_ALL);
    }


    /**
     * Returns all the region entries of type = KREIS
     *
     * @return List with Region Business Entities
     */
    public List<RegionenBE> findAllByType(final String type) {
        return basicDAO.selectEntityList(REGIONEN, FIND_ALL_BY_TYPE, type);
    }

    /**
     * Returns a single region entity
     *
     * @return single regionBE entity
     */
    public RegionenBE findById(final Long id) {
        return basicDAO.selectSingleEntity(REGIONEN, FIND_BY_ID, id);
    }



}
