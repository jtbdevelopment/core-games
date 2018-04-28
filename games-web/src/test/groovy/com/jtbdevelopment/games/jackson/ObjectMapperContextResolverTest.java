package com.jtbdevelopment.games.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.util.GroovyTestCase;
import javax.ws.rs.ext.Provider;
import junit.framework.TestCase;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Date: 1/14/15 Time: 6:55 AM
 */
public class ObjectMapperContextResolverTest extends GroovyTestCase {

  private ObjectMapper mapper = Mockito.mock(ObjectMapper.class);
  private ObjectMapperContextResolver resolver = new ObjectMapperContextResolver(mapper);

  @Test
  public void testGetContext() {
    TestCase.assertSame(mapper, resolver.getContext(null));
  }

  @Test
  public void testClassAnnotations() {
    TestCase.assertTrue(ObjectMapperContextResolver.class.isAnnotationPresent(Provider.class));
  }
}
