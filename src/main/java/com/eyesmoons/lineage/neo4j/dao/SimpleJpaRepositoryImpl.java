package com.eyesmoons.lineage.neo4j.dao;

import com.eyesmoons.lineage.exception.CustomException;
import com.eyesmoons.lineage.neo4j.domain.BaseNodeEntity;
import com.eyesmoons.lineage.utils.BeanCopyUtil;
import org.neo4j.ogm.session.Session;
import org.springframework.beans.BeanUtils;
import org.springframework.data.neo4j.repository.support.SimpleNeo4jRepository;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Optional;

/**
 * 扩展SimpleNeo4jRepository类，在save前做一些处理
 */
public class SimpleJpaRepositoryImpl<T, ID extends Serializable> extends SimpleNeo4jRepository<T, ID> {

    /**
     * Creates a new {@link SimpleNeo4jRepository} to manage objects of the given domain type.
     * @param domainClass must not be {@literal null}.
     * @param session     must not be {@literal null}.
     */
    public SimpleJpaRepositoryImpl(Class<T> domainClass, Session session) {
        super(domainClass, session);
    }

    @Override
    public <S extends T> S save(S entity) {
        if (entity instanceof BaseNodeEntity) {
            this.beforeSave(entity);
        }
        return super.save(entity);
    }

    @Override
    public <S extends T> Iterable<S> save(Iterable<S> ses, int depth) {
        if (!ses.iterator().hasNext()) {
            return super.save(ses, depth);
        }
        final S next = ses.iterator().next();
        if (next instanceof BaseNodeEntity) {
            ses.forEach(this::beforeSave);
        }
        return super.save(ses, depth);
    }

    /**
     * 目的：更新node时，只更新非空的属性
     */
    @SuppressWarnings("unchecked")
    private <S extends T> void beforeSave(S entity) {
        final BaseNodeEntity baseNodeEntity = (BaseNodeEntity) entity;
        ID entityId = (ID) baseNodeEntity.getPk();
        if (StringUtils.isEmpty(entityId)) {
            // 异常抛出，pk不能为空
            throw new CustomException("entity id can not be null");
        }
        // 若ID非空 则查询最新数据
        Optional<T> optionalT = findById(entityId);
        // 若根据ID查询结果为空,则新增
        if (!optionalT.isPresent()) {
            return;
        }
        // 获取最新对象
        T target = optionalT.get();
        // 拷贝查询对象属性中保存对象为空的属性 到 保存对象
        BeanUtils.copyProperties(target, entity, BeanCopyUtil.getNonNullProperties(entity));
    }
}
