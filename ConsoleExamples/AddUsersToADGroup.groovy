/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package ConsoleExamples

import org.evisaenkov.atlassian.library.LdapOperations
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.LogManager

Logger logger = LogManager.getLogger("org.evisaenkov.atlassian")
LdapOperations ldapOperations = new LdapOperations()

String groupName = 'adGroup'
String spaceName = 'OU=Synchronized'
String connection = 'corporate'
String username = "testUser"

logger.warn('Getting group info')
Map group = ldapOperations.getGroupDNByName(groupName, spaceName, connection)
String groupDN = group[0]['dn']
logger.warn('Getting user info')
Map user = ldapOperations.getInfoByUsername(['login': username], connection)
String userDN = user[0]['dn']
logger.warn('Adding user')
Map result = ldapOperations.addUserToGroup(userDN, groupDN, connection)
logger.warn("Adding ${username} to ${groupName}: ${result}")
