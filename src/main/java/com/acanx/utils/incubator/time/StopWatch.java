package com.acanx.utils.incubator.time;

import com.acanx.annotation.Alpha;
import com.acanx.annotation.Nullable;

import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * ACANX-Utils / com.acanx.utils / StopWatch.java
 * 文件由 ACANX 创建于 2019/7/26 - 17:37
 * Description  StopWatch: Spring框架工具类
 *
 * Simple stop watch, allowing for timing of a number of tasks,
 * exposing total running time and running time for each named task.
 *
 * <p>Conceals use of {@code System.currentTimeMillis()}, improving the
 * readability of application code and reducing the likelihood of calculation errors.
 *
 * <p>Note that this object is not designed to be thread-safe and does not
 * use synchronization.
 *
 * <p>This class is normally used to verify performance during proof-of-concepts
 * and in development, rather than as part of production applications.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 0.0.1.10  May 2, 2001
 *
 */
@Alpha
public class StopWatch {

    /**
     * Identifier of this stop watch.
     * Handy when we have output from multiple stop watches
     * and need to distinguish between them in log or console output.
     */
    private final String id;

    private boolean keepTaskList = true;

    private final List<TaskInfo> taskList = new LinkedList<>();

    /** Start time of the current task. */
    private long startTimeMillis;

    /** Name of the current task. */
    @Nullable
    private String currentTaskName;

    @Nullable
    private TaskInfo lastTaskInfo;

    private int taskCount;

    /** Total running time. */
    private long totalTimeMillis;


    /**
     * Construct a new stop watch. Does not start any task.
     */
    public StopWatch() {
        this("");
    }

    /**
     * Construct a new stop watch with the given id.
     * Does not start any task.
     * @param id identifier for this stop watch.
     * Handy when we have output from multiple stop watches
     * and need to distinguish between them.
     */
    public StopWatch(String id) {
        this.id = id;
    }


    /**
     * Return the id of this stop watch, as specified on construction.
     * @return the id (empty String by default)
     * @since 4.2.2
     * @see #StopWatch(String)
     */
    public String getId() {
        return this.id;
    }

    /**
     * Determine whether the TaskInfo array is built over time. Set this to
     * "false" when using a StopWatch for millions of intervals, or the task
     * info structure will consume excessive memory. Default is "true".
     *
     * @param keepTaskList keepTaskList
     */
    public void setKeepTaskList(boolean keepTaskList) {
        this.keepTaskList = keepTaskList;
    }


    /**
     * Start an unnamed task. The results are undefined if {@link #stop()}
     * or timing methods are called without invoking this method.
     * @see #stop()
     */
    public void start() throws IllegalStateException {
        start("");
    }

    /**
     * Start a named task. The results are undefined if {@link #stop()}
     * or timing methods are called without invoking this method.
     * @param taskName the name of the task to start
     * @see #stop()
     */
    public void start(String taskName) throws IllegalStateException {
        if (this.currentTaskName != null) {
            throw new IllegalStateException("Can't start StopWatch: it's already running");
        }
        this.currentTaskName = taskName;
        this.startTimeMillis = System.currentTimeMillis();
    }

    /**
     * Stop the current task. The results are undefined if timing
     * methods are called without invoking at least one pair
     * {@code start()} / {@code stop()} methods.
     * @see #start()
     *
     * @throws IllegalStateException IllegalStateException
     */
    public void stop() throws IllegalStateException {
        if (this.currentTaskName == null) {
            throw new IllegalStateException("Can't stop StopWatch: it's not running");
        }
        long lastTime = System.currentTimeMillis() - this.startTimeMillis;
        this.totalTimeMillis += lastTime;
        this.lastTaskInfo = new TaskInfo(this.currentTaskName, lastTime);
        if (this.keepTaskList) {
            this.taskList.add(this.lastTaskInfo);
        }
        ++this.taskCount;
        this.currentTaskName = null;
    }

    /**
     * Return whether the stop watch is currently running.
     * @see #currentTaskName()
     * @return  结果
     */
    public boolean isRunning() {
        return (this.currentTaskName != null);
    }

    /**
     * Return the name of the currently running task, if any.
     * @since 4.2.2
     * @see #isRunning()
     * @return 结果
     */
    @Nullable
    public String currentTaskName() {
        return this.currentTaskName;
    }


    /**
     * Return the time taken by the last task.
     *
     * @return  结果
     * @throws IllegalStateException IllegalStateException
     */
    public long getLastTaskTimeMillis() throws IllegalStateException {
        if (this.lastTaskInfo == null) {
            throw new IllegalStateException("No tasks run: can't get last task interval");
        }
        return this.lastTaskInfo.getTimeMillis();
    }

    /**
     * Return the name of the last task.
     *
     * @return 结果
     * @throws IllegalStateException IllegalStateException
     */
    public String getLastTaskName() throws IllegalStateException {
        if (this.lastTaskInfo == null) {
            throw new IllegalStateException("No tasks run: can't get last task name");
        }
        return this.lastTaskInfo.getTaskName();
    }

    /**
     * Return the last task as a TaskInfo object.
     *
     * @return 结果
     * @throws IllegalStateException IllegalStateException
     */
    public TaskInfo getLastTaskInfo() throws IllegalStateException {
        if (this.lastTaskInfo == null) {
            throw new IllegalStateException("No tasks run: can't get last task info");
        }
        return this.lastTaskInfo;
    }


    /**
     * Return the total time in milliseconds for all tasks.
     *
     * @return 结果
     */
    public long getTotalTimeMillis() {
        return this.totalTimeMillis;
    }

    /**
     * Return the total time in seconds for all tasks.
     *
     *
     * @return 结果
     */
    public double getTotalTimeSeconds() {
        return this.totalTimeMillis / 1000.0;
    }

    /**
     * Return the number of tasks timed.
     *
     * @return 结果
     */
    public int getTaskCount() {
        return this.taskCount;
    }

    /**
     * Return an array of the data for tasks performed.
     *
     * @return 结果
     */
    public TaskInfo[] getTaskInfo() {
        if (!this.keepTaskList) {
            throw new UnsupportedOperationException("Task info is not being kept!");
        }
        return this.taskList.toArray(new TaskInfo[0]);
    }


    /**
     * Return a short description of the total running time.
     *
     * @return  结果
     */
    public String shortSummary() {
        return "StopWatch '" + getId() + "': running time (millis) = " + getTotalTimeMillis();
    }

    /**
     * Return a string with a table describing all tasks performed.
     * For custom reporting, call getTaskInfo() and use the task info directly.
     *
     * @return  结果
     */
    public String prettyPrint() {
        StringBuilder sb = new StringBuilder(shortSummary());
        sb.append('\n');
        if (!this.keepTaskList) {
            sb.append("No task info kept");
        }
        else {
            sb.append("-----------------------------------------\n");
            sb.append("ms     %     Task name\n");
            sb.append("-----------------------------------------\n");
            NumberFormat nf = NumberFormat.getNumberInstance();
            nf.setMinimumIntegerDigits(5);
            nf.setGroupingUsed(false);
            NumberFormat pf = NumberFormat.getPercentInstance();
            pf.setMinimumIntegerDigits(3);
            pf.setGroupingUsed(false);
            for (TaskInfo task : getTaskInfo()) {
                sb.append(nf.format(task.getTimeMillis())).append("  ");
                sb.append(pf.format(task.getTimeSeconds() / getTotalTimeSeconds())).append("  ");
                sb.append(task.getTaskName()).append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * Return an informative string describing all tasks performed
     * For custom reporting, call {@code getTaskInfo()} and use the task info directly.
     *
     * @return 结果
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(shortSummary());
        if (this.keepTaskList) {
            for (TaskInfo task : getTaskInfo()) {
                sb.append("; [").append(task.getTaskName()).append("] took ").append(task.getTimeMillis());
                long percent = Math.round((100.0 * task.getTimeSeconds()) / getTotalTimeSeconds());
                sb.append(" = ").append(percent).append("%");
            }
        }
        else {
            sb.append("; no task info kept");
        }
        return sb.toString();
    }


    /**
     * Inner class to hold data about one task executed within the stop watch.
     */
    public static final class TaskInfo {

        private final String taskName;

        private final long timeMillis;

        TaskInfo(String taskName, long timeMillis) {
            this.taskName = taskName;
            this.timeMillis = timeMillis;
        }

        /**
         * Return the name of this task.
         *
         * @return name
         */
        public String getTaskName() {
            return this.taskName;
        }

        /**
         * Return the time in milliseconds this task took.
         *
         * @return  timeMillis
         */
        public long getTimeMillis() {
            return this.timeMillis;
        }

        /**
         * Return the time in seconds this task took.
         *
         * @return timeSeconds
         */
        public double getTimeSeconds() {
            return (this.timeMillis / 1000.0);
        }
    }

}
