//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package mm.com.mytelpay.family.business.otp;

public interface SmartOTP {
    void generateOTP(SendOTPReqDTO request);

    boolean verifyOTP(VerifyOTPReqDTO request);

    boolean activeSmartOTP(ActiveReqDTO request);

    boolean deActiveSmartOTP(DeactivateReqDTO request);

    boolean verifyPIN(VerifySmartOTPPINReqDTO request);
}
