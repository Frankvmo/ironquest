package com.darrenswhite.rs.ironquest.player;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * {@link Service} for retrieving skill data from the Hiscores.
 *
 * @author Darren S. White
 */
@Service
public class HiscoreService {

  private static final Logger LOG = LogManager.getLogger(HiscoreService.class);
  private static final CSVFormat CSV_FORMAT = CSVFormat.DEFAULT.withDelimiter(',');

  /**
   * First row is total level, so skip it.
   */
  private static final int ROW_OFFSET = 1;

  private final String url;

  public HiscoreService(@Value("${hiscores.url}") String url) {
    this.url = url;
  }

  /**
   * Retrieve skill xp data for the given username.
   *
   * @param name the username
   * @return map of xp for each skill
   */
  public Map<Skill, Double> load(String name) {
    Map<Skill, Double> skillXps = new EnumMap<>(Skill.class);

    LOG.debug("Loading hiscores for player: {}...", name);

    try {
      String hiscoresUrl = String
          .format(url, URLEncoder.encode(name, StandardCharsets.UTF_8.toString()));

      try (InputStreamReader in = new InputStreamReader(new URL(hiscoresUrl).openStream())) {
        CSVParser parser = CSV_FORMAT.parse(in);
        List<CSVRecord> records = parser.getRecords();

        for (int i = ROW_OFFSET; i < Skill.values().length + 1; i++) {
          Skill skill = Skill.getById(i);
          CSVRecord r = records.get(i);

          if (skill != null) {
            double xp = Math.max(Skill.INITIAL_XPS.get(skill), Double.parseDouble(r.get(2)));

            skillXps.put(skill, xp);
          } else {
            LOG.warn("Unknown skill with id: {}", i);
          }
        }
      }
    } catch (IOException e) {
      LOG.warn("Failed to load hiscores for player: {}", name, e);
    }

    return skillXps;
  }
}
