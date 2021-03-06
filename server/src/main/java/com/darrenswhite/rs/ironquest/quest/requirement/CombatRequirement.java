package com.darrenswhite.rs.ironquest.quest.requirement;

import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.quest.Quest;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Objects;

/**
 * A class representing a combat requirement for a {@link Quest}.
 *
 * @author Darren S. White
 */
@JsonDeserialize(builder = CombatRequirement.Builder.class)
public class CombatRequirement extends Requirement {

  private final int level;

  CombatRequirement(Builder builder) {
    super(builder.ironman, builder.recommended);
    this.level = builder.level;
  }

  public int getLevel() {
    return level;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CombatRequirement)) {
      return false;
    }
    CombatRequirement that = (CombatRequirement) o;
    return ironman == that.ironman && recommended == that.recommended && level == that.level;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final int hashCode() {
    return Objects.hash(ironman, recommended, level);
  }

  /**
   * {@inheritDoc}
   *
   * @return <tt>true</tt> if the player has the required combat level; <tt>false</tt> otherwise.
   */
  @Override
  protected boolean testPlayer(Player player) {
    return player.getCombatLevel() >= level;
  }

  public static class Builder {

    private int level;
    private boolean ironman;
    private boolean recommended;

    public Builder() {
    }

    public Builder(int level) {
      this.level = level;
    }

    public Builder withLevel(int level) {
      this.level = level;
      return this;
    }

    public Builder withIronman(boolean ironman) {
      this.ironman = ironman;
      return this;
    }

    public Builder withRecommended(boolean recommended) {
      this.recommended = recommended;
      return this;
    }

    public CombatRequirement build() {
      return new CombatRequirement(this);
    }
  }
}
