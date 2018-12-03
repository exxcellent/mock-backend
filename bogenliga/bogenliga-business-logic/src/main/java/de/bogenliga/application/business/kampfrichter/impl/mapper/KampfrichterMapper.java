package de.bogenliga.application.business.kampfrichter.impl.mapper;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.function.Function;
import de.bogenliga.application.business.dsbmitglied.api.types.DsbMitgliedDO;
import de.bogenliga.application.business.kampfrichter.api.types.KampfrichterDO;
import de.bogenliga.application.business.kampfrichter.impl.entity.KampfrichterBE;
import de.bogenliga.application.common.component.mapping.ValueObjectMapper;
import de.bogenliga.application.common.time.DateProvider;

/**
 * I convert the kampfrichter DataObjects and BusinessEntities.
 *
 */
public class KampfrichterMapper implements ValueObjectMapper {

    /**
     * Converts a {@link KampfrichterBE} to a {@link KampfrichterDO}
     *
     */
    public static final Function<KampfrichterBE, KampfrichterDO> toKampfrichterDO = be -> {

        final Long userId = be.getKampfrichterUserId();
        final Long wettkampfId = be.getKampfrichterWettkampfId();
        final boolean leitend = be.isKampfrichterLeitend();

        // technical parameter
        Long createdByUserId = be.getCreatedByUserId();
        Long lastModifiedByUserId = be.getLastModifiedByUserId();
        Long version = be.getVersion();

        OffsetDateTime createdAtUtc = DateProvider.convertTimestamp(be.getCreatedAtUtc());
        OffsetDateTime lastModifiedAtUtc = DateProvider.convertTimestamp(be.getLastModifiedAtUtc());

        return new KampfrichterDO(userId, wettkampfId, leitend,
                createdAtUtc, createdByUserId, lastModifiedAtUtc, lastModifiedByUserId, version);
    };

    /**
     * Converts a {@link DsbMitgliedDO} to a {@link KampfrichterBE}
     */
    public static final Function<DsbMitgliedDO, KampfrichterBE> toKampfrichterBE = dsbMitgliedDO -> {

        Timestamp createdAtUtcTimestamp = DateProvider.convertOffsetDateTime(dsbMitgliedDO.getCreatedAtUtc());
        Timestamp lastModifiedAtUtcTimestamp = DateProvider.convertOffsetDateTime(dsbMitgliedDO.getLastModifiedAtUtc());

        KampfrichterBE kampfrichterBE = new KampfrichterBE();
        kampfrichterBE.setKampfrichterUserId((long)998877);
        kampfrichterBE.setKampfrichterWettkampfId((long)9999);
        kampfrichterBE.setKampfrichterLeitend(false);

        kampfrichterBE.setCreatedAtUtc(createdAtUtcTimestamp);
        kampfrichterBE.setCreatedByUserId(dsbMitgliedDO.getCreatedByUserId());
        kampfrichterBE.setLastModifiedAtUtc(lastModifiedAtUtcTimestamp);
        kampfrichterBE.setLastModifiedByUserId(dsbMitgliedDO.getLastModifiedByUserId());
        kampfrichterBE.setVersion(dsbMitgliedDO.getVersion());

        return kampfrichterBE;
    };


    /**
     * Private constructor
     */
    private KampfrichterMapper() {
        // empty private constructor
    }
}
