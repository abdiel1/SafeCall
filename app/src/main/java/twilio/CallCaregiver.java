/*
 *  Copyright (c) 2011 by Twilio, Inc., all rights reserved.
 *
 *  Use of this software is subject to the terms and conditions of 
 *  the Twilio Terms of Service located at http://www.twilio.com/legal/tos
 */

package twilio;

import android.content.Context;

public class CallCaregiver
{
    private TwilioPhone phone;
    private static CallCaregiver callCaregiver;

    private CallCaregiver(Context context)
    {
        phone = new TwilioPhone(context);
    }

    public static CallCaregiver getInstance(Context context){
        if(callCaregiver == null){
            callCaregiver = new CallCaregiver(context);
        }
        return callCaregiver;
    }

    public void connect(String number)
    {
        phone.connect(number);
    }

    public void disconnect()
    {
        phone.disconnect();
    }
}