package be.tgermain.m3uepgmatcher.matcher;

import be.tgermain.m3uepgmatcher.epg.EpgEntry;
import be.tgermain.m3uepgmatcher.m3u.M3uEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatcherReport {

    public enum MatchingType {
        NONE(false),
        IGNORE(true),
        EPG_ID__TVG_AME(true),
        EPG_ID__CHANNEL_NAME(true),
        EPG_NAME__TVG_NAME(true),
        EPG_NAME__CHANNEL_NAME(true),
        LEVENSHTEIN(true);

        private boolean found;

        MatchingType(boolean found) {
            this.found = found;
        }

        public boolean isFound() {
            return found;
        }
    }

    public class MatchReport {
        private M3uEntry m3uEntry;
        private EpgEntry epgEntry;
        private MatchingType matchingType;

        public MatchReport(M3uEntry m3uEntry, EpgEntry epgEntry, MatchingType matchingType) {
            this.m3uEntry = m3uEntry;
            this.epgEntry = epgEntry;
            this.matchingType = matchingType;
        }

        public M3uEntry getM3uEntry() {
            return m3uEntry;
        }

        public EpgEntry getEpgEntry() {
            return epgEntry;
        }

        public MatchingType getMatchingType() {
            return matchingType;
        }
    }


    private List<MatchReport> matchReports;
    private Map<MatchingType, Integer> foundBy;
    private int found;
    private int notFound;

    public MatcherReport() {
        matchReports = new ArrayList<>();
        foundBy = new HashMap<>();
        for (MatchingType value : MatchingType.values()) {
            foundBy.put(value, 0);
        }
    }

    public void addReport(M3uEntry m3uEntry, EpgEntry epgEntry, MatchingType type) {
        matchReports.add(new MatchReport(m3uEntry, epgEntry, type));

        if (type != MatchingType.IGNORE) {

            if (type == MatchingType.NONE) {
                notFound++;
            } else {
                found++;
            }
        }
        Integer count = foundBy.get(type);
        foundBy.put(type, ++count);
    }

    public Map<MatchingType, Integer> getFoundBy() {
        return foundBy;
    }

    public int getFoundCount() {
        return found;
    }

    public int getNotFoundCount() {
        return notFound;
    }

    public double getPercentageFound() {
        return found * 1.0 / (found + notFound) * 100.0;
    }

    public List<MatchReport> getMatchReports() {
        return matchReports;
    }
}
