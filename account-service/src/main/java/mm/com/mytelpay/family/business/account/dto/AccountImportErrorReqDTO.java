package mm.com.mytelpay.family.business.account.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountImportErrorReqDTO {
    private AccountImportReqDTO accountImportReqDTO;
    private String message;
}
