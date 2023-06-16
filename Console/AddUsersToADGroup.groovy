import org.evisaenkov.atlassian.library.LdapOperations

LdapOperations ldapOperations = new LdapOperations()

String groupName = 'adGroup'
String spaceName = 'OU=Synchronized'
String connection = 'corporate'
String username = "testUser"

log.warn('Getting group info')
Map group = ldapOperations.getGroupDNByName(groupName, spaceName, connection)
String groupDN = group[0]['dn']
log.warn('Getting user info')
Map user = ldapOperations.getInfoByUsername(['login': username], connection)
String userDN = user[0]['dn']
log.warn('Adding user')
Map result = ldapOperations.addUserToGroup(userDN, groupDN, connection)
log.warn("Adding ${username} to ${groupName}: ${result}")
