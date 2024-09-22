package mm.com.mytelpay.family.utils;

public class NoticeTemplate {

    private NoticeTemplate() {

    }

    public static final String EU_SEND_NOTIFICATION_APPROVE_BOOKING_CAR = "config_noti_request_booking_car";

    public static final String EU_SEND_NOTIFICATION_APPROVE_BOOKING_CAR_SUCCESS = "send_eu_notification_approve_booking_car";

    public static final String EU_SEND_NOTIFICATION_REJECT_BOOKING_CAR = "send_eu_notification_reject_booking_car";

    public static final String SEND_NOTIFICATION_TO_DRIVER_MANAGEMENT = "config_noti_request_assign_driver";

    public static final String SEND_NOTIFICATION_TO_APPROVAL_USER = "config_sms_noti_registered_user";

    public static final String SEND_NOTIFICATION_TO_REGISTER = "config_app_noti_approval_user";

    public static final String SEND_NOTIFICATION_USER_ADDED = "config_sms_noti_create_user";

    public static final String SEND_NOTIFICATION_TO_DRIVER = "config_send_notification_to_driver";

    public static final String SEND_NOTIFICATION_TO_EU_HAVE_BREAKFAST = "config_send_notification_to_eu_have_breakfast";

    public static final String SEND_NOTIFICATION_TO_EU_HAVE_DINNER = "config_send_notification_to_eu_have_dinner";

    public static final String SEND_NOTIFICATION_TO_EU_HAVE_LUNCH = "config_send_notification_to_eu_have_lunch";

    public static final String SEND_NOTIFICATION_TOPUP_BALANCE = "send_notification_topup_balance";

    public static final String EU_SEND_NOTIFICATION_APPROVE_BOOKING_HOTEL = "send_eu_notification_approve_booking_hotel";

    public static final String EU_SEND_NOTIFICATION_REJECT_BOOKING_HOTEL = "send_eu_notification_reject_booking_hotel";

    public static final String SEND_ADMIN_NOTIFICATION_APPROVE_BOOKING_HOTEL = "config_noti_request_booking_hotel";

    public static final String PAY_LOAD = "payload";

    public static final String TIME_JOB = "time job";

    public static final String ID_PAYLOAD = "idPayload";

    public static final String MYTEL_FAMILY = "Mytel-Family";

    public static final String ADMIN_APPROVAL_SMS_DEFAULT_MESSAGE = "The account you registered at Mytel family system has been {action}";

    public static final String IMPORT_USER_NOTIFY_SMS_DEFAULT_MESSAGE = "You have been created an account. MyTel Family app login account information is {phone}/{password}";

    public static final String BOOKING_EXPIRED_REASON = "Expired-Booking has expired by not being approved.";

    public static final String BOOKING_EXPIRED_REASON_NOT_STARTED = "Expired-Booking has expired by not being started.";

    public static final String SEND_NOTICE_BOOKING_CAR_EXPIRED = "config_notice_cancel_booking_car";

    public static final String SEND_NOTICE_BOOKING_CAR_NOT_STARTED = "config_notice_cancel_booking_car_not_started";

    public static final String SEND_NOTICE_BOOKING_HOTEL_EXPIRED = "config_notice_cancel_booking_hotel";


    public static final String EU_SEND_NOTIFICATION_APPROVE_BOOKING_CAR_DEFAULT = "{\n" +
            "    \"en\": {\n" +
            "        \"subject\": \"Approve booking car\",\n" +
            "        \"content\": \"You have received a new car booking request from {{username}}, please check and verify request.\"\n" +
            "    },\n" +
            "    \"my\": {\n" +
            "        \"subject\": \"ကားကြိုတင်စာရင်းသွင်းခြင်းကို အတည်ပြုပါ\",\n" +
            "        \"content\": \"သင်သည် {{username}} ထံမှ ကားကြိုတင်စာရင်းသွင်းရန် တောင်းဆိုချက်ကို လက်ခံရရှိထားပြီး၊ ကျေးဇူးပြု၍ တောင်းဆိုချက်ကို စစ်ဆေးပြီး အတည်ပြုပါ.\"\n" +
            "    }\n" +
            "}";

    public static final String SEND_NOTIFICATION_TO_DRIVER_MANAGEMENT_DEFAULT = "{\n" +
            "        \"en\":{\n" +
            "            \"subject\":\"Assign booking car\",\n" +
            "            \"content\":\"You have received a new car booking request from {{username}}, please check and assign driver for booking.\"\n" +
            "        },\n" +
            "        \"my\":{\n" +
            "            \"subject\":\"Booking လုပ်ထားသောကားကို သတ်မှတ်ပေးပါ\",\n" +
            "            \"content\":\"သင်သည် {{username}} ထံမှ ကားကြိုတင်စာရင်းသွင်းရန် တောင်းဆိုချက်ကို လက်ခံရရှိထားပြီး၊ ကျေးဇူးပြု၍ ကြိုတင်စာရင်းသွင်းရန်အတွက် ယာဉ်မောင်းကို စစ်ဆေးပြီး တာဝန်ပေးလိုက်ပါ.\"\n" +
            "        }\n" +
            "}";

    public static final String EU_SEND_NOTIFICATION_APPROVE_SUCCESS_BOOKING_CAR_DEFAULT = "{\n" +
            "        \"en\":{\n" +
            "            \"subject\":\"Create booking car success\",\n" +
            "            \"content\":\"Your booking car request has been approved.\"\n" +
            "        },\n" +
            "        \"my\":{\n" +
            "            \"subject\":\"ကားကြိုတင်စာရင်းသွင်းခြင်း အောင်မြင်မှုကို Create ပါ\",\n" +
            "            \"content\":\"သင်၏ကားကြိုတင်မှာယူမှုတောင်းဆိုချက်ကို အတည်ပြုပြီးဖြစ်သည်.\"\n" +
            "        }\n" +
            "}";

    public static final String SEND_NOTIFICATION_TO_DRIVER_DEFAULT = "{\n" +
            "        \"en\":{\n" +
            "            \"subject\":\"You has new trip\",\n" +
            "            \"content\":\"You have been assigned to the trip of {{username}}.\"\n" +
            "        },\n" +
            "        \"my\":{\n" +
            "            \"subject\":\"သင့်တွင် ခရီးစဉ်အသစ်ရှိသည်\",\n" +
            "            \"content\":\"{{username}} ၏ ခရီးစဉ်တွင် သင့်အား တာဝန်ပေးထားပါသည်.\"\n" +
            "        }\n" +
            "    }";

    public static final String EU_SEND_NOTIFICATION_REJECT_BOOKING_CAR_DEFAULT = "{\n" +
            "        \"en\":{\n" +
            "            \"subject\":\"Booking car is rejected\",\n" +
            "            \"content\":\"Your booking request has been rejected by {{username}}.\"\n" +
            "        },\n" +
            "        \"my\":{\n" +
            "            \"subject\":\"ကားကြိုတင်မှာယူခြင်းကို ပယ်ချပါသည်\",\n" +
            "            \"content\":\"သင်၏ ကြိုတင်မှာယူမှု တောင်းဆိုချက်ကို {{username}} မှ ပယ်ချခဲ့သည်.\"\n" +
            "        }\n" +
            "}";

    public static final String SEND_NOTIFICATION_TO_EU_HAVE_BREAKFAST_DEFAULT = "{\n" +
            "        \"en\":{\n" +
            "            \"subject\":\"Breakfast\",\n" +
            "            \"content\":\"You have breakfast ordered for tomorrow\"\n" +
            "        },\n" +
            "        \"my\":{\n" +
            "            \"subject\":\"မနက်စာ\",\n" +
            "            \"content\":\"မနက်\u200Bဖြန်\u200Bအတွက်\u200B မနက်\u200Bစာ မှာထားပြီ\"\n" +
            "        }\n" +
            "}";

    public static final String SEND_NOTIFICATION_TO_EU_HAVE_DINNER_DEFAULT = "{\n" +
            "        \"en\":{\n" +
            "            \"subject\":\"Dinner\",\n" +
            "            \"content\":\"You have dinner ordered for today\"\n" +
            "        },\n" +
            "        \"my\":{\n" +
            "            \"subject\":\"ညစာ\",\n" +
            "            \"content\":\"ဒီနေ့အတွက် ညစာ မှာထားပြီ\"\n" +
            "        }\n" +
            "}";

    public static final String SEND_NOTIFICATION_TO_EU_HAVE_LUNCH_DEFAULT = "{\n" +
            "        \"en\":{\n" +
            "            \"subject\":\"Lunch\",\n" +
            "            \"content\":\"You have lunch ordered for today\"\n" +
            "        },\n" +
            "        \"my\":{\n" +
            "            \"subject\":\"နေ့လည်စာ\",\n" +
            "            \"content\":\"ဒီနေ့အတွက် နေ့လည်စာ မှာထားပြီ\"\n" +
            "        }\n" +
            "}";

    public static final String SEND_NOTIFICATION_TOPUP_BALANCE_DEFAULT_VALUE = "{\n" +
            "    \"en\":{\n" +
            "            \"subject\":\"Topup balance\",\n" +
            "            \"content\":\"You have been added {{noOfPoint}} points by Chef {{chefName}} to the Mytel Family app\"\n" +
            "        },\n" +
            "    \"my\": {\n" +
            "        \"subject\":\"ငွေဖြည့်လက်ကျန်\",\n" +
            "        \"content\":\"Mytel Family App တွင် စားဖိုမှူး {{chefName}} မှ သင့်အား {{noOfPoint}} အမှတ်များ ပေါင်းထည့်ထားပါသည်\"\n" +
            "        }    \n" +
            "}";

    public static final String EU_SEND_NOTIFICATION_APPROVE_BOOKING_HOTEL_DEFAULT = "{\n" +
            "        \"en\":{\n" +
            "            \"subject\":\"Create booking hotel success\",\n" +
            "            \"content\":\"Your booking hotel request has been approved\"\n" +
            "        },\n" +
            "        \"my\":{\n" +
            "            \"subject\":\"Hotel Bookingလုပ်ခြင်းအောင်မြင်မှုဖန်တီးပါ\",\n" +
            "            \"content\":\"သင်၏ Hotel ကြိုတင်မှာယူမှုတောင်းဆိုချက်ကို အတည်ပြုပြီးဖြစ်သည်.\"\n" +
            "        }\n" +
            "}";

    public static final String EU_SEND_NOTIFICATION_REJECT_BOOKING_HOTEL_DEFAULT = "{\n" +
            "        \"en\":{\n" +
            "            \"subject\":\"Booking hotel is rejected\",\n" +
            "            \"content\":\"Your booking request has been rejected by {{username}}\"\n" +
            "        },\n" +
            "        \"my\":{\n" +
            "            \"subject\":\"Hotel Booking ကို ပယ်ချသည်\",\n" +
            "            \"content\":\"သင်၏ ကြိုတင်မှာယူမှု တောင်းဆိုချက်ကို {{username}} မှ ပယ်ချခဲ့သည်\"\n" +
            "        }\n" +
            "}";

    public static final String SEND_ADMIN_NOTIFICATION_APPROVE_BOOKING_HOTEL_DEFAULT = "{\n" +
            "    \"en\": {\n" +
            "        \"subject\": \"Approve booking hotel\",\n" +
            "        \"content\": \"You have received a new hotel booking request from {{username}}, please check and verify request.\"\n" +
            "    },\n" +
            "    \"my\": {\n" +
            "        \"subject\": \"Hotel Booking ကိုအတည်ပြုပါ\",\n" +
            "        \"content\": \"သင်သည် {{username}} ထံမှ ဟိုတယ်ကြိုတင်စာရင်းသွင်းမှု တောင်းဆိုချက်အသစ်ကို လက်ခံရရှိထားပြီး၊ ကျေးဇူးပြု၍ တောင်းဆိုချက်ကို စစ်ဆေးပြီး အတည်ပြုပါ.\"\n" +
            "    }\n" +
            "}";


    public static final String SEND_NOTICE_BOOKING_HOTEL_EXPIRED_DEFAULT = "{\n" +
            "        \"en\":{\n" +
            "            \"subject\":\"Expired Booking Hotel\",\n" +
            "            \"content\":\"Booking has expired by not being approved\"\n" +
            "        },\n" +
            "        \"my\":{\n" +
            "            \"subject\":\"သက်တမ်းကုန်ကြိုတင်စာရင်းသွင်းထားသောဟိုတယ်\",\n" +
            "            \"content\":\"အတည်မပြုခြင်းကြောင့် ကြိုတင်စာရင်းသွင်းမှု သက်တမ်းကုန်သွားပါပြီ\"\n" +
            "        }\n" +
            "}";

    public static final String SEND_NOTICE_BOOKING_CAR_EXPIRED_DEFAULT = "{\n" +
            "        \"en\":{\n" +
            "            \"subject\":\"Expired Booking Car\",\n" +
            "            \"content\":\"Booking has expired by not being approved\"\n" +
            "        },\n" +
            "        \"my\":{\n" +
            "            \"subject\":\"သက်တမ်းကုန်ကြိုတင်စာရင်းသွင်းထားသောကား\",\n" +
            "            \"content\":\"အတည်မပြုခြင်းကြောင့် ကြိုတင်စာရင်းသွင်းမှု သက်တမ်းကုန်သွားပါပြီ\"\n" +
            "        }\n" +
            "}";

    public static final String SEND_NOTICE_BOOKING_CAR_NOT_STARTED_EXPIRED_DEFAULT = "{\n" +
            "        \"en\":{\n" +
            "            \"subject\":\"Expired Booking Car\",\n" +
            "            \"content\":\"Booking has expired by not being started\"\n" +
            "        },\n" +
            "        \"my\":{\n" +
            "            \"subject\":\"သက်တမ်းကုန်ကြိုတင်စာရင်းသွင်းထားသောကား\",\n" +
            "            \"content\":\"စတင်ခြင်းမပြုဘဲ ကြိုတင်စာရင်းသွင်းခြင်းမှာ သက်တမ်းကုန်ဆုံးသွားပါပြီ\"\n" +
            "        }\n" +
            "}";


    public static final String REGISTER_NOTIFY_SMS_DEFAULT_MESSAGE = "{\n" +
            "    \"en\":{\n" +
            "            \"subject\":\"New Register Account\",\n" +
            "            \"content\":\"The phone number {{phoneNumber}} has been successfully registered on the Mytel family app. Please verify and update the account status for the user\"\n" +
            "        },\n" +
            "    \"my\": {\n" +
            "        \"subject\":\"အကောင့်အသစ် မှတ်ပုံတင်ပါ။\",\n" +
            "        \"content\":\"ဖုန်းနံပါတ် {phoneNumber} ကို Mytel မိသားစုအက်ပ်တွင် အောင်မြင်စွာ စာရင်းသွင်းပြီးပါပြီ။ အသုံးပြုသူအတွက် အကောင့်အခြေအနေကို အတည်ပြုပြီး အပ်ဒိတ်လုပ်ပါ။\"\n" +
            "        }    \n" +
            "}";
}
