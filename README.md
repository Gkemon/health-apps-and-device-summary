# All health app and device data summery

## Integrated apps:
1) Google fit. (Fully done and fetching users steps and heart data from Google fit API But there is a note from [Google team](https://developers.google.com/fit/android)
>Note: The Fit Android API has been deprecated as of May 11, 2022 and will be turned down at the end of 2024. See the Fit Android API to Health Connect migration guide for instructions on which API or platform to migrate to. For a comparison of Health Connect with the Fit Android and Fitbit Web APIs, see the Health Connect comparison guide. To learn more about Health Connect and integrate with the API, see the Health Connect documentation.

As most of the devices and health apps are not adopting it right now and my region and device is not supporting Health Connect App so I can't implement it.

2) Huawei Health. (Partially done and waiting for API key approval from Huawei Developer Console and it might 3-7 days)
3) Samsung Health. (Not implemented as for now Samsung is stopping sharing all health data. Reference is [here](https://developer.samsung.com/health/android/data/guide/process.html) and it says 
>We are currently going through an update to better support our partners. For that reason, we will not be accepting any applications for the Partner Apps Program at this time.
4) Xiaomi MI fit. (Not implemented as it seems they don't share any API for getting health data)
5) Garmin Connect. (Not implemented as to get their API and SDK is limited to companies and institutions and it also takes 3-7 days to get API key for a company or institution)


## Integrated devices:
1) Bluetooth Low Energy Devices. (Fully done and it is showing paired connected BLE devices and some data from it)
2) Amazfit GTR 2. (Not implemented as they required only its controller app to control it. I tried it via a reverse engineering way from this repo - https://github.com/Freeyourgadget/Gadgetbridge but it is now archieved and it is prevented by device with it's firmware. But it is possible as it is controlled by [Zepp app](https://play.google.com/store/apps/details?id=com.huami.watch.hmwatchmanager&hl=en&gl=US) and it has [REST official API](https://github.com/zepp-health/rest-api/wiki) but it also has approval process and it says in doc that - 
>The SDKs have NOT been formally released, but ready for early integration. To get the latest release schedule, please contact us.

 <p align="center">
<a href="health-apps-and-device-summary"><img alt="Maintained" src="https://github.com/Gkemon/health-apps-and-device-summary/blob/master/home-screen.jpeg" height="600"/></a>
<a href="health-apps-and-device-summary"><img alt="Maintained" src="https://github.com/Gkemon/health-apps-and-device-summary/blob/master/details-screen.jpeg" height="600"/></a>
</p>

# Here is the video preview

<div align="center">
      <a href="https://www.youtube.com/shorts/Woo1q73tHio">
     <img 
      src="https://user-images.githubusercontent.com/22210002/188214530-14ceede9-58e2-4698-8970-f69b33802525.png" 
      alt="Everything Is AWESOME" 
      style="width:400px">
      </a>
</div>


  



