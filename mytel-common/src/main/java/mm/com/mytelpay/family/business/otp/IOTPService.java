//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package mm.com.mytelpay.family.business.otp;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface IOTPService {
    @POST("is/v2.0/generateOTP")
    Call<ResponseBody> generateOTP(@Body Object data);

    @POST("is/v2.0/verifyOTP")
    Call<ResponseBody> verifyOTP(@Body Object data);

    @POST("is/v2.0/activate")
    Call<ResponseBody> activate(@Body Object data);

    @POST("is/v2.0/deActivate")
    Call<ResponseBody> deactivate(@Body Object data);

    @POST("is/v2.0/verifyPIN")
    Call<ResponseBody> verifyPIN(@Body Object data);
}
