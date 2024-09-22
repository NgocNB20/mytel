package mm.com.mytelpay.family.business.rolehasfunction;

import mm.com.mytelpay.family.business.AccountBaseBusiness;
import mm.com.mytelpay.family.business.role.dto.SimpleIdRole;
import mm.com.mytelpay.family.business.rolehasfunction.dto.FunctionPerRole;
import mm.com.mytelpay.family.business.rolehasfunction.dto.PerRoleDTO;
import mm.com.mytelpay.family.business.rolehasfunction.dto.RoleDetailDTO;
import mm.com.mytelpay.family.exception.AccountErrorCode;
import mm.com.mytelpay.family.exception.error.BusinessEx;
import mm.com.mytelpay.family.model.entities.Function;
import mm.com.mytelpay.family.model.entities.Role;
import mm.com.mytelpay.family.model.entities.RoleHasFunction;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RoleHasFunctionIml extends AccountBaseBusiness implements RoleHasFunctionService{

    @Override
    public boolean perRole(PerRoleDTO functionIdDTO , HttpServletRequest httpServletRequest) {
        Role role = roleRepository.findRoleById(functionIdDTO.getRoleId()).orElseThrow(() -> {
            throw new BusinessEx(functionIdDTO.getRoleId() , AccountErrorCode.Role.ROLE_DOES_NOT_EXISTS, null);
        });
        List<RoleHasFunction> roleHasFunctions = roleHasFunctionRepository.findByRoleId(role.getId());
        roleHasFunctionRepository.deleteAll(roleHasFunctions);
        for (int i = 0 ; i < functionIdDTO.getFunctionIdList().size() ; i++){
            Optional<Function> function = functionRepository.findById(functionIdDTO.getFunctionIdList().get(i));
            if(function.isEmpty()) {
                continue;
            }
            RoleHasFunction roleHasFunction = new RoleHasFunction();
            roleHasFunction.setRoleId(functionIdDTO.getRoleId() );
            roleHasFunction.setFunctionId(functionIdDTO.getFunctionIdList().get(i));
            roleHasFunctionRepository.save(roleHasFunction);
        }
        return true;
    }

    @Override
    public RoleDetailDTO getInfoPerRole(SimpleIdRole request, HttpServletRequest httpServletRequest) {
        Role role = roleRepository.findRoleById(request.getRoleId()).orElseThrow(() -> {
            throw new BusinessEx(request.getRoleId() , AccountErrorCode.Role.ROLE_DOES_NOT_EXISTS, null);
        });
        RoleDetailDTO roleDetailDTO = new RoleDetailDTO();
        roleDetailDTO.setRoleId(role.getId());
        roleDetailDTO.setRoleCode(role.getCode());
        roleDetailDTO.setRoleName(role.getName());
        List<Function> functions = functionRepository.findAll();
        List<FunctionPerRole> functionPerRoles = new ArrayList<>();
        for (Function function : functions) {
            FunctionPerRole functionPerRole = new FunctionPerRole();
            functionPerRole.setFunctionId(function.getId());
            functionPerRole.setFunctionCode(function.getCode());
            functionPerRole.setFunctionName(function.getName());
            functionPerRole.setChecked(roleHasFunctionRepository.findByRoleIdAndAndFunctionId(request.getRoleId(), function.getId()).isPresent());
            functionPerRole.setParentId(function.getParentId());
            functionPerRoles.add(functionPerRole);
        }
        roleDetailDTO.setPerRole(functionPerRoles);
        return roleDetailDTO;
    }

}
