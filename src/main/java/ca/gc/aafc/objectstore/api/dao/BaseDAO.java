package ca.gc.aafc.objectstore.api.dao;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.inject.Inject;
import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.hibernate.Session;
import org.hibernate.SimpleNaturalIdLoadAccess;
import org.hibernate.annotations.NaturalId;
import org.springframework.stereotype.Component;

/**
 * Base Data Access Object layer. This class should be the only one holding a reference to the {@link EntityManager}.
 *
 */
@Component
public class BaseDAO {
  
  @PersistenceContext
  private EntityManager entityManager;
  
  @Inject
  private Validator validator;
  
  /**
   * This method can be used to inject the EntityManager into an external object.
   * 
   * @param emConsumer
   */
  public <T> T createWithEntityManager(Function<EntityManager, T> creator) {
    Objects.requireNonNull(creator);
    return creator.apply(entityManager);
  }
  
  /**
   * Utility function that can check if a lazy loaded attribute is actually loaded.
   * @param entity
   * @param fieldName
   * @return
   */
  public Boolean isLoaded(Object entity, String fieldName) {
    Objects.requireNonNull(entity);
    Objects.requireNonNull(fieldName);

    PersistenceUnitUtil unitUtil = entityManager.getEntityManagerFactory()
        .getPersistenceUnitUtil();
    return unitUtil.isLoaded(entity, fieldName);
  }

  /**
   * Find an entity by it's naturalId. The method assumes that the naturalId is unique.
   * 
   * @param uuid
   * @param entityClass
   * @return
   */
  public <T> T findOneByNaturalId(UUID uuid, Class<T> entityClass) {
    T objectStoreMetadata = entityManager.unwrap(Session.class)
        .bySimpleNaturalId(entityClass)
        .load(uuid);
    return objectStoreMetadata;
  }
  
  /**
   * Find an entity by a specific property. The method assumes that the property is unique.
   * 
   * @param clazz
   * @param property
   * @param value
   * @return the entity or null if not found
   */
  public <T> T findOneByProperty(Class<T> clazz, String property, Object value) {
    // Create a criteria to retrieve the specific property.
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<T> criteria = criteriaBuilder.createQuery(clazz);
    Root<T> root = criteria.from(clazz);

    criteria.where(criteriaBuilder.equal(root.get(property), value));
    criteria.select(root);

    TypedQuery<T> query = entityManager.createQuery(criteria);
    try {
      return query.getSingleResult();
    } catch (NoResultException nrEx) {
      return null;
    }
  }
  
  /**
   * Check for the existence of a record by natural id (as uuid).
   * @param uuid
   * @param entityClass
   * @return
   */
  public boolean existsByNaturalId(UUID uuid, Class<?> entityClass) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> cq = cb.createQuery(Long.class);
    Root<?> from = cq.from(entityClass);
    
    cq.select(cb.count(from));
    cq.where(cb.equal(from.get(extractNaturalIdFieldName(entityClass)), uuid));

    TypedQuery<Long> tq = entityManager.createQuery(cq);
    return tq.getSingleResult() > 0;
  }
  
  /**
   * Give a reference to an entity that should exist without actually loading it. Useful to set
   * relationships without loading the entity.
   * 
   * @param entityClass
   * @param uuid
   * @return
   */
  public <T> T getReferenceByNaturalId(Class<T> entityClass, UUID uuid) {
    SimpleNaturalIdLoadAccess<T> loadAccess = entityManager.unwrap(Session.class)
        .bySimpleNaturalId(entityClass);
    return loadAccess.getReference(uuid);
  }
  
  /**
   * Set a relationship by calling the provided {@link Consumer} with a reference Entity loaded by
   * NaturalId.
   * 
   * @param entityClass
   * @param uuid
   * @param objConsumer
   */
  public <T> void setRelationshipUsing(Class<T> entityClass, UUID uuid, Consumer<T> objConsumer) {
    objConsumer.accept(getReferenceByNaturalId(entityClass, uuid));
  }
  
  /**
   * Save the provided entity.
   * 
   * @param entity
   */
  public void save(Object entity) {
    entityManager.persist(entity);
  }
  
  /**
   * Delete the provided entity.
   * 
   * @param entity
   */
  public void delete(Object entity) {
    entityManager.remove(entity);
  }
  
  /**
   * Same as {@link Validator#validate(Object, Class...)}
   * 
   * @param entity
   *          the entity to validate (not null)
   * @return constraint violations or an empty set if none
   */
  public <T> Set<ConstraintViolation<T>> validateEntity(T entity) {
    return validator.validate(entity);
  }
  
  /**
   * Given a class, this method will extract the name of the field (using the {@link Column}
   * annotation when that field is annotated with {@link NaturalId}.
   * 
   * @param entityClass
   * @return name if the NaturalId field or null if none
   */
  private String extractNaturalIdFieldName(Class<?> entityClass) {
    Optional<Column> naturalIdColumn = Arrays.stream(entityClass.getDeclaredMethods())
        .filter(m -> m.getAnnotation(NaturalId.class) != null)
        .map(m -> m.getAnnotation(Column.class))
        .findFirst();

    if (naturalIdColumn.isPresent()) {
      return naturalIdColumn.get().name();
    }
    return null;
  }
  
}
