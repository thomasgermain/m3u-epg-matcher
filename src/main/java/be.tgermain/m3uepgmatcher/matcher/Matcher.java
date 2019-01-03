package be.tgermain.m3uepgmatcher.matcher;

import be.tgermain.m3uepgmatcher.epg.EpgEntry;
import be.tgermain.m3uepgmatcher.m3u.M3uEntry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Matcher {

    private Map<String, Set<EpgEntry>> epgDisplayNames;
    private Map<String, Set<EpgEntry>> epgIds;
    private List<EpgEntry> epgs;

    private MatcherReport matcherReport;

    public Matcher(List<EpgEntry> epgs) {
        this.epgs = epgs;
        matcherReport = new MatcherReport();
        epgDisplayNames = new LinkedHashMap<>();
        epgIds = new LinkedHashMap<>();

        for (EpgEntry epgEntry : epgs) {
            Set<EpgEntry> idsSet = epgIds.get(epgEntry.getChannelId());
            if (idsSet == null) {
                idsSet = new HashSet<>();
            }
            idsSet.add(epgEntry);
            epgIds.put(epgEntry.getChannelId(), idsSet);

            Set<EpgEntry> namesSet = epgDisplayNames.get(ChannelNameCleaner.clean(epgEntry.getChannelDisplayName()));
            if (namesSet == null) {
                namesSet = new HashSet<>();
            }
            namesSet.add(epgEntry);

            epgDisplayNames.put(ChannelNameCleaner.clean(epgEntry.getChannelDisplayName()), namesSet);
        }
    }

    public MatcherReport checkEpgs(List<M3uEntry> channels) {
        for (M3uEntry channel : channels) {
            if (!channel.getChannelName().startsWith("######")) {
                EpgEntry epg = getMatchingEpgEntry(channel);

                if(epg != null) {
                    channel.setTvgId(epg.getChannelId());
                    channel.setTvgName(epg.getChannelDisplayName());
                    channel.setTvgLogo("http:" + epg.getIconUri());
                }
            } else {
                matcherReport.addReport(channel, null, MatcherReport.MatchingType.IGNORE);
            }
        }

        return matcherReport;
    }

    private EpgEntry getMatchingEpgEntry(M3uEntry channel) {
        EpgEntry epg;
        MatcherReport.MatchingType type;

        epg = getBestUsingEpgId(channel.getTvgName(), channel.getChannelName());
        type = MatcherReport.MatchingType.EPG_ID__TVG_AME;
        if (epg == null) {
            epg = getBestUsingEpgId(channel.getChannelName(), channel.getTvgName());
            type = MatcherReport.MatchingType.EPG_ID__CHANNEL_NAME;

            if (epg == null) {
                epg = getBestUsingChannelNames(channel.getTvgName(), channel.getChannelName());
                type = MatcherReport.MatchingType.EPG_NAME__TVG_NAME;

                if (epg == null) {
                    epg = getBestUsingChannelNames(channel.getChannelName(), channel.getTvgName());
                    type = MatcherReport.MatchingType.EPG_NAME__CHANNEL_NAME;

                    if (epg == null) {
                        epg = getBestUsingLevenshtein(channel);
                        type = MatcherReport.MatchingType.LEVENSHTEIN;

                        if (epg == null) {
                            type = MatcherReport.MatchingType.NONE;
                        }
                    }

                }
            }
        }

        matcherReport.addReport(channel, epg, type);

        return epg;
    }

    private EpgEntry getBestUsingEpgId(String name, String otherName) {
        return getBestUsing(epgIds, name, otherName);
    }

    private EpgEntry getBestUsingChannelNames(String name, String otherName) {
        return getBestUsing(epgDisplayNames, name, otherName);
    }

    private EpgEntry getBestUsingLevenshtein(M3uEntry channel) {
        Map<Integer, EpgEntry> entryByDistance = new HashMap<>();

        String cleanedChannelName = ChannelNameCleaner.clean(channel.getChannelName());
        if (cleanedChannelName.length() > 2) {
            for (EpgEntry epg : epgs) {

                int dist1 = distance(ChannelNameCleaner.clean(epg.getChannelDisplayName()), cleanedChannelName);

                if (dist1 == 0) {
                    return epg;
                } else {
                    entryByDistance.put(dist1, epg);

                    int dist2 = distance(epg.getChannelId().toUpperCase(), cleanedChannelName);
                    if (dist2 == 0) {
                        return epg;
                    } else if (dist2 < 4) {
                        entryByDistance.put(dist2, epg);
                    }
                }
            }
            /*if (entryByDistance.containsKey(1) && cleanedChannelName.length() < 5) {
                EpgEntry epg = entryByDistance.get(1);
                if (ChannelNameCleaner.clean(epg.getChannelId()).contains(cleanedChannelName)) {
                    matcherReport.addReport(channel, epg, MatcherReport.MatchingType.LEVENSHTEIN);
                    return epg;
                }
            } else */

            if (cleanedChannelName.length() < 6) {
                if (entryByDistance.containsKey(1)) {
                    EpgEntry epg = entryByDistance.get(1);
                    if (ChannelNameCleaner.clean(epg.getChannelDisplayName()).contains(cleanedChannelName)
                            || epg.getChannelId().toUpperCase().contains(cleanedChannelName)) {
                        return epg;
                    }
                }
            } else if (cleanedChannelName.length() < 7) {
                for (Integer dist : new int[]{1, 2}) {
                    if (entryByDistance.containsKey(dist)) {
                        return entryByDistance.get(dist);
                    }
                }
            } else {
                for (Integer dist : new int[]{1, 2, 3}) {
                    if (entryByDistance.containsKey(dist)) {
                        return entryByDistance.get(dist);
                    }
                }
            }
        }

        return null;
    }

    private EpgEntry getBestUsing(Map<String, Set<EpgEntry>> base, String name, String secondName) {
        EpgEntry epg = null;
        if (name != null) {
            Set<EpgEntry> set = base.get(ChannelNameCleaner.clean(name));
            if (set != null) {
                if (set.size() == 1) {
                    epg = set.iterator().next();
                } else if (set.size() > 1) {
                    for (EpgEntry epgEntry : set) {
                        if (epgEntry.getChannelDisplayName().equals(secondName)) {
                            epg = epgEntry;
                        }
                    }
                }
            }
        }
        return epg;
    }

    public int distance(String a, String b) {
        a = a.toLowerCase();
        b = b.toLowerCase();
        // i == 0
        int[] costs = new int[b.length() + 1];
        for (int j = 0; j < costs.length; j++)
            costs[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            // j == 0; nw = lev(i - 1, j)
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }


}
