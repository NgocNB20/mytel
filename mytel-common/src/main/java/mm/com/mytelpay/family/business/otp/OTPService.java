//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package mm.com.mytelpay.family.business.otp;

public interface OTPService {
    void generateOTP(SendOTPReqDTO request);

    boolean verifyOTP(VerifyOTPReqDTO request);
}
