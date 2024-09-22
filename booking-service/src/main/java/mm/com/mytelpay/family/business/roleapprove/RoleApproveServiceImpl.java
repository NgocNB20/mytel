package mm.com.mytelpay.family.business.roleapprove;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mm.com.mytelpay.family.business.BookingBaseBusiness;
import mm.com.mytelpay.family.business.roleapprove.dto.FilterRoleApproveReqDTO;
import mm.com.mytelpay.family.enums.BookingType;
import mm.com.mytelpay.family.model.RoleApprove;
import mm.com.mytelpay.family.repo.RoleApproveRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Log4j2
@AllArgsConstructor
@Service
public class RoleApproveServiceImpl extends BookingBaseBusiness implements RoleApproveService {

    @Autowired
    RoleApproveRepository roleApproveRepository;

    @Override
    public List<RoleApprove> filterRoleApprove(FilterRoleApproveReqDTO reqDTO, HttpServletRequest httpServletRequest) {
        BookingType bookingType = StringUtils.isBlank(reqDTO.getType()) ? null : BookingType.valueOf(reqDTO.getType());
        return roleApproveRepository.filterRoleApprove(
                bookingType,
                reqDTO.getIsAssign(),
                reqDTO.getLevel(),
                reqDTO.getRoleId()
        );
    }
}
