package com.shoppinglive.api.support;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
import jakarta.persistence.metamodel.EntityType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class DatabaseCleanup {

    @PersistenceContext
    private EntityManager entityManager;

    private List<String> tableNames = new ArrayList<>();

    @PostConstruct
    public void afterPropertiesSet() {
        Set<EntityType<?>> entities = entityManager.getMetamodel().getEntities();
        tableNames = entities.stream()
                .filter(entityType -> {
                    Class<?> javaType = entityType.getJavaType();
                    return javaType.getAnnotation(Entity.class) != null &&
                            // 단일 테이블 상속을 사용하지만 부모 클래스가 아닌 엔티티는 건너뜁니다
                            (javaType.getAnnotation(Inheritance.class) != null ||
                                    javaType.getAnnotation(DiscriminatorValue.class) == null);
                })
                .map(entityType -> {
                    Class<?> javaType = entityType.getJavaType();
                    Table tableAnnotation = javaType.getAnnotation(Table.class);

                    return tableAnnotation == null || tableAnnotation.name().isEmpty()
                            ? javaType.getSimpleName().toLowerCase()
                            : tableAnnotation.name();
                })
                .toList();
    }

    @Transactional
    public void execute() {
        entityManager.flush();
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();

        for (String tableName : tableNames) {
            entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
        }

        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
    }
}