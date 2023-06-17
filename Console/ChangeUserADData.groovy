import com.onresolve.scriptrunner.ldap.LdapUtil

import javax.naming.directory.BasicAttribute
import javax.naming.directory.DirContext
import javax.naming.directory.ModificationItem

Map modifyUserAttributes(String userDn, Map changeData, String connection) {
	Map ldapResult = [:]
	
	ModificationItem[] mods = new ModificationItem[changeData.size()]
	Integer i = 0
	changeData.each { entry ->
		mods[i] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
				new BasicAttribute(entry.key.toString(), entry.value.toString()))
	}
	
	LdapUtil.withTemplate(connection) { template ->
		try {
			template.modifyAttributes(userDn, mods)
			ldapResult = ['success': 'Operation completed successfully']
		} catch (Exception ignored) {
			ldapResult = ['error': ignored]
		}
	}
	return ldapResult
}

String userDN = "CN=tester,OU=Users,OU=Service Accounts,OU=Synchronized,DC=corp,DC=example,DC=com"
Map changeData = [
		'title': 'TEST'
]

modifyUserAttributes(userDN, changeData, "corporate")
