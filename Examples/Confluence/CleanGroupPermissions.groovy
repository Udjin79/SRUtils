/*
 * Created 2023.
 * @author Evgeniy Isaenkov
 * @github https://github.com/Udjin79/SRUtils
 */

package Examples.Confluence

import com.atlassian.confluence.internal.security.SpacePermissionContext
import com.atlassian.confluence.security.SpacePermission
import com.atlassian.confluence.security.SpacePermissionManager
import com.atlassian.confluence.spaces.Space
import com.atlassian.confluence.spaces.SpaceManager
import com.atlassian.sal.api.component.ComponentLocator

SpacePermissionManager spacePermissionManager = ComponentLocator.getComponent(SpacePermissionManager)
SpaceManager spaceManager = ComponentLocator.getComponent(SpaceManager)

List<Space> all = spaceManager.getAllSpaces()
Set<String> groups = new HashSet<>()
groups.add("confluence-administrators")
groups.add("confluence-users")

for (Space space : all) {
	List<SpacePermission> spacePermissionsToRemove = new ArrayList<>(space.getPermissions())
	for (SpacePermission spacePermission : spacePermissionsToRemove) {
		if (spacePermission.isGroupPermission() && groups.contains(spacePermission.getGroup()))
			spacePermissionManager.removePermission(spacePermission)
	}
}