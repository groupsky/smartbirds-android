package org.bspb.smartbirds.pro.tools;

import org.bspb.smartbirds.pro.content.TrackingLocation;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by groupsky on 05.12.16.
 */

public class GpxWriter {

    private static final DateFormat GPX_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);

    private final OutputStreamWriter output;

    static {
        GPX_DATE_FORMATTER.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public GpxWriter(OutputStreamWriter output) {
        this.output = output;
    }

    public void writeHeader() throws IOException {
        output.write(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<gpx xmlns=\"http://www.topografix.com/GPX/1/1\"\n" +
                        "     version=\"1.1\"\n" +
                        "     creator=\"SmartBirds Pro\"\n" +
                        "     xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
                        "     xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\">\n" +
                        "  <metadata>\n" +
                        "    <time>" + GPX_DATE_FORMATTER.format(new Date()) + "</time>\n" +
                        "  </metadata>\n" +
                        "  <trk>\n");
    }

    public void writePosition(TrackingLocation location) throws IOException {
        output.write(
                "    <trkseg>\n" +
                        "      <trkpt lat=\"" + location.latitude + "\" lon=\"" + location.longitude + "\">\n" +
                        (location.altitude != null ? "        <ele>" + location.altitude + "</ele>\n" : "") +
                        "        <time>" + GPX_DATE_FORMATTER.format(new Date(location.time)) + "</time>\n" +
                        "      </trkpt>\n" +
                        "    </trkseg>\n");
    }

    public void writeFooter() throws IOException {
        output.write(
                "  </trk>\n" +
                        "</gpx>\n");
    }

}