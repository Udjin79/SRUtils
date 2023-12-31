/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.Console

import com.atlassian.greenhopper.service.sprint.Sprint
import com.atlassian.jira.issue.MutableIssue
import org.evisaenkov.atlassian.library.IssueOperations
import org.evisaenkov.atlassian.library.BoardsOperations
import org.evisaenkov.atlassian.library.SprintsOperations

IssueOperations issueOperations = new IssueOperations()
BoardsOperations boardsOperations = new BoardsOperations()
SprintsOperations sprintsOperations = new SprintsOperations()

// Enter the name of the board to which you want to add the issue to the first active sprint
long rapidBoardId = 100L
long sprintId = 1234

MutableIssue issue = issueOperations.getIssue('TEST-1234')
Collection<Sprint> sprintsList = sprintsOperations.getSprintsForView(boardsOperations.getBoard(rapidBoardId))
Sprint sprint = sprintsList.find { Sprint entry ->
	entry.id == sprintId
}

if (sprint) {
	sprintsOperations.moveIssuesToSprint(issue, sprint)
}

