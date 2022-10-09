package org.codeworks.dsp.model.generator;

import org.apache.commons.codec.digest.DigestUtils;
import org.codeworks.dsp.model.entities.CategoryDictionary;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;

/**
 * Created by benjaminkc on 16/10/20.
 *
 * For CategoryDictionary Table ID : HashId
 */
public class CategoryKeyGenerator implements IdentifierGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) throws HibernateException {

        if (!(o instanceof CategoryDictionary)) throw new HibernateException("category.update.error.identify");

        CategoryDictionary dic = (CategoryDictionary)o;

        StringBuffer sb = new StringBuffer();

        sb.append(dic.getCategoryId());
        sb.append(dic.getCategoryTag().getTagCode());
        sb.append(dic.getCategoryParentId());

        return DigestUtils.md5Hex(sb.toString());
    }
}
