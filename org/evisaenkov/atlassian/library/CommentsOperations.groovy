/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package org.evisaenkov.atlassian.library

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.entity.property.JsonEntityPropertyManager
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.comments.Comment
import com.atlassian.jira.issue.comments.CommentManager
import com.atlassian.jira.user.ApplicationUser
import org.evisaenkov.atlassian.library.UserOperations
import java.util.function.BiFunction

/**
 * Class for comments operations with SR
 * @author Evgeniy Isaenkov
 */
class CommentsOperations {
	
	CommentManager commentManager = ComponentAccessor.getCommentManager()
	UserOperations userOperations = new UserOperations()
	JsonEntityPropertyManager jsonManager = ComponentAccessor.getComponent(JsonEntityPropertyManager)
	ApplicationUser techUser = userOperations.getTechUser()
	
	void addComment(MutableIssue issue, String comment, Boolean event = true) {
		commentManager.create(issue, techUser, comment, event)
	}
	
	void addInternalComment(MutableIssue issue, String comment, Boolean event = true) {
		Comment newComment = commentManager.create(issue, techUser, comment, event)
		jsonManager.put(techUser, "sd.comment.property", newComment.getId(), "sd.public.comment", "{ \"internal\" : true}", (BiFunction) null, false)
	}
	
	void addCommentByCurrentUser(MutableIssue issue, String comment, Boolean event = true) {
		ApplicationUser user = userOperations.getCurrentUser()
		commentManager.create(issue, user, comment, event)
	}
	
	void deleteCommentsForIssue(MutableIssue issue) {
		commentManager.deleteCommentsForIssue(issue)
	}
	
	Collection<String> getAllCommentsBody(MutableIssue issue) {
		return commentManager.getComments(issue)*.getBody()
	}
	
	List<Comment> getAllComments(MutableIssue issue) {
		return commentManager.getComments(issue)
	}
	
}
