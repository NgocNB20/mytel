package mm.com.mytelpay.family.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class NoNeedAuthorUrls {

//    Note: remember to set the correct position for the endpoints
    private static final Set<String> ACCOUNT_SERVICE_NO_NEED_AUTHOR_URLS = new HashSet<>(Arrays.asList(
            "/user/family/logout",
            "/user/family/verifyForgotPass",
            "/user/family/changePass",
            "/user/family/profile",
            "/user/family/profile/update",
            "/user/family/getPermission",
            "/user/family/lockAccount",
            "/user/listDriver",
            "/user/getDetail",
            "/user/getDevice",
            "/user/getListUser",
            "/user/getAccountReport",
            "/unit/checkUnit",
            "/unit/getListUnit",
            "/unit/getListUnit",
            "/unit/getUnitForChef",
            "/user/getListAccounts"
    ));

    private static final Set<String> BOOKING_SERVICE_NO_NEED_AUTHOR_URLS = new HashSet<>(Arrays.asList(
            "/bookingCar/assign/listCar",
            "/bookingCar/assign/listDriver",
            "/bookingCar/listReason",
            "/bookingCar/checkCarOnTrip",
            "/bookingHotel/checkHotelInBooking"
    ));

    private static final Set<String> RESOURCE_SERVICE_NO_NEED_AUTHOR_URLS = new HashSet<>(Arrays.asList(
            "/user/family/listNotification",
            "/user/family/readNotification",
            "/user/family/notification/delete",
            "/public/notification/sendNotice",
            "/public/applicationSetting/getByKey",
            "/applicationSetting/getPublicHolidaysInYear",
            "/canteen/getByIds",
            "/canteen/getDetail",
            "/canteen/getCanteenForChef",
            "/hotel/getDetail",
            "/hotel/getByIds",
            "/hotel/filter",
            "/hotel/filterForApp",
            "/hotel/getDetail",
            "/hotel/getByIds",
            "/meal/getDetail",
            "/menu/getDetail",
            "/menu/filter",
            "/car/carReport",
            "/car/getDetail",
            "/province/getList",
            "/province/getDetail",
            "/district/filter",
            "/district/getDetail",
            "/canteen/checkCanteenForChef"
    ));

    private static NoNeedAuthorUrls instance;

    private NoNeedAuthorUrls() {}

    public static NoNeedAuthorUrls getInstance() {
//      This class is a singleton, ensuring only one instance of NoNeedAuthorUrls is created
        if (instance == null) {
            instance = new NoNeedAuthorUrls();
        }
        return instance;
    }

    public Set<String> getUrls() {
        Set<String> allUrls = new HashSet<>();
        allUrls.addAll(ACCOUNT_SERVICE_NO_NEED_AUTHOR_URLS);
        allUrls.addAll(BOOKING_SERVICE_NO_NEED_AUTHOR_URLS);
        allUrls.addAll(RESOURCE_SERVICE_NO_NEED_AUTHOR_URLS);
        return allUrls;
    }

}

