# All health app and device data summery

Integrated apps:
1) Google fit. (Fully done and fetching users steps and heart data from Google fit API)
2) Huawei Health. (Partially done and waiting for API key approval from Huawei Developer Console and it might 3-7 days)
3) Samsung Health. (Not implemented as for now Samsung is stopping sharing all health data.Ref - (https://developer.samsung.com/health/android/data/guide/process.html)
4) Xiaomi MI fit. (Not implemented as it seems they don't share any API for getting health data)
5) Garmin Connect. (Not implemented as to get their API and SDK is limited to companies and institutions and it also takes 3-7 days to get API key for a company or institution)

Integrated devices:
1) Bluetooth Low Energy Devices. (Fully done and it is showing paired connected BLE devices and some data from it)
2) Amazfit GTR 2. (Not implemented as they required only its controller app to control it. I tried it via a reverse engineering way from this repo - https://github.com/Freeyourgadget/Gadgetbridge but it is now archieved and it is prevented by device with it's firmware. But it is possible as it is controlled by Zepp 
