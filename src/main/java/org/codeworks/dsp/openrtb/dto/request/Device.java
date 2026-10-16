package org.codeworks.dsp.openrtb.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/**
 * OpenRTB 2.5 Device object
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Device implements Serializable {
    private static final long serialVersionUID = 1L;

    private String ua;
    private Geo geo;
    private Integer dnt;
    private Integer lmt;
    private String ip;
    private String ipv6;
    private Integer devicetype;
    private String make;
    private String model;
    private String os;
    private String osv;
    private Integer h;
    private Integer w;
    private Integer ppi;
    private Float pxratio;
    private Integer js;
    private String language;
    private String carrier;
    private Integer connectiontype;
    private String ifa;

    public Device() {}

    public String getUa() { return ua; }
    public void setUa(String ua) { this.ua = ua; }

    public Geo getGeo() { return geo; }
    public void setGeo(Geo geo) { this.geo = geo; }

    public Integer getDnt() { return dnt; }
    public void setDnt(Integer dnt) { this.dnt = dnt; }

    public Integer getLmt() { return lmt; }
    public void setLmt(Integer lmt) { this.lmt = lmt; }

    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }

    public String getIpv6() { return ipv6; }
    public void setIpv6(String ipv6) { this.ipv6 = ipv6; }

    public Integer getDevicetype() { return devicetype; }
    public void setDevicetype(Integer devicetype) { this.devicetype = devicetype; }

    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getOs() { return os; }
    public void setOs(String os) { this.os = os; }

    public String getOsv() { return osv; }
    public void setOsv(String osv) { this.osv = osv; }

    public Integer getH() { return h; }
    public void setH(Integer h) { this.h = h; }

    public Integer getW() { return w; }
    public void setW(Integer w) { this.w = w; }

    public Integer getPpi() { return ppi; }
    public void setPpi(Integer ppi) { this.ppi = ppi; }

    public Float getPxratio() { return pxratio; }
    public void setPxratio(Float pxratio) { this.pxratio = pxratio; }

    public Integer getJs() { return js; }
    public void setJs(Integer js) { this.js = js; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getCarrier() { return carrier; }
    public void setCarrier(String carrier) { this.carrier = carrier; }

    public Integer getConnectiontype() { return connectiontype; }
    public void setConnectiontype(Integer connectiontype) { this.connectiontype = connectiontype; }

    public String getIfa() { return ifa; }
    public void setIfa(String ifa) { this.ifa = ifa; }
}
