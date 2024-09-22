//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package mm.com.mytelpay.family.business.sms;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ISmsGW {
    @POST("smppgw/v1.0/action/submit")
    Call<ResponseBody> sendSMPP(@Body Object data);
}
