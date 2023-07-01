/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.Confluence

import com.atlassian.confluence.user.AuthenticatedUserImpersonator
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal
import com.atlassian.confluence.user.ConfluenceUser
import org.evisaenkov.atlassian.library.ConfluenceOperations
import org.evisaenkov.atlassian.library.Variables

ConfluenceOperations confluenceOperations = new ConfluenceOperations()
ConfluenceUser serviceAccountUser = confluenceOperations.getUserByUserName(Variables.SERVICE_ACCOUNT)

AuthenticatedUserImpersonator.REQUEST_AGNOSTIC.asUser({
	// List of operations, that must be executed under service (or any other) account. For example creation or editing of pages, spaces, etc...
	String currentUserKey = AuthenticatedUserThreadLocal.get().name
	// Here you can see, that current user was set to service account.
	log.warn "Current user is ${currentUserKey}"
}, serviceAccountUser)
