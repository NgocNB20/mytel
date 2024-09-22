package mm.com.mytelpay.family.business.role;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mm.com.mytelpay.family.business.AccountBaseBusiness;
import mm.com.mytelpay.family.business.role.dto.AddRoleReqDTO;
import mm.com.mytelpay.family.business.role.dto.EditRoleReqDTO;
import mm.com.mytelpay.family.business.role.dto.FilterRoleReqDTO;
import mm.com.mytelpay.family.business.role.dto.FilterRoleResDTO;
import mm.com.mytelpay.family.enums.RoleType;
import mm.com.mytelpay.family.exception.AccountErrorCode;
import mm.com.mytelpay.family.exception.error.BusinessEx;
import mm.com.mytelpay.family.model.entities.AccountRole;
import mm.com.mytelpay.family.model.entities.Role;
import mm.com.mytelpay.family.models.dto.SimpleRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static mm.com.mytelpay.family.utils.PageableUtils.pageable;

@Log4j2
@AllArgsConstructor
@Service
public class RoleServiceImpl extends AccountBaseBusiness implements RoleService {

    @Override
    public boolean addRole(AddRoleReqDTO reqDTO) {
        roleRepository.findRoleByCode(RoleType.valueOf(reqDTO.getCode())).ifPresent(role ->{
            throw new BusinessEx(reqDTO.getRequestId() , AccountErrorCode.Role.ROLE_CODE_WAS_EXISTS, null);
        });
        Role role = mapper.map(reqDTO , Role.class);
        role.setId(UUID.randomUUID().toString());
        roleRepository.save(role);
        return true;
    }

    @Override
    public boolean deleteRole(SimpleRequest request) {
        Role role = roleRepository.findRoleById(request.getId()).orElseThrow(() -> {
            throw new BusinessEx(request.getRequestId() , AccountErrorCode.Role.ROLE_DOES_NOT_EXISTS , null);
        });
        Optional<AccountRole> accountRole = accountRoleRepository.findByRoleId(request.getId());
        if(!accountRole.isEmpty()){
            throw new BusinessEx(request.getRequestId() , AccountErrorCode.Role.ROLE_ASSIGNED_TO_THE_USER , null);
        }else {
            roleRepository.delete(role);
        }
        return true;
    }


    @Override
    public Page<FilterRoleResDTO> getList(FilterRoleReqDTO reqDTO) {
        Pageable pageable = pageable(reqDTO.getPageIndex(), reqDTO.getPageSize() , reqDTO.getSortBy() , reqDTO.getSortOrder());
        Page<FilterRoleResDTO> resDTOPage =  roleRepository.getList(
                reqDTO.getName(),
                StringUtils.isBlank(reqDTO.getCode()) ? null : RoleType.valueOf(reqDTO.getCode()),
                pageable
        );
        if(resDTOPage.getContent().isEmpty()){
            throw new BusinessEx(reqDTO.getRequestId() , AccountErrorCode.Role.NO_DATA_FOUND_FOR_THE_CURRENT_PARAMETERS , null);
        }
        List<FilterRoleResDTO> list = resDTOPage.getContent();
        return new PageImpl<>(list , pageable , resDTOPage.getTotalElements());
    }

    @Override
    public List<FilterRoleResDTO> getAll() {
        return roleRepository.getAllWithoutPagination();
    }


    public Role mapRoleEditToRole (EditRoleReqDTO reqDTO , Role role){
        if (StringUtils.isNotBlank(reqDTO.getName())){
            role.setName(reqDTO.getName());
        }
        return role;
    }

}
