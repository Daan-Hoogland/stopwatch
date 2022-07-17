package io.hoogland.models;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Stopwatch implements Serializable {
    /**
     * Start time of the stopwatch.
     */
    private long start = new Date().getTime();

    /**
     * End time of the stopwatch.
     */
    private long end;

    /**
     * Boolean indicating whether the timer is paused (used for display)
     */
    private boolean isPaused;

    /**
     * Long indicating the start of the current pause (if paused)
     */
    private Long pauseStart;

    /**
     * Total time the stopwatch has been paused.
     */
    private long pauseTotal;

    /**
     * Returns the elapsed time factoring in the time the timer is paused.
     *
     * @return Long containing the elapsed time.
     */
    public Long getElapsedTime() {
        if (this.start == 0L) {
            System.err.println("test");
        }
        end = new Date().getTime();
        long result = 0L;
        long pausedTime = 0L;

        if (isPaused) {
            result = ((pauseStart - start) - pauseTotal) / 1000;
        } else {
            result = ((end - start) - pauseTotal) / 1000;
        }

        return result;
    }

    /**
     * Ends the pause and calculates the length of the pause in long format.
     *
     * @param pauseEnd Date formatted as long when the pause ends.
     */
    public void endPause(long pauseEnd) {
        if (pauseStart != null) {
            long pauseElapsed = pauseEnd - pauseStart;
            this.pauseStart = null;
            this.pauseTotal += pauseElapsed;
        }
    }
}
