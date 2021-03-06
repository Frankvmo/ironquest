package com.darrenswhite.rs.ironquest.quest.reward;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.Skill;
import com.darrenswhite.rs.ironquest.util.MapBuilder;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class LampRewardTest {

  @Nested
  class GetChoices {

    @Test
    void shouldReturnEachSkillRequirement() {
      Player player = new Player.Builder().build();

      LampReward lampReward = new LampReward.Builder().withRequirements(
          new MapBuilder<Set<Skill>, Integer>()
              .put(new HashSet<>(Arrays.asList(Skill.ATTACK, Skill.DEFENCE)), 1)
              .put(new HashSet<>(Arrays.asList(Skill.SUMMONING, Skill.PRAYER)), 1).build()).build();

      Set<Set<Skill>> choices = lampReward.getChoices(player, Collections.emptySet());

      assertThat(choices,
          containsInAnyOrder(new HashSet<>(Arrays.asList(Skill.ATTACK, Skill.DEFENCE)),
              new HashSet<>(Arrays.asList(Skill.SUMMONING, Skill.PRAYER))));
    }

    @Test
    void shouldExcludeChoicesWithMissingLevelRequirements() {
      Player player = new Player.Builder().build();

      LampReward lampReward = new LampReward.Builder().withRequirements(
          new MapBuilder<Set<Skill>, Integer>().put(Collections.singleton(Skill.COOKING), 2)
              .put(Collections.singleton(Skill.MAGIC), 1).build()).build();

      Set<Set<Skill>> choices = lampReward.getChoices(player, Collections.emptySet());

      assertThat(choices, equalTo(Collections.singleton(Collections.singleton(Skill.MAGIC))));
    }

    @Test
    void shouldReturnEachSkillRequirementAsSingletonWhenSingleChoice() {
      Player player = new Player.Builder().build();

      LampReward lampReward = new LampReward.Builder().withSingleChoice(true).withRequirements(
          new MapBuilder<Set<Skill>, Integer>()
              .put(new HashSet<>(Arrays.asList(Skill.ATTACK, Skill.DEFENCE)), 1).build()).build();

      Set<Set<Skill>> choices = lampReward.getChoices(player, Collections.emptySet());

      assertThat(choices, containsInAnyOrder(Collections.singleton(Skill.ATTACK),
          Collections.singleton(Skill.DEFENCE)));
    }

    @Test
    void shouldReturnEachSkillRequirementWhenExclusive() {
      Player player = new Player.Builder().build();

      LampReward lampReward = new LampReward.Builder().withExclusive(true).withRequirements(
          new MapBuilder<Set<Skill>, Integer>()
              .put(new HashSet<>(Arrays.asList(Skill.ATTACK, Skill.DEFENCE)), 1).build()).build();

      Set<Set<Skill>> choices = lampReward.getChoices(player, Collections.emptySet());

      assertThat(choices, equalTo(
          Collections.singleton(new HashSet<>(Arrays.asList(Skill.ATTACK, Skill.DEFENCE)))));
    }

    @Test
    void shouldExcludePreviousChoicesWhenExclusive() {
      Player player = new Player.Builder().build();

      LampReward lampReward = new LampReward.Builder().withExclusive(true).withRequirements(
          new MapBuilder<Set<Skill>, Integer>()
              .put(new HashSet<>(Arrays.asList(Skill.ATTACK, Skill.DEFENCE)), 1)
              .put(new HashSet<>(Arrays.asList(Skill.STRENGTH, Skill.CONSTITUTION)), 1).build())
          .build();

      Set<Set<Skill>> choices = lampReward.getChoices(player,
          Collections.singleton(new HashSet<>(Arrays.asList(Skill.ATTACK, Skill.DEFENCE))));

      assertThat(choices, equalTo(
          Collections.singleton(new HashSet<>(Arrays.asList(Skill.STRENGTH, Skill.CONSTITUTION)))));
    }
  }

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class GetXpForSkills {

    @Test
    void shouldThrowExceptionForDynamicLampWithMultipleSkills() {
      Player player = new Player.Builder().build();

      LampReward lampReward = new LampReward.Builder().withType(LampType.SMALL_XP).withRequirements(
          new MapBuilder<Set<Skill>, Integer>()
              .put(new HashSet<>(Arrays.asList(Skill.ATTACK, Skill.DEFENCE)), 1).build()).build();

      assertThrows(DynamicLampRewardException.class, () -> lampReward.getXpForSkills(player,
          new HashSet<>(Arrays.asList(Skill.DUNGEONEERING, Skill.CONSTITUTION))));
    }

    @ParameterizedTest
    @MethodSource("shouldReturnXpForLamp")
    void shouldReturnXpForLamp(LampType lampType, double xp, double multiplier,
        Map<Set<Skill>, Integer> requirements, Set<Skill> skills, double expectedXp) {
      Player player = new Player.Builder().withSkillXps(
          new MapBuilder<Skill, Double>().put(Skill.DIVINATION, Skill.MAX_XP).put(Skill.RANGED, 0d)
              .build()).build();

      LampReward lampReward = new LampReward.Builder().withType(lampType).withXp(xp)
          .withMultiplier(multiplier).withRequirements(requirements).build();

      double actualXp = lampReward.getXpForSkills(player, skills);

      assertThat(actualXp, equalTo(expectedXp));
    }

    Stream<Arguments> shouldReturnXpForLamp() {
      return Stream.of(Arguments.of(LampType.XP, 100, 1,
          new MapBuilder<Set<Skill>, Integer>().put(Collections.singleton(Skill.RANGED), 1).build(),
          Collections.emptySet(), 100), Arguments.of(LampType.XP, 100, 1.5,
          new MapBuilder<Set<Skill>, Integer>().put(Collections.singleton(Skill.RANGED), 1).build(),
          Collections.emptySet(), 150), Arguments.of(LampType.SMALL_XP, 0, 1,
          new MapBuilder<Set<Skill>, Integer>().put(Collections.singleton(Skill.RANGED), 1).build(),
          Collections.singleton(Skill.RANGED), 62), Arguments.of(LampType.MEDIUM_XP, 0, 1,
          new MapBuilder<Set<Skill>, Integer>().put(Collections.singleton(Skill.RANGED), 1).build(),
          Collections.singleton(Skill.RANGED), 125), Arguments.of(LampType.LARGE_XP, 0, 1,
          new MapBuilder<Set<Skill>, Integer>().put(Collections.singleton(Skill.RANGED), 1).build(),
          Collections.singleton(Skill.RANGED), 250), Arguments.of(LampType.HUGE_XP, 0, 1,
          new MapBuilder<Set<Skill>, Integer>().put(Collections.singleton(Skill.RANGED), 1).build(),
          Collections.singleton(Skill.RANGED), 500), Arguments.of(LampType.DRAGONKIN, 0, 1,
          new MapBuilder<Set<Skill>, Integer>().put(Collections.singleton(Skill.RANGED), 1).build(),
          Collections.singleton(Skill.RANGED), 4), Arguments.of(LampType.XP, 100, 1,
          new MapBuilder<Set<Skill>, Integer>().put(Collections.singleton(Skill.DIVINATION), 1)
              .build(), Collections.emptySet(), 100), Arguments.of(LampType.XP, 100, 1.5,
          new MapBuilder<Set<Skill>, Integer>().put(Collections.singleton(Skill.DIVINATION), 1)
              .build(), Collections.emptySet(), 150), Arguments.of(LampType.SMALL_XP, 0, 1,
          new MapBuilder<Set<Skill>, Integer>().put(Collections.singleton(Skill.DIVINATION), 1)
              .build(), Collections.singleton(Skill.DIVINATION), 8602), Arguments
          .of(LampType.MEDIUM_XP, 0, 1,
              new MapBuilder<Set<Skill>, Integer>().put(Collections.singleton(Skill.DIVINATION), 1)
                  .build(), Collections.singleton(Skill.DIVINATION), 17204), Arguments
          .of(LampType.LARGE_XP, 0, 1,
              new MapBuilder<Set<Skill>, Integer>().put(Collections.singleton(Skill.DIVINATION), 1)
                  .build(), Collections.singleton(Skill.DIVINATION), 34408), Arguments
          .of(LampType.HUGE_XP, 0, 1,
              new MapBuilder<Set<Skill>, Integer>().put(Collections.singleton(Skill.DIVINATION), 1)
                  .build(), Collections.singleton(Skill.DIVINATION), 68816), Arguments
          .of(LampType.DRAGONKIN, 0, 1,
              new MapBuilder<Set<Skill>, Integer>().put(Collections.singleton(Skill.DIVINATION), 1)
                  .build(), Collections.singleton(Skill.DIVINATION), 48029));
    }
  }

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class MeetsRequirements {

    @Test
    void shouldReturnTrueWhenNoRequirements() {
      Player player = new Player.Builder().build();

      LampReward lampReward = new LampReward.Builder().withType(LampType.XP).withXp(100)
          .withRequirements(Collections.emptyMap()).build();

      assertThat(lampReward.meetsRequirements(player), equalTo(true));
    }

    @ParameterizedTest
    @MethodSource("shouldCheckEliteSkillHasMissingRequirements")
    void shouldCheckEliteSkillHasMissingRequirements(int craftingLevel, int divinationLevel,
        int smithingLevel, boolean meetsRequirements) {
      Player player = new Player.Builder().withSkillXps(new MapBuilder<Skill, Double>()
          .put(Skill.CRAFTING, Skill.CRAFTING.getXpAtLevel(craftingLevel))
          .put(Skill.DIVINATION, Skill.DIVINATION.getXpAtLevel(divinationLevel))
          .put(Skill.SMITHING, Skill.SMITHING.getXpAtLevel(smithingLevel)).put(Skill.INVENTION, 0d)
          .build()).build();

      LampReward lampReward = new LampReward.Builder().withType(LampType.XP).withXp(100)
          .withRequirements(
              new MapBuilder<Set<Skill>, Integer>().put(Collections.singleton(Skill.INVENTION), 1)
                  .build()).build();

      assertThat(lampReward.meetsRequirements(player), equalTo(meetsRequirements));
    }

    Stream<Arguments> shouldCheckEliteSkillHasMissingRequirements() {
      return Stream.of(Arguments.of(1, 1, 1, false), Arguments.of(79, 79, 79, false),
          Arguments.of(80, 79, 79, false), Arguments.of(79, 80, 79, false),
          Arguments.of(79, 79, 80, false), Arguments.of(80, 80, 79, false),
          Arguments.of(79, 80, 80, false), Arguments.of(80, 79, 80, false),
          Arguments.of(80, 80, 80, true));
    }

    @Test
    void shouldReturnFalseWhenLevelTooLow() {
      Player player = new Player.Builder().withSkillXps(
          new MapBuilder<Skill, Double>().put(Skill.HERBLORE, Skill.HERBLORE.getXpAtLevel(79))
              .build()).build();

      LampReward lampReward = new LampReward.Builder().withType(LampType.XP).withXp(100)
          .withRequirements(
              new MapBuilder<Set<Skill>, Integer>().put(Collections.singleton(Skill.HERBLORE), 80)
                  .build()).build();

      assertThat(lampReward.meetsRequirements(player), equalTo(false));
    }
  }

  @Nested
  class Equals {

    @Test
    void shouldVerifyEqualsAndHashCode() {
      EqualsVerifier.forClass(LampReward.class).verify();
    }
  }
}
