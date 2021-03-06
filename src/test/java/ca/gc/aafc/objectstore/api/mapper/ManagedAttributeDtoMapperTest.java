package ca.gc.aafc.objectstore.api.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import ca.gc.aafc.objectstore.api.dto.ManagedAttributeDto;
import ca.gc.aafc.objectstore.api.entities.ManagedAttribute;
import ca.gc.aafc.objectstore.api.entities.ManagedAttribute.ManagedAttributeType;
import ca.gc.aafc.objectstore.api.testsupport.factories.ManagedAttributeFactory;

public class ManagedAttributeDtoMapperTest {

  private static final ManagedAttributeMapper DTO_MAPPER = Mappers
      .getMapper(ManagedAttributeMapper.class);

  @Test
  public void testGivenManagedAttribute_mapsToManagedAttributeDto() {
    
    String[] acceptedValues  = new String[] {"CataloguedObject"};
    
    // given
    ManagedAttribute managedAttribute = ManagedAttributeFactory.newManagedAttribute()
        .id(1)
        .acceptedValues(acceptedValues).build();

    // when
    ManagedAttributeDto managedAttributeDto = DTO_MAPPER.toDto(managedAttribute);

    // then
    assertEquals(managedAttribute.getName(), managedAttributeDto.getName());
    assertEquals(managedAttribute.getManagedAttributeType(), managedAttributeDto.getManagedAttributeType());
    assertEquals(managedAttribute.getAcceptedValues().length, managedAttributeDto.getAcceptedValues().size());
    
    for(int i=0 ; i< managedAttribute.getAcceptedValues().length; i++) {
      assertEquals(managedAttribute.getAcceptedValues()[i], managedAttributeDto.getAcceptedValues().get(i));
    }
  }


  @Test
  public void testGivenManagedAttributeDto_mapsToManagedAttribute() {
    
    String[] acceptedValues  = new String[] {"dosal"};
    
    // given
    ManagedAttributeDto managedAttributeDto = new ManagedAttributeDto();
    managedAttributeDto.setName("specimen_view");
    managedAttributeDto.setManagedAttributeType(ManagedAttributeType.STRING);
    managedAttributeDto.setAcceptedValues(Arrays.asList(acceptedValues));

    // when
    ManagedAttribute managedAttribute = DTO_MAPPER.toEntity(managedAttributeDto);

    // then
    assertEquals(managedAttributeDto.getName(), managedAttribute.getName());
    assertEquals(managedAttributeDto.getManagedAttributeType(), managedAttribute.getManagedAttributeType());
    assertEquals(managedAttributeDto.getAcceptedValues().size(), managedAttribute.getAcceptedValues().length);
    
    for(int i=0 ; i< managedAttributeDto.getAcceptedValues().size(); i++) {
      assertEquals(managedAttributeDto.getAcceptedValues().get(i), managedAttribute.getAcceptedValues()[i]);
    }  
  }

}
