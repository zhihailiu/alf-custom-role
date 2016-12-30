# Alfresco Customize SiteCollaborator Role II

## Use Case
Allow Collaborator to delete documents 

## Solution - Create new site role by extending SiteCollaboator
1. acmePermissionDefinitions.xml
Define new/custom role SiteAcmeCollaborator = SiteCollaborator + Delete permission
Note that permissionSet of st:site, the permission definitions must include original roles (SiteManager etc), as well as the new role.
2. bootstrap-context.xml
Bootstrap custom permission definitions
3. alf-custom-role-amp.properties
Add labels for new site role in Share

### New Site vs Existing Site
Alfresco uses permissionSet st:site definition as a template. For a new site created after this module is deployed, this role behaves exactly the same as the default ones:
- it gets created when a new site is created.
- it shows up in Manage Permissions
- it shows up in Set roles in Site Membership > Add User

However, an old site, for example swsdp, created before this module is deployed will not have the role. You can use a webscript to create a group, then use JavaScript to set the permission at site level.

1. Add group
http://localhost:8080/alfresco/s/com/acme/group/groupzone
POST
application/json
```
{
    "shortName": "site_swsdp_SiteAcmeCollaborator",
    "displayName": "site_swsdp_SiteAcmeCollaborator",
    "parentShortName": "site_swsdp",
    "zones": ["AUTH.ALF", "APP.SHARE"]
}
```
Note that the zones must be set as ["AUTH.ALF", "APP.SHARE"]. The zones are different from groups manually created in Share Admin Tools. They belong to APP.DEFAULT zone.

2. Set permission
```
var swsdpSite = search.findNode("workspace://SpacesStore/..."); // nodeRef of site swsdp
swsdpSite.setPermission("SiteAcmeCollaborator", "GROUP_site_swsdp_SiteAcmeCollaborator");
```

### Test
Add user "abeecher" to SiteAcmeCollabortor group. Login as "abeecher" and be able to see "Move to" and "Delete document" menus in Document List and Document Details views.
