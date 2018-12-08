package de.bogenliga.application.business.wettkampf.api.types;

import java.time.OffsetDateTime;
import java.util.Objects;
import de.bogenliga.application.common.component.types.CommonDataObject;
import de.bogenliga.application.common.component.types.DataObject;

/**
 * Contains the values of the wettkampf business entity.
 *
 * @author Daniel Schott
 */
public class WettkampfDO extends CommonDataObject implements DataObject {


    private Long id;
    private Long veranstaltungsId;
    private String datum;
    private String wettkampfOrt;
    private String wettkampfBeginn;
    private Long wettkampfTag;
    private Long wettkampfDisziplinId;
    private Long wettkampfTypId;

    public WettkampfDO(final Long id, final Long veranstaltungsId, final String datum, final String wettkampfOrt,
                       final String wettkampfBeginn, final Long wettkampfTag, final Long wettkampfDisziplinId,
                       final Long wettkampfTypId,
                       final OffsetDateTime createdAtUtc, final Long createdByUserId,
                       final Long version){
        this.id = id;
        this.veranstaltungsId = veranstaltungsId;
        this.datum = datum;
        this.wettkampfOrt= wettkampfOrt;
        this.wettkampfBeginn = wettkampfBeginn;
        this.wettkampfTag = wettkampfTag;
        this.wettkampfDisziplinId = wettkampfDisziplinId;
        this.wettkampfTypId = wettkampfTypId;
        this.createdAtUtc = createdAtUtc;
        this.createdByUserId = createdByUserId;
        this.version = version;

    }

    /**
     * Constructor with optional parameters
     * @param id
     * @param veranstaltungsId
     * @param datum
     * @param wettkampfOrt
     * @param wettkampfBeginn
     * @param wettkampfTag
     * @param wettkampfDisziplinId
     * @param wettkampfTypId
     * @param createdAtUtc
     * @param createdByUserId
     * @param lastModifiedAtUtc
     * @param lastModifiedByUserId
     * @param version
     *
     */
    public WettkampfDO(final Long id, final Long veranstaltungsId, final String datum, final String wettkampfOrt,
                       final String wettkampfBeginn, final Long wettkampfTag, final Long wettkampfDisziplinId,
                       final Long wettkampfTypId,
                       final OffsetDateTime createdAtUtc, final Long createdByUserId, OffsetDateTime lastModifiedAtUtc, Long lastModifiedByUserId,
                       final Long version){
        this.id = id;
        this.veranstaltungsId = veranstaltungsId;
        this.datum = datum;
        this.wettkampfOrt= wettkampfOrt;
        this.wettkampfBeginn = wettkampfBeginn;
        this.wettkampfTag = wettkampfTag;
        this.wettkampfDisziplinId = wettkampfDisziplinId;
        this.wettkampfTypId = wettkampfTypId;
        this.createdAtUtc = createdAtUtc;
        this.createdByUserId = createdByUserId;
        this. version = version;
        this.lastModifiedAtUtc = lastModifiedAtUtc;
        this.lastModifiedByUserId= lastModifiedByUserId;

    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVeranstaltungsId() {
        return veranstaltungsId;
    }

    public void setVeranstaltungsId(Long veranstaltungsId) {
        this.veranstaltungsId = veranstaltungsId;
    }

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public String getWettkampfOrt() {
        return wettkampfOrt;
    }

    public void setWettkampfOrt(String wettkampfOrt) {
        wettkampfOrt = wettkampfOrt;
    }

    public String getWettkampfBeginn() {
        return wettkampfBeginn;
    }

    public void setWettkampfBeginn(String wettkampfBeginn) {
        this.wettkampfBeginn = wettkampfBeginn;
    }

    public Long getWettkampfTag() {
        return wettkampfTag;
    }


    public void setWettkampfTag(Long wettkampfTag) {
        this.wettkampfTag = wettkampfTag;
    }

    public Long getWettkampfDisziplinId() {
        return wettkampfDisziplinId;
    }

    public void setWettkampfDisziplinId(Long wettkampfDisziplinId) {
        this.wettkampfDisziplinId = wettkampfDisziplinId;
    }

    public Long getWettkampfTypId() {
        return wettkampfTypId;
    }

    public void setWettkampfTypId(Long wettkampfTypId) {
        this.wettkampfTypId = wettkampfTypId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, veranstaltungsId, datum , wettkampfOrt, wettkampfBeginn, wettkampfTag,
                wettkampfDisziplinId, wettkampfTypId,
                createdByUserId, lastModifiedAtUtc,
                lastModifiedByUserId, version);
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof WettkampfDO) {
            final WettkampfDO that = (WettkampfDO) o;
            return (this.getId() == that.getId() && this.getVeranstaltungsId() == that.getVeranstaltungsId() &&
                    this.getDatum() == that.getDatum() && this.getWettkampfOrt().equals(that.getWettkampfOrt()) &&
                    this.getWettkampfBeginn() == that.getWettkampfBeginn() &&
                    this.getWettkampfTag() == that.getWettkampfTag() &&
                    this.getWettkampfDisziplinId() == that.getWettkampfDisziplinId() &&
                    this.getWettkampfTypId() == that.getWettkampfTypId());
        }
        return false;

    }

}
