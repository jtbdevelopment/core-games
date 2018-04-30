package com.jtbdevelopment.games.rest.handlers;

import static com.jtbdevelopment.games.GameCoreTestCase.PFOUR;
import static com.jtbdevelopment.games.GameCoreTestCase.PTHREE;
import static com.jtbdevelopment.games.GameCoreTestCase.PTWO;

import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.exceptions.system.FailedToFindPlayersException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Date: 11/10/14 Time: 6:56 PM
 */
public class AbstractHandlerTest {

  private AbstractPlayerRepository playerRepository = Mockito.mock(AbstractPlayerRepository.class);
  private TestHandler handler = new TestHandler(playerRepository);

  @Test
  public void testLoadPlayerMD5() {
    Mockito.when(playerRepository.findByMd5(PTHREE.getMd5())).thenReturn(PTHREE);
    Assert.assertSame(PTHREE, handler.loadPlayerMD5(PTHREE.getMd5()));
  }

  @Test(expected = FailedToFindPlayersException.class)
  public void testLoadPlayerMD5Fails() {
    Mockito.when(playerRepository.findByMd5(PTHREE.getMd5())).thenReturn(null);
    handler.loadPlayerMD5(PTHREE.getMd5());
  }

  @Test
  public void testLoadPlayerMD5s() {
    Mockito.when(playerRepository.findByMd5In(
        Arrays.asList(PTWO.getMd5(), PFOUR.getMd5(), PTHREE.getMd5())))
        .thenReturn(Arrays.asList(PTWO, PTHREE, PFOUR));
    Assert.assertEquals(
        new HashSet<>(Arrays.asList(PTWO, PTHREE, PFOUR)),
        handler.loadPlayerMD5s(Arrays.asList(PTWO.getMd5(), PFOUR.getMd5(), PTHREE.getMd5())));
  }

  @Test(expected = FailedToFindPlayersException.class)
  public void testLoadPlayerMD5sWithPartialLoad() {
    Mockito.when(playerRepository.findByMd5In(
        Arrays.asList(PTWO.getMd5(), PFOUR.getMd5(), PTHREE.getMd5())))
        .thenReturn(Arrays.asList(PTWO, PFOUR));
    handler.loadPlayerMD5s(
        Arrays.asList(PTWO.getMd5(), PFOUR.getMd5(), PTHREE.getMd5()));
  }

  @Test
  public void testLoadPlayer() {
    Mockito.when(playerRepository.findById(PTWO.getId())).thenReturn(Optional.of(PTWO));
    Assert.assertSame(PTWO, handler.loadPlayer(PTWO.getId()));
  }

  @Test(expected = FailedToFindPlayersException.class)
  public void testLoadPlayerFindsNull() {
    Mockito.when(playerRepository.findById(PTWO.getId())).thenReturn(Optional.empty());
    handler.loadPlayer(PTWO.getId());
  }

  private class TestHandler extends AbstractHandler {

    public TestHandler(AbstractPlayerRepository playerRepository) {
      this.playerRepository = playerRepository;
    }
  }
}
