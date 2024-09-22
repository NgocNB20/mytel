package mm.com.mytelpay.family.business.account.service;

import mm.com.mytelpay.family.business.balance.dto.GetBalanceHistoriesReqDTO;
import mm.com.mytelpay.family.business.balance.dto.GetBalanceHistoriesResDTO;
import mm.com.mytelpay.family.business.balance.dto.TopupReqDTO;
import mm.com.mytelpay.family.business.balance.dto.UpdateBalanceReqDTO;
import mm.com.mytelpay.family.enums.BalanceActionType;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

public interface AccountBalanceService {

    boolean topupBalance(@Valid @RequestBody TopupReqDTO request, HttpServletRequest httpServletRequest);

    boolean updateBalance(UpdateBalanceReqDTO request, HttpServletRequest httpServletRequest);

    Page<GetBalanceHistoriesResDTO> getBalanceHistories(GetBalanceHistoriesReqDTO request, List<BalanceActionType> actionTypeList, HttpServletRequest httpServletRequest);

    Page<GetBalanceHistoriesResDTO> getTopupHistories(GetBalanceHistoriesReqDTO request, List<BalanceActionType> actionTypeList, HttpServletRequest httpServletRequest);


}
