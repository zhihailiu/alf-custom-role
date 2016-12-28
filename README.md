# Alfresco Custom Role

## Use Case
Allow Collaborator to delete documents 

## Solution - Create new site role by extending SiteCollaboator
1. acmePermissionDefinitions.xml
Define new/custom role SiteAcmeCollaborator = SiteCollaborator + Delete permission
2. bootstrap-context.xml
Bootstrap custom permission definitions
3. alf-custom-role-amp.properties
Add labels for new site role

## Test
Add user "abeecher" to SiteAcmeCollabortor group. Login as "abeecher" and be able to see "Move to" and "Delete document" menus in Document List and Document Details views.

### Note
1. Must include existing roles in permissionSet definition for st:site, not just the new role.
2. Alfresco uses permissionSet st:site definition as a template. So the new site role behaves exactly the same as the default ones.
- it gets created when a new site is created
- it shows up in Manage Permissions
- it shows up in Set roles in Site Membership > Add User
