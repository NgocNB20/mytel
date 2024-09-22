package mm.com.mytelpay.family.business.rolehasfunction;


import mm.com.mytelpay.family.business.role.dto.SimpleIdRole;
import mm.com.mytelpay.family.business.rolehasfunction.dto.PerRoleDTO;
import mm.com.mytelpay.family.business.rolehasfunction.dto.RoleDetailDTO;

import javax.servlet.http.HttpServletRequest;

public interface RoleHasFunctionService {
    boolean perRole (PerRoleDTO functionIdDTO, HttpServletRequest httpServletRequest);

    RoleDetailDTO getInfoPerRole (SimpleIdRole simpleIdRole, HttpServletRequest httpServletRequest);
}
