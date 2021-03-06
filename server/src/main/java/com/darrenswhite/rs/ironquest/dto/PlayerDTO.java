package com.darrenswhite.rs.ironquest.dto;

import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.Skill;
import java.util.Map;
import java.util.Objects;

/**
 * Data Transfer Object for {@link Player}.
 *
 * @author Darren S. White
 */
public class PlayerDTO {

  private final String name;
  private final Map<Skill, Integer> levels;
  private final int questPoints;
  private final int totalLevel;
  private final int combatLevel;

  PlayerDTO(Builder builder) {
    this.name = builder.name;
    this.levels = builder.levels;
    this.questPoints = builder.questPoints;
    this.totalLevel = builder.totalLevel;
    this.combatLevel = builder.combatLevel;
  }

  public String getName() {
    return name;
  }

  public Map<Skill, Integer> getLevels() {
    return levels;
  }

  public int getQuestPoints() {
    return questPoints;
  }

  public int getTotalLevel() {
    return totalLevel;
  }

  public int getCombatLevel() {
    return combatLevel;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof PlayerDTO)) {
      return false;
    }
    PlayerDTO playerDTO = (PlayerDTO) o;
    return questPoints == playerDTO.questPoints && totalLevel == playerDTO.totalLevel
        && combatLevel == playerDTO.combatLevel && Objects.equals(name, playerDTO.name) && Objects
        .equals(levels, playerDTO.levels);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final int hashCode() {
    return Objects.hash(name, levels, questPoints, totalLevel, combatLevel);
  }

  public static class Builder {

    private String name;
    private Map<Skill, Integer> levels;
    private int questPoints;
    private int totalLevel;
    private int combatLevel;

    public Builder setName(String name) {
      this.name = name;
      return this;
    }

    public Builder withLevels(Map<Skill, Integer> levels) {
      this.levels = levels;
      return this;
    }

    public Builder withQuestPoints(int questPoints) {
      this.questPoints = questPoints;
      return this;
    }

    public Builder withTotalLevel(int totalLevel) {
      this.totalLevel = totalLevel;
      return this;
    }

    public Builder withCombatLevel(int combatLevel) {
      this.combatLevel = combatLevel;
      return this;
    }

    public PlayerDTO build() {
      return new PlayerDTO(this);
    }
  }
}
