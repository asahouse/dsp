package org.codeworks.dsp.rtbCall.dto.material;

import java.io.Serializable;
import java.util.List;

/**
 * Created by benjaminkc on 16/10/24.
 */
public class RtbUserCategory implements Serializable {
    private List<Integer> gender;
    private List<Integer> age;
    private List<Integer> education;
    private List<Integer> marriage;
    private List<Integer> career;

    public List<Integer> getGender() {
        return gender;
    }

    public void setGender(List<Integer> gender) {
        this.gender = gender;
    }

    public List<Integer> getAge() {
        return age;
    }

    public void setAge(List<Integer> age) {
        this.age = age;
    }

    public List<Integer> getEducation() {
        return education;
    }

    public void setEducation(List<Integer> education) {
        this.education = education;
    }

    public List<Integer> getMarriage() {
        return marriage;
    }

    public void setMarriage(List<Integer> marriage) {
        this.marriage = marriage;
    }

    public List<Integer> getCareer() {
        return career;
    }

    public void setCareer(List<Integer> career) {
        this.career = career;
    }
}
