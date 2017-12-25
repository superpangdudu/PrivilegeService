package com.plus3.privilege.manager;

import com.plus3.privilege.dao.AdminDetails;
import com.plus3.privilege.dao.PermissionDetails;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;

/**
 *
 */
public class GrantedDateReader {

    @Autowired private SimpleDataReader simpleDataReader;

    public <T> List<T> read(AdminDetails adminDetails,
                            PermissionDetails permissionDetails,
                            String table, Class<T> clazz, int from, int limit) {
        // super administrator
        if (adminDetails.getAdmin().getId() == 0)
            return simpleDataReader.simpleQuery(table, clazz, from, limit);

        // no permission details specified
        if (permissionDetails == null)
            return simpleDataReader.simpleQuery(table, clazz,
                    adminDetails.getAdmin().getGroupToken(), true,
                    from, limit);

        Collection<String> grantedGroupList = permissionDetails.getGrantedGroups();
        if (grantedGroupList == null)
            return simpleDataReader.simpleQuery(table, clazz,
                    adminDetails.getAdmin().getGroupToken(), true,
                    from, limit);

        if (grantedGroupList.size() == 1) {
            if (grantedGroupList.toArray()[0].equals("*"))
                return simpleDataReader.simpleQuery(table, clazz,
                        adminDetails.getAdmin().getGroupToken(), true,
                        from, limit);
            else if (grantedGroupList.toArray()[0].equals("self"))
                return simpleDataReader.simpleQuery(table, clazz,
                        adminDetails.getAdmin().getId(),
                        from, limit);
            else if (grantedGroupList.toArray()[0].equals("group"))
                return simpleDataReader.simpleQuery(table, clazz,
                        adminDetails.getAdmin().getGroupToken(), false,
                        from, limit);
        }

        return simpleDataReader.simpleQuery(table, clazz, grantedGroupList, from, limit);
    }
}
