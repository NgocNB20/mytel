//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package mm.com.mytelpay.family.business.sms;

import mm.com.mytelpay.family.config.http.CommonServiceGenerator;
import mm.com.mytelpay.family.config.http.HttpConfig;
import okhttp3.ResponseBody;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
public class SMSService extends CommonServiceGenerator implements ISendSMS {
    @Autowired
    SMSAdapterConfig smsAdapterConfig;
    private static final Logger logger = LogManager.getLogger(SMSService.class.getName());

    public SMSService() {
    }

    @Autowired
    public void initParameter(SMSAdapterConfig smsAdapterConfig) {
        Map<String, String> header = new HashMap();
        header.put("Authorization", "Basic " + Base64.getEncoder().encodeToString((smsAdapterConfig.getUsername() + ":" + smsAdapterConfig.getPassword()).getBytes()));
        HttpConfig httpConfig = new HttpConfig(smsAdapterConfig.getUrl(), smsAdapterConfig.getConnectTimeout(), smsAdapterConfig.getReadTimeout(), smsAdapterConfig.getWriteTimeout(), header);
        this.setHttpConfig(httpConfig);
        this.retrofitService = this.buildRetrofit(false);
    }

    public void sendSMS(SMSReqDTO request) {
        if (request.getSender() == null) {
            request.setAddress(this.smsAdapterConfig.getSender());
        } else {
            request.setAddress(request.getSender());
        }

        ISmsGW iSmsGW = (ISmsGW)this.createService(ISmsGW.class);
        Call<ResponseBody> call = iSmsGW.sendSMPP(request);
        call.enqueue(new Callback<ResponseBody>() {
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                SMSService.logger.info("success");
            }

            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                SMSService.logger.error("send message error");
            }
        });
    }
}
