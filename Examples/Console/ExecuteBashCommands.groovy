/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.Console

/**
 * Execution on *nix commands is pretty simple, just use method execute(), available for String objects.
 */

// Example to get network config
"ifconfig".execute().text
// Example to get listing of directory
"ls /jira_dir/tmp/".execute().text