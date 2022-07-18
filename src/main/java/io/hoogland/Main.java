package io.hoogland;

import io.hoogland.models.Stopwatch;
import io.hoogland.utils.FileUtils;
import io.hoogland.utils.OSUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Main {

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

        // Set fallback icon for prefix.
        String prefix = "\uE151";

        // Check if prefix argument is given, if so change it to given prefix.
        if (arguments.stream().anyMatch(arg -> arg.toLowerCase().contains("--prefix="))) {
            // Find the String in the list that has the prefix.
            for (String arg : arguments) {
                if (arg.toLowerCase().startsWith("--prefix=")) {
                    prefix = arg.substring(arg.indexOf("=") + 1);
                }
            }
        }

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

                    if (readStopwatch.isPaused()) {
                        // If the timer is paused, print the prefix and 'paused', if not print the prefix as well as the elapsed time.
                        System.out.printf("%%{F%s}%s %s%%{F-}", PAUSED_COLOR, prefix, readStopwatch.getFormattedElapsedTime());
                    } else {
                        System.out.printf("%s %s", prefix, readStopwatch.getFormattedElapsedTime());
                    }
                } else {
                    // If the timer hasn't been started print only the prefix icon.
                    System.out.println(prefix);
                }
            } else if (arguments.contains("stop") || arguments.contains("reset")) {
                // If the argument is stop or reset, make sure the file is deleted on exit.
                // This makes it so that if the program runs again it ends up printing only the prefix (see line 57)
                File stopwatchFile = new File(PATH);
                if (stopwatchFile.exists()) {
                    Stopwatch readStopwatch = (Stopwatch) FileUtils.readFromFile(stopwatchFile);
                    // Check if the system the application is run on is running linux or not (requires dunst).
                    if (OSUtils.isUnix()) {
                        // Check if the --notif-icon argument is present.
                        if (arguments.stream().anyMatch(arg -> arg.toLowerCase().contains("--notif-icon="))) {
                            // Find the entry that specifies the notif icon path.
                            arguments.forEach(arg -> {
                                if (arg.toLowerCase().startsWith("--notif-icon=")) {
                                    // Extract the path from the argument.
                                    String iconPath = arg.substring(arg.indexOf("=") + 1);
                                    try {
                                        // Launch a dunst notification using dunstify, with urgency high and a timeout of 10 seconds.
                                        Runtime.getRuntime().exec(new String[]{"dunstify", "Stopwatch stopped",
                                                "Final elapsed time: " + readStopwatch.getFormattedElapsedTime(), "--timeout=10000",
                                                "--raw_icon=" + iconPath});
                                    } catch (IOException e) {
                                        System.err.println("Unable to send notification.");
                                        throw new RuntimeException(e);
                                    }
                                }
                            });
                        }

                    }

                    stopwatchFile.deleteOnExit();
                }
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
