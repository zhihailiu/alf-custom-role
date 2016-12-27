# Alfresco Custom Role

## Use Case
Allow Collaborator to delete documents 

## Solution 1 - Create Custom Role
- Define a custom role with delete permission
- Create a custom group as sub group of site_xxx_SiteCollaborator (by subgrouping SiteCollaborator, it inherits all Collaborator permissions on this site)
- Set documentLibrary permission to custom role & custom group (Delete permission is inherited)

### Development
1. acmePermissionDefinitions.xml
Define custom role AcmeCollaborator with Delete permission
    <permissionSet type="cm:content" expose="selected">
        <permissionGroup name="AcmeCollaborator" allowFullControl="false" expose="true">
            <includePermissionGroup permissionGroup="Delete" type="sys:base" />
        </permissionGroup>
    </permissionSet>
2. bootstrap
    <bean id="acme-permissionDefinitions" parent="permissionModelBootstrap">
        <property name="model" value="alfresco/module/${project.artifactId}/model/acmePermissionDefinitions.xml" />
    </bean>

### Configuration
1. Create site Sandbox
2. Admin Tools > Groups > add subgroup site_sandbox_SiteAcmeCollaborator to site_sandbox_SiteCollaborator
3. Admin Tools > Users > add user bwayne to group site_sandbox_SiteAcmeCollaborator
4. Admin Tools > JavaScript Console, add permission to AcmeCollaborator for documentLibrary of Site sandbox

var nodeRef= "workspace://SpacesStore/e02b0912-4890-485c-9e8c-9ebf45b9de29";  // nodeRef of documentLibrary of sandbox
var node = search.findNode(nodeRef); 
var permissions = node.getPermissions();
node.setPermission("AcmeCollaborator", "GROUP_site_sandbox_SiteAcmeCollaborator");
logger.log(permissions);

5. login as bwayne and check
5.1 In Document List view, Selected Items > Move to & Delete menus 
5.2 In Document Details view, Move & Delete menus

Con:
+ manual post deployment configuration
+ configuration has to be done through Admin Tool (JavaScript Console, Groups & Users) by administrator
+ configuration required per site

