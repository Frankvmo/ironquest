package com.darrenswhite.rs.ironquest.quest.requirement;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.QuestStatus;
import com.darrenswhite.rs.ironquest.quest.Quest;
import com.darrenswhite.rs.ironquest.quest.Quest.Builder;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class QuestRequirementTest {

  @Nested
  class TestPlayer {

    @Test
    void shouldMeetRequirement() {
      QuestRequirement questRequirement = new QuestRequirement.Builder(new Quest.Builder().build())
          .build();
      Quest requiredQuest = questRequirement.getQuest();
      Quest questWithRequirement = new Quest.Builder().withRequirements(
          new QuestRequirements.Builder().withQuests(Collections.singleton(questRequirement))
              .build()).build();

      Player playerWithCompletedQuestRequirement = new Player.Builder()
          .withQuests(new HashSet<>(Arrays.asList(requiredQuest, questWithRequirement))).build();

      playerWithCompletedQuestRequirement.setQuestStatus(requiredQuest, QuestStatus.COMPLETED);

      assertThat(questRequirement.testPlayer(playerWithCompletedQuestRequirement), equalTo(true));
    }

    @Test
    void shouldNotMeetRequirement() {
      Quest requiredQuest = new Builder().build();
      QuestRequirement questRequirement = new QuestRequirement.Builder(requiredQuest).build();
      Quest questWithRequirement = new Quest.Builder().withRequirements(
          new QuestRequirements.Builder().withQuests(Collections.singleton(questRequirement))
              .build()).build();

      Player playerWithIncompleteQuestRequirement = new Player.Builder()
          .withQuests(new HashSet<>(Arrays.asList(requiredQuest, questWithRequirement))).build();

      assertThat(questRequirement.testPlayer(playerWithIncompleteQuestRequirement), equalTo(false));
    }
  }

  @Nested
  class Equals {

    @Test
    void shouldVerifyEqualsAndHashCode() {
      EqualsVerifier.forClass(QuestRequirement.class)
          .withPrefabValues(Quest.class, new Quest.Builder(0).build(), new Quest.Builder(1).build())
          .verify();
    }
  }
}
