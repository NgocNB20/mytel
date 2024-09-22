package mm.com.mytelpay.family.business.role;

import mm.com.mytelpay.family.business.role.dto.AddRoleReqDTO;
import mm.com.mytelpay.family.business.role.dto.FilterRoleReqDTO;
import mm.com.mytelpay.family.business.role.dto.FilterRoleResDTO;
import mm.com.mytelpay.family.models.dto.SimpleRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface RoleService {

    boolean addRole (AddRoleReqDTO reqDTO);

    boolean deleteRole(SimpleRequest request);

    Page<FilterRoleResDTO> getList (FilterRoleReqDTO reqDTO);

    List<FilterRoleResDTO> getAll();

}
