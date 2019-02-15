import ch.qos.logback.classic.Level
import ch.qos.logback.classic.encoder.PatternLayoutEncoder

import java.nio.charset.Charset

def appenderList = []

appender("CONSOLE", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        charset = Charset.forName("UTF-8")
        pattern = "%d %-5level [ %t ] %class{36}.%M:%L | %m %n"
    }
}
appenderList << "CONSOLE"

root(Level.INFO, appenderList)
