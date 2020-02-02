package com.darrenswhite.rs.ironquest.dto;

import java.util.Objects;

/**
 * Data Transfer Object for {@link com.darrenswhite.rs.ironquest.quest.Quest}.
 *
 * @author Darren S. White
 */
public class QuestDTO {

  private final String displayName;

  QuestDTO(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    QuestDTO questDTO = (QuestDTO) o;
    return Objects.equals(displayName, questDTO.displayName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(displayName);
  }

  public static class Builder {

    private String displayName;

    public Builder withDisplayName(String displayName) {
      this.displayName = displayName;
      return this;
    }

    public QuestDTO build() {
      return new QuestDTO(displayName);
    }
  }
}