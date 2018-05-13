package com.jtbdevelopment.games.mongo.players;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase;
import java.lang.reflect.Field;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Date: 11/9/14 Time: 3:44 PM
 */
public class MongoPlayerTest extends MongoGameCoreTestCase {

  @Test
  public void testClassAnnotations() {
    assertTrue(MongoPlayer.class.isAnnotationPresent(Document.class));
    assertEquals("player", MongoPlayer.class.getAnnotation(Document.class).collection());
    assertTrue(MongoPlayer.class.isAnnotationPresent(JsonIgnoreProperties.class));
    assertArrayEquals(
        Arrays.asList("idAsString", "sourceAndSourceId").toArray(),
        MongoPlayer.class.getAnnotation(JsonIgnoreProperties.class).value());
  }

  @Test
  public void testIdAnnotations() throws NoSuchFieldException {
    Field f = MongoPlayer.class.getDeclaredField("id");
    Assert.assertNotNull(f.getAnnotation(Id.class));
  }

  @Test
  public void testMd5Annotations() throws NoSuchFieldException {
    Field f = MongoPlayer.class.getDeclaredField("md5");
    Assert.assertNotNull(f.getAnnotation(Indexed.class));
  }

  @Test
  public void testIdAsString() {
    assertEquals(PONE.getIdAsString(), PONE.getId().toHexString());
  }

  @Test
  public void testIdAsStringNullId() {
    MongoPlayer p = MongoGameCoreTestCase.makeSimplePlayer("677");
    p.setId(null);
    Assert.assertNull(p.getIdAsString());
  }

  @Test
  public void testEquals() {
    assertEquals(PONE, PONE);
    Assert.assertFalse(PONE.equals(PTWO));
    MongoPlayer copy = new MongoPlayer();
    copy.setId(PONE.getId());
    assertEquals(copy, PONE);
    Assert.assertNotEquals("String", PONE);
    Assert.assertNotEquals(PONE, null);
  }

  @Test
  public void testHashCode() {
    ObjectId SOMEID = new ObjectId(StringUtils.rightPad("1234", 24, "0"));
    MongoPlayer player = new MongoPlayer();
    player.setId(SOMEID);
    assertEquals(SOMEID.toHexString().hashCode(), player.hashCode());
  }

  @Test
  public void testToString() {
    MongoPlayer player = new MongoPlayer();
    player.setId(new ObjectId(StringUtils.rightPad("0a123", 24, "0")));
    player.setDisabled(false);
    player.setDisplayName("BAYMAX");
    player.setSourceId("BAYMAX");
    player.setSource("BIG HERO 6");

    assertEquals(
        "Player{id='0a1230000000000000000000', source='BIG HERO 6', sourceId='BAYMAX', displayName='BAYMAX', disabled=false}",
        player.toString());
  }

  @Test
  public void testMD5() {
    assertEquals("196c643ff2d27ff53cbd574c08c7726f", PONE.getMd5());
  }

  @Test
  public void testMD5FromBlank() {
    MongoPlayer p = new MongoPlayer();
    assertEquals("", p.getMd5());
  }

}
