# Social Cafe Android Sample App

Sample app for social cafe, counterpart to the web version: http://social-cafe.herokuapp.com/

Author: Vikas Gupta (www.facebook.com/vg)

## Installing

1. If you can't wait to try out the Social Cafe, the SocialCafe.apk is included in the root folder. Install it on your handset or emulator. Note that Single Sign On requires the Facebook app which can either be installed from the Google Play store (latest version) or using the Facebook.apk (old version) provided in the Facebook SDK.

1. Building and installing the samples requires you to add the Facebook SDK. Follow instructions at https://developers.facebook.com/docs/mobile/android/build/

1. Open Eclipse and create 'Android Project from Existing Code'. Use fbsamples/android-social-cafe folder as the Rood Directory

1. Link the Facebook SDK as Library in the Android settings of the Project Properties. Remove the existing Facebook Library if any as it likely be failing depending on your Facebook SDK location.

1. Note that the Single Sign On would not work using the pre-defined APP_ID in the SocialCafeApplication.java. This is because SSO requires defining the keyhash for your debug keystore in the app settings. You can however, create or use an existing app and define the og actions and objects as described below. If you are not familiar with the Open Graph concepts or would like a refresher, please read https://developers.facebook.com/docs/opengraph/
	1. Use the existing app or create a new Facebook app id at https://developers.facebook.com/apps and define following objects and actions:

		action - 'order', object - 'beverage'. Keep the default settings.

		action - like (built-in).

	1. Host some sample beverage objects (aka URLs) at the domain connected with your app. Instructions here - https://developers.facebook.com/docs/opengraph/objects/. If you don't have a host server readily available, you can use Heroku to setup a domain and host your URLs. Instructions here - https://developers.facebook.com/blog/post/558/
	1. Create a keyhash using your debug keystore and add this to your app settings. Follow instructions here - https://developers.facebook.com/docs/mobile/android/build/#sig
	1. Use your app id as the APP_ID in in SocialCafeApplication.java
	1. In SocialCafeApplication.java:onCreate() method, change the Drinks objects to the ones you defined.


1. Save, build and run the project.


## Code Structure

* SplashActivity.java - This activity is the launcher activity.

* LoginActivity.java - This shows the 'Login with Facebook' and starts the single sign on process

* SocialCafeApplication.java - This holds the facebook object and handles to user data. This also save and restore sessions and user data.

* MenuFragmentActivity.java - This displays the main menu with user name and picture and drinks menu. User select a drink on this screen to order

* OrderDrinkFragmentActivity.java - This displays the order page with the og story that will be published. Note that Friends, Location tagging and adding picture functionality is not yet supported.

* ConfirmationFragmentActivity.java - This shows the drink order confirmation page. User can like the drink by clicking on the Enjoy button.

* ApplicationStore.java - Stores session and user data in private storage for the application.

## Documentation

## Additional Resources

Facebook SDK for Android documentation can be found at https://developers.facebook.com/docs/reference/androidsdk/

## Contributing

All contributors must agree to and sign the [Facebook CLA](https://developers.facebook.com/opensource/cla) prior to submitting Pull Requests. We cannot accept Pull Requests until this document is signed and submitted.

## License

Copyright 2012-present Facebook, Inc.

You are hereby granted a non-exclusive, worldwide, royalty-free license to use, copy, modify, and distribute this software in source code or binary form for use in connection with the web services and APIs provided by Facebook.

As with any software that integrates with the Facebook platform, your use of this software is subject to the Facebook Developer Principles and Policies [http://developers.facebook.com/policy/]. This copyright notice shall be included in all copies or substantial portions of the software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
