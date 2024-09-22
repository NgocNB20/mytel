package mm.com.mytelpay.family.business;

import mm.com.mytelpay.family.business.otp.OTPService;
import mm.com.mytelpay.family.business.otp.SendOTPReqDTO;
import mm.com.mytelpay.family.business.otp.VerifyOTPReqDTO;
import mm.com.mytelpay.family.business.sms.SMSReqDTO;
import mm.com.mytelpay.family.business.sms.SMSService;
import mm.com.mytelpay.family.exception.error.BusinessEx;
import mm.com.mytelpay.family.models.dto.CommonResponseDTO;
import mm.com.mytelpay.family.utils.Translator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseBusiness {
    public Logger logger = LogManager.getLogger(this.getClass().getSimpleName());

    public BaseBusiness() {
    }

    @Autowired
    private OTPService otpService;

    @Autowired
    private SMSService smsService;

    public CommonResponseDTO generateDefaultResponse(String requestId, Object result, Object... objects) {
        CommonResponseDTO commonResponse = new CommonResponseDTO("00000", Translator.toLocale("00000"), result);
        commonResponse.setRequestId(requestId);
        return commonResponse;
    }

    public void sendSMS(String msisdn, String content) {
        SMSReqDTO smsReqDTO = new SMSReqDTO();
        smsReqDTO.setNumber(msisdn);
        smsReqDTO.setContent(content);
        logger.info("Send message: " + content + " to " + msisdn);
        smsService.sendSMS(smsReqDTO);
    }

    public void generateOTP(String msisdn, String requestId, String mobileDevice, Boolean isTOTP) {
        try {
            SendOTPReqDTO sendOTPReqDTO = this.genOTPRequest(msisdn, requestId);
            sendOTPReqDTO.setMsisdn(msisdn);
            sendOTPReqDTO.setRequestId(requestId);
            sendOTPReqDTO.setIsTOTP(isTOTP);
            if (mobileDevice != null) {
                sendOTPReqDTO.setMobileDevice(mobileDevice);
            }

            otpService.generateOTP(sendOTPReqDTO);
        }catch (Exception e){
            throw new BusinessEx("12906", "12906");
        }
    }

    public SendOTPReqDTO genOTPRequest(String msisdn, String requestId) {
        SendOTPReqDTO sendOTPReqDTO = new SendOTPReqDTO();
        sendOTPReqDTO.setRequestId(requestId);
        sendOTPReqDTO.setMsisdn(msisdn);
        sendOTPReqDTO.setAccountId((String)null);
        sendOTPReqDTO.setIsTOTP(Boolean.FALSE);
        sendOTPReqDTO.setCurrentTime(String.valueOf(System.currentTimeMillis()));
        return sendOTPReqDTO;
    }

    public boolean verifyOTP(String msisdn, String otp, String requestId, String mobileDevice, Boolean isTOTP) {
        VerifyOTPReqDTO verifyOTPReqDTO = new VerifyOTPReqDTO();
        verifyOTPReqDTO.setMsisdn(msisdn);
        verifyOTPReqDTO.setOtp(otp);
        verifyOTPReqDTO.setRequestId(requestId);
        verifyOTPReqDTO.setIsTOTP(isTOTP);
        verifyOTPReqDTO.setCurrentTime(String.valueOf(System.currentTimeMillis()));
        if (mobileDevice != null) {
            verifyOTPReqDTO.setMobileDevice(mobileDevice);
        }

        if (!otpService.verifyOTP(verifyOTPReqDTO)) {
            throw new BusinessEx("12014", "12014");
        } else {
            return true;
        }
    }
}
