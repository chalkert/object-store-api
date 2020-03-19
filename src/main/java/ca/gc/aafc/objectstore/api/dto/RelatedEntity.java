package ca.gc.aafc.objectstore.api.dto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to declare the matching entity of a class.
 * Main use case it to declare the matching entity of a dto.
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RelatedEntity {
  public Class<?> value();
}

