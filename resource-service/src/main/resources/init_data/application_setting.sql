INSERT INTO mytel_family_resource.application_setting (id, created_at, last_updated_at, description, app_key, status, value) VALUES ('018629be-5fa3-4014-a928-70e704a41f43', '2023-06-07 10:19:06.400616', '2023-05-15 13:40:01.764000', 'send notifications to users saying they have dinner tomorrow', 'config_send_notification_to_eu_have_dinner', 'ACTIVE', '{
    "en": {
        "subject": "Dinner",
        "content": "You have dinner ordered for today"
    },
    "my": {
        "subject": "ညစာ",
        "content": "ဒီနေ့အတွက် ညစာ မှာထားပြီ"
    }
}');
INSERT INTO mytel_family_resource.application_setting (id, created_at, last_updated_at, description, app_key, status, value) VALUES ('09581f0e-b828-450b-aa32-eb5b3a24bc7b', '2023-05-15 13:43:22.894000', '2023-05-16 09:56:56.655000', 'config the price of each meal', 'config_meal_price', 'ACTIVE', '[
{
        "mealType": "BREAKFAST",
        "price": 10
    },
    {
        "mealType": "LUNCH",
        "price": 20
    },
    {
        "mealType": "DINNER",
        "price": 30
    }
]');
INSERT INTO mytel_family_resource.application_setting (id, created_at, last_updated_at, description, app_key, status, value) VALUES ('0c300006-d975-4f6c-8141-77e750f70fa1', '2023-05-15 09:51:28.443000', '2023-06-07 09:47:18.148000', 'National Holidays in Myanmar in 2023
Note: Please manually create additional settings for each year in the format like this config, otherwise the system will not recognize it', 'public_holiday_2023', 'ACTIVE', '{
  "New Year\'s Day": "01/01/2023",
  "Independence Day": "04/01/2023",
  "Union Day": "12/02/2023",
  "Peasants\' Day": "02/03/2023",
  "Full Moon Day of Tabaung": "05/03/2023",
  "Armed Forces Day": "27/03/2023",
  "Thingyan Water Festival Day 1": "09/04/2023",
  "Thingyan Water Festival Day 2": "10/04/2023",
  "Thingyan Water Festival Day 3": "11/04/2023",
  "Thingyan Water Festival Day 4": "12/04/2023",
  "Myanmar New Year": "17/04/2023",
  "Labour Day": "01/05/2023",
  "Full Moon Day of Kasong": "03/05/2023",
  "Martyrs\' Day": "19/07/2023",
  "Full Moon Day of Waso": "01/08/2023",
  "Full Moon Day of Thadingyut": "29/10/2023",
  "Full Moon Day of Tazaungmone": "27/11/2023",
  "National Day": "07/12/2023",
  "Christmas Day": "25/12/2023",
  "New Year Holiday": "31/12/2023"
}');
INSERT INTO mytel_family_resource.application_setting (id, created_at, last_updated_at, description, app_key, status, value) VALUES ('1987c54f-3483-4aad-bf58-ea1ba0485be4', '2023-06-06 07:50:32.780466', '0000-00-00 00:00:00.000000', 'template noti send a notification to user when chef topup balance', 'send_notification_topup_balance', 'ACTIVE', '{
    "en":{
            "subject":"Topup balance",
            "content":"You have been added {{noOfPoint}} points by Chef {{chefName}} to the Mytel Family app"
        },
    "my": {
        "subject":"ငွေဖြည့်လက်ကျန်",
        "content":"Mytel Family App တွင် စားဖိုမှူး {{chefName}} မှ သင့်အား {{noOfPoint}} အမှတ်များ ပေါင်းထည့်ထားပါသည်"
        }    
}');
INSERT INTO mytel_family_resource.application_setting (id, created_at, last_updated_at, description, app_key, status, value) VALUES ('1e769011-caff-48d3-8727-18cb74722c6d', '2023-04-25 03:24:08.685000', '2023-04-25 03:24:08.685000', 'template noti send a notification import user', 'send-notification-user-added', 'ACTIVE', '{
    "en":{
            "subject":"",
            "content":"You have been created an account. MyTel Family app login account information is {phone}/{password}"
        },
    "my": {
        "subject":"",
        "content":"အကောင့်တစ်ခု ဖန်တီးပြီးပါပြီ။ MyTel Family အက်ပ်၏ အကောင့်ဝင်ရောက်ခြင်း အချက်အလက်သည် {phone}/{password} ဖြစ်သည်"
        }    
}');
INSERT INTO mytel_family_resource.application_setting (id, created_at, last_updated_at, description, app_key, status, value) VALUES ('24303f01-24a1-4dca-a563-94bd47fa5b4d', '2023-04-25 03:18:53.667046', '2023-04-25 03:15:12.316000', 'template noti send a notification to register', 'send-notification-to-register', 'ACTIVE', '{
    "en":{
            "subject":"",
            "content":"The account you registered at Mytel family system has been {action}"
        },
    "my": {
        "subject":"",
        "content":"Mytel မိသားစုစနစ်တွင် သင်မှတ်ပုံတင်ထားသော အကောင့်သည် {လုပ်ဆောင်ချက်} ဖြစ်သည်"
        }    
}');
INSERT INTO mytel_family_resource.application_setting (id, created_at, last_updated_at, description, app_key, status, value) VALUES ('341b5644-87fd-4edb-948f-b2abe840cc15', '2023-06-07 10:19:06.210458', '0000-00-00 00:00:00.000000', '', 'send_eu_notification_reject_booking_car', 'ACTIVE', '{
    "en": {
        "subject": "Booking hotel is rejected",
        "content": "Your booking request has been rejected by {{username}}"
    },
    "my": {
        "subject": "Hotel Booking ကို ပယ်ချသည်",
        "content": "သင်၏ ကြိုတင်မှာယူမှု တောင်းဆိုချက်ကို {{username}} မှ ပယ်ချခဲ့သည်"
    }
}');
INSERT INTO mytel_family_resource.application_setting (id, created_at, last_updated_at, description, app_key, status, value) VALUES ('49ca11df-5502-48c7-80a4-c04054a2c86d', '2023-06-07 10:19:06.320432', '2023-04-17 07:11:44.792000', 'template noti send a notification to driver', 'config_send_notification_to_driver', 'ACTIVE', '{
    "en": {
        "subject": "You has new trip",
        "content": "You have been assigned to the trip of {{username}}."
    },
    "my": {
        "subject": "သင့်တွင် ခရီးစဉ်အသစ်ရှိသည်",
        "content": "{{username}} ၏ ခရီးစဉ်တွင် သင့်အား တာဝန်ပေးထားပါသည်."
    }
}');
INSERT INTO mytel_family_resource.application_setting (id, created_at, last_updated_at, description, app_key, status, value) VALUES ('6c6471c2-1a2e-4cfd-8769-abda600b386c', '2023-06-06 07:50:32.836557', '0000-00-00 00:00:00.000000', null, 'config_noti_request_booking_hotel', 'ACTIVE', '{
    "en"{
        "subject": "Approve booking hotel",
        "content": "You have received a new hotel booking request from {{username}}, please check and verify request."
    },
    "my": {
        "subject": "Hotel Booking ကိုအတည်ပြုပါ",
        "content": "သင်သည် {{username}} ထံမှ ဟိုတယ်ကြိုတင်စာရင်းသွင်းမှု တောင်းဆိုချက်အသစ်ကို လက်ခံရရှိထားပြီး၊ ကျေးဇူးပြု၍ တောင်းဆိုချက်ကို စစ်ဆေးပြီး အတည်ပြုပါ."
    }
}');
INSERT INTO mytel_family_resource.application_setting (id, created_at, last_updated_at, description, app_key, status, value) VALUES ('6f5ffb9c-2d2f-4a09-9fb1-32b7f60eee33', '2023-06-07 10:19:06.236830', '2023-05-15 13:40:09.283000', 'send notifications to users saying they have lunch tomorrow', 'config_send_notification_to_eu_have_lunch', 'ACTIVE', '{
    "en": {
        "subject": "Lunch",
        "content": "You have lunch ordered for today"
    },
    "my": {
        "subject": "နေ့လည်စာ",
        "content": "ဒီနေ့အတွက် နေ့လည်စာ မှာထားပြီ"
    }
}');
INSERT INTO mytel_family_resource.application_setting (id, created_at, last_updated_at, description, app_key, status, value) VALUES ('754958d3-ecc4-41dc-80c9-f9c82bbb03cc', '2023-06-07 10:19:06.264600', '0000-00-00 00:00:00.000000', 'template send notice to Eu informing booking car has been cancel because it has not been started', 'config_notice_cancel_booking_car_not_started', 'ACTIVE', '{
    "en": {
        "subject": "Expired Booking Car",
        "content": "Booking has expired by not being started"
    },
    "my": {
        "subject": "သက်တမ်းကုန်ကြိုတင်စာရင်းသွင်းထားသောကား",
        "content": "စတင်ခြင်းမပြုဘဲ ကြိုတင်စာရင်းသွင်းခြင်းမှာ သက်တမ်းကုန်ဆုံးသွားပါပြီ"
    }
}');
INSERT INTO mytel_family_resource.application_setting (id, created_at, last_updated_at, description, app_key, status, value) VALUES ('7e537c70-1ef0-465e-a842-5112cd257a34', '2023-05-15 16:06:09.904000', '2023-06-07 15:52:42.249000', 'Users can only scan qr in the range of time
Note: if you edit this config, please follow the current format.', 'time_scan_qr_meal', 'ACTIVE', '{
   "Breakfast": "06:00-08:30",
   "Lunch": "08:30-13:00",
   "Dinner": "13:00-20:00"
}');
INSERT INTO mytel_family_resource.application_setting (id, created_at, last_updated_at, description, app_key, status, value) VALUES ('8ef28bec-4fd9-40d1-a649-906592cc50d5', '2023-06-06 07:50:32.621594', '2023-05-10 14:45:52.933000', 'template noti send to person has role approve', 'config_noti_request_booking_car', 'ACTIVE', '{
    "en"{
        "subject": "Approve booking car",
        "content": "You have received a new car booking request from {{username}}, please check and verify request."
    },
    "my": {
        "subject": "ကားကြိုတင်စာရင်းသွင်းခြင်းကို အတည်ပြုပါ",
        "content": "သင်သည် {{username}} ထံမှ ကားကြိုတင်စာရင်းသွင်းရန် တောင်းဆိုချက်ကို လက်ခံရရှိထားပြီး၊ ကျေးဇူးပြု၍ တောင်းဆိုချက်ကို စစ်ဆေးပြီး အတည်ပြုပါ."
    }
}');
INSERT INTO mytel_family_resource.application_setting (id, created_at, last_updated_at, description, app_key, status, value) VALUES ('9b48f634-da72-11ed-afa1-0242ac120002', '2023-06-07 10:13:56.481045', '2023-06-07 14:08:16.831000', 'Config list reason booking car', 'config_reason_booking_car', 'ACTIVE', '{
    "en": [
        " Personal reason/trip canceled",
        "Booking has not been approved",
        "Change time or change destination"
    ],
    "my": [
        "ကိုယ်ရေးကိုယ်တာ အကြောင်းပြချက်/ ခရီးစဉ်ကို ပယ်ဖျက်လိုက်သည်။",
        "ဟိုတယ်ကြိုတင်စာရင်းသွင်းခြင်းကို အတည်မပြုပါ။",
        "အချိန်ပြောင်းပါ သို့မဟုတ် ဦးတည်ရာကို ပြောင်းပါ။"
    ]
}');
INSERT INTO mytel_family_resource.application_setting (id, created_at, last_updated_at, description, app_key, status, value) VALUES ('9e69ead6-f556-11ed-a05b-0242ac120003', '2023-06-07 10:13:56.451849', '2023-06-07 14:14:35.140000', 'Config list reason booking meal', 'config_reason_booking_meal', 'ACTIVE', '{
    "en": [
        "Personal reason",
        "No longer need to order",
        "Change order information"
    ],
    "my": [
        "ကိုယ်ပိုင်အကြောင်းပြချက်",
        "အော်ဒါမှာစရာမလိုတော့ဘူး။",
        "မှာယူမှုအချက်အလက်ကို ပြောင်းလဲပါ။"
    ]
}');
INSERT INTO mytel_family_resource.application_setting (id, created_at, last_updated_at, description, app_key, status, value) VALUES ('ae578b5b-74b1-42d9-982e-2565c31d2829', '2023-06-07 10:19:06.292492', '0000-00-00 00:00:00.000000', 'template send notice to Eu informing booking hotel has been cancel because it has not been approved', 'config_notice_cancel_booking_hotel', 'ACTIVE', '{
    "en": {
        "subject": "Expired Booking Hotel",
        "content": "Booking has expired by not being approved"
    },
    "my": {
        "subject": "သက်တမ်းကုန်ကြိုတင်စာရင်းသွင်းထားသောဟိုတယ်",
        "content": "အတည်မပြုခြင်းကြောင့် ကြိုတင်စာရင်းသွင်းမှု သက်တမ်းကုန်သွားပါပြီ"
    }
}');
INSERT INTO mytel_family_resource.application_setting (id, created_at, last_updated_at, description, app_key, status, value) VALUES ('b0fdac1a-0c64-43b0-a049-c99040373ee8', '2023-06-07 09:43:23.273000', '2023-06-07 09:48:26.262000', 'Expiration time to book meal of the day', 'deadline_time_booking_meal', 'ACTIVE', '{
   "Breakfast": "00:00",
   "Lunch": "10:00",
   "Dinner": "14:00"
}');
INSERT INTO mytel_family_resource.application_setting (id, created_at, last_updated_at, description, app_key, status, value) VALUES ('b612954a-fad1-11ed-be56-0242ac120002', '2023-06-07 10:19:06.453547', '0000-00-00 00:00:00.000000', null, 'send_eu_notification_approve_booking_hotel', 'ACTIVE', '{
    "en": {
        "subject": "Create booking hotel success",
        "content": "Your booking hotel request has been approved"
    },
    "my": {
        "subject": "Hotel Bookingလုပ်ခြင်းအောင်မြင်မှုဖန်တီးပါ",
        "content": "သင်၏ Hotel ကြိုတင်မှာယူမှုတောင်းဆိုချက်ကို အတည်ပြုပြီးဖြစ်သည်."
    }
}');
INSERT INTO mytel_family_resource.application_setting (id, created_at, last_updated_at, description, app_key, status, value) VALUES ('b9d62d70-fe0b-4d5b-b827-8c494263212a', '2023-06-07 10:19:06.347014', '2023-04-11 10:54:38.060000', 'template noti send a notification to the user reporting success', 'send_eu_notification_approve_booking_car', 'ACTIVE', '{
    "en": {
        "subject": "Create booking car success",
        "content": "Your booking car request has been approved."
    },
    "my": {
        "subject": "ကားကြိုတင်စာရင်းသွင်းခြင်း အောင်မြင်မှုကို Create ပါ",
        "content": "သင်၏ကားကြိုတင်မှာယူမှုတောင်းဆိုချက်ကို အတည်ပြုပြီးဖြစ်သည်."
    }
}');
INSERT INTO mytel_family_resource.application_setting (id, created_at, last_updated_at, description, app_key, status, value) VALUES ('ba4924c8-939e-4372-8504-65b1d73764df', '2023-06-07 10:13:01.019086', '2023-06-07 14:07:50.297000', null, 'config_reason_booking_hotel', 'ACTIVE', '{
    "en": [
        "Personal reason/trip canceled",
        "Booking has not been approved",
        "Change time or change destination"
    ],
    "my": [
        "ကိုယ်ရေးကိုယ်တာ အကြောင်းပြချက်/ ခရီးစဉ်ကို ပယ်ဖျက်လိုက်သည်။",
        "ဟိုတယ်ကြိုတင်စာရင်းသွင်းခြင်းကို အတည်မပြုပါ။",
        "အချိန်ပြောင်းပါ သို့မဟုတ် ဦးတည်ရာကို ပြောင်းပါ။"
    ]
}');
INSERT INTO mytel_family_resource.application_setting (id, created_at, last_updated_at, description, app_key, status, value) VALUES ('bf4a4483-14f1-4320-8f49-bef697012906', '2023-06-07 09:49:36.247000', '2023-06-07 13:48:30.805000', 'Expiration time to cancel booking meal of the day', 'deadline_time_cancel_booking_meal', 'ACTIVE', '{
   "Breakfast": "00:00",
   "Lunch": "08:00",
   "Dinner": "14:00"
}');
INSERT INTO mytel_family_resource.application_setting (id, created_at, last_updated_at, description, app_key, status, value) VALUES ('c320144d-d4a1-4af1-9e0d-45ac0675adb6', '2023-06-07 10:19:06.479306', '2023-04-13 15:54:19.528000', 'template noti send a notification to driver manager', 'config_noti_request_assign_driver', 'ACTIVE', '{
    "en": {
        "subject": "Assign booking car",
        "content": "You have received a new car booking request from {{username}}, please check and assign driver for booking."
    },
    "my": {
        "subject": "Booking လုပ်ထားသောကားကို သတ်မှတ်ပေးပါ",
        "content": "သင်သည် {{username}} ထံမှ ကားကြိုတင်စာရင်းသွင်းရန် တောင်းဆိုချက်ကို လက်ခံရရှိထားပြီး၊ ကျေးဇူးပြု၍ ကြိုတင်စာရင်းသွင်းရန်အတွက် ယာဉ်မောင်းကို စစ်ဆေးပြီး တာဝန်ပေးလိုက်ပါ."
    }
}');
INSERT INTO mytel_family_resource.application_setting (id, created_at, last_updated_at, description, app_key, status, value) VALUES ('c6d2baa5-650a-4cd7-a347-a70ef273a7fe', '2023-06-07 10:19:06.180933', '2023-05-15 13:40:14.166000', 'send notifications to users saying they have breakfast tomorrow', 'config_send_notification_to_eu_have_breakfast', 'ACTIVE', '{
    "en": {
        "subject": "Breakfast",
        "content": "You have breakfast ordered for tomorrow"
    },
    "my": {
        "subject": "မနက်စာ",
        "content": "မနက်​ဖြန်​အတွက်​ မနက်​စာ မှာထားပြီ"
    }
}');
INSERT INTO mytel_family_resource.application_setting (id, created_at, last_updated_at, description, app_key, status, value) VALUES ('ed057d63-d28e-4e2a-a712-4f9b3ae845da', '2023-06-07 10:19:06.426330', '2023-04-18 03:37:19.258000', 'template noti send a notification to driver is assigned', 'send_eu_notification_reject_booking_car', 'ACTIVE', '{
    "en": {
        "subject": "Booking car is rejected",
        "content": "Your booking request has been rejected by {{username}}."
    },
    "my": {
        "subject": "ကားကြိုတင်မှာယူခြင်းကို ပယ်ချပါသည်",
        "content": "သင်၏ ကြိုတင်မှာယူမှု တောင်းဆိုချက်ကို {{username}} မှ ပယ်ချခဲ့သည်."
    }
}');
INSERT INTO mytel_family_resource.application_setting (id, created_at, last_updated_at, description, app_key, status, value) VALUES ('fd031666-0420-11ee-be56-0242ac120002', '2023-06-07 10:19:06.374044', '0000-00-00 00:00:00.000000', 'template send notice to Eu informing booking car has been cancel because it has not been approved', 'config_notice_cancel_booking_car', 'ACTIVE', '{
    "en": {
        "subject": "Expired Booking Car",
        "content": "Booking has expired by not being approved"
    },
    "my": {
        "subject": "သက်တမ်းကုန်ကြိုတင်စာရင်းသွင်းထားသောကား",
        "content": "အတည်မပြုခြင်းကြောင့် ကြိုတင်စာရင်းသွင်းမှု သက်တမ်းကုန်သွားပါပြီ"
    }
}');
