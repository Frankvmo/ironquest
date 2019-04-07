package com.darrenswhite.rs.ironquest.action;

import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.QuestEntry;
import com.darrenswhite.rs.ironquest.player.QuestStatus;
import com.darrenswhite.rs.ironquest.quest.Quest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A class representing an {@link Action} to complete a {@link Quest}.
 *
 * @author Darren S. White
 */
public class QuestAction extends Action {

  private static final Logger LOG = LogManager.getLogger(QuestAction.class);

  private final QuestEntry entry;

  public QuestAction(Player player, QuestEntry entry) {
    super(player, false);
    this.entry = entry;
  }

  public QuestEntry getQuestEntry() {
    return entry;
  }

  @Override
  public String getMessage() {
    return entry.getQuest().getDisplayName();
  }

  @Override
  public boolean meetsRequirements(Player player) {
    return entry.getQuest().meetsAllRequirements(player);
  }

  @Override
  protected void processPlayer(Player player) {
    LOG.debug("Completing quest: {}", entry.getQuest().getDisplayName());

    entry.setStatus(QuestStatus.COMPLETED);
    entry.getQuest().getXpRewards().forEach(player::addSkillXP);
  }
}
