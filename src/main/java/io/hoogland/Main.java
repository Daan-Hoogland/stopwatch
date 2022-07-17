package io.hoogland;

import io.hoogland.models.Stopwatch;
import io.hoogland.utils.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Main {
    /**
     * Prefix icon printed in front of the timer/state.
     */
    public static final String PREFIX = "\uE151";

    /**
     * Temp directory used to store stopwatch objects.
     */
    public static final String TEMP_DIR = System.getProperty("java.io.tmpdir");

    /**
     * Name of the file used to store the stopwatch data.
     */
    public static final String FILE_NAME = "stopwatch.tmp";

    /**
     * Full path to the file where the stopwatch data is stored.
     */
    public static final String PATH = TEMP_DIR + File.separator + FILE_NAME;

    /**
     * Time of the text when timer is paused. Hex format.
     */
    public static final String PAUSED_COLOR = "#CA6889";

    public static void main(String[] args) {
        // Directory of temporary files on system.

        // Cast arguments to lowercase for easier matching.
        List<String> arguments = Arrays.stream(args).map(String::toLowerCase).toList();

        try {
            if (arguments.contains("start")) {
                // If start is an argument, start a new stopwatch and write it to file.
                Stopwatch stopwatch = new Stopwatch();
                FileUtils.writeToFile(stopwatch, new File(PATH));
            } else if (arguments.contains("status")) {
                // If status is an argument check if the file exists, if so read it.
                File stopwatchFile = new File(PATH);
                if (stopwatchFile.exists()) {
                    Stopwatch readStopwatch = (Stopwatch) FileUtils.readFromFile(new File(PATH));
                    // Figure out the format by checking if the timer has been running for over an hour or not.
                    String format;
                    if (readStopwatch.getElapsedTime() >= 3600L) {
                        format = "HH:mm:ss";
                    } else {
                        format = "mm:ss";
                    }
                    // Format the time with timezone UTC
                    DateTimeFormatter formatter =
                            DateTimeFormatter.ofPattern(format).withZone(ZoneId.of("UTC"));

                    // Convert the time (long) to the format decided on above.
                    Instant instant = Instant.ofEpochSecond(readStopwatch.getElapsedTime());
                    if (readStopwatch.isPaused()) {
                        // If the timer is paused, print the prefix and 'paused', if not print the prefix as well as the elapsed time.
                        System.out.printf("%%{F%s}%s %s%%{F-}", PAUSED_COLOR, PREFIX, formatter.format(instant));
                    } else {
                        System.out.println(PREFIX + " " + formatter.format(instant));
                    }
                } else {
                    // If the timer hasn't been started print only the prefix icon.
                    System.out.println(PREFIX);
                }
            } else if (arguments.contains("stop") || arguments.contains("reset")) {
                // If the argument is stop or reset, make sure the file is deleted on exit.
                // This makes it so that if the program runs again it ends up printing only the prefix (see line 57)
                File stopwatchFile = new File(PATH);
                stopwatchFile.deleteOnExit();
            } else {
                // Any other argument or an empty argument will end here. It will find the file and check if it exists.
                File stopwatchFile = new File(PATH);
                if (stopwatchFile.exists()) {
                    Stopwatch readStopwatch = (Stopwatch) FileUtils.readFromFile(stopwatchFile);
                    if (readStopwatch.getPauseStart() != null) {
                        // If it exists and the timer has been paused and there is a start time to the pause time,
                        // calculate the length of the pause and indicate the timer is no longer paused (boolean)
                        readStopwatch.endPause(new Date().getTime());
                        readStopwatch.setPaused(false);
                    } else {
                        // If it isn't paused, set the start time of the pause and set the boolean to true.
                        readStopwatch.setPauseStart(new Date().getTime());
                        readStopwatch.setPaused(true);
                    }
                    // Write the pause changes (start/end time, boolean) to the file.
                    FileUtils.writeToFile(readStopwatch, new File(PATH));
                } else {
                    // If the file doesn't exist (no timer is running), start the timer at 0.
                    Stopwatch stopwatch = new Stopwatch();
                    FileUtils.writeToFile(stopwatch, new File(PATH));
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Stopwatch time not found.");
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.err.printf("Something went wrong whilst writing/reading from file. " +
                    "Check permissions for temp folder at %s.", TEMP_DIR);
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
