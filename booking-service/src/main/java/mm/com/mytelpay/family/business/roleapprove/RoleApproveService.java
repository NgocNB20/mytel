package mm.com.mytelpay.family.business.roleapprove;

import mm.com.mytelpay.family.business.roleapprove.dto.FilterRoleApproveReqDTO;
import mm.com.mytelpay.family.model.RoleApprove;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface RoleApproveService {

    List<RoleApprove> filterRoleApprove(FilterRoleApproveReqDTO reqDTO, HttpServletRequest httpServletRequest);

}
