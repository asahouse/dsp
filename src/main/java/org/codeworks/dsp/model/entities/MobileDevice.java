package org.codeworks.dsp.model.entities;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonView;
import org.codeworks.dsp.model.views.campaign.EditView;
import org.hibernate.validator.constraints.Length;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "mobile_device")
public class MobileDevice implements Serializable {

    @JsonView(EditView.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JsonView(EditView.class)
    @Length(max = 20)
    @Column(name = "carrier", length = 20)
    private String carrier;

    @JsonView(EditView.class)
    @Length(max = 20)
    @Column(name = "networkType", length = 20)
    private String networkType;

    @JsonView(EditView.class)
    @Length(max = 20)
    @Column(name = "os", length = 20)
    private String os;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Set<Carrier> getCarrier() {
        if (StringUtils.isEmpty(carrier))
            return new HashSet<>();
        else {
            Set<String> strSet = new HashSet<>(Arrays.asList(carrier.split(",")));
            return strSet.stream().map(s -> Carrier.fromValue(Integer.parseInt(s))).collect(Collectors.toSet());
        }
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public void addCarrier(Carrier carrier) {
        if (StringUtils.isEmpty(this.carrier))
            this.carrier = carrier.toValue().toString();
        else
            this.carrier += carrier.toValue().toString();
    }

    public Set<NetworkType> getNetworkType() {
        if (StringUtils.isEmpty(networkType))
            return new HashSet<>();
        else {
            Set<String> strSet = new HashSet<>(Arrays.asList(networkType.split(",")));
            return strSet.stream().map(s -> NetworkType.fromValue(Integer.parseInt(s))).collect(Collectors.toSet());
        }
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }

    public void addNetworkType(NetworkType networkType) {
        if (StringUtils.isEmpty(this.networkType))
            this.networkType = networkType.toValue().toString();
        else
            this.networkType += networkType.toValue().toString();
    }

    public Set<Os> getOs() {
        if (StringUtils.isEmpty(os))
            return new HashSet<>();
        else {
            Set<String> strSet = new HashSet<>(Arrays.asList(os.split(",")));
            return strSet.stream().map(s -> Os.fromValue(Integer.parseInt(s))).collect(Collectors.toSet());
        }
    }

    public void setOs(String os) {
        this.os = os;
    }

    public void addOs(Os os) {
        if (StringUtils.isEmpty(this.os))
            this.os = os.toValue().toString();
        else
            this.os += os.toValue().toString();
    }

    public enum Carrier {
        chinaMobile(0), chinaUnicom(1), chinaTelecom(2);

        private Integer value;

        Carrier(Integer value) {
            this.value = value;
        }

        public static Carrier fromValue(Integer value) {
            if (value != null) {
                for (Carrier carrier : values()) {
                    if (carrier.value.equals(value)) {
                        return carrier;
                    }
                }
            }
            return null;
        }

        @JsonValue
        public Integer toValue() {
            return value;
        }
    }

    public enum NetworkType {
        unknown(0), wifi(1), g2(2), g3(3), g4(4);

        private Integer value;

        NetworkType(Integer value) {
            this.value = value;
        }

        public static NetworkType fromValue(Integer value) {
            if (value != null) {
                for (NetworkType networkType : values()) {
                    if (networkType.value.equals(value)) {
                        return networkType;
                    }
                }
            }
            return null;
        }

        @JsonValue
        public Integer toValue() {
            return value;
        }
    }

    public enum Os {
        unknown(0), ios(1), android(2), windows(3);

        private Integer value;

        Os(Integer value) {
            this.value = value;
        }

        public static Os fromValue(Integer value) {
            if (value != null) {
                for (Os os : values()) {
                    if (os.value.equals(value)) {
                        return os;
                    }
                }
            }
            return null;
        }

        @JsonValue
        public Integer toValue() {
            return value;
        }
    }

}
