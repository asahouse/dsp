package org.codeworks.dsp.rtbCall.api.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.codeworks.dsp.model.entities.BaseEntity;
import org.codeworks.dsp.rtbCall.dto.RtbDTO;
import org.codeworks.dsp.rtbCall.dto.response.RtbResponse;

import java.io.Serializable;
import java.util.List;

/**
 * Created by benjaminkc on 16/10/27.
 */
public interface RtbApi<T extends RtbDTO, E extends BaseEntity> extends Serializable {

    RtbResponse<T> sync(E entity) throws JsonProcessingException ;

    RtbResponse<T> sync(List<E> entities) throws JsonProcessingException;

    RtbResponse<T> delete(List<Integer> ids) throws JsonProcessingException;

    List<T> convert(List<E> entities);

}
