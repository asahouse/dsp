package org.codeworks.dsp.openrtb.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/**
 * OpenRTB 2.5 Geo object
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Geo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Float lat;
    private Float lon;
    private Integer type;
    private String country;
    private String region;
    private String regionfips104;
    private String metro;
    private String city;
    private String zip;
    private Integer utcoffset;

    public Geo() {}

    public Float getLat() { return lat; }
    public void setLat(Float lat) { this.lat = lat; }

    public Float getLon() { return lon; }
    public void setLon(Float lon) { this.lon = lon; }

    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public String getRegionfips104() { return regionfips104; }
    public void setRegionfips104(String regionfips104) { this.regionfips104 = regionfips104; }

    public String getMetro() { return metro; }
    public void setMetro(String metro) { this.metro = metro; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getZip() { return zip; }
    public void setZip(String zip) { this.zip = zip; }

    public Integer getUtcoffset() { return utcoffset; }
    public void setUtcoffset(Integer utcoffset) { this.utcoffset = utcoffset; }
}
