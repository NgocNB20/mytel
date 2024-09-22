package mm.com.mytelpay.family.business.otp;

import mm.com.mytelpay.family.business.sms.SMSService;
import mm.com.mytelpay.family.config.http.CommonServiceGenerator;
import mm.com.mytelpay.family.config.http.HttpConfig;
import okhttp3.ResponseBody;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.Map;

@Service
public final class SmartOTPImpl extends CommonServiceGenerator implements OTPService, SmartOTP {
    final OTPConfig otpConfig;
    private static final Logger logger = LogManager.getLogger(SMSService.class.getName());

    public SmartOTPImpl(OTPConfig otpConfig) {
        this.otpConfig = otpConfig;
    }

    @Autowired
    public void initParam() {
        HttpConfig httpConfig = new HttpConfig(this.otpConfig.getUrl(), this.otpConfig.getConnectionTimeout(), this.otpConfig.getConnectionTimeout(), this.otpConfig.getConnectionTimeout(), (Map)null);
        this.setHttpConfig(httpConfig);
        this.retrofitService = this.buildRetrofit(false);
    }

    public void generateOTP(SendOTPReqDTO request) {
        IOTPService iTelegram = (IOTPService)this.createService(IOTPService.class);
        Call<ResponseBody> callGetInfo = iTelegram.generateOTP(request);
        callGetInfo.enqueue(new Callback<ResponseBody>() {
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                SmartOTPImpl.logger.info("success");
            }

            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                SmartOTPImpl.logger.info("send otp failed");
            }
        });
    }

    public boolean verifyOTP(VerifyOTPReqDTO request) {
        IOTPService iTelegram = (IOTPService)this.createService(IOTPService.class);
        Call<ResponseBody> callGetInfo = iTelegram.verifyOTP(request);
        boolean isValid = false;

        try {
            Response<ResponseBody> response = callGetInfo.execute();
            if (response.isSuccessful()) {
                isValid = true;
            }
        } catch (Exception var6) {
            var6.printStackTrace();
        }

        return isValid;
    }

    public boolean activeSmartOTP(ActiveReqDTO request) {
        IOTPService iTelegram = (IOTPService)this.createService(IOTPService.class);
        Call<ResponseBody> call = iTelegram.activate(request);

        try {
            Response<ResponseBody> response = call.execute();
            if (response.code() == 200) {
                return true;
            }
        } catch (Exception var5) {
            var5.printStackTrace();
        }

        return false;
    }

    public boolean deActiveSmartOTP(DeactivateReqDTO request) {
        IOTPService iTelegram = (IOTPService)this.createService(IOTPService.class);
        Call<ResponseBody> call = iTelegram.deactivate(request);

        try {
            Response<ResponseBody> response = call.execute();
            if (response.code() == 200) {
                return true;
            }
        } catch (Exception var5) {
            var5.printStackTrace();
        }

        return false;
    }

    public boolean verifyPIN(VerifySmartOTPPINReqDTO request) {
        IOTPService iotpService = (IOTPService)this.createService(IOTPService.class);
        Call<ResponseBody> call = iotpService.verifyPIN(request);

        try {
            Response<ResponseBody> response = call.execute();
            if (response.code() == 200) {
                return true;
            }
        } catch (Exception var5) {
            var5.printStackTrace();
        }

        return false;
    }

    public OTPConfig getOtpConfig() {
        return this.otpConfig;
    }
}
