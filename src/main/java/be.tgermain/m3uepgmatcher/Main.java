package be.tgermain.m3uepgmatcher;

import be.tgermain.m3uepgmatcher.epg.EpgEntry;
import be.tgermain.m3uepgmatcher.epg.EpgParser;
import be.tgermain.m3uepgmatcher.m3u.M3uEntry;
import be.tgermain.m3uepgmatcher.m3u.M3uParser;
import be.tgermain.m3uepgmatcher.m3u.M3uWriter;
import be.tgermain.m3uepgmatcher.matcher.ChannelNameCleaner;
import be.tgermain.m3uepgmatcher.matcher.Matcher;
import be.tgermain.m3uepgmatcher.matcher.MatcherReport;
import org.xml.sax.SAXException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {

    public static void main(String args[]) throws IOException, SAXException {

        String m3uFile = args[0];
        String epgFile = args[1];

        List<M3uEntry> channels = new M3uParser().parse(new FileInputStream(m3uFile));
        List<EpgEntry> epgs = new EpgParser().parse(new FileInputStream(epgFile));

        MatcherReport matcherReport = new Matcher(epgs).checkEpgs(channels);

        List<M3uEntry> m3uEntries = matcherReport.getMatchReports().stream().map(MatcherReport.MatchReport::getM3uEntry).collect(Collectors.toList());
        printReport(matcherReport);

        new M3uWriter().write(m3uEntries, new FileOutputStream(m3uFile.replace(".m3u", "_enriched.m3u")));


    }

    private static void printReport(MatcherReport matcherReport) {
        System.out.println("##### STAT #####");
        System.out.println(matcherReport.getPercentageFound());
        for (Map.Entry<MatcherReport.MatchingType, Integer> entry : matcherReport.getFoundBy().entrySet()) {
            System.out.println(entry.getKey() + " ->> " + entry.getValue());
        }

        System.out.println("");
        System.out.println("################");
        System.out.println("");

        System.out.println("##### FOUND MAPPING #####");
        for (MatcherReport.MatchReport matchReport : matcherReport.getMatchReports()) {
            if (matchReport.getMatchingType().isFound()) {
                if (matchReport.getEpgEntry() != null) {
                    System.out.println(matchReport.getM3uEntry().getChannelName() + "/" + matchReport.getEpgEntry().getChannelId());
                }
            }
        }

        System.out.println("");
        System.out.println("################");
        System.out.println("");

        System.out.println("##### NOT FOUND MAPPING #####");
        for (MatcherReport.MatchReport matchReport : matcherReport.getMatchReports()) {
            if (!matchReport.getMatchingType().isFound()) {
                System.out.println(matchReport.getM3uEntry().getChannelName() + " cleaned to " + ChannelNameCleaner.clean(matchReport.getM3uEntry().getChannelName()));
            }
        }
    }
}
