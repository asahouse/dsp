package org.codeworks.dsp.openrtb.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.List;

/**
 * OpenRTB 2.5 Publisher object
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Publisher implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private List<String> cat;
    private String domain;

    public Publisher() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<String> getCat() { return cat; }
    public void setCat(List<String> cat) { this.cat = cat; }

    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }
}
