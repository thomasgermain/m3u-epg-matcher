package be.tgermain.m3uepgmatcher.epg;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class EpgParser {

    public EpgParser() {
    }

    public List<EpgEntry> parse(InputStream stream) throws SAXException, IOException {
        List<EpgEntry> epgEntries = new ArrayList<>();
        XMLReader xmlreader = XMLReaderFactory.createXMLReader();
        xmlreader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        xmlreader.setContentHandler(new DefaultHandler() {
            private EpgEntry epgEntry;
            private boolean displayName;
            private StringBuilder channelName;

            @Override
            public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
                if (localName.equals("channel")) {
                    channelName = new StringBuilder();
                    epgEntry = new EpgEntry();
                    epgEntry.setChannelId(atts.getValue("id"));
                } else if (localName.equals("display-name")) {
                    displayName = true;
                } else if (localName.equals("icon")) {
                    epgEntry.setIconUri(atts.getValue("src"));
                }
            }

            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {
                if(localName.equals("channel")) {
                    epgEntry.setChannelDisplayName(epgEntry.getChannelDisplayName().trim());
                    epgEntries.add(epgEntry);
                } else if (localName.equals("display-name")) {
                    displayName = false;
                }
            }

            @Override
            public void characters(char[] ch, int start, int length) throws SAXException {
                if (displayName) {
                    channelName.append(String.copyValueOf(ch, start, length));
                    epgEntry.setChannelDisplayName(channelName.toString());
                }
            }
        });
        xmlreader.parse(new InputSource(stream));
        return epgEntries;
    }
}
