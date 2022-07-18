# Stopwatch
Simple stopwatch application I use to track time and display it in a Polybar module.

# Usage
Compile the application and run the jar using `java -jar <jarname>.jar`

The application uses the following arguments
- `start` - starts the stopwatch.
- `status` - shows the elapsed time.
  - To change the prefix icon shown in front of the timer, use the `--prefix` parameter. 
    - Usage example: `--prefix=`
- `stop` or `reset` - resets the stopwatch back to 00:00.
  - If you're using dunst to handle notifications, the `--notif-icon` parameter is available to change the icon the notification uses. 
    - Usage example: `--notif-icon=/path/to/your/icon.png`
- Running the application without arguments will pause the stopwatch if running, and start it if it isn't.



## Usage with Polybar
The following code block can be used to show the stopwatch on your polybar. Left click starts/pauses the stopwatch, and the middle click resets it completely. The interval is set to 0.2 so it doesn't accasionally skip a number or count up too fast.

Make sure to change the paths to where you save the jar/script obviously.
```
[module/stopwatch]
type = custom/script
click-left = exec ~/.config/polybar/scripts/stopwatch.sh pause
click-middle = exec ~/.config/polybar/scripts/stopwatch.sh reset
exec = ~/.config/polybar/scripts/stopwatch.sh status
label = "%output%"
interval = 0.2
```

The script I call in the polybar config only specifies a specific Java version, as the default system wide version is lower than I compiled the jar with.
```sh
#!/bin/sh
export PATH="/usr/lib/jvm/java-18-openjdk/bin/:$PATH"
exec java -jar "/home/dan/.config/polybar/scripts/stopwatch.jar" "$@"
```

# Todo
- [ ] Allow customizing of timezone
- [ ] Clean up main.java and split logic up into multiple methods/classes

# Libraries used:
- [Project Lombok](https://projectlombok.org/)
