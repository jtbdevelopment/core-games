package com.jtbdevelopment.games.dev.utilities.players;

import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.players.AbstractPlayer;
import com.jtbdevelopment.games.players.ManualPlayer;
import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.players.PlayerFactory;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Date: 11/22/2014
 * Time: 1:48 PM
 */
public class ManualPlayerMaker {

  private static Logger logger = LoggerFactory.getLogger(ManualPlayerMaker.class);
  private static PasswordEncoder passwordEncoder;
  private static PlayerFactory playerFactory;

  public static void main(final String[] args) {
    ApplicationContext ctx = new AnnotationConfigApplicationContext("com.jtbdevelopment");

    final AbstractPlayerRepository repository = ctx
        .getBean(AbstractPlayerRepository.class);
    playerFactory = ctx.getBean(PlayerFactory.class);
    passwordEncoder = ctx.getBean(PasswordEncoder.class);

    Player[] players = new Player[]{
        makePlayer("Manual Player1", "M1@MANUAL.COM", "M1", "assets/avatars/maleprofile.png"),
        makePlayer("Manual Player2", "M2@MANUAL.COM", "M2", "assets/avatars/femaleprofile.png"),
        makePlayer("Manual Player3", "M3@MANUAL.COM", "M3", "assets/avatars/maleprofile.png"),
        makePlayer("Manual Player4", "M4@MANUAL.COM", "M4", "assets/avatars/femaleprofile.png"),
        makePlayer("Manual Player5", "M5@MANUAL.COM", "M5", "assets/avatars/maleprofile.png"),
        makePlayer("Manual Player6", "M6@MANUAL.COM", "M6", "assets/avatars/femaleprofile.png")};

    Arrays.stream(players).forEach(it -> {
      AbstractPlayer loaded = repository.findBySourceAndSourceId(it.getSource(), it.getSourceId());
      if (loaded == null) {
        logger.info("Creating player " + it);
        repository.save((AbstractPlayer) it);
      } else {
        if (loaded instanceof ManualPlayer) {
          ((ManualPlayer) loaded).setPassword(((ManualPlayer) it).getPassword());
        }
        loaded.setImageUrl(it.getImageUrl());
        loaded.setDisplayName(it.getDisplayName());
        loaded.setSourceId(it.getSourceId());
        repository.save(loaded);
        logger.info("Player already exists " + it + ", updating.");
      }


    });

    logger.info("Complete");
    ((AnnotationConfigApplicationContext) ctx).stop();
  }

  private static Player makePlayer(final String displayName, final String sourceId,
      final String password, final String imageUrl) {
    Player manualPlayer = playerFactory.newManualPlayer();
    manualPlayer.setDisabled(false);
    manualPlayer.setAdminUser(true);
    if (manualPlayer instanceof ManualPlayer) {
      ((ManualPlayer) manualPlayer).setPassword(passwordEncoder.encode(password));
      ((ManualPlayer) manualPlayer).setVerified(true);
    }
    manualPlayer.setDisplayName(displayName);
    manualPlayer.setSourceId(sourceId);
    manualPlayer.setImageUrl(imageUrl);
    return manualPlayer;
  }
}
