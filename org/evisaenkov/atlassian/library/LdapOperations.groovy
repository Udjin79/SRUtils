/*
 * Copyright (c) 2022-2023.
 * @author Evgeniy Isaenkov
 */

package org.evisaenkov.atlassian.library

import com.onresolve.scriptrunner.ldap.LdapUtil
import org.springframework.ldap.core.AttributesMapper

import javax.naming.directory.*

/**
 * Basic methods to work with LDAP
 */
class LdapOperations {
	
	Map ldapResult
	String baseDN = 'OU=Users,OU=Synchronized'
	
	/**
	 * Retrieving user information from LDAP by his full or partial login
	 * @param query - format ['login':'username'] for user login (full or partial) or ['displayName':'User Name'] for user display name (full or partial)
	 * @param connection - connection, used to connect to LDAP
	 * @return - mapped data, with user(s) information
	 */
	Map getInfoByUsername(Map query, String connection) {
		if (query['baseDN']) {
			baseDN = query['baseDN']
		}
		
		GString queryString = query.login == null ? "(CN=*${query.displayName}*)" : "(samaccountname=*${query.login}*)"
		List<BasicAttributes> results = LdapUtil.withTemplate(connection) { item ->
			item.search(baseDN, queryString, SearchControls.SUBTREE_SCOPE, { BasicAttributes attributes ->
				attributes
			} as AttributesMapper<BasicAttributes>)
		}
		
		Map<Object, Object> result = [:]
		
		result = results.collect { BasicAttributes user ->
			[
					username  : user.get('samaccountname').get(0).toString(),
					memberOf  : user.get('memberof').getAll()*.replaceAll(/(CN=)(.*?),.*/, /$2/),
					fullName  : user.get('name') ? user.get('name').get(0).toString() : null,
					title     : user.get('title') ? user.get('title').get(0).toString() : null,
					email     : user.get('mail') ? user.get('mail').get(0).toString() : null,
					mobile    : user.get('mobile') ? user.get('mobile').get(0).toString() : null,
					company   : user.get('company') ? user.get('company').get(0).toString() : null,
					department: user.get('department') ? user.get('department').get(0).toString() : null,
					chief     : user.get('manager') != null ? user.get('manager').get(0).toString().replaceAll(/(CN=)(.*?),.*/, /$2/) : null,
					dn        : user.get('distinguishedname') ? user.get('distinguishedname').get(0).toString() : null
			]
		} as Map<Object, Object>
		return result
	}
	
	/**
	 * Retrieving user information from LDAP by his full or partial login
	 * @param query - format ['login':'username'] for user login (full or partial) or ['displayName':'User Name'] for user display name (full or partial)
	 * @param connection - connection, used to connect to LDAP
	 * @return - raw AD BasicAttributes data, with user(s) information
	 */
	BasicAttributes getRawInfoByUsername(Map query, String connection) {
		if (query['baseDN']) {
			baseDN = query['baseDN']
		}
		
		GString queryString = query.login == null ? "(CN=${query.displayName})" : "(samaccountname=${query.login})"
		List<BasicAttributes> results = LdapUtil.withTemplate(connection) { item ->
			item.search(baseDN, queryString, SearchControls.SUBTREE_SCOPE, { BasicAttributes attributes ->
				attributes
			} as AttributesMapper<BasicAttributes>)
		}
		
		return results[0]
	}
	
	String getUsernameByFullName(String fullName) {
		def usersList = LdapUtil.withTemplate('corporate') { template ->
			template.search(baseDN, "(CN=*${fullName}*)", SearchControls.SUBTREE_SCOPE, { BasicAttributes attributes ->
				attributes.get('samaccountname').get(0).toString()
			} as AttributesMapper<String>)
		}
		return usersList[0]
	}
	
	/**
	 * Retrieving group information from LDAP by his full or partial login
	 * @param query - format ['login':'username'] for user login (full or partial) or ['displayName':'User Name'] for user display name (full or partial)
	 * @param connection - connection, used to connect to LDAP
	 * @return - raw AD BasicAttributes data, with user(s) information
	 */
	BasicAttributes getRawInfoByName(Map query, String connection) {
		if (query['baseDN']) {
			baseDN = query['baseDN']
		}
		
		GString queryString = query.login == null ? "(CN=${query.displayName})" : "(samaccountname=${query.login})"
		List<BasicAttributes> results = LdapUtil.withTemplate(connection) { item ->
			item.search(baseDN, queryString, SearchControls.SUBTREE_SCOPE, { BasicAttributes attributes ->
				attributes
			} as AttributesMapper<BasicAttributes>)
		}
		
		return results[0]
	}
	
	/**
	 * Retrieving group DN string except base DN
	 * @param query - group name full
	 * @param spaceDN - space DN, to search for group
	 * @param connection - connection, used to connect to LDAP
	 * @return - mapped data, with group DN information
	 */
	Map getGroupDNByName(String query, String spaceDN, String connection) {
		def results = LdapUtil.withTemplate(connection) { item ->
			item.search(spaceDN, "(&(objectClass=group)(name=${query}))", SearchControls.SUBTREE_SCOPE, { attributes ->
				attributes
			} as AttributesMapper<BasicAttributes>)
		}
		
		Map<Object, Object> result = [:]
		
		result = results.collect { BasicAttributes group ->
			[
					name   : group.get('samaccountname').get(0).toString(),
					members: group.get('member') ? group.get('member').getAll()*.replaceAll(/(CN=)(.*?),.*/, /$2/) : null,
					dn     : group.get('distinguishedname').get(0).toString().replaceAll(/([\s\S]*?)(,DC=corp,DC=example,DC=com)/, /$1/)
			]
		} as Map<Object, Object>
		return result
	}
	
	List<BasicAttributes> getADGroupsInfoBySpaceDN(String spaceDN, String adGroupName = '', String description = '') {
		String query = description == '' ? "(name=*${adGroupName})" : "(name=*)(description=*${description}*)"
		def groupsList = LdapUtil.withTemplate('corporate') { template ->
			template.search(spaceDN, "(&(objectClass=group)${query})", SearchControls.SUBTREE_SCOPE, { BasicAttributes attributes ->
				try {
					attributes
				} catch (Exception e) {
					log.warn(e.getMessage())
				}
			} as AttributesMapper<BasicAttributes>)
		}
		return groupsList
	}
	
	/**
	 * Add user attributes
	 * @param userDn - full user DN in format CN=John Doe,OU=Users,OU=Synchronized,DC=corp,DC=example,DC=com
	 * @param groupDn - group DN in format CN=test-group
	 * @param connection - connection, used to connect to LDAP
	 */
	Map addUserToGroup(String userDn, String groupDn, String connection) {
		ModificationItem[] mods = new ModificationItem[1]
		Attribute mod = new BasicAttribute('member', userDn)
		mods[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE, mod)
		
		LdapUtil.withTemplate(connection) { template ->
			try {
				template.modifyAttributes(groupDn, mods)
				ldapResult = ['success': 'Operation completed successfully']
			} catch (Exception ignored) {
				ldapResult = ['error': ignored]
			}
		}
		return ldapResult
	}
	
	/**
	 * Remove user attributes
	 * @param userDn - full user DN in format CN=John Doe,OU=Users,OU=Synchronized,DC=corp,DC=example,DC=com
	 * @param groupDn - group DN in format CN=test-group
	 * @param connection - connection, used to connect to LDAP
	 */
	Map removeUserFromGroup(String userDn, String groupDn, String connection) {
		ModificationItem[] mods = new ModificationItem[1]
		Attribute mod = new BasicAttribute('member', userDn)
		mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, mod)
		
		LdapUtil.withTemplate(connection) { template ->
			try {
				template.modifyAttributes(groupDn, mods)
				ldapResult = ['success': 'Operation completed successfully']
			} catch (Exception ignored) {
				ldapResult = ['error': ignored]
			}
		}
		return ldapResult
	}
	/**
	 * Modify user attributes
	 * @param userDn - full user DN in format CN=John Doe,OU=Users,OU=Synchronized,DC=corp,DC=example,DC=com
	 * @param connection - connection, used to connect to LDAP
	 * Map changeData = [
	 * 'title':'TEST'
	 * ]
	 */
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
	
	/**
	 *
	 * @param Map <String, String> changeData = [
	 * 'oldUserDN': 'CN=John Doe,OU=IT Support,OU=Services,OU=IAS,OU=Users,OU=Synchronized',
	 * 'newUserDN': 'CN=John Notdoe,OU=IT Support,OU=Services,OU=IAS,OU=Users,OU=Synchronized'
	 * ]
	 * @param connection
	 * @return
	 */
	Map renameUser(Map<String, String> changeData, String connection) {
		Map ldapResult = [:]
		
		LdapUtil.withTemplate(connection) { template ->
			try {
				template.rename(changeData['oldUserDN'], changeData['newUserDN'])
				ldapResult = ['success': 'Operation completed successfully']
			} catch (Exception ignored) {
				ldapResult = ['error': ignored]
			}
		}
		return ldapResult
	}
}
