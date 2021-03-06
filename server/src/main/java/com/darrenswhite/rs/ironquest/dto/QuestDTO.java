package com.darrenswhite.rs.ironquest.dto;

import com.darrenswhite.rs.ironquest.quest.Quest;
import java.util.Objects;

/**
 * Data Transfer Object for {@link Quest}.
 *
 * @author Darren S. White
 */
public class QuestDTO {

  private final String displayName;

  QuestDTO(Builder builder) {
    this.displayName = builder.displayName;
  }

  public String getDisplayName() {
    return displayName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof QuestDTO)) {
      return false;
    }
    QuestDTO questDTO = (QuestDTO) o;
    return Objects.equals(displayName, questDTO.displayName);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final int hashCode() {
    return Objects.hash(displayName);
  }

  public static class Builder {

    private String displayName;

    public Builder withDisplayName(String displayName) {
      this.displayName = displayName;
      return this;
    }

    public QuestDTO build() {
      return new QuestDTO(this);
    }
  }
}
