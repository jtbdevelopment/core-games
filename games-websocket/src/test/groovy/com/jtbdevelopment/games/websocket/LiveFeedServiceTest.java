package com.jtbdevelopment.games.websocket;

import com.jtbdevelopment.games.websocket.WebSocketMessage.MessageType;
import java.lang.reflect.Method;
import java.util.Arrays;
import org.atmosphere.client.TrackMessageSizeInterceptor;
import org.atmosphere.config.service.ManagedService;
import org.atmosphere.config.service.Message;
import org.atmosphere.config.service.Ready;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEventImpl;
import org.atmosphere.cpr.AtmosphereResourceImpl;
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor;
import org.atmosphere.interceptor.HeartbeatInterceptor;
import org.atmosphere.interceptor.SuspendTrackerInterceptor;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Date: 12/22/14 Time: 7:41 PM
 */
public class LiveFeedServiceTest {

  private LiveFeedService liveFeedService = new LiveFeedService();

  @Test
  public void testOnConnect() {
    liveFeedService.id = "TESTID";
    String uuid = "UUID";
    AtmosphereResource resource = Mockito.mock(AtmosphereResource.class);
    Mockito.when(resource.uuid()).thenReturn(uuid);
    WebSocketMessage message = liveFeedService.onReady(resource);
    Assert.assertEquals(MessageType.Heartbeat, message.getMessageType());
    Assert.assertNull(message.getGame());
    Assert.assertEquals("connected to " + liveFeedService.id, message.getMessage());
  }

  @Test
  public void testOnMessageLoop() {
    WebSocketMessage message = new WebSocketMessage();
    Assert.assertSame(message, liveFeedService.onMessage(message));
  }

  @Test
  public void testClassAnnotations() {
    ManagedService a = LiveFeedService.class.getAnnotation(ManagedService.class);
    Assert.assertNotNull(a);
    Assert.assertEquals("/livefeed/{id: [a-zA-Z][a-zA-Z_0-9]*}", a.path());
    Assert.assertArrayEquals(Arrays
            .asList(SpringSecuritySessionInterceptor.class,
                AtmosphereResourceLifecycleInterceptor.class, TrackMessageSizeInterceptor.class,
                SuspendTrackerInterceptor.class, HeartbeatInterceptor.class).toArray(),
        a.interceptors());
    Assert.assertArrayEquals(Arrays.asList("supportSession=true").toArray(),
        a.atmosphereConfig());
  }

  @Test
  public void testOnReadyAnnotations() throws NoSuchMethodException {
    Method m = LiveFeedService.class.getMethod("onReady", new Class[]{AtmosphereResource.class});
    Assert.assertNotNull(m);
    Ready r = m.getAnnotation(Ready.class);
    Assert.assertNotNull(r);
    Assert.assertArrayEquals(Arrays.asList(WebSocketJSONConverter.class).toArray(), r.encoders());
  }

  @Test
  public void testOnMessageAnnotations() throws NoSuchMethodException {
    Method m = LiveFeedService.class.getMethod("onMessage", new Class[]{WebSocketMessage.class});
    Assert.assertNotNull(m);
    Message message = m.getAnnotation(Message.class);
    Assert.assertNotNull(message);
    Assert.assertArrayEquals(Arrays.asList(WebSocketJSONConverter.class).toArray(),
        message.encoders());
    Assert.assertArrayEquals(Arrays.asList(WebSocketJSONConverter.class).toArray(),
        message.decoders());
  }

  @Test
  public void testOnDisconnectOnCancel() {
    AtmosphereResourceEventImpl r = new AtmosphereResourceEventImpl(new AtmosphereResourceImpl(),
        true, false);
    liveFeedService.onDisconnect(r);
    //  No asserts since its all logging right now
  }

  @Test
  public void testOnDisconnectOnClosedByClient() {
    AtmosphereResourceEventImpl r = new AtmosphereResourceEventImpl(new AtmosphereResourceImpl(),
        false, false, true, null);
    liveFeedService.onDisconnect(r);
    //  No asserts since its all logging right now
  }

  @Test
  public void testOnDisconnectOnOther() {
    AtmosphereResourceEventImpl r = new AtmosphereResourceEventImpl(new AtmosphereResourceImpl(),
        false, false, false, null);
    liveFeedService.onDisconnect(r);
    //  No asserts since its all logging right now
  }
}
