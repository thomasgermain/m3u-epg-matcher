package be.tgermain.m3uepgmatcher.matcher;

import java.text.Normalizer;

/**
 * Created by Thomas on 28/05/2018.
 */
public class ChannelNameCleaner {

    private ChannelNameCleaner() {
    }

    public static String clean(String name) {
        String clean = Normalizer.normalize(name, Normalizer.Form.NFD);


        return clean.toUpperCase()
                .replace("FR-BE", "")
                .replace("|FR*|", "")
                .replace("FR:", "")
                .replace("|FR |", "")
                .replace("|FR|", "")
                .replace(" FR ", "")
                .replace("|BE*|", "")
                .replace("|BE|", "")
                .replace("|VIP |", "")
                .replace("FHD", "")
                .replace("FULL HD", "")
                .replace("FULLHD+", "")
                .replace("FULLHD", "")
                .replace("SD", "")
                .replace(" HD", "")
                .replace("HD ", "")
                .replace("720P", "")
                .replace("( 1080P )", "")
                .replace("(1080P)", "")
                .replace("1080P", "")
                .replace("1080", "")
                .replace("720", "")
                .replace("BELGIUM", "")
                .replace("(BACKUP)", "")
                .replace("BACKUP", "")
                .replace("BKP", "")
                .replace("&", "")
                .replace(" ET ", "")
                .replaceAll("\\+", "")
                .replaceAll("FR$", "")
                .replaceAll(" $", "")
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "")
                .replaceAll(" ", "");
    }

    public static void main(String[] args) {
        String str = "Chasse &amp; Pêche";
        System.out.println(ChannelNameCleaner.clean(str));
    }
}
