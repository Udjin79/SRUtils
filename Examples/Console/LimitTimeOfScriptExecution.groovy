/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.Console

import groovy.transform.TimedInterrupt

import java.sql.Timestamp
import java.util.concurrent.TimeUnit

@TimedInterrupt (value = 1L, unit = TimeUnit.SECONDS)
def doSomething() {
	Timestamp startTime = new Date().time as Timestamp
	wait(10000)
	Timestamp finishTime = new Date().time as Timestamp
	log.warn("Time passed - ${finishTime - startTime}")
}

