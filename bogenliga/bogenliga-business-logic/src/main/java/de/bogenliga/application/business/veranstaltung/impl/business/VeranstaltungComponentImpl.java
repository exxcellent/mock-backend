package de.bogenliga.application.business.veranstaltung.impl.business;

import java.util.ArrayList;
import java.util.List;

import de.bogenliga.application.business.sportjahr.SportjahrDO;
import de.bogenliga.application.business.wettkampf.impl.dao.WettkampfDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import de.bogenliga.application.business.liga.impl.dao.LigaDAO;
import de.bogenliga.application.business.liga.impl.entity.LigaBE;
import de.bogenliga.application.business.user.impl.dao.UserDAO;
import de.bogenliga.application.business.user.impl.entity.UserBE;
import de.bogenliga.application.business.veranstaltung.api.VeranstaltungComponent;
import de.bogenliga.application.business.veranstaltung.api.types.VeranstaltungDO;
import de.bogenliga.application.business.veranstaltung.impl.dao.VeranstaltungDAO;
import de.bogenliga.application.business.veranstaltung.impl.entity.VeranstaltungBE;
import de.bogenliga.application.business.veranstaltung.impl.mapper.VeranstaltungMapper;
import de.bogenliga.application.business.wettkampftyp.impl.dao.WettkampfTypDAO;
import de.bogenliga.application.business.wettkampftyp.impl.entity.WettkampfTypBE;
import de.bogenliga.application.common.errorhandling.ErrorCode;
import de.bogenliga.application.common.errorhandling.exception.BusinessException;
import de.bogenliga.application.common.validation.Preconditions;


/**
 * @author Daniel Schott, daniel.schott@student.reutlingen-university.de
 */
@Component
public class VeranstaltungComponentImpl implements VeranstaltungComponent {

    private static final String PRECONDITION_MSG_VERANSTALTUNG_ID = "VeranstaltungDO must not be null";
    private static final String PRECONDITION_MSG_VERANSTALTUNG = "VeranstaltungDO must not be null";
    private static final String PRECONDITION_MSG_CURRENT_VERANSTALTUNG = " Current veranstaltungDO id must not be null";
    private static final String PRECONDITION_MSG_VERANSTALTUNG_LIGA_LEITER_ID = "VeranstaltungLigaLeiterId must not be null";
    private static final String PRECONDITION_MSG_VERANSTALTUNG_MELDE_DEADLINE = " veranstaltungMeldeDeadline must be not null";
    private static final String PRECONDITION_MSG_VERANSTALTUNG_WETTKAMPF_ID = "Veranstaltungwettkampfid must be not null";
    private static final String PRECONDITION_MSG_VERANSTALTUNG_LIGA_ID = "Veranstaltungligaid must be not null";
    private static final String PRECONDITION_MSG_VERANSTALTUNG_SPORTJAHR = "veranstaltungsportjahr must be not null";
    private static final String PRECONDITION_MSG_VERANSTALTUNG_NAME = "veranstaltungname must be not null";
    private static final String PRECONDITION_MSG_CURRENT_DSBMITGLIED = "Current dsbmitglied id must not be negative";
    private static final String PRECONDITION_MSG_VERANSTALTUNG_LIGALEITER_EMAIL = "ligaleiter email must be not null";
    private static final String PRECONDITION_MSG_VERANSTALTUNG_WETTKAMPFTYP_NAME = "veranstaltungtypname must be not null";
    private static final String PRECONDITION_MSG_VERANSTALTUNG_LIGA_NAME = "veranstaltungliganame must be not null";
    private static final String PRECONDITION_MSG_VERANSTALTUNG_LIGA_ALREADY_HAS_VERANSTALTUNG = "liga already has a veranstaltung assigned for this year";
    private final VeranstaltungDAO veranstaltungDAO;
    private final LigaDAO ligaDAO;
    private final WettkampfTypDAO wettkampftypDAO;
    private final UserDAO userDAO;
    private final WettkampfDAO wettkampfDAO;


    /**
     * Constructor for VeranstaltungComponentImpl - Autowired by springboot
     *
     * @param veranstaltungDAO
     */

    @Autowired
    public VeranstaltungComponentImpl(final VeranstaltungDAO veranstaltungDAO, final LigaDAO ligaDAO,
                                      final WettkampfTypDAO wettkampftypDAO, final UserDAO userDAO,
                                      final WettkampfDAO wettkampfDAO) {
        this.veranstaltungDAO = veranstaltungDAO;
        this.ligaDAO = ligaDAO;
        this.wettkampftypDAO = wettkampftypDAO;
        this.userDAO = userDAO;
        this.wettkampfDAO = wettkampfDAO;
    }


    /**
     * findAll-Method gives all Veranstaltungen from the dataBase
     *
     * @return
     */
    @Override
    public List<VeranstaltungDO> findAll() {
        final ArrayList<VeranstaltungDO> returnList = new ArrayList<>();
        final List<VeranstaltungBE> veranstaltungBEList = veranstaltungDAO.findAll();
        final List<String> cacheList = new ArrayList<String>();


        for (int i = 0; i < veranstaltungBEList.size(); i++) {

            returnList.add(i, notNull(veranstaltungBEList.get(i)));

        }
        /*
        for(int x = 0;x<returnList.size();x++){
            cacheList.add(wettkampftypDAO.findById(returnList.get(x).getVeranstaltung_wettkampftyp_id()).getName());
            System.out.println("test: " + cacheList.get(x));
            System.out.println("test2: " + returnList.get(x).getVeranstaltung_wettkampftyp_id());
        }*/
        return returnList;
    }


    @Override
    public VeranstaltungDO findById(final long id) {
        Preconditions.checkArgument(id >= 0, PRECONDITION_MSG_VERANSTALTUNG_ID);

        final VeranstaltungBE result = veranstaltungDAO.findById(id);

        if (result == null) {
            throw new BusinessException(ErrorCode.ENTITY_NOT_FOUND_ERROR,
                    String.format("No result found for ID '%s'", id));
        }

        return notNull(result);
    }


    @Override
    public List<VeranstaltungDO> findByLigaleiterId(long ligaleiterId) {

        final ArrayList<VeranstaltungDO> returnList = new ArrayList<>();
        final List<VeranstaltungBE> veranstaltungBEList = veranstaltungDAO.findByLigaleiterId(ligaleiterId);

        for (int i = 0; i < veranstaltungBEList.size(); i++) {

            returnList.add(i, notNull(veranstaltungBEList.get(i)));

        }

        return returnList;
    }


    @Override
    public VeranstaltungDO update(final VeranstaltungDO veranstaltungDO, final long currentDsbMitgliedId) {
        checkVeranstaltungDO(veranstaltungDO, currentDsbMitgliedId);
        Preconditions.checkArgument(veranstaltungDO.getVeranstaltungID() >= 0, PRECONDITION_MSG_VERANSTALTUNG_ID);

        final VeranstaltungBE veranstaltungBE = VeranstaltungMapper.toVeranstaltungBE.apply(veranstaltungDO);
        System.out.println("\n\n");
        System.out.println(veranstaltungBE.toString());
        final VeranstaltungBE persistedVeranstaltungBE = veranstaltungDAO.update(veranstaltungBE, currentDsbMitgliedId);

        return notNull(persistedVeranstaltungBE);
    }


    @Override
    public VeranstaltungDO create(final VeranstaltungDO veranstaltungDO, final long currentDsbMitgliedId) {
        checkVeranstaltungDO(veranstaltungDO, currentDsbMitgliedId);
        Preconditions.checkArgument(
                validLiga(veranstaltungDO.getVeranstaltungLigaID(), veranstaltungDO.getVeranstaltungSportJahr()),
                PRECONDITION_MSG_VERANSTALTUNG_LIGA_ALREADY_HAS_VERANSTALTUNG);

        final VeranstaltungBE veranstaltungBE = VeranstaltungMapper.toVeranstaltungBE.apply(veranstaltungDO);
        System.out.println("\n\n");
        System.out.println(veranstaltungBE.toString());
        final VeranstaltungBE persistedVeranstaltungBE = veranstaltungDAO.create(veranstaltungBE, currentDsbMitgliedId);

        //create Wettkampftag 0
        this.wettkampfDAO.createWettkampftag0(persistedVeranstaltungBE.getVeranstaltung_id(), currentDsbMitgliedId);

        return notNull(persistedVeranstaltungBE);
    }


    @Override
    public void delete(final VeranstaltungDO veranstaltungDO, final long currentDsbMitgliedId) {
        Preconditions.checkNotNull(veranstaltungDO, PRECONDITION_MSG_VERANSTALTUNG);
        Preconditions.checkArgument(veranstaltungDO.getVeranstaltungID() >= 0, PRECONDITION_MSG_VERANSTALTUNG_ID);
        Preconditions.checkArgument(currentDsbMitgliedId >= 0, PRECONDITION_MSG_CURRENT_VERANSTALTUNG);

        final VeranstaltungBE veranstaltungBE = VeranstaltungMapper.toVeranstaltungBE.apply(veranstaltungDO);

        veranstaltungDAO.delete(veranstaltungBE, currentDsbMitgliedId);

    }


    @Override
    public List<VeranstaltungDO> findBySportjahr(long sportjahr) {
        final ArrayList<VeranstaltungDO> returnList = new ArrayList<VeranstaltungDO>();
        final List<VeranstaltungBE> veranstaltungBEList = veranstaltungDAO.findBySportjahr(sportjahr);
        for (int i = 0; i < veranstaltungBEList.size(); i++) {

            returnList.add(i, notNull(veranstaltungBEList.get(i)));

        }
        return returnList;
    }


    @Override
    public List<SportjahrDO> findAllSportjahreDestinct() {
        return veranstaltungDAO.findAllSportjahreDestinct();

    }


    @Override
    public List<VeranstaltungDO> findByLigaID(long ligaID) {
        final ArrayList<VeranstaltungDO> returnList = new ArrayList<VeranstaltungDO>();
        final List<VeranstaltungBE> veranstaltungBEList = veranstaltungDAO.findByLigaID(ligaID);
        for (int i = 0; i < veranstaltungBEList.size(); i++) {

            returnList.add(i, notNull(veranstaltungBEList.get(i)));

        }
        return returnList;
    }


    private void checkVeranstaltungDO(final VeranstaltungDO veranstaltungDO, final long currentDsbMitgliedId) {
        Preconditions.checkNotNull(veranstaltungDO, PRECONDITION_MSG_VERANSTALTUNG);
        Preconditions.checkArgument(currentDsbMitgliedId >= 0, PRECONDITION_MSG_CURRENT_DSBMITGLIED);
        Preconditions.checkNotNull(veranstaltungDO.getVeranstaltungName(), PRECONDITION_MSG_VERANSTALTUNG_NAME);
        //
        Preconditions.checkNotNull(veranstaltungDO.getVeranstaltungMeldeDeadline(),
                PRECONDITION_MSG_VERANSTALTUNG_MELDE_DEADLINE);
        Preconditions.checkArgument(veranstaltungDO.getVeranstaltungLigaleiterID() >= 0,
                PRECONDITION_MSG_VERANSTALTUNG_LIGA_LEITER_ID);
        Preconditions.checkArgument(veranstaltungDO.getVeranstaltungWettkampftypID() >= 0,
                PRECONDITION_MSG_VERANSTALTUNG_WETTKAMPF_ID);
        Preconditions.checkArgument(veranstaltungDO.getVeranstaltungLigaID() >= 0,
                PRECONDITION_MSG_VERANSTALTUNG_LIGA_ID);
        Preconditions.checkNotNull(veranstaltungDO.getVeranstaltungSportJahr(),
                PRECONDITION_MSG_VERANSTALTUNG_SPORTJAHR);

    }


    /**
     * Checks if a Liga already has a Veranstaltung in a specific Sportjahr
     *
     * @param liga_id   The ID of the Liga to check
     * @param sportjahr The Sportjahr to check
     *
     * @return true: when no Veranstaltung exists for Liga in Sportjahr false: when there already is a Veranstaltung for
     * Liga in Sportjahr
     */
    private boolean validLiga(final long liga_id, final long sportjahr) {
        List<VeranstaltungDO> all_veranstaltungen = this.findAll();
        for (VeranstaltungDO vdo : all_veranstaltungen) {
            if (vdo.getVeranstaltungLigaID() == liga_id && vdo.getVeranstaltungSportJahr() == sportjahr) {
                return false;
            }
        }
        return true;
    }


    private VeranstaltungDO notNull(VeranstaltungBE veranstaltungBE) {
        LigaBE tempLigaBE = new LigaBE();
        WettkampfTypBE tempWettkampfTypBE = new WettkampfTypBE();
        UserBE tempUserBE = new UserBE();

        if (veranstaltungBE.getVeranstaltung_liga_id() != null) {
            tempLigaBE = ligaDAO.findById(veranstaltungBE.getVeranstaltung_liga_id());
        }
        if (veranstaltungBE.getVeranstaltung_wettkampftyp_id() != null) {
            tempWettkampfTypBE = wettkampftypDAO.findById(veranstaltungBE.getVeranstaltung_wettkampftyp_id());
        }
        if (veranstaltungBE.getVeranstaltung_ligaleiter_id() != null) {
            tempUserBE = userDAO.findById(veranstaltungBE.getVeranstaltung_ligaleiter_id());
        }

        return VeranstaltungMapper.toVeranstaltungDO(veranstaltungBE, tempUserBE, tempWettkampfTypBE, tempLigaBE);
    }
}

